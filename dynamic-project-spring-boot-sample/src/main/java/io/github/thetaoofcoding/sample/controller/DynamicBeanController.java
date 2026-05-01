package io.github.thetaoofcoding.sample.controller;

import io.github.thetaoofcoding.dynamicbean.core.SAM;
import io.github.thetaoofcoding.dynamicbean.model.RefreshableBeanModel;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import io.github.thetaoofcoding.dynamicbean.service.RefreshableBeanService;

@RequestMapping("/dynamicBean")
@RestController
public record DynamicBeanController(ApplicationContext applicationContext,
                                    RefreshableBeanService refreshableBeanService) {

    @GetMapping
    public Object list() {
        return refreshableBeanService.list();
    }

    @PostMapping
    public Object create(@RequestBody RefreshableBeanModel refreshableBeanModel) {
        return refreshableBeanService.create(refreshableBeanModel);
    }

    @DeleteMapping("/{beanName}")
    public Object remove(@PathVariable("beanName") String beanName) {
        return refreshableBeanService.remove(beanName);
    }

    @PutMapping
    public Object update(@RequestBody RefreshableBeanModel refreshableBeanModel) {
        return refreshableBeanService.update(refreshableBeanModel);
    }

    @GetMapping("/beanNames")
    public Object setRefreshBean() {
        return applicationContext.getBeanNamesForType(SAM.class);
    }

    @GetMapping("/execute/{beanName}")
    public Object getRefreshBean(@PathVariable("beanName") String beanName, @RequestParam(required = false, value = "param") String param) {
        var sam = applicationContext.getBean(beanName, SAM.class);
        sam.execute(param);
        return "运行成功，请查看控制台。";
    }
}
