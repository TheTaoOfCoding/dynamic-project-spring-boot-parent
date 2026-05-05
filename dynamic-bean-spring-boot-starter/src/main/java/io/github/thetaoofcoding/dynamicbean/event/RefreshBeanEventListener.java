package io.github.thetaoofcoding.dynamicbean.event;

import io.github.thetaoofcoding.dynamicbean.groovy.GroovyShellFactory;
import io.github.thetaoofcoding.dynamicbean.scope.RefreshableScope;
import io.github.thetaoofcoding.dynamicbean.groovy.ResourceResolver;
import io.github.thetaoofcoding.dynamicbean.model.RefreshableBeanModel;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

public record RefreshBeanEventListener(RefreshableScope refreshableScope, GroovyShellFactory groovyShellFactory) {

    @TransactionalEventListener(value = RefreshBeanEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(RefreshBeanEvent refreshBeanEvent) {
        refreshBeanEvent.getSource()
                .forEach((eventType, refreshBeanModel) -> {
                    switch (eventType) {
                        case DEL -> del(refreshBeanModel);
                        case ADD -> add(refreshBeanModel);
                        default -> throw new IllegalArgumentException("unknown event type");
                    }
                });
    }

    // 新增时，注册 BeanDefinition
    private void add(RefreshableBeanModel refreshableBeanModel) {
        var beanDefinitionHolder = ResourceResolver.ResourceResolvers.beanDefinitionResolver(groovyShellFactory)
                .resolve(refreshableBeanModel);

        // 注册 bean
        refreshableScope.register(beanDefinitionHolder);
    }

    // 删除 bean
    private void del(RefreshableBeanModel refreshableBeanModel) {
        var beanName = refreshableBeanModel.beanName();
        refreshableScope.remove(beanName);
    }
}
