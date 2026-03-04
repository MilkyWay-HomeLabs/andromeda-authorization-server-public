package org.derleta.authorization.service.token;

import org.derleta.authorization.domain.entity.token.RefreshTokenIncidentEntity;
import org.derleta.authorization.repository.impl.token.RefreshTokenIncidentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenIncidentServiceTest {

    @Mock
    private RefreshTokenIncidentRepository repository;

    @InjectMocks
    private RefreshTokenIncidentService service;

    @Test
    void testGetPage() {
        when(repository.countAll()).thenReturn(10L);
        List<RefreshTokenIncidentEntity> items = Collections.singletonList(new RefreshTokenIncidentEntity());
        when(repository.findPage(0, 10)).thenReturn(items);

        Page<RefreshTokenIncidentEntity> page = service.getPage(0, 10);

        assertNotNull(page);
        assertEquals(10, page.getTotalElements());
        assertEquals(1, page.getContent().size());
        verify(repository).findPage(0, 10);
    }

    @Test
    void testGetPage_SafeValues() {
        when(repository.countAll()).thenReturn(0L);
        when(repository.findPage(0, 1)).thenReturn(Collections.emptyList());

        service.getPage(-1, 0);

        verify(repository).findPage(0, 1);
    }

    @Test
    void testGetPage_MaxSize() {
        when(repository.countAll()).thenReturn(0L);
        when(repository.findPage(0, 200)).thenReturn(Collections.emptyList());

        service.getPage(0, 300);

        verify(repository).findPage(0, 200);
    }

    @Test
    void testGetFrom() {
        Instant from = Instant.now();
        List<RefreshTokenIncidentEntity> items = Collections.singletonList(new RefreshTokenIncidentEntity());
        when(repository.findByDateFrom(eq(from), anyInt())).thenReturn(items);

        Page<RefreshTokenIncidentEntity> page = service.getFrom(from, 10);

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
        verify(repository).findByDateFrom(from, 10);
    }

    @Test
    void testGetFrom_NullDate() {
        assertThrows(IllegalArgumentException.class, () -> service.getFrom(null, 10));
    }

    @Test
    void testGetFrom_SafeLimit() {
        Instant from = Instant.now();
        when(repository.findByDateFrom(eq(from), eq(1))).thenReturn(Collections.emptyList());
        service.getFrom(from, 0);
        verify(repository).findByDateFrom(from, 1);

        when(repository.findByDateFrom(eq(from), eq(5000))).thenReturn(Collections.emptyList());
        service.getFrom(from, 6000);
        verify(repository).findByDateFrom(from, 5000);
    }

    @Test
    void testGetLast24h() {
        when(repository.findByDateFrom(any(Instant.class), eq(10))).thenReturn(Collections.emptyList());
        Page<RefreshTokenIncidentEntity> page = service.getLast24h(10);
        assertNotNull(page);
        verify(repository).findByDateFrom(any(Instant.class), eq(10));
    }

    @Test
    void testFindById_Success() {
        RefreshTokenIncidentEntity entity = new RefreshTokenIncidentEntity();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<RefreshTokenIncidentEntity> result = service.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
    }

    @Test
    void testFindById_InvalidId() {
        assertTrue(service.findById(0L).isEmpty());
        assertTrue(service.findById(-1L).isEmpty());
    }
}
