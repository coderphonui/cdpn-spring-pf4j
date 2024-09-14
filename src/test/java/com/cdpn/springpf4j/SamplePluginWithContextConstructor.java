package com.cdpn.springpf4j;

import lombok.Getter;
import org.springframework.context.ApplicationContext;

@Getter
public class SamplePluginWithContextConstructor extends SpringPlugin{
    private final PluginContext pluginContext;
    public SamplePluginWithContextConstructor(final PluginContext pluginContext) {
        this.pluginContext = pluginContext;
    }

    @Override
    public ApplicationContext createApplicationContext() {
        return null;
    }
}
