package io.github.thetaoofcoding.sample;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SpringBootTest
public class DynamicBeanTests {

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        var info = String.format("%s %s %s", "-".repeat(8), testInfo.getDisplayName(), "-".repeat(8));
        System.out.println(info);
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        var info = String.format("%s %s %s\n", "-".repeat(8), testInfo.getDisplayName(), "-".repeat(8));
        System.out.println(info);
    }

    // Runnable 类型任务
    @Test
    void runnable() {
        var runnable = applicationContext.getBean("runnable-task", Runnable.class);
        runnable.run();
    }

    // Consumer 类型任务
    @Test
    void consumer() {
        var consumer = (Consumer<String>) applicationContext.getBean("consumer-task", Consumer.class);
        var name = "TheTaoOfCoding";
        consumer.accept(name);
    }

    // Supplier 类型任务
    @Test
    void supplier() {
        var supplier = (Supplier<String>) applicationContext.getBean("supplier-task", Supplier.class);
        var name = supplier.get();
        System.out.println("Who am I ? " + name);
    }

    // Function 类型任务
    @Test
    void function() {
        var function = (Function<String, Integer>) applicationContext.getBean("function-task", Function.class);
        var length = function.apply("*".repeat(10));
        System.out.println("length : " + length);
    }

    // Predicate 类型任务
    @Test
    void predicate() {
        var predicate = (Predicate<String>) applicationContext.getBean("predicate-task", Predicate.class);
        var name1 = "Tom";
        var name2 = "TheTaoOfCoding";
        boolean allowed1 = predicate.test(name1);
        boolean allowed2 = predicate.test(name2);
        System.out.println("allowed1 = " + allowed1);
        System.out.println("allowed2 = " + allowed2);
    }

    // 使用 ioc 查找依赖
    @Test
    void inject() {
        var runnable = applicationContext.getBean("run-4-ioc", Runnable.class);
            runnable.run();
    }

    // 使用 ThreadLocal 传递线程变量
    @Test
    void threadLocal() {
        var locals = applicationContext.getBean("groovyContext", ThreadLocal.class);
        locals.set("TheTaoOfCoding");

        System.out.println("before : locals.get() = " + locals.get());
        var runnable = applicationContext.getBean("run-4-locals", Runnable.class);
        runnable.run();
        System.out.println("after : locals.get() = " + locals.get());
    }
}
