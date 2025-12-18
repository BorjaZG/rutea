package com.svalero.rutea.service;

import com.svalero.rutea.domain.Categoria;
import com.svalero.rutea.exception.ResourceNotFoundException;
import com.svalero.rutea.repository.CategoriaRepository;
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
class CategoriaServiceTest {
    @Mock private CategoriaRepository categoriaRepository;
    @InjectMocks private CategoriaService categoriaService;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Museos");
        categoria.setActiva(true);
    }

    @Test
    void testFindById() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        assertNotNull(categoriaService.findById(1L));
        assertEquals("Museos", categoriaService.findById(1L).getNombre());
    }

    @Test
    void testFindById_NotFound() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> categoriaService.findById(99L));
    }

    @Test
    void testSave() {
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);
        assertNotNull(categoriaService.save(categoria));
        verify(categoriaRepository, times(1)).save(categoria);
    }
}