package com.cdpn.springpf4j;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class SpringPluginManagerTest {
    @Test
    public void setApplicationContext() {
        SpringPluginManager springPluginManager = new SpringPluginManager();
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        springPluginManager.setApplicationContext(applicationContext);
        assertEquals(applicationContext, springPluginManager.getApplicationContext());
    }

    @Test
    public void init() {
        SpringPluginManager springPluginManager = new SpringPluginManager();
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        springPluginManager.setApplicationContext(applicationContext);
        springPluginManager.init();
    }


}
