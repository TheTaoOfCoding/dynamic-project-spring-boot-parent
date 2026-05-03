package io.github.thetaoofcoding.dynamicbean.groovy;

import io.github.thetaoofcoding.dynamicbean.core.SAM;
import io.github.thetaoofcoding.dynamicbean.model.RefreshableBeanModel;
import io.github.thetaoofcoding.dynamicbean.scope.RefreshableScope;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@FunctionalInterface
public interface SourceResolver<T, R> {
    R resolve(T t);

    class SourceResolvers {
        public static SourceResolver<RefreshableBeanModel, BeanDefinitionHolder> beanDefinitionResolver(GroovyShellFactory groovyShellFactory) {
            return refreshableBeanModel -> {
                var script = refreshableBeanModel.script();
                var sam = (SAM<?, ?>) groovyShellFactory.create()
                        .evaluate(script);
                var beanName = refreshableBeanModel.beanName();
                var beanDefinition = BeanDefinitionBuilder
                        .genericBeanDefinition(SAM.class, () -> sam)
                        .setScope(RefreshableScope.SCOPE_NAME)
                        .getBeanDefinition();
                return new BeanDefinitionHolder(beanDefinition, beanName);
            };
        }

        public static SourceResolver<Environment, JdbcTemplate> earlyJdbcTemplateResolver() {
            return environment -> {
                var url = environment.getProperty("spring.datasource.url");
                var username = environment.getProperty("spring.datasource.username");
                var password = environment.getProperty("spring.datasource.password");
                var dataSource = new DriverManagerDataSource(url, username, password);
                return new JdbcTemplate(dataSource);
            };
        }
    }
}
