package io.github.thetaoofcoding.dynamicbean.autoconfigure;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import io.github.thetaoofcoding.dynamicbean.event.RefreshBeanEventListener;
import io.github.thetaoofcoding.dynamicbean.groovy.GroovyShellFactory;
import io.github.thetaoofcoding.dynamicbean.groovy.GroovyVariables;
import io.github.thetaoofcoding.dynamicbean.groovy.ResourceResolver;
import io.github.thetaoofcoding.dynamicbean.model.RefreshableBeanModel;
import io.github.thetaoofcoding.dynamicbean.scope.RefreshableScope;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 提前初始化的资源配置
@Slf4j
@Configuration
public class EarlySourceRegistrar {

    // 自定义作用域
    @Bean
    public static RefreshableScope refreshableScope(DefaultListableBeanFactory defaultListableBeanFactory) {
        return new RefreshableScope(defaultListableBeanFactory);
    }

    @Bean
    public static CustomScopeConfigurer customScopeConfigurer(RefreshableScope refreshableScope) {
        var customScopeConfigurer = new CustomScopeConfigurer();
        customScopeConfigurer.addScope(RefreshableScope.SCOPE_NAME, refreshableScope);
        return customScopeConfigurer;
    }

    @Bean("groovyContext")
    public static ThreadLocal<Object> threadLocal() {
        return new ThreadLocal<>();
    }

    @Bean
    public static GroovyShellFactory groovyShellFactory(ApplicationContext applicationContext, @Qualifier("groovyContext") ThreadLocal<Object> threadLocal, Environment environment) {
        return () -> {
            // 默认导包
            var importCustomizer = new ImportCustomizer();
            importCustomizer.addImports("io.github.thetaoofcoding.dynamicbean.core.SAM");

            // 安全沙箱配置
            var defaultBlacklist = List.of("java.lang.System", "java.lang.Runtime");
            var addBlacklist = Binder.get(environment)
                    .bind("dynamic-bean.security.blacklist", Bindable.listOf(String.class))
                    .orElse(List.of());
            var blacklist = Stream.concat(addBlacklist.stream(), defaultBlacklist.stream())
                    .toList();
            var secureCustomizer = new SecureASTCustomizer();
            secureCustomizer.setDisallowedImports(blacklist);
            secureCustomizer.setDisallowedReceivers(blacklist);
            secureCustomizer.setIndirectImportCheckEnabled(true);

            // 合并配置
            var compilerConfiguration = new CompilerConfiguration();
            compilerConfiguration.addCompilationCustomizers(importCustomizer, secureCustomizer);

            // 使用独立类加载器
            var parentLoader = applicationContext.getClassLoader();
            var groovyClassLoader = new GroovyClassLoader(parentLoader);

            // 绑定上下文变量
            var binding = new Binding();
            binding.setVariable(GroovyVariables.VARIABLE_IOC, applicationContext);
            binding.setVariable(GroovyVariables.VARIABLE_LOCALS, threadLocal);

            // 创建 GroovyShell
            return new GroovyShell(groovyClassLoader, binding, compilerConfiguration);
        };
    }

    @Bean
    public static BeanDefinitionRegistryPostProcessor beanDefinitionRegistryPostProcessor(GroovyShellFactory groovyShellFactory, Environment environment) {
        return registry -> {
            log.info("Registering dynamic bean definitions...");
            var jdbcTemplate = ResourceResolver.SourceResolvers.earlyJdbcTemplateResolver()
                    .resolve(environment);
            var refreshableBeanModels = jdbcTemplate.query("select id, bean_name, script, description from refreshable_bean", RefreshableBeanModel::of);
            var beanDefinitionHolders = refreshableBeanModels.stream()
                    .map(ResourceResolver.SourceResolvers.beanDefinitionResolver(groovyShellFactory)::resolve)
                    .peek(beanDefinitionHolder -> log.debug("register dynamic bean : '{}'", beanDefinitionHolder.getBeanName()))
                    .collect(Collectors.toSet());
            beanDefinitionHolders.forEach(beanDefinitionHolder -> registry.registerBeanDefinition(beanDefinitionHolder.getBeanName(), beanDefinitionHolder.getBeanDefinition()));
        };
    }

    @Bean
    public static RefreshBeanEventListener refreshBeanEventListener(RefreshableScope refreshableScope, GroovyShellFactory groovyShellFactory) {
        return new RefreshBeanEventListener(refreshableScope, groovyShellFactory);
    }
}
