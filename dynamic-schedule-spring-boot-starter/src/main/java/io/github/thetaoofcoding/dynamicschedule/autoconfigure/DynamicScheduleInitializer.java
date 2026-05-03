package io.github.thetaoofcoding.dynamicschedule.autoconfigure;

import io.github.thetaoofcoding.dynamicschedule.service.ScheduledTaskDefinitionService;
import io.github.thetaoofcoding.dynamicschedule.service.ScheduledTaskService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DynamicScheduleInitializer {
    private final ScheduledTaskDefinitionService scheduledTaskDefinitionService;
    private final ScheduledTaskService scheduledTaskService;

    @PostConstruct
    public void initialize() {
        log.info("Initializing dynamic scheduled tasks...");
        var scheduledTaskDefinitions = scheduledTaskDefinitionService.list();
        scheduledTaskDefinitions.stream()
                .peek(scheduledTaskDefinition -> log.debug("register dynamic scheduled task : '{}'", scheduledTaskDefinition.registryKey()))
                .forEach(scheduledTaskService::register);
    }
}
