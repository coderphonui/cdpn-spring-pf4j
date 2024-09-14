package com.cdpn.springpf4j;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpringPluginTest {
    @Mock
    private ConfigurableApplicationContext configurableApplicationContext;


    @Test
    public void stop_should_close_configurable_application_context() {
        SpringPlugin springPlugin = new SpringPlugin() {
            @Override
            public ApplicationContext createApplicationContext() {
                return configurableApplicationContext;
            }
        };
        springPlugin.getApplicationContext();
        springPlugin.stop();
        verify(configurableApplicationContext).close();
        assertNull(springPlugin.applicationContext);
    }

    @Test
    public void stop_should_only_set_applicationContext_by_null_when_it_is_not_ConfigurableApplicationContext() {
        SpringPlugin springPlugin = new SpringPlugin() {
            @Override
            public ApplicationContext createApplicationContext() {
                return mock(ApplicationContext.class);
            }
        };
        springPlugin.getApplicationContext();
        springPlugin.stop();
        verify(configurableApplicationContext, times(0)).close();
        assertNull(springPlugin.applicationContext);
    }

    @Test
    public void getApplicationContext() {
        SpringPlugin springPlugin = new SpringPlugin() {
            @Override
            public ApplicationContext createApplicationContext() {
                // Workaround to verify the method is called
                configurableApplicationContext.getDisplayName();
                return configurableApplicationContext;
            }
        };
        springPlugin.getApplicationContext();
        assertNotNull(springPlugin.applicationContext);
        verify(configurableApplicationContext, times(1)).getDisplayName();

        springPlugin.getApplicationContext();
        verify(configurableApplicationContext, times(1)).getDisplayName();
    }
}
