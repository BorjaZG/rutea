package com.svalero.rutea.service;

import com.svalero.rutea.domain.Resena;
import com.svalero.rutea.exception.ResourceNotFoundException;
import com.svalero.rutea.repository.ResenaRepository;
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
class ResenaServiceTest {
    @Mock private ResenaRepository resenaRepository;
    @InjectMocks private ResenaService resenaService;
    private Resena resena;

    @BeforeEach
    void setUp() {
        resena = new Resena();
        resena.setId(1L);
        resena.setComentario("Muy buena ruta");
        resena.setValoracion(5);
    }

    @Test
    void testFindById() {
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));
        assertNotNull(resenaService.findById(1L));
    }

    @Test
    void testFindById_NotFound() {
        when(resenaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> resenaService.findById(99L));
    }

    @Test
    void testSave() {
        when(resenaRepository.save(any(Resena.class))).thenReturn(resena);
        assertNotNull(resenaService.save(resena));
    }
}