package io.github.thetaoofcoding.dynamicbean.service;

import io.github.thetaoofcoding.dynamicbean.model.RefreshableBeanModel;

import java.util.List;

public interface RefreshableBeanService {

    List<RefreshableBeanModel> list();

    int create(RefreshableBeanModel refreshableBeanModel);

    int update(RefreshableBeanModel refreshableBeanModel);

    int remove(String beanName);
}