package com.cdpn.springpf4j;

import org.junit.jupiter.api.Test;
import org.pf4j.PluginManager;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class SpringExtensionFactoryTest {

    @Test
    public void create_should_return_null_value_when_cannot_resolve_application_context() {
        PluginManager pluginManager = mock(PluginManager.class);
        ApplicationContextResolver applicationContextResolver = mock(ApplicationContextResolver.class);
        when(applicationContextResolver.resolve(StubClass.class, pluginManager)).thenReturn(Optional.empty());
        SpringExtensionFactory springExtensionFactory = new SpringExtensionFactory(pluginManager, applicationContextResolver);
        assertNull(springExtensionFactory.create(StubClass.class));
    }

    @Test
    public void create_should_trigger_spring_creation_when_application_context_found() {
        PluginManager pluginManager = mock(PluginManager.class);
        ApplicationContextResolver applicationContextResolver = mock(ApplicationContextResolver.class);
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContextResolver.resolve(StubClass.class, pluginManager)).thenReturn(Optional.of(applicationContext));
        AutowireCapableBeanFactory beanFactory = mock(AutowireCapableBeanFactory.class);
        when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(beanFactory);
        StubClass stubClass = new StubClass();
        when(beanFactory.autowire(StubClass.class, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false))
                .thenReturn(stubClass);
        SpringExtensionFactory springExtensionFactory = new SpringExtensionFactory(pluginManager, applicationContextResolver);
        assertNotNull(springExtensionFactory.create(StubClass.class));
        verify(beanFactory).autowire(StubClass.class, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);
        verify(beanFactory).autowireBean(stubClass);
    }


}
