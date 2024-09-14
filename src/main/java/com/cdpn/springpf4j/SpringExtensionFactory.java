package com.cdpn.springpf4j;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.ExtensionFactory;
import org.pf4j.PluginManager;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

@Slf4j
public class SpringExtensionFactory implements ExtensionFactory {

    private static final int AUTOWIRE_CONSTRUCTOR = AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;

    /**
     * The plugin manager is used for retrieving a plugin from a given extension class
     * and as a fallback supplier of an application context.
     */
    protected final PluginManager pluginManager;

    protected final ApplicationContextResolver applicationContextResolver;

    public SpringExtensionFactory(final PluginManager pluginManager, final ApplicationContextResolver applicationContextResolver) {
        this.pluginManager = pluginManager;
        this.applicationContextResolver = applicationContextResolver;
    }



    @Override
    public <T> T create(final Class<T> extensionClass) {
        return applicationContextResolver.resolve(extensionClass, pluginManager)
                .map(applicationContext -> createWithSpring(extensionClass, applicationContext))
                .orElse(null);
    }


    @SuppressWarnings("unchecked")
    protected <T> T createWithSpring(final Class<T> extensionClass, final ApplicationContext applicationContext) {
        final AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();

        log.debug("Instantiate extension class '{}' by using constructor autowiring.", extensionClass.getName());
        // Autowire by constructor. This does not include the other types of injection (setters and/or fields).
        final Object autowiredExtension = beanFactory.autowire(extensionClass, AUTOWIRE_CONSTRUCTOR,
                // The value of the 'dependencyCheck' parameter is actually irrelevant as the using constructor of 'RootBeanDefinition'
                // skips action when the autowire mode is set to 'AUTOWIRE_CONSTRUCTOR'. Although the default value in
                // 'AbstractBeanDefinition' is 'DEPENDENCY_CHECK_NONE', so it is set to false here as well.
                false);
        log.trace("Created extension instance by constructor injection: {}", autowiredExtension);

        log.debug("Completing autowiring of extension: {}", autowiredExtension);
        // Autowire by using remaining kinds of injection (e. g. setters and/or fields).
        beanFactory.autowireBean(autowiredExtension);
        log.trace("Autowiring has been completed for extension: {}", autowiredExtension);

        return (T) autowiredExtension;
    }

}