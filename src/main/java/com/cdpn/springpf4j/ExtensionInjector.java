package com.cdpn.springpf4j;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ExtensionInjector {
    protected final SpringPluginManager springPluginManager;
    protected final AbstractAutowireCapableBeanFactory beanFactory;
    public ExtensionInjector(SpringPluginManager springPluginManager) {
        this.springPluginManager = springPluginManager;
        this.beanFactory = (AbstractAutowireCapableBeanFactory) springPluginManager.getApplicationContext().getAutowireCapableBeanFactory();
    }

    public void injectExtensions() {
        // add extensions from classpath (non plugin)
        Set<String> extensionClassNames = springPluginManager.getExtensionClassNames(null);
        registerExtensionWithClassNames(extensionClassNames, getClass().getClassLoader());
        // add extensions for each started plugin
        List<PluginWrapper> startedPlugins = springPluginManager.getStartedPlugins();
        for (PluginWrapper plugin : startedPlugins) {
            log.debug("Registering extensions of the plugin '{}' as beans", plugin.getPluginId());
            extensionClassNames = springPluginManager.getExtensionClassNames(plugin.getPluginId());
            registerExtensionWithClassNames(extensionClassNames, plugin.getPluginClassLoader());
        }
    }

    private void registerExtensionWithClassNames(Set<String> extensionClassNames, ClassLoader classLoader) {
        for (String extensionClassName : extensionClassNames) {
            try {
                log.debug("Register extension '{}' as bean with extensionClassName", extensionClassName);
                Class<?> extensionClass = classLoader.loadClass(extensionClassName);
                registerExtension(extensionClass);
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Register an extension as bean.
     * Current implementation register extension as singleton using {@code beanFactory.registerSingleton()}.
     * The extension instance is created using {@code pluginManager.getExtensionFactory().create(extensionClass)}.
     * The bean name is the extension class name.
     * Override this method if you wish other register strategy.
     */
    private void registerExtension(Class<?> extensionClass) {
        Map<String, ?> extensionBeanMap = springPluginManager.getApplicationContext().getBeansOfType(extensionClass);
        if (extensionBeanMap.isEmpty()) {
            Object extension = springPluginManager.getExtensionFactory().create(extensionClass);
            beanFactory.registerSingleton(extensionClass.getName(), extension);
        } else {
            log.debug("Bean registration aborted! Extension '{}' already existed as bean!", extensionClass.getName());
        }
    }
}
