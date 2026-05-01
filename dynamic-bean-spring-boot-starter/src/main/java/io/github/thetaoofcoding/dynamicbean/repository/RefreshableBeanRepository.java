package io.github.thetaoofcoding.dynamicbean.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import io.github.thetaoofcoding.dynamicbean.model.RefreshableBeanModel;

import java.util.List;

public record RefreshableBeanRepository(JdbcTemplate jdbcTemplate) {

    public List<RefreshableBeanModel> selectAll() {
        final var sql = "select id, bean_name, script, description from refreshable_bean";
        return jdbcTemplate.query(sql, RefreshableBeanModel::of);
    }

    public RefreshableBeanModel selectOne(String beanName) {
        final var sql = "SELECT id, bean_name, script, description FROM refreshable_bean WHERE bean_name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, RefreshableBeanModel::of, beanName);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public int insert(RefreshableBeanModel refreshableBeanModel) {
        final var sql = "INSERT INTO refreshable_bean (bean_name, script, description) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, refreshableBeanModel.beanName(), refreshableBeanModel.script(), refreshableBeanModel.description());
    }

    public int update(RefreshableBeanModel refreshableBeanModel) {
        final var sql = "UPDATE refreshable_bean SET script = ?, description = ? WHERE bean_name = ?";
        return jdbcTemplate.update(sql, refreshableBeanModel.script(), refreshableBeanModel.description(), refreshableBeanModel.beanName());
    }

    public int delete(String beanName) {
        final var sql = "DELETE FROM refreshable_bean WHERE bean_name = ?";
        return jdbcTemplate.update(sql, beanName);
    }
}