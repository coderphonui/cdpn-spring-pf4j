package com.cdpn.springpf4j;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.Plugin;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

import static java.util.Objects.nonNull;

@Slf4j
public class ApplicationContextResolver {

    public  <T> Optional<ApplicationContext> resolve(final Class<T> extensionClass, PluginManager pluginManager) {
        final Plugin plugin = Optional.ofNullable(pluginManager.whichPlugin(extensionClass))
                .map(PluginWrapper::getPlugin)
                .orElse(null);

        final ApplicationContext applicationContext;

        if (plugin instanceof SpringPlugin) {
            log.debug("  Extension class ' {}' belongs to spring-plugin '{}' and will be autowired by using its application context.",
                    nameOf(extensionClass), nameOf(plugin));
            applicationContext = ((SpringPlugin) plugin).getApplicationContext();
        } else if (pluginManager instanceof SpringPluginManager) {
            log.debug("  Extension class ' {}' belongs to a non spring-plugin (or main application) '{}, " +
                            "but the used PF4J plugin-manager is a spring-plugin-manager. " +
                            "Therefore the extension class will be autowired by using the managers application contexts",
                    nameOf(extensionClass), nameOf(plugin));
            applicationContext = ((SpringPluginManager) pluginManager).getApplicationContext();
        } else {
            log.warn("  No application contexts can be used for instantiating extension class '{}'. " +
                            "This extension neither belongs to a PF4J spring-plugin (id: '{}') " +
                            "nor is the used plugin manager a spring-plugin-manager (used manager: '{}'). " +
                            "At perspective of PF4J this seems highly uncommon in combination with a factory " +
                            "which only reason for existence is using spring (and its application context) and should at least be reviewed. " +
                            "In fact no autowiring can be applied although autowire flag was set to 'true'. " +
                            "Instantiating will fallback to standard Java reflection.",
                    nameOf(extensionClass), nameOf(plugin), nameOf(pluginManager.getClass()));
            applicationContext = null;
        }

        return Optional.ofNullable(applicationContext);
    }

    private String nameOf(final Plugin plugin) {
        return nonNull(plugin)
                ? plugin.getClass().getName()
                : "system";
    }

    private <T> String nameOf(final Class<T> clazz) {
        return clazz.getName();
    }
}
