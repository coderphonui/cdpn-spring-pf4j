package com.cdpn.springpf4j;

import org.junit.jupiter.api.Test;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApplicationContextResolverTest {
    @Test
    public void resolve_should_return_null_when_plugin_cannot_be_resolved() {
        ApplicationContextResolver applicationContextResolver = new ApplicationContextResolver();
        PluginManager pluginManager = mock(PluginManager.class);
        assertTrue(applicationContextResolver.resolve(StubClass.class, pluginManager).isEmpty());

        PluginWrapper pluginWrapper = mock(PluginWrapper.class);
        when(pluginManager.whichPlugin(StubClass.class)).thenReturn(pluginWrapper);
        assertTrue(applicationContextResolver.resolve(StubClass.class, pluginManager).isEmpty());

        when(pluginWrapper.getPlugin()).thenReturn(null);
        assertTrue(applicationContextResolver.resolve(StubClass.class, pluginManager).isEmpty());
    }

    @Test
    public void resolve_should_return_plugin_context_when_plugin_is_SpringPlugin() {
        ApplicationContextResolver applicationContextResolver = new ApplicationContextResolver();
        PluginManager pluginManager = mock(PluginManager.class);
        PluginWrapper pluginWrapper = mock(PluginWrapper.class);
        when(pluginManager.whichPlugin(StubClass.class)).thenReturn(pluginWrapper);
        SpringPlugin springPlugin = mock(SpringPlugin.class);
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(springPlugin.getApplicationContext()).thenReturn(applicationContext);
        when(pluginWrapper.getPlugin()).thenReturn(springPlugin);
        assertTrue(applicationContextResolver.resolve(StubClass.class, pluginManager).isPresent());
        assertEquals(applicationContext, applicationContextResolver.resolve(StubClass.class, pluginManager).get());
    }

    @Test
    public void resolve_should_return_springPluginManager_context_when_plugin_is_not_springPlugin() {
        ApplicationContextResolver applicationContextResolver = new ApplicationContextResolver();
        SpringPluginManager pluginManager = mock(SpringPluginManager.class);
        PluginWrapper pluginWrapper = mock(PluginWrapper.class);
        when(pluginManager.whichPlugin(StubClass.class)).thenReturn(pluginWrapper);
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(pluginManager.getApplicationContext()).thenReturn(applicationContext);
        assertTrue(applicationContextResolver.resolve(StubClass.class, pluginManager).isPresent());
        assertEquals(applicationContext, applicationContextResolver.resolve(StubClass.class, pluginManager).get());
    }

    @Test
    public void resolve_should_return_null_when_plugin_is_not_spring_plugin_and_pluginManager_is_not_SpringPluginManager() {
        ApplicationContextResolver applicationContextResolver = new ApplicationContextResolver();
        PluginManager pluginManager = mock(PluginManager.class);
        PluginWrapper pluginWrapper = mock(PluginWrapper.class);
        when(pluginManager.whichPlugin(StubClass.class)).thenReturn(pluginWrapper);
        assertTrue(applicationContextResolver.resolve(StubClass.class, pluginManager).isEmpty());
    }
}
