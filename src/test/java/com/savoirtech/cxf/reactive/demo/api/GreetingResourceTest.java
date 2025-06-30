package com.savoirtech.cxf.reactive.demo.api;

import jakarta.ws.rs.core.StreamingOutput;
import org.junit.jupiter.api.Test;
import io.reactivex.Single;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class GreetingResourceTest {

    private final GreetingResource resource = new GreetingResource();

    @Test
    void testGreet() {
        Single<String> single = resource.greet("John");
        String result = single.blockingGet(); // blocking get to extract the value
        assertThat(result).isEqualTo("Hello, John!");
    }

    @Test
    void testGreetStream() throws IOException {
        StreamingOutput output = resource.greetStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        output.write(baos);
        String result = baos.toString();
        assertThat(result).isEqualTo("one\n" + "two\n" + "three\n");
    }

    @Test
    void testStreamFlowableAsJson() throws IOException {
        StreamingOutput output = resource.streamFlowableAsJson();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        output.write(baos);

        String result = baos.toString();
        // Expected JSON array with Greeting objects
        assertThat(result).startsWith("[");
        assertThat(result).endsWith("]");
        assertThat(result).contains("\"message\":\"Hello one\"");
        assertThat(result).contains("\"message\":\"Hello two\"");
        assertThat(result).contains("\"message\":\"Hello three\"");
    }

    @Test
    void testStreamFlowablePlain() throws IOException {
        StreamingOutput output = resource.streamFlowablePlain();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        output.write(baos);

        String result = baos.toString();
        assertThat(result).isEqualTo("one\n" + "two\n" + "three\n");
    }

    @Test
    void testStreamFlowableSSE() throws IOException {
        StreamingOutput output = resource.streamFlowableSSE();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        output.write(baos);

        String result = baos.toString();
        String expected = "data: one\n\n" +
                "data: two\n\n" +
                "data: three\n\n";
        assertThat(result).isEqualTo(expected);
    }
}
