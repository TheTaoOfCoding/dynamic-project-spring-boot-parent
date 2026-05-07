package io.github.thetaoofcoding.dynamicbean.event;

import io.github.thetaoofcoding.dynamicbean.groovy.GroovyShellFactory;
import io.github.thetaoofcoding.dynamicbean.scope.RefreshableScope;
import io.github.thetaoofcoding.dynamicbean.groovy.ResourceResolver;
import io.github.thetaoofcoding.dynamicbean.model.RefreshableBeanModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
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

    // 处理新增事件
    private void add(RefreshableBeanModel refreshableBeanModel) {
        log.debug("handle add event：{}", refreshableBeanModel.beanName());
        var beanDefinitionHolder = ResourceResolver.ResourceResolvers.beanDefinitionResolver(groovyShellFactory)
                .resolve(refreshableBeanModel);
        refreshableScope.register(beanDefinitionHolder);
    }

    // 处理删除事件
    private void del(RefreshableBeanModel refreshableBeanModel) {
        log.debug("handle delete event：{}", refreshableBeanModel.beanName());
        var beanName = refreshableBeanModel.beanName();
        refreshableScope.remove(beanName);
    }
}
