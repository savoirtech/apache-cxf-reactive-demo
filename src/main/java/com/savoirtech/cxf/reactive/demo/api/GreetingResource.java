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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Flowable;
import io.reactivex.Single;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.StreamingOutput;

import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
@Path("/greet")
@Produces(MediaType.APPLICATION_JSON)
public class GreetingResource {

    /**
     * great.
     * <p>
     *     Simply return a greeting message.
     *     We are using a Single<String> to return the greeting.
     * </p>
     * @param name
     * @return Single<String>
     */
    @GET
    @Path("/{name}")
    public Single<String> greet(@PathParam("name") String name) {
        return Single.just("Hello, " + name + "!");
    }

    /**
     * greetStream.
     * <p>
     *     In this example, we are returning a StreamingOutput, which is a JAX-RS 2.0 feature.
     *     The StreamingOutput is used to stream data back to the client.
     *     We control when the stream is closed, flushed, and we can also control the chunk size.
     * </p>
     * @return StreamingOutput
     */
    @GET
    @Path("/stream")
    @Produces(MediaType.TEXT_PLAIN)
    public StreamingOutput greetStream() {
        return output -> {
            for (String item : new String[]{"one", "two", "three"}) {
                output.write((item + "\n").getBytes());
                output.flush();
            }
        };
    }

    /**
     * streamFlowableAsJson.
     * <p>
     *     In this example, we are returning a StreamingOutput, which is a JAX-RS 2.0 feature.
     *     The StreamingOutput is used to stream data back to the client.
     *     We use manual StreamingOutput workaround as onStreamingAsyncSubscriber lacks
     *     the ability to infer T.class, so it can't find bean properties.
     * </p>
     * @return StreamingOutput
     */
    @GET
    @Path("/stream-flowable")
    @Produces(MediaType.APPLICATION_JSON)
    public StreamingOutput streamFlowableAsJson() {
        Flowable<Greeting> greetings = Flowable.just("one", "two", "three")
                .map(s -> new Greeting("Hello " + s));

        return output -> {
            ObjectMapper mapper = new ObjectMapper();
            output.write("[".getBytes());

            final boolean[] first = {true};
            greetings.blockingForEach(g -> {
                try {
                    if (!first[0]) {
                        output.write(",".getBytes());
                    }
                    String json = mapper.writeValueAsString(g);
                    output.write(json.getBytes());
                    output.flush();
                    first[0] = false;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            output.write("]".getBytes());
        };
    }

    /**
     * streamFlowablePlain.
     * <p>
     *     In this example, we are returning a StreamingOutput, which is a JAX-RS 2.0 feature.
     *     The StreamingOutput is used to stream data back to the client.
     * </p>
     * @return
     */
    @GET
    @Path("/flowable-plain")
    @Produces(MediaType.TEXT_PLAIN)
    public StreamingOutput streamFlowablePlain() {
        Flowable<String> flow = Flowable.just("one", "two", "three");

        return output -> {
            flow.blockingForEach(item -> {
                output.write((item + "\n").getBytes());
                output.flush(); // flush each item to client
            });
        };
    }


    /**
     * streamFlowableSSE.
     * <p>
     *     In this example, we are returning a StreamingOutput, which is a JAX-RS 2.0 feature.
     * </p>
     * @return StreamingOutput
     */
    @GET
    @Path("/flowable-sse")
    @Produces("text/event-stream")
    public StreamingOutput streamFlowableSSE() {
        Flowable<String> stream = Flowable.just("one", "two", "three");

        return output -> {
            stream.blockingForEach(item -> {
                String event = "data: " + item + "\n\n";
                output.write(event.getBytes());
                output.flush();
            });
        };
    }

}
