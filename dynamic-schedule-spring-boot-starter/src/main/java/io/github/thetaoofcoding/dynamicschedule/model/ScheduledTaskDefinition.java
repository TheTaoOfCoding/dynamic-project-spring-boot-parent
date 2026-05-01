package io.github.thetaoofcoding.dynamicschedule.model;

import java.sql.ResultSet;

public record ScheduledTaskDefinition(Long id, String beanName, String cronExpression, String registryKey,
                                      String description) {
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
