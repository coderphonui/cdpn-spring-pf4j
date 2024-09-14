package com.cdpn.springpf4j;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.DefaultPluginFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.lang.reflect.Constructor;

@Slf4j
public class SpringPluginFactory extends DefaultPluginFactory {

    public SpringPluginFactory() {
        super();
    }

    @Override
    protected Plugin createInstance(Class<?> pluginClass, PluginWrapper pluginWrapper) {
        PluginContext context = new PluginContext(pluginWrapper.getRuntimeMode(), pluginWrapper.getPluginClassLoader());
        try {
            Constructor<?> constructor = pluginClass.getConstructor(PluginContext.class);
            return (Plugin) constructor.newInstance(context);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
