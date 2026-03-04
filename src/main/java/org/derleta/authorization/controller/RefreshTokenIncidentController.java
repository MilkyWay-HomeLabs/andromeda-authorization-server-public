package org.derleta.authorization.controller;

import org.derleta.authorization.controller.assembler.RefreshTokenIncidentModelAssembler;
import org.derleta.authorization.controller.dto.response.RefreshTokenIncidentResponse;
import org.derleta.authorization.domain.entity.token.RefreshTokenIncidentEntity;
import org.derleta.authorization.service.token.RefreshTokenIncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1")
public class RefreshTokenIncidentController {

    public static final String DEFAULT_PATH = "table/tokens/incidents/refresh";

    private final RefreshTokenIncidentService service;
    private final RefreshTokenIncidentModelAssembler incidentModelAssembler;
    private final PagedResourcesAssembler<RefreshTokenIncidentEntity> pagedResourcesAssembler;

    @Autowired
    public RefreshTokenIncidentController(RefreshTokenIncidentService service, RefreshTokenIncidentModelAssembler incidentModelAssembler, PagedResourcesAssembler<RefreshTokenIncidentEntity> pagedResourcesAssembler) {
        this.service = service;
        this.incidentModelAssembler = incidentModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/" + DEFAULT_PATH, produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<PagedModel<RefreshTokenIncidentResponse>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        final Page<RefreshTokenIncidentEntity> incidentsPage = service.getPage(page, size);
        final PagedModel<RefreshTokenIncidentResponse> model =
                pagedResourcesAssembler.toModel(incidentsPage, incidentModelAssembler);
        return ResponseEntity.ok(model);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/" + DEFAULT_PATH + "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<RefreshTokenIncidentResponse> getById(@PathVariable long id) {
        return service.findById(id)
                .map(incidentModelAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/" + DEFAULT_PATH + "/from", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<PagedModel<RefreshTokenIncidentResponse>> getFrom(
            @RequestParam("from") Instant from,
            @RequestParam(defaultValue = "1000") int limit
    ) {
        final Page<RefreshTokenIncidentEntity> incidents = service.getFrom(from, limit);
        final PagedModel<RefreshTokenIncidentResponse> model =
                pagedResourcesAssembler.toModel(incidents, incidentModelAssembler);
        return ResponseEntity.ok(model);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/" + DEFAULT_PATH + "/last-24h", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<PagedModel<RefreshTokenIncidentResponse>> getLast24h(
            @RequestParam(defaultValue = "1000") int limit
    ) {
        final Page<RefreshTokenIncidentEntity> incidents = service.getLast24h(limit);
        final PagedModel<RefreshTokenIncidentResponse> model =
                pagedResourcesAssembler.toModel(incidents, incidentModelAssembler);
        return ResponseEntity.ok(model);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/" + DEFAULT_PATH + "/from-timestamp", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<PagedModel<RefreshTokenIncidentResponse>> getFromTimestamp(
            @RequestParam("from") Timestamp from,
            @RequestParam(defaultValue = "1000") int limit
    ) {
        final var incidents = service.getFrom(from.toInstant(), limit);
        final PagedModel<RefreshTokenIncidentResponse> model =
                pagedResourcesAssembler.toModel(incidents, incidentModelAssembler);
        return ResponseEntity.ok(model);
    }

}
