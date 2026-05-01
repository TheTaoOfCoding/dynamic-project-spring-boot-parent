package io.github.thetaoofcoding.sample.controller;

import io.github.thetaoofcoding.dynamicschedule.model.ScheduledTaskDefinition;
import io.github.thetaoofcoding.dynamicschedule.service.ScheduledTaskDefinitionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dynamicSchedule")
public record DynamicScheduleController(ScheduledTaskDefinitionService scheduledTaskDefinitionService) {

    @GetMapping
    public Object list() {
        return scheduledTaskDefinitionService.list();
    }

    @PostMapping
    public Object create(@RequestBody ScheduledTaskDefinition scheduledTaskDefinition) {
        return scheduledTaskDefinitionService.create(scheduledTaskDefinition);
    }


    @DeleteMapping("/{registryKey}")
    public Object remove(@PathVariable("registryKey") String registryKey) {
        return scheduledTaskDefinitionService.remove(registryKey);
    }


    @PutMapping
    public Object update(@RequestBody ScheduledTaskDefinition scheduledTaskDefinition) {
        return scheduledTaskDefinitionService.update(scheduledTaskDefinition);
    }
}
