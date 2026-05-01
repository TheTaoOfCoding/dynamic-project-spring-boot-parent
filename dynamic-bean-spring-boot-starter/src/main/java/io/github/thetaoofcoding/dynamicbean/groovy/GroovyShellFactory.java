package io.github.thetaoofcoding.dynamicbean.groovy;

import groovy.lang.GroovyShell;

@FunctionalInterface
public interface GroovyShellFactory {
    GroovyShell create();
}