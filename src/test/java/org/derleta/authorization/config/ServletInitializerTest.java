package org.derleta.authorization.config;

import org.derleta.authorization.AndromedaAuthorizationServerApplication;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.builder.SpringApplicationBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServletInitializerTest {

    @Test
    void testConfigure() {
        ServletInitializer servletInitializer = new ServletInitializer();
        SpringApplicationBuilder springApplicationBuilder = Mockito.mock(SpringApplicationBuilder.class);
        when(springApplicationBuilder.sources(any())).thenReturn(springApplicationBuilder);

        servletInitializer.configure(springApplicationBuilder);

        verify(springApplicationBuilder).sources(AndromedaAuthorizationServerApplication.class);
    }
}
