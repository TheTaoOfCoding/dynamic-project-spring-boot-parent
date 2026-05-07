package io.github.thetaoofcoding.dynamicbean.event;

import org.springframework.context.ApplicationEvent;
import io.github.thetaoofcoding.dynamicbean.model.RefreshableBeanModel;

import java.util.LinkedHashMap;
import java.util.Map;

public class RefreshBeanEvent extends ApplicationEvent {

    public enum EventType {
        ADD,
        DEL
    }

    private RefreshBeanEvent(Map<EventType, RefreshableBeanModel> refreshBeanModel) {
        super(refreshBeanModel);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<EventType, RefreshableBeanModel> getSource() {
        return (Map<EventType, RefreshableBeanModel>) super.getSource();
    }

    public static RefreshBeanEvent addWith(RefreshableBeanModel refreshableBeanModel) {
        return new RefreshBeanEvent(Map.of(EventType.ADD, refreshableBeanModel));
    }

    public static RefreshBeanEvent deleteWith(RefreshableBeanModel refreshableBeanModel) {
        return new RefreshBeanEvent(Map.of(EventType.DEL, refreshableBeanModel));
    }

    /*
     * 使用 LinkedHashMap 控制写入顺序，确保消费时先执行 DEL，再执行 ADD
     */
    public static RefreshBeanEvent updateWith(RefreshableBeanModel beforeModel, RefreshableBeanModel afterModel) {
        var map = new LinkedHashMap<EventType, RefreshableBeanModel>();
        map.put(EventType.DEL, beforeModel);
        map.put(EventType.ADD, afterModel);
        return new RefreshBeanEvent(map);
    }
}
