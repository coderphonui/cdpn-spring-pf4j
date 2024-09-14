package com.cdpn.springpf4j;

import org.pf4j.Plugin;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

public abstract class SpringPlugin extends Plugin {
    protected ApplicationContext applicationContext;
    public SpringPlugin() {

    }

    public final ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            applicationContext = createApplicationContext();
        }

        return applicationContext;
    }

    @Override
    public void stop() {
        // close applicationContext
        if (applicationContext instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) applicationContext).close();
        }

        applicationContext = null;
    }

    public abstract ApplicationContext createApplicationContext();
}
