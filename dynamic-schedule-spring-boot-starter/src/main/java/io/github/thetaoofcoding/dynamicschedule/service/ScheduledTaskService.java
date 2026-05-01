package io.github.thetaoofcoding.dynamicschedule.service;

import io.github.thetaoofcoding.dynamicschedule.model.ScheduledTaskDefinition;

public interface ScheduledTaskService {

    void register(ScheduledTaskDefinition scheduledTaskDefinition);

    void unregister(String registryKey);

    void reregister(ScheduledTaskDefinition scheduledTaskDefinition);
}
