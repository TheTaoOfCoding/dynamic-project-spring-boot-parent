package io.github.thetaoofcoding.dynamicschedule.autoconfigure;

import io.github.thetaoofcoding.dynamicschedule.service.ScheduledTaskDefinitionService;
import io.github.thetaoofcoding.dynamicschedule.service.ScheduledTaskService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DynamicScheduleInitializer {
    private final ScheduledTaskDefinitionService scheduledTaskDefinitionService;
    private final ScheduledTaskService scheduledTaskService;

    @PostConstruct
    public void initialize() {
        var scheduledTaskDefinitions = scheduledTaskDefinitionService.list();
        scheduledTaskDefinitions.forEach(scheduledTaskService::register);
    }
}
