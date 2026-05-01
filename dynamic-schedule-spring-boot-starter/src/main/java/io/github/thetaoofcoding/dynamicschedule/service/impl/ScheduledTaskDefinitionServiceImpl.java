package io.github.thetaoofcoding.dynamicschedule.service.impl;

import io.github.thetaoofcoding.dynamicschedule.model.ScheduledTaskDefinition;
import io.github.thetaoofcoding.dynamicschedule.repository.ScheduledTaskDefinitionRepository;
import io.github.thetaoofcoding.dynamicschedule.service.ScheduledTaskService;
import io.github.thetaoofcoding.dynamicschedule.service.ScheduledTaskDefinitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
public class ScheduledTaskDefinitionServiceImpl implements ScheduledTaskDefinitionService {

    private final ScheduledTaskDefinitionRepository scheduledTaskDefinitionRepository;
    private final ScheduledTaskService scheduledTaskService;

    @Override
    public List<ScheduledTaskDefinition> list() {
        return scheduledTaskDefinitionRepository.selectAll();
    }

    @Override
    @Transactional
    public int create(ScheduledTaskDefinition scheduledTaskDefinition) {
        int result = scheduledTaskDefinitionRepository.insert(scheduledTaskDefinition);
        scheduledTaskService.register(scheduledTaskDefinition);
        return result;
    }

    @Override
    @Transactional
    public int update(ScheduledTaskDefinition scheduledTaskDefinition) {
        var result = scheduledTaskDefinitionRepository.update(scheduledTaskDefinition);
        scheduledTaskService.reregister(scheduledTaskDefinition);
        return result;
    }

    @Override
    @Transactional
    public int remove(String registryKey) {
        var result = scheduledTaskDefinitionRepository.delete(registryKey);
        scheduledTaskService.unregister(registryKey);
        return result;
    }
}
