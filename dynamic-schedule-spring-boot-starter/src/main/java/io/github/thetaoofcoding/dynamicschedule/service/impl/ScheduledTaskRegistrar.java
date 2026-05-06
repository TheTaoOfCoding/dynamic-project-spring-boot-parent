package io.github.thetaoofcoding.dynamicschedule.service.impl;

import io.github.thetaoofcoding.dynamicbean.core.SAM;
import io.github.thetaoofcoding.dynamicschedule.model.ScheduledTaskDefinition;
import io.github.thetaoofcoding.dynamicschedule.service.ScheduledTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Slf4j
public record ScheduledTaskRegistrar(
        ThreadPoolTaskScheduler threadPoolTaskScheduler,
        @Qualifier("scheduledFutureMap") Map<String, ScheduledFuture<?>> scheduledFutureMap,
        ApplicationContext applicationContext) implements ScheduledTaskService {

    @Override
    public void register(ScheduledTaskDefinition scheduledTaskDefinition) {
        var beanName = scheduledTaskDefinition.beanName();
        var cronExpression = scheduledTaskDefinition.cronExpression();
        var registryKey = scheduledTaskDefinition.registryKey();

        log.debug("register dynamic scheduled task : '{}'", registryKey);

        scheduledFutureMap.computeIfAbsent(registryKey, _ -> {
            // 查找动态任务（SAM 类型）
            var dynamicTask = getDynamicTask(beanName);
            // 创建并注册定时任务
            return threadPoolTaskScheduler.schedule(dynamicTask, new CronTrigger(cronExpression));
        });
    }

    @Override
    public void unregister(String registryKey) {
        log.debug("unregister dynamic scheduled task : '{}'", registryKey);
        scheduledFutureMap.computeIfPresent(registryKey, (_, v) -> {
            // 取消定时任务
            v.cancel(true);
            // 从注册表中移除
            return null;
        });
    }

    @Override
    public void reregister(ScheduledTaskDefinition scheduledTaskDefinition) {
        log.debug("reregister dynamic scheduled task : '{}'", scheduledTaskDefinition.registryKey());
        synchronized (scheduledFutureMap) {
            // 取消并移除
            unregister(scheduledTaskDefinition.registryKey());
            // 重新注册
            register(scheduledTaskDefinition);
        }
    }

    private Runnable getDynamicTask(String beanName) {
        return applicationContext.getBean(beanName, SAM.class);
    }
}
