package io.github.thetaoofcoding.dynamicschedule.model;

import io.github.thetaoofcoding.dynamicbean.util.Assert;

import java.sql.ResultSet;

public record ScheduledTaskDefinition(Long id, String beanName, String cronExpression, String registryKey,
                                      String description) {

    public boolean diff(ScheduledTaskDefinition another) {
        Assert.isTrue(another, Assert.Predicates::isNotNull, () -> new NullPointerException("another cannot be null"));
        return Assert.Predicates.isNotEq(beanName, another.beanName)
                || Assert.Predicates.isNotEq(cronExpression, another.cronExpression);
    }

    public static ScheduledTaskDefinition of(ResultSet rs, int rowNum) {
        try {
            var id = rs.getLong("id");
            var beanName = rs.getString("bean_name");
            var cronExpression = rs.getString("cron_expression");
            var registryKey = rs.getString("registry_key");
            var description = rs.getString("description");
            return new ScheduledTaskDefinition(id, beanName, cronExpression, registryKey, description);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
