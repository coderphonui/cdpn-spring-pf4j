# What / Why cdpn-spring-pf4j?

This implementation borrows most of the code from https://github.com/pf4j/pf4j-spring (pf4j-spring)

I am building a project that requires using a plugin system and I decided to use pf4j and pf4j-spring library.
During my usage, I found that the pf4j-spring library has some incompatibility issues with the libraries in my project. 
I have fixed the issue and test the compatibility with Spring boot v3.3.2 (at the time I forked and fixed this library).

To better maintain the compatibility with the Spring Boot and other libraries, I decided to fork the pf4j-spring library and maintain it myself.

What I have added to the library:

* Updated the library to work with Spring Boot version to 3.x
* Clean up all code that is not necessary for my cases (eg: I do not support plugin not following Spring plugin implementation, autowire is a MUST). I believe in "Less code, less bugs"
* Added test cases to cover 100% code branches. It was the only way that helped me to understand the current implementation better and fixed the issue. No system can be maintained without test cases.

After all, I think those updates may be helpful for someone who is looking for a Spring plugin system and wants to use pf4j library with the Spring way. So, I decided to share it with the community.

# How to use it?

You can follow the original tutorials at: https://ralemy.github.io/pf4j-spring-tutorial/
Just to highlight some keynotes for what you should implement for a full project:

You should have 3 main components:

* Plugin interface: Define the plugin interface that all plugins should implement
* Plugin container: the component that loads all plugins and manages them. In this component, you can wire loaded plugins, call to their interface method, and load up your extended functionalities.
* Plugin: the component that implements the plugin interface. This component will be loaded by the plugin container.

Some people can choose to implement the plugin container inside the application, but I prefer to implement it inside a separated jar library. This helps to embed the plugin container into another application easily.

Using the cdnp-spring-pf4j library, you must implement with the Spring way.

### Plugin interface

* The plugin interface MUST extend the ExtensionPoint interface (this is in the Plugin interface component)

```java
public interface ExamplePluginInterface extends ExtensionPoint {
    // Your plugin interface methods
}
```

### Plugin implementation
The plugin implementation MUST include the dependency to the plugin interface and implement the plugin interface.

The plugin implementation MUST be annotated with @extension (this is in the Plugin component)

```java
@Extension
public class RealPluginExtension implements ExamplePluginInterface {
   // Your plugin implementation
}
```

* Besides the plugin interface, you also need to define a Plugin class inside your plugin. This plugin MUST extend the SpringPlugin class. Below is the example of the plugin class:

```java
import com.cdpn.springpf4j.PluginContext;
import com.cdpn.springpf4j.SpringPlugin;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ExamplePlugin extends SpringPlugin {
    private final PluginContext pluginContext;
    public ExamplePlugin(PluginContext pluginContext) {
        super();
        this.pluginContext = pluginContext;
    }
    @Override
    public ApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setClassLoader(pluginContext.classLoader());
        applicationContext.register(ApplicationConfiguration.class);
        applicationContext.refresh();
        return applicationContext;
    }
}

```
You also need to follow the guideline to package your plugin.

### Plugin container

The plugin container only needs to implement the configuration to load the SpringPluginManager

```java
import com.cdpn.springpf4j.SpringPluginManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentContainerConfig {
    @Bean
    public SpringPluginManager pluginManager() {
        return new SpringPluginManager();
    }
}

```
and you may need another PluginConfig to do more things with the loaded plugins

```java
import com.cdpn.springpf4j.SpringPluginManager;
import jakarta.annotation.PreDestroy;
import lombok.Setter;
import org.pf4j.PluginManager;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class PluginConfig implements BeanFactoryAware {
    private final SpringPluginManager pluginManager;
    @Setter
    private BeanFactory beanFactory;

    @Autowired
    public PluginConfig(SpringPluginManager pm) {
        this.pluginManager = pm;
    }
    @Bean
    @DependsOn("pluginManager")
    public List<FunctionCallback> registeredFunctions(PluginManager pm) {
        List<FunctionCallback> functionList = new ArrayList<>();
        List<AgentPluginInterface> pluginInterfaces = pm.getExtensions(AgentPluginInterface.class);
        for (AgentPluginInterface plugin : pluginInterfaces) {
            functionList.addAll(plugin.registerFunctions());
        }
        return functionList;
    }

    @PreDestroy
    public void cleanup() {
        pluginManager.stopPlugins();
    }

}
```
In the above example code, I tried to get all the FunctionCallback from the loaded plugins and build up the list of FunctionCallback to provide for my AI engine. 
This is just an example, you can do whatever you want with the loaded plugins.

## How to change the plugin folder?

You can use the System parameter pf4j.pluginsDir to change the plugin folder.

Example:
```shell
java -jar -Dpf4j.pluginsDir=plugins your-application-jar.jar
```

From now on, you just need to copy the plugin jar file to the plugin folder and restart your application to load the new plugin.

Hot reload is supported by the pf4j library, but I do not use it in my case. You can refer to the pf4j documentation for more information.
