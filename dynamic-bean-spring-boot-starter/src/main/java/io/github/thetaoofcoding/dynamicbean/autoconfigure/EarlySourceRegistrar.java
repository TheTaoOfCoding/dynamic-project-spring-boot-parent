package io.github.thetaoofcoding.dynamicbean.autoconfigure;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import io.github.thetaoofcoding.dynamicbean.event.RefreshBeanEventListener;
import io.github.thetaoofcoding.dynamicbean.groovy.GroovyShellFactory;
import io.github.thetaoofcoding.dynamicbean.groovy.GroovyVariables;
import io.github.thetaoofcoding.dynamicbean.groovy.SourceResolver;
import io.github.thetaoofcoding.dynamicbean.model.RefreshableBeanModel;
import io.github.thetaoofcoding.dynamicbean.scope.RefreshableScope;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.stream.Collectors;

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
    public static GroovyShellFactory groovyShellFactory(ApplicationContext applicationContext, @Qualifier("groovyContext") ThreadLocal<Object> threadLocal) {
        return () -> {
            // 默认导包
            var customizer = new ImportCustomizer();
            customizer.addImports("io.github.thetaoofcoding.dynamicbean.core.SAM");

            // 绑定上下文变量
            var binding = new Binding();
            binding.setVariable(GroovyVariables.VARIABLE_IOC, applicationContext);
            binding.setVariable(GroovyVariables.VARIABLE_LOCALS, threadLocal);

            // 使用独立类加载器
            var groovyClassLoader = new GroovyClassLoader();

            // 配置编译器
            var config = new CompilerConfiguration();
            config.addCompilationCustomizers(customizer);
            return new GroovyShell(groovyClassLoader, binding, config);
        };
    }

    @Bean
    public static BeanDefinitionRegistryPostProcessor beanDefinitionRegistryPostProcessor(GroovyShellFactory groovyShellFactory, Environment environment) {
        return registry -> {
            log.info("starting DatabaseMode BeanDefinitionRegistry.");
            var jdbcTemplate = SourceResolver.SourceResolvers.earlyJdbcTemplateResolver().resolve(environment);
            var refreshBeanModels = jdbcTemplate.query("select id, bean_name, script, description from refreshable_bean", RefreshableBeanModel::of);
            var beanDefinitionHolders = refreshBeanModels.stream()
                    .map(SourceResolver.SourceResolvers.beanDefinitionResolver(groovyShellFactory)::resolve)
                    .peek(beanDefinitionHolder -> log.debug("register beanDefinition [{}]", beanDefinitionHolder.getBeanName()))
                    .collect(Collectors.toSet());
            beanDefinitionHolders.forEach(beanDefinitionHolder -> registry.registerBeanDefinition(beanDefinitionHolder.getBeanName(), beanDefinitionHolder.getBeanDefinition()));
        };
    }

    @Bean
    public static RefreshBeanEventListener refreshBeanEventListener(RefreshableScope refreshableScope, GroovyShellFactory groovyShellFactory) {
        return new RefreshBeanEventListener(refreshableScope, groovyShellFactory);
    }
}
