package com.cdpn.springpf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pf4j.ExtensionFactory;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExtensionInjectorTest {
    @Mock
    private SpringPluginManager springPluginManager;
    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private AbstractAutowireCapableBeanFactory beanFactory;
    @Mock
    private ExtensionFactory extensionFactory;

    @BeforeEach
    public void setup() {
        when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(beanFactory);
        when(springPluginManager.getApplicationContext()).thenReturn(applicationContext);

    }
    @Test
    public void injectExtensions_when_adding_extension_from_classpath() {
        StubClass extension = new StubClass();
        when(extensionFactory.create(StubClass.class)).thenReturn(extension);
        when(springPluginManager.getExtensionClassNames(null)).thenReturn(Set.of(StubClass.class.getName()));
        when(springPluginManager.getExtensionFactory()).thenReturn(extensionFactory);

        ExtensionInjector extensionInjection = new ExtensionInjector(springPluginManager);
        extensionInjection.injectExtensions();

        verify(extensionFactory, times(1)).create(StubClass.class);
        verify(beanFactory, times(1)).registerSingleton(StubClass.class.getName(), extension);
    }

    @Test
    public void injectExtensions_should_not_throw_exception_when_adding_extension_from_classpath_with_unknown_class_name() {
        StubClass extension = new StubClass();
        when(extensionFactory.create(StubClass.class)).thenReturn(extension);
        when(springPluginManager.getExtensionClassNames(null)).thenReturn(Set.of(StubClass.class.getName(), "UnknownClassName"));
        when(springPluginManager.getExtensionFactory()).thenReturn(extensionFactory);

        ExtensionInjector extensionInjection = new ExtensionInjector(springPluginManager);
        extensionInjection.injectExtensions();

        verify(extensionFactory, times(1)).create(StubClass.class);
        verify(beanFactory, times(1)).registerSingleton(StubClass.class.getName(), extension);
    }

    @Test
    public void injectExtensions_when_adding_extension_from_classpath_and_extension_exist() {
        when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(beanFactory);
        StubClass extension = new StubClass();
        when(applicationContext.getBeansOfType(StubClass.class)).thenReturn(Map.of(extension.getClass().getName(), extension));
        when(springPluginManager.getApplicationContext()).thenReturn(applicationContext);
        when(springPluginManager.getExtensionClassNames(null)).thenReturn(Set.of(StubClass.class.getName()));

        ExtensionInjector extensionInjection = new ExtensionInjector(springPluginManager);
        extensionInjection.injectExtensions();

        verify(extensionFactory, times(0)).create(StubClass.class);
        verify(beanFactory, times(0)).registerSingleton(StubClass.class.getName(), extension);
    }

    @Test
    public void injectExtensions_when_adding_extension_for_started_plugins() {
        StubClass extension = new StubClass();
        when(extensionFactory.create(StubClass.class)).thenReturn(extension);
        when(springPluginManager.getExtensionFactory()).thenReturn(extensionFactory);
        when(springPluginManager.getApplicationContext()).thenReturn(applicationContext);

        PluginWrapper pluginWrapper = mock(PluginWrapper.class);
        when(pluginWrapper.getPluginId()).thenReturn("pluginId");
        when(pluginWrapper.getPluginClassLoader()).thenReturn(getClass().getClassLoader());
        when(springPluginManager.getExtensionClassNames(null)).thenReturn(Set.of());
        when(springPluginManager.getExtensionClassNames(pluginWrapper.getPluginId())).thenReturn(Set.of(StubClass.class.getName()));
        List<PluginWrapper> startedPlugins = List.of(pluginWrapper);
        when(springPluginManager.getStartedPlugins()).thenReturn(startedPlugins);

        ExtensionInjector extensionInjection = new ExtensionInjector(springPluginManager);
        extensionInjection.injectExtensions();

        verify(extensionFactory, times(1)).create(StubClass.class);
        verify(beanFactory, times(1)).registerSingleton(StubClass.class.getName(), extension);
    }

    @Test
    public void injectExtensions_should_not_throw_exception_when_adding_extension_for_started_plugins_with_unknown_class() {
        StubClass extension = new StubClass();
        when(extensionFactory.create(StubClass.class)).thenReturn(extension);
        when(springPluginManager.getExtensionFactory()).thenReturn(extensionFactory);
        when(springPluginManager.getApplicationContext()).thenReturn(applicationContext);

        PluginWrapper pluginWrapper = mock(PluginWrapper.class);
        when(pluginWrapper.getPluginId()).thenReturn("pluginId");
        when(pluginWrapper.getPluginClassLoader()).thenReturn(getClass().getClassLoader());
        when(springPluginManager.getExtensionClassNames(null)).thenReturn(Set.of());
        when(springPluginManager.getExtensionClassNames(pluginWrapper.getPluginId())).thenReturn(Set.of(StubClass.class.getName(), "UnknownClassName"));
        List<PluginWrapper> startedPlugins = List.of(pluginWrapper);
        when(springPluginManager.getStartedPlugins()).thenReturn(startedPlugins);

        ExtensionInjector extensionInjection = new ExtensionInjector(springPluginManager);
        extensionInjection.injectExtensions();

        verify(extensionFactory, times(1)).create(StubClass.class);
        verify(beanFactory, times(1)).registerSingleton(StubClass.class.getName(), extension);
    }
}
