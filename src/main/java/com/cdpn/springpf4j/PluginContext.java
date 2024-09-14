package com.cdpn.springpf4j;

import org.pf4j.RuntimeMode;


public record PluginContext(RuntimeMode runtimeMode, ClassLoader classLoader) {

}
