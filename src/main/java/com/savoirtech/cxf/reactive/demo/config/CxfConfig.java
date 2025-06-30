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
package com.savoirtech.cxf.reactive.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.cxf.jaxrs.rx2.server.ReactiveIOInvoker;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.savoirtech.cxf.reactive.demo.api.GreetingResource;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import java.util.Collections;

@Configuration
public class CxfConfig {

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public Bus springBus() {
        return new org.apache.cxf.bus.spring.SpringBus();
    }

    @Bean
    public ServletRegistrationBean<CXFServlet> cxfServlet() {
        return new ServletRegistrationBean<>(new CXFServlet(), "/api/*");
    }

    @Bean
    public Server jaxrsServer(Bus bus) {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setBus(bus);
        factory.setAddress("/"); // relative to /api
        factory.setInvoker(new ReactiveIOInvoker());
        factory.setServiceBeans(Collections.singletonList(new GreetingResource()));

        // Customize ObjectMapper to avoid FAIL_ON_EMPTY_BEANS error
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        JacksonJsonProvider jsonProvider = new JacksonJsonProvider();
        jsonProvider.setMapper(mapper);

        factory.setProviders(Collections.singletonList(jsonProvider));
        return factory.create();
    }
}


