package org.derleta.authorization.service.token;

import org.derleta.authorization.domain.entity.token.RefreshTokenIncidentEntity;
import org.derleta.authorization.repository.impl.token.RefreshTokenIncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class RefreshTokenIncidentService {

    RefreshTokenIncidentRepository repository;

    @Autowired
    public RefreshTokenIncidentService(RefreshTokenIncidentRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<RefreshTokenIncidentEntity> getPage(final int page, final int size) {
        final int safePage = Math.max(0, page);
        final int safeSize = Math.max(1, Math.min(200, size));

        final long total = repository.countAll();
        final List<RefreshTokenIncidentEntity> items = repository.findPage(safePage, safeSize);

        return new PageImpl<>(items, PageRequest.of(safePage, safeSize), total);
    }

    @Transactional(readOnly = true)
    public Page<RefreshTokenIncidentEntity> getFrom(final Instant fromInclusive, final int limit) {
        if (fromInclusive == null) {
            throw new IllegalArgumentException("fromInclusive cannot be null");
        }

        final int safeLimit = Math.max(1, Math.min(5000, limit));
        final List<RefreshTokenIncidentEntity> items = repository.findByDateFrom(fromInclusive, safeLimit);

        final int pageSize = Math.max(1, items.size());
        return new PageImpl<>(items, PageRequest.of(0, pageSize), items.size());
    }

    @Transactional(readOnly = true)
    public Page<RefreshTokenIncidentEntity> getLast24h(final int limit) {
        final Instant from = Instant.now().minus(Duration.ofHours(24));
        return getFrom(from, limit);
    }

    @Transactional(readOnly = true)
    public Optional<RefreshTokenIncidentEntity> findById(final long id) {
        if (id <= 0) {
            return Optional.empty();
        }
        return repository.findById(id);
    }

}
