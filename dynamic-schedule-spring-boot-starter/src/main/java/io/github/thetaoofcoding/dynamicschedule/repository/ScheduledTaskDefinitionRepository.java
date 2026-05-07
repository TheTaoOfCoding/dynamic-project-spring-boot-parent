package io.github.thetaoofcoding.dynamicschedule.repository;

import io.github.thetaoofcoding.dynamicschedule.model.ScheduledTaskDefinition;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public record ScheduledTaskDefinitionRepository(JdbcTemplate jdbcTemplate) {
    public List<ScheduledTaskDefinition> selectAll() {
        final var sql = "select id, bean_name, cron_expression, registry_key, description from scheduled_task_definition";
        return jdbcTemplate.query(sql, ScheduledTaskDefinition::of);
    }

    public ScheduledTaskDefinition selectOne(String registryKey) {
        final var sql = "select id, bean_name, cron_expression, registry_key, description from scheduled_task_definition where registry_key = ?";
        try {
            return jdbcTemplate.queryForObject(sql, ScheduledTaskDefinition::of, registryKey);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public int insert(ScheduledTaskDefinition scheduledTaskDefinition) {
        final var sql = "insert into scheduled_task_definition (bean_name, cron_expression, registry_key, description) values (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, scheduledTaskDefinition.beanName(), scheduledTaskDefinition.cronExpression(), scheduledTaskDefinition.registryKey(), scheduledTaskDefinition.description());
    }

    public int update(ScheduledTaskDefinition scheduledTaskDefinition) {
        final var sql = "update scheduled_task_definition set bean_name = ?, cron_expression = ?, description = ? where registry_key = ?";
        return jdbcTemplate.update(sql, scheduledTaskDefinition.beanName(), scheduledTaskDefinition.cronExpression(), scheduledTaskDefinition.description(), scheduledTaskDefinition.registryKey());
    }

    public int delete(String registryKey) {
        final var sql = "delete from scheduled_task_definition where registry_key = ?";
        return jdbcTemplate.update(sql, registryKey);
    }
}
