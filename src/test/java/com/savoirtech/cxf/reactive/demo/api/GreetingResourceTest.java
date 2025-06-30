/*
 * Copyright (c) 2012-2025 Savoir Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
