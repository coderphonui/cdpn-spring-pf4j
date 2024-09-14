package com.cdpn.springpf4j;
import lombok.Getter;
import org.pf4j.PluginManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingletonSpringExtensionFactory extends SpringExtensionFactory {

    private final List<String> extensionClassNames;

    @Getter
    private final Map<String, Object> cache;

    public SingletonSpringExtensionFactory(PluginManager pluginManager, ApplicationContextResolver applicationContextResolver, String... extensionClassNames) {
        super(pluginManager, applicationContextResolver);

        this.extensionClassNames = Arrays.asList(extensionClassNames);
        cache = new HashMap<>(); // simple cache implementation
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> extensionClass) {
        String extensionClassName = extensionClass.getName();
        if (cache.containsKey(extensionClassName)) {
            return (T) cache.get(extensionClassName);
        }

        T extension = super.create(extensionClass);
        // Caching only if no extensionClassNames are specified or the extensionClassName is in the list
        if (extensionClassNames.isEmpty() || extensionClassNames.contains(extensionClassName)) {
            cache.put(extensionClassName, extension);
        }

        return extension;
    }

}