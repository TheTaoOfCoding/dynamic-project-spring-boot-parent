package io.github.thetaoofcoding.dynamicschedule.autoconfigure;

import io.github.thetaoofcoding.dynamicschedule.repository.ScheduledTaskDefinitionRepository;
import io.github.thetaoofcoding.dynamicschedule.service.impl.ScheduledTaskDefinitionServiceImpl;
import io.github.thetaoofcoding.dynamicschedule.service.impl.ScheduledTaskRegistrar;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Import({DynamicScheduleInitializer.class, ScheduledTaskDefinitionServiceImpl.class, ScheduledTaskRegistrar.class, ScheduledTaskDefinitionRepository.class})
@AutoConfiguration
public class DynamicScheduleAutoConfiguration {

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        var threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
        threadPoolTaskScheduler.setThreadNamePrefix("ScheduleTaskThread - ");
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskScheduler.setAwaitTerminationSeconds(30);
        return threadPoolTaskScheduler;
    }

    @Bean(name = "scheduledFutureMap")
    public Map<String, ScheduledFuture<?>> scheduledFutureMap() {
        return new ConcurrentHashMap<>();
    }
}
