package com.cdpn.springpf4j;

import org.junit.jupiter.api.Test;
import org.pf4j.PluginWrapper;
import org.pf4j.RuntimeMode;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpringPluginFactoryTest {
    @Test
    public void createInstance_should_return_null_when_the_class_not_have_constructor_with_plugin_context() {
        SpringPluginFactory springPluginFactory = new SpringPluginFactory();
        PluginWrapper pluginWrapper = mock(PluginWrapper.class);
        when(pluginWrapper.getRuntimeMode()).thenReturn(RuntimeMode.DEPLOYMENT);
        when(pluginWrapper.getPluginClassLoader()).thenReturn(getClass().getClassLoader());
        assertNull(springPluginFactory.createInstance(StubClass.class, pluginWrapper));
    }

    @Test
    public void createInstance_should_return_plugin_instance_when_the_class_have_constructor_with_plugin_context() {
        SpringPluginFactory springPluginFactory = new SpringPluginFactory();
        PluginWrapper pluginWrapper = mock(PluginWrapper.class);
        when(pluginWrapper.getRuntimeMode()).thenReturn(RuntimeMode.DEPLOYMENT);
        when(pluginWrapper.getPluginClassLoader()).thenReturn(getClass().getClassLoader());

        assertNotNull(springPluginFactory.createInstance(SamplePluginWithContextConstructor.class, pluginWrapper));
    }
}
