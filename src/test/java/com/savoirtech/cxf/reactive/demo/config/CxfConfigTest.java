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

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class CxfConfigTest {

    @Test
    void testBeansCreated() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(CxfConfig.class);

        Bus bus = ctx.getBean(Bus.class);
        assertThat(bus).isNotNull();

        ServletRegistrationBean<?> servletBean = ctx.getBean(ServletRegistrationBean.class);
        assertThat(servletBean).isNotNull();
        assertThat(servletBean.getServlet()).isInstanceOf(CXFServlet.class);
        assertThat(servletBean.getUrlMappings()).contains("/api/*");

        Server server = ctx.getBean(Server.class);
        assertThat(server).isNotNull();

        // You cannot call getServiceBeans() on Server.
        // But you can check the server endpoint address:
        String address = server.getEndpoint().getEndpointInfo().getAddress();
        assertThat(address).isEqualTo("/");

        ctx.close();
    }
}
