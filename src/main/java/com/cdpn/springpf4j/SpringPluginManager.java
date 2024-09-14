package com.cdpn.springpf4j;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.DefaultPluginManager;
import org.pf4j.ExtensionFactory;
import org.pf4j.PluginFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Setter
@Getter
@Slf4j
public class SpringPluginManager extends DefaultPluginManager implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public SpringPluginManager() {
        super();
    }

    @Override
    protected ExtensionFactory createExtensionFactory() {
        return new SpringExtensionFactory(this, new ApplicationContextResolver());
    }


    /**
     * This method load, start plugins and inject extensions in Spring
     */
    @PostConstruct
    public void init() {
        loadPlugins();
        startPlugins();
        new ExtensionInjector(this).injectExtensions();
    }

    @Override
    protected PluginFactory createPluginFactory() {
        return new SpringPluginFactory();
    }
}
