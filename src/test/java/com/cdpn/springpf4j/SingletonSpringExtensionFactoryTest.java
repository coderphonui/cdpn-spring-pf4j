package com.cdpn.springpf4j;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pf4j.PluginManager;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SingletonSpringExtensionFactoryTest {
    @Mock
    private PluginManager pluginManager;
    @Mock
    private ApplicationContextResolver applicationContextResolver;
    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private AutowireCapableBeanFactory beanFactory;

    @Test
    public void create_return_null_when_cannot_resolve_application_context() {
        SingletonSpringExtensionFactory factory = new SingletonSpringExtensionFactory(pluginManager, applicationContextResolver);
        assertNull(factory.create(StubClass.class));
    }

    @Test
    public void create_should_trigger_super_creation_and_cache_when_extension_class_name_empty() {
        when(applicationContextResolver.resolve(StubClass.class, pluginManager)).thenReturn(Optional.of(applicationContext));
        when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(beanFactory);
        StubClass stubClass = new StubClass();
        when(beanFactory.autowire(StubClass.class, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false))
                .thenReturn(stubClass);
        SingletonSpringExtensionFactory springExtensionFactory = new SingletonSpringExtensionFactory(pluginManager, applicationContextResolver);
        assertNotNull(springExtensionFactory.create(StubClass.class));
        verify(beanFactory, times(1)).autowire(StubClass.class, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);
        verify(beanFactory, times(1)).autowireBean(stubClass);
        assertEquals(stubClass, springExtensionFactory.create(StubClass.class));
        assertEquals(stubClass, springExtensionFactory.getCache().get(StubClass.class.getName()));

        // The object should cache and return. No trigger of creating the object again.
        assertNotNull(springExtensionFactory.create(StubClass.class));
        verify(beanFactory, times(1)).autowire(StubClass.class, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);
        verify(beanFactory, times(1)).autowireBean(stubClass);

    }

    @Test
    public void create_should_trigger_super_creation_and_cache_when_extension_class_name_contain_extension_class() {
        when(applicationContextResolver.resolve(StubClass.class, pluginManager)).thenReturn(Optional.of(applicationContext));
        when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(beanFactory);
        StubClass stubClass = new StubClass();
        when(beanFactory.autowire(StubClass.class, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false))
                .thenReturn(stubClass);
        SingletonSpringExtensionFactory springExtensionFactory = new SingletonSpringExtensionFactory(pluginManager, applicationContextResolver, StubClass.class.getName());
        assertNotNull(springExtensionFactory.create(StubClass.class));
        verify(beanFactory, times(1)).autowire(StubClass.class, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);
        verify(beanFactory, times(1)).autowireBean(stubClass);
        assertEquals(stubClass, springExtensionFactory.create(StubClass.class));
        assertEquals(stubClass, springExtensionFactory.getCache().get(StubClass.class.getName()));

        // The object should cache and return. No trigger of creating the object again.
        assertNotNull(springExtensionFactory.create(StubClass.class));
        verify(beanFactory, times(1)).autowire(StubClass.class, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);
        verify(beanFactory, times(1)).autowireBean(stubClass);

    }


    @Test
    public void create_should_not_cache_when_extensionClassName_does_not_contain_className() {
        when(applicationContextResolver.resolve(StubClass.class, pluginManager)).thenReturn(Optional.of(applicationContext));
        when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(beanFactory);
        StubClass stubClass = new StubClass();
        when(beanFactory.autowire(StubClass.class, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false))
                .thenReturn(stubClass);
        SingletonSpringExtensionFactory springExtensionFactory = new SingletonSpringExtensionFactory(pluginManager,
                applicationContextResolver,"com.cdpn.springpf4j.StubClass2");
        assertNotNull(springExtensionFactory.create(StubClass.class));
        verify(beanFactory, times(1)).autowire(StubClass.class, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);
        verify(beanFactory, times(1)).autowireBean(stubClass);

        // The object should cache and return. No trigger of creating the object again.
        assertNotNull(springExtensionFactory.create(StubClass.class));
        verify(beanFactory, times(2)).autowire(StubClass.class, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);
        verify(beanFactory, times(2)).autowireBean(stubClass);

    }
}
