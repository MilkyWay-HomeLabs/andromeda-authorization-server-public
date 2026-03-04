package org.derleta.authorization.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.HateoasSortHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.data.web.PagedResourcesAssemblerArgumentResolver;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvcConfig is a configuration class that implements {@link WebMvcConfigurer}
 * to set up customization points for Spring MVC's configuration.
 * <p>
 * This class enables the support for Hypermedia as the Application Level (HAL) format
 * using {@link EnableHypermediaSupport}, and provides beans for resolving pageable
 * and sortable parameters as well as assembling paginated resources in HATEOAS-based APIs.
 * <p>
 * The primary purpose of this configuration is to simplify the integration and usage
 * of Spring HATEOAS in applications by providing the necessary beans and enabling
 * hypermedia functionality out of the box.
 */
@Configuration
@ComponentScan
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Creates and returns a {@link HateoasPageableHandlerMethodArgumentResolver} bean.
     * <p>
     * This method initializes a {@link HateoasPageableHandlerMethodArgumentResolver} instance
     * configured with a {@link HateoasSortHandlerMethodArgumentResolver}. It is primarily used
     * to handle pageable parameters in HATEOAS-based APIs, enabling efficient resolution and
     * processing of pageable requests.
     *
     * @return a {@link HateoasPageableHandlerMethodArgumentResolver} instance configured
     * for handling pageable parameters in HATEOAS-based APIs.
     */
    @Bean
    public HateoasPageableHandlerMethodArgumentResolver pageableResolver() {
        return new HateoasPageableHandlerMethodArgumentResolver(sortResolver());
    }

    /**
     * Creates and returns a {@link HateoasSortHandlerMethodArgumentResolver} bean.
     * <p>
     * This method provides a {@link HateoasSortHandlerMethodArgumentResolver} instance,
     * enabling the resolution of sort parameters in HATEOAS-based APIs. It is commonly
     * used in conjunction with pageable handlers or other components that require sort handling.
     *
     * @return a {@link HateoasSortHandlerMethodArgumentResolver} instance for handling sort parameters.
     */
    @Bean
    public HateoasSortHandlerMethodArgumentResolver sortResolver() {
        return new HateoasSortHandlerMethodArgumentResolver();
    }

    /**
     * Creates and returns a {@link PagedResourcesAssembler} bean.
     * <p>
     * This method provides a {@link PagedResourcesAssembler} instance configured with
     * a {@link HateoasPageableHandlerMethodArgumentResolver}. The assembler is used to convert
     * pageable query results into hypermedia-based paginated representations, enabling the
     * creation of pageable HATEOAS-compliant APIs.
     *
     * @return a {@link PagedResourcesAssembler} instance suitable for assembling
     * paged resources in a HATEOAS-based API.
     */
    @Bean
    public PagedResourcesAssembler<?> pagedResourcesAssembler() {
        return new PagedResourcesAssembler<>(pageableResolver(), null);
    }

    /**
     * Creates and returns a {@link PagedResourcesAssemblerArgumentResolver} bean.
     * <p>
     * This method initializes an argument resolver that facilitates the construction
     * of {@link PagedResourcesAssembler} instances. It resolves pageable parameters
     * and binds them to the PagedResourcesAssembler for use in creating paginated responses.
     *
     * @return a {@link PagedResourcesAssemblerArgumentResolver} instance configured with a
     * {@link HateoasPageableHandlerMethodArgumentResolver}.
     */
    @Bean
    public PagedResourcesAssemblerArgumentResolver pagedResourcesAssemblerArgumentResolver() {
        return new PagedResourcesAssemblerArgumentResolver(pageableResolver());
    }

}
