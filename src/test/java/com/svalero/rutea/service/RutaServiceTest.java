package com.svalero.rutea.service;

import com.svalero.rutea.domain.Ruta;
import com.svalero.rutea.exception.ResourceNotFoundException;
import com.svalero.rutea.repository.RutaRepository;
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
class RutaServiceTest {
    @Mock private RutaRepository rutaRepository;
    @InjectMocks private RutaService rutaService;
    private Ruta ruta;

    @BeforeEach
    void setUp() {
        ruta = new Ruta();
        ruta.setId(1L);
        ruta.setTitulo("Ruta Tapas");
        ruta.setDificultad("BAJA");
    }

    @Test
    void testFindById() {
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(ruta));
        assertNotNull(rutaService.findById(1L));
    }

    @Test
    void testFindById_NotFound() {
        when(rutaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> rutaService.findById(99L));
    }

    @Test
    void testSave() {
        when(rutaRepository.save(any(Ruta.class))).thenReturn(ruta);
        assertNotNull(rutaService.save(ruta));
    }
}