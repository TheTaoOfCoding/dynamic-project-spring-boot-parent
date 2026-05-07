package io.github.thetaoofcoding.dynamicschedule.service.impl;

import io.github.thetaoofcoding.dynamicbean.util.Assert;
import io.github.thetaoofcoding.dynamicbean.util.Assert.Predicates;
import io.github.thetaoofcoding.dynamicschedule.model.ScheduledTaskDefinition;
import io.github.thetaoofcoding.dynamicschedule.repository.ScheduledTaskDefinitionRepository;
import io.github.thetaoofcoding.dynamicschedule.service.ScheduledTaskService;
import io.github.thetaoofcoding.dynamicschedule.service.ScheduledTaskDefinitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
public class ScheduledTaskDefinitionServiceImpl implements ScheduledTaskDefinitionService {
    private final ScheduledTaskDefinitionRepository scheduledTaskDefinitionRepository;
    private final ScheduledTaskService scheduledTaskService;
    private final ApplicationContext applicationContext;

    @Override
    public List<ScheduledTaskDefinition> list() {
        return scheduledTaskDefinitionRepository.selectAll();
    }

    @Override
    @Transactional
    public int create(ScheduledTaskDefinition scheduledTaskDefinition) {
        preCheck(scheduledTaskDefinition);

        var cronExpression = scheduledTaskDefinition.cronExpression();
        var beanName = scheduledTaskDefinition.beanName();
        legalCheck(cronExpression, beanName);

        var registryKey = scheduledTaskDefinition.registryKey();
        notExistsCheck(registryKey);

        int result = scheduledTaskDefinitionRepository.insert(scheduledTaskDefinition);
        scheduledTaskService.register(scheduledTaskDefinition);
        return result;
    }

    @Override
    @Transactional
    public int update(ScheduledTaskDefinition scheduledTaskDefinition) {
        preCheck(scheduledTaskDefinition);

        var cronExpression = scheduledTaskDefinition.cronExpression();
        var beanName = scheduledTaskDefinition.beanName();
        legalCheck(cronExpression, beanName);

        var registryKey = scheduledTaskDefinition.registryKey();
        var oldRecord = existsCheck(registryKey);

        var result = scheduledTaskDefinitionRepository.update(scheduledTaskDefinition);
        if (oldRecord.diff(scheduledTaskDefinition)) scheduledTaskService.reregister(scheduledTaskDefinition);
        return result;
    }

    @Override
    @Transactional
    public int remove(String registryKey) {
        existsCheck(registryKey);

        var result = scheduledTaskDefinitionRepository.delete(registryKey);
        scheduledTaskService.unregister(registryKey);
        return result;
    }

    // 全量参数前置校验
    private void preCheck(ScheduledTaskDefinition scheduledTaskDefinition) {
        var beanName = scheduledTaskDefinition.beanName();
        var cronExpression = scheduledTaskDefinition.cronExpression();
        var registryKey = scheduledTaskDefinition.registryKey();
        var description = scheduledTaskDefinition.description();
        Assert.isTrue(beanName, Predicates::strNotBlank, () -> new IllegalArgumentException("beanName cannot be empty"));
        Assert.isTrue(cronExpression, Predicates::strNotBlank, () -> new IllegalArgumentException("cronExpression cannot be empty"));
        Assert.isTrue(registryKey, Predicates::strNotBlank, () -> new IllegalArgumentException("registryKey cannot be empty"));
        Assert.isTrue(description, Predicates::strNotBlank, () -> new IllegalArgumentException("description cannot be empty"));
    }

    // 合法性校验
    private void legalCheck(String cronExpression, String beanName) {
        // 要求 cronExpression 有效
        Assert.isTrue(cronExpression, CronExpression::isValidExpression, () -> new IllegalArgumentException("cronExpression '%s' is invalid".formatted(cronExpression)));

        // 要求 动态任务存在
        Assert.isTrue(beanName, applicationContext::containsBean, () -> new IllegalArgumentException("dynamicBean '%s' doesn't exists".formatted(beanName)));
    }

    // 不存在性校验
    private void notExistsCheck(String registryKey) {
        // 要求 scheduledTaskDefinition 不存在（数据库检查）
        var oldRecord = scheduledTaskDefinitionRepository.selectOne(registryKey);
        Assert.isTrue(oldRecord, Predicates::isNull, () -> new IllegalArgumentException("scheduledTaskDefinition '%s' already exists".formatted(registryKey)));

        // 要求 scheduledFuture 不存在（注册表检查）
        Assert.isFalse(registryKey, scheduledTaskService::contains, () -> new IllegalArgumentException("scheduledFuture '%s' already exists".formatted(registryKey)));
    }

    // 存在性校验
    private ScheduledTaskDefinition existsCheck(String registryKey) {
        // 要求 scheduledTaskDefinition 存在（数据库检查）
        var oldRecord = scheduledTaskDefinitionRepository.selectOne(registryKey);
        Assert.isTrue(oldRecord, Predicates::isNotNull, () -> new IllegalArgumentException("scheduledTaskDefinition '%s' doesn't exists".formatted(registryKey)));

        // 要求 scheduledFuture 存在（注册表检查）
        Assert.isTrue(registryKey, scheduledTaskService::contains, () -> new IllegalArgumentException("scheduledFuture '%s' doesn't exists".formatted(registryKey)));

        return oldRecord;
    }
}
