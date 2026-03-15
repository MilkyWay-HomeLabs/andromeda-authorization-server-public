package org.derleta.authorization.config;

import org.junit.jupiter.api.Test;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.HateoasSortHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.data.web.PagedResourcesAssemblerArgumentResolver;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class WebMvcConfigTest {

    private final WebMvcConfig webMvcConfig = new WebMvcConfig();

    @Test
    void testPageableResolver() {
        HateoasPageableHandlerMethodArgumentResolver resolver = webMvcConfig.pageableResolver();
        assertNotNull(resolver, "Pageable resolver should not be null");
    }

    @Test
    void testSortResolver() {
        HateoasSortHandlerMethodArgumentResolver resolver = webMvcConfig.sortResolver();
        assertNotNull(resolver, "Sort resolver should not be null");
    }

    @Test
    void testPagedResourcesAssembler() {
        PagedResourcesAssembler<?> assembler = webMvcConfig.pagedResourcesAssembler();
        assertNotNull(assembler, "Paged resources assembler should not be null");
    }

    @Test
    void testPagedResourcesAssemblerArgumentResolver() {
        PagedResourcesAssemblerArgumentResolver resolver = webMvcConfig.pagedResourcesAssemblerArgumentResolver();
        assertNotNull(resolver, "Paged resources assembler argument resolver should not be null");
    }
}
