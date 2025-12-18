package com.svalero.rutea.service;

import com.svalero.rutea.domain.PuntoInteres;
import com.svalero.rutea.exception.ResourceNotFoundException;
import com.svalero.rutea.repository.PuntoInteresRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PuntoInteresServiceTest {
    @Mock private PuntoInteresRepository puntoRepository;
    @InjectMocks private PuntoInteresService puntoService;
    private PuntoInteres punto;

    @BeforeEach
    void setUp() {
        punto = new PuntoInteres();
        punto.setId(1L);
        punto.setNombre("Plaza Central");
        punto.setLatitud(40.0);
        punto.setLongitud(1.0);
    }

    @Test
    void testFindById() {
        when(puntoRepository.findById(1L)).thenReturn(Optional.of(punto));
        assertNotNull(puntoService.findById(1L));
    }

    @Test
    void testFindById_NotFound() {
        when(puntoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> puntoService.findById(99L));
    }

    @Test
    void testSave() {
        when(puntoRepository.save(any(PuntoInteres.class))).thenReturn(punto);
        assertNotNull(puntoService.save(punto));
    }
}