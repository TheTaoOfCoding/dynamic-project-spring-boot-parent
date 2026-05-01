package io.github.thetaoofcoding.dynamicschedule.service;

import io.github.thetaoofcoding.dynamicschedule.model.ScheduledTaskDefinition;

import java.util.List;

public interface ScheduledTaskDefinitionService {
    List<ScheduledTaskDefinition> list();

    int create(ScheduledTaskDefinition scheduledTaskDefinition);

    int update(ScheduledTaskDefinition scheduledTaskDefinition);

    int remove(String registryKey);
}
