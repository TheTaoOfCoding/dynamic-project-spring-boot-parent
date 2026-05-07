package io.github.thetaoofcoding.dynamicschedule.event;

import io.github.thetaoofcoding.dynamicbean.event.RefreshBeanEvent;
import org.springframework.transaction.event.TransactionalEventListener;

public class RefreshBeanEventListener {

    // todo 脏任务处理逻辑 -- 待定
    @TransactionalEventListener(RefreshBeanEvent.class)
    public void handle(RefreshBeanEvent refreshBeanEvent) {
    }
}
