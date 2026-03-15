package org.derleta.authorization.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.HateoasSortHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.data.web.PagedResourcesAssemblerArgumentResolver;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
class WebMvcConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    void getPage_withValidParameters_shouldInitializeHateoasBeans() {
        HateoasPageableHandlerMethodArgumentResolver pageableResolver =
                applicationContext.getBean(HateoasPageableHandlerMethodArgumentResolver.class);
        HateoasSortHandlerMethodArgumentResolver sortResolver =
                applicationContext.getBean(HateoasSortHandlerMethodArgumentResolver.class);
        PagedResourcesAssembler<?> pagedResourcesAssembler =
                applicationContext.getBean(PagedResourcesAssembler.class);
        PagedResourcesAssemblerArgumentResolver pagedResourcesAssemblerArgumentResolver =
                applicationContext.getBean(PagedResourcesAssemblerArgumentResolver.class);

        assertThat(pageableResolver).isNotNull();
        assertThat(sortResolver).isNotNull();
        assertThat(pagedResourcesAssembler).isNotNull();
        assertThat(pagedResourcesAssemblerArgumentResolver).isNotNull();
    }

    @Test
    void getPage_withAllowedCorsRequests_shouldAllowCorsRequestsFromAllowedOrigins() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(options("/some-endpoint")
                        .header("Origin", "https://localhost:3000")
                        .header("Content-Type", "application/json")
                        .header("Authorization", "application/json")
                        .header("Access-Control-Request-Method", HttpMethod.POST.name()))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"))
                .andExpect(header().string("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString("GET")))
                .andExpect(header().string("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString("POST")))
                .andExpect(header().string("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString("PUT")))
                .andExpect(header().string("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString("DELETE")))
                .andExpect(header().string("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString("OPTIONS")));
    }

    @Test
    void preflight_withAnyRequestHeaders_shouldBeAllowedBecauseAllowedHeadersIsWildcard() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(options("/some-endpoint")
                        .header("Origin", "https://localhost:3000")
                        .header("Access-Control-Request-Method", HttpMethod.GET.name())
                        .header("Access-Control-Request-Headers", "Disallowed-Header"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://localhost:3000"));
    }

    @Test
    void corsPreflight_fromHttpLocalhost_shouldBeAllowedIfOriginIsAllowed() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(options("/some-endpoint")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", HttpMethod.GET.name()))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    void rejectCorsRequests_fromDisallowedOrigins_shouldReturnForbidden() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(options("/some-endpoint")
                        .header("Origin", "https://malicious-site.com")
                        .header("Access-Control-Request-Method", HttpMethod.GET.name()))
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"))
                .andExpect(status().isForbidden());
    }
}