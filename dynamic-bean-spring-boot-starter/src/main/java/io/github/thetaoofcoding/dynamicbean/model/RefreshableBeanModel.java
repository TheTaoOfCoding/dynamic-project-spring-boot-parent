package io.github.thetaoofcoding.dynamicbean.model;

import io.github.thetaoofcoding.dynamicbean.util.Assert;
import io.github.thetaoofcoding.dynamicbean.util.Assert.Predicates;

import java.sql.ResultSet;

public record RefreshableBeanModel(Long id, String beanName, String script, String description) {

    public boolean diff(RefreshableBeanModel another) {
        Assert.isTrue(another, Predicates::isNotNull, () -> new NullPointerException("another cannot be null"));
        return Predicates.isNotEq(script, another.script);
    }

    public static RefreshableBeanModel of(ResultSet rs, int rowNum) {
        try {
            var id = rs.getLong("id");
            var beanName = rs.getString("bean_name");
            var script = rs.getString("script");
            var description = rs.getString("description");
            return new RefreshableBeanModel(id, beanName, script, description);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
