package io.github.thetaoofcoding.dynamicbean.service.impl;

import io.github.thetaoofcoding.dynamicbean.util.Assert;
import io.github.thetaoofcoding.dynamicbean.util.Assert.Predicates;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import io.github.thetaoofcoding.dynamicbean.repository.RefreshableBeanRepository;
import io.github.thetaoofcoding.dynamicbean.model.RefreshableBeanModel;
import io.github.thetaoofcoding.dynamicbean.event.RefreshBeanEvent;
import io.github.thetaoofcoding.dynamicbean.service.RefreshableBeanService;

import java.util.List;

@RequiredArgsConstructor
public class RefreshableBeanServiceImpl implements RefreshableBeanService {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final RefreshableBeanRepository refreshableBeanRepository;
    private final ApplicationContext applicationContext;

    @Override
    public List<RefreshableBeanModel> list() {
        return refreshableBeanRepository.selectAll();
    }

    @Override
    @Transactional
    public int create(RefreshableBeanModel refreshableBeanModel) {
        preCheck(refreshableBeanModel);

        var beanName = refreshableBeanModel.beanName();
        notExistsCheck(beanName);

        var result = refreshableBeanRepository.insert(refreshableBeanModel);
        refresh(RefreshBeanEvent.addWith(refreshableBeanModel));
        return result;
    }

    @Override
    @Transactional
    public int update(RefreshableBeanModel refreshableBeanModel) {
        preCheck(refreshableBeanModel);

        var beanName = refreshableBeanModel.beanName();
        var oldRecord = existsCheck(beanName);

        var result = refreshableBeanRepository.update(refreshableBeanModel);
        // 只有当 script 发生变化时，才触发后续的刷新操作
        if (oldRecord.diff(refreshableBeanModel)) refresh(RefreshBeanEvent.updateWith(oldRecord, refreshableBeanModel));
        return result;
    }

    @Override
    @Transactional
    public int remove(String beanName) {
        var oldRecord = existsCheck(beanName);
        var result = refreshableBeanRepository.delete(beanName);
        refresh(RefreshBeanEvent.deleteWith(oldRecord));
        return result;
    }

    private void refresh(RefreshBeanEvent refreshBeanEvent) {
        applicationEventPublisher.publishEvent(refreshBeanEvent);
    }

    // 全量参数前置校验
    private void preCheck(RefreshableBeanModel refreshableBeanModel) {
        var beanName = refreshableBeanModel.beanName();
        var script = refreshableBeanModel.script();
        var description = refreshableBeanModel.description();
        Assert.isTrue(beanName, Predicates::strNotBlank, () -> new IllegalArgumentException("beanName cannot be empty"));
        Assert.isTrue(script, Predicates::strNotBlank, () -> new IllegalArgumentException("script cannot be empty"));
        Assert.isTrue(description, Predicates::strNotBlank, () -> new IllegalArgumentException("description cannot be empty"));
    }

    // 不存在性校验
    private void notExistsCheck(String beanName) {
        // 要求 beanDefinition 不存在
        Assert.isFalse(beanName, applicationContext::containsBean, () -> new IllegalArgumentException("beanDefinition '%s' already exists".formatted(beanName)));

        // 要求 refreshableBean 不存在
        var oldRecord = refreshableBeanRepository.selectOne(beanName);
        Assert.isTrue(oldRecord, Predicates::isNull, () -> new IllegalArgumentException("refreshableBean '%s' already exists".formatted(beanName)));
    }

    // 存在性校验
    private RefreshableBeanModel existsCheck(String beanName) {
        // 要求 beanDefinition 存在
        Assert.isTrue(beanName, applicationContext::containsBean, () -> new IllegalArgumentException("beanDefinition '%s' doesn't exists".formatted(beanName)));

        // 要求 refreshableBean 存在
        var oldRecord = refreshableBeanRepository.selectOne(beanName);
        Assert.isTrue(oldRecord, Predicates::isNotNull, () -> new IllegalArgumentException("refreshableBean '%s' doesn't exists".formatted(beanName)));

        return oldRecord;
    }
}
