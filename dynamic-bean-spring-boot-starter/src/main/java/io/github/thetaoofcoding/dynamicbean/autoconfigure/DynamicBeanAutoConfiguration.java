package io.github.thetaoofcoding.dynamicbean.autoconfigure;

import io.github.thetaoofcoding.dynamicbean.repository.RefreshableBeanRepository;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;
import io.github.thetaoofcoding.dynamicbean.service.impl.RefreshableBeanServiceImpl;

@Import({EarlyResourceRegistrar.class, RefreshableBeanServiceImpl.class, RefreshableBeanRepository.class})
@AutoConfiguration
public class DynamicBeanAutoConfiguration {
}
