package com.savoirtech.cxf.reactive.demo.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GreetingTest {

    @Test
    void testDefaultConstructorAndSetMessage() {
        Greeting greeting = new Greeting();
        assertThat(greeting.getMessage()).isNull();

        greeting.setMessage("Hello");
        assertThat(greeting.getMessage()).isEqualTo("Hello");
    }

    @Test
    void testParameterizedConstructor() {
        Greeting greeting = new Greeting("Hi there!");
        assertThat(greeting.getMessage()).isEqualTo("Hi there!");
    }
}
