package io.github.thetaoofcoding.dynamicbean.scope;

import io.github.thetaoofcoding.dynamicbean.core.SAM;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 自定义域对象，存储 RefreshAble Bean
public record RefreshableScope(DefaultListableBeanFactory defaultListableBeanFactory,
                               Map<String, SAM<?, ?>> singletonCache,
                               Map<String, Runnable> destructionCallbackCache) implements Scope {

    public static final String SCOPE_NAME = "REFRESHABLE_SCOPE";

    public RefreshableScope(DefaultListableBeanFactory defaultListableBeanFactory) {
        this(defaultListableBeanFactory, new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
    }

    @Override
    public SAM<?, ?> get(String name, ObjectFactory<?> objectFactory) {
        return singletonCache.computeIfAbsent(name, beanName -> {
            // 注册销毁回调
            registerDestructionCallback(beanName, () -> defaultListableBeanFactory.removeBeanDefinition(beanName));
            // 创建 bean
            return (SAM<?, ?>) objectFactory.getObject();
        });
    }

    @Override
    public Object remove(String name) {
        synchronized (this) {
            destructionCallbackCache.computeIfPresent(name, (_, v) -> {
                // 执行销毁回调
                v.run();
                // 从缓存中移除
                return null;
            });
            // 从缓存中移除 bean 实例
            return singletonCache.remove(name);
        }
    }

    public void register(BeanDefinitionHolder beanDefinitionHolder) {
        var beanName = beanDefinitionHolder.getBeanName();
        var beanDefinition = beanDefinitionHolder.getBeanDefinition();
        synchronized (this) {
            // 注册 beanDefinition
            registerBeanDefinition(beanName, beanDefinition);
            // 同步注册销毁回调
            registerDestructionCallback(beanName, () -> defaultListableBeanFactory.removeBeanDefinition(beanName));
        }
    }

    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        destructionCallbackCache.putIfAbsent(name, callback);
    }
}
