package com.svalero.rutea.service;

import com.svalero.rutea.domain.Categoria;
import com.svalero.rutea.dto.CategoriaInDto;
import com.svalero.rutea.dto.CategoriaOutDto;
import com.svalero.rutea.exception.CategoriaNotFoundException;
import com.svalero.rutea.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria1;
    private Categoria categoria2;

    @BeforeEach
    void setUp() {
        categoria1 = Categoria.builder()
                .id(1L)
                .activa(true)
                .costePromedio(10f)
                .descripcion("desc")
                .iconoUrl("http://icon")
                .nombre("Montaña")
                .ordenPrioridad(1)
                .build();

        categoria2 = Categoria.builder()
                .id(2L)
                .activa(false)
                .costePromedio(0f)
                .descripcion(null)
                .iconoUrl(null)
                .nombre("Monumento")
                .ordenPrioridad(0)
                .build();
    }

    // -------------------- ADD --------------------

    @Test
    void add_shouldReturnOutDto() {
        CategoriaInDto in = CategoriaInDto.builder()
                .activa(true)
                .costePromedio(10f)
                .descripcion("desc")
                .iconoUrl("http://icon")
                .nombre("Montaña")
                .ordenPrioridad(1)
                .build();

        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria1);

        CategoriaOutDto out = categoriaService.add(in);

        assertNotNull(out);
        assertEquals(1L, out.getId());
        assertEquals("Montaña", out.getNombre());
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    // -------------------- FIND ALL (FILTROS) --------------------

    @Test
    void findAll_shouldUse3Filters_whenActivaNombreOrdenPresent() {
        when(categoriaRepository.findByActivaAndNombreContainingIgnoreCaseAndOrdenPrioridad(true, "mon", 1))
                .thenReturn(List.of(categoria1));

        List<CategoriaOutDto> result = categoriaService.findAll(true, "mon", 1);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(categoriaRepository).findByActivaAndNombreContainingIgnoreCaseAndOrdenPrioridad(true, "mon", 1);
        verifyNoMoreInteractions(categoriaRepository);
    }

    @Test
    void findAll_shouldUse2Filters_whenActivaAndNombrePresent() {
        when(categoriaRepository.findByActivaAndNombreContainingIgnoreCase(true, "mon"))
                .thenReturn(List.of(categoria1));

        List<CategoriaOutDto> result = categoriaService.findAll(true, "mon", null);

        assertEquals(1, result.size());
        verify(categoriaRepository).findByActivaAndNombreContainingIgnoreCase(true, "mon");
    }

    @Test
    void findAll_shouldUse2Filters_whenActivaAndOrdenPresent() {
        when(categoriaRepository.findByActivaAndOrdenPrioridad(true, 1))
                .thenReturn(List.of(categoria1));

        List<CategoriaOutDto> result = categoriaService.findAll(true, null, 1);

        assertEquals(1, result.size());
        verify(categoriaRepository).findByActivaAndOrdenPrioridad(true, 1);
    }

    @Test
    void findAll_shouldUse2Filters_whenNombreAndOrdenPresent() {
        when(categoriaRepository.findByNombreContainingIgnoreCaseAndOrdenPrioridad("mon", 1))
                .thenReturn(List.of(categoria1));

        List<CategoriaOutDto> result = categoriaService.findAll(null, "mon", 1);

        assertEquals(1, result.size());
        verify(categoriaRepository).findByNombreContainingIgnoreCaseAndOrdenPrioridad("mon", 1);
    }

    @Test
    void findAll_shouldUse1Filter_whenOnlyActivaPresent() {
        when(categoriaRepository.findByActiva(true)).thenReturn(List.of(categoria1));

        List<CategoriaOutDto> result = categoriaService.findAll(true, null, null);

        assertEquals(1, result.size());
        verify(categoriaRepository).findByActiva(true);
    }

    @Test
    void findAll_shouldUse1Filter_whenOnlyNombrePresent() {
        when(categoriaRepository.findByNombreContainingIgnoreCase("mon")).thenReturn(List.of(categoria1));

        List<CategoriaOutDto> result = categoriaService.findAll(null, "mon", null);

        assertEquals(1, result.size());
        verify(categoriaRepository).findByNombreContainingIgnoreCase("mon");
    }

    @Test
    void findAll_shouldUse1Filter_whenOnlyOrdenPresent() {
        when(categoriaRepository.findByOrdenPrioridad(1)).thenReturn(List.of(categoria1));

        List<CategoriaOutDto> result = categoriaService.findAll(null, null, 1);

        assertEquals(1, result.size());
        verify(categoriaRepository).findByOrdenPrioridad(1);
    }

    @Test
    void findAll_shouldReturnAll_whenNoFilters() {
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria1, categoria2));

        List<CategoriaOutDto> result = categoriaService.findAll(null, null, null);

        assertEquals(2, result.size());
        verify(categoriaRepository).findAll();
    }

    // -------------------- FIND BY ID --------------------

    @Test
    void findById_shouldReturnOutDto_whenExists() throws Exception {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria1));

        CategoriaOutDto out = categoriaService.findById(1L);

        assertEquals(1L, out.getId());
        assertEquals("Montaña", out.getNombre());
        verify(categoriaRepository).findById(1L);
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoriaNotFoundException.class, () -> categoriaService.findById(99L));
        verify(categoriaRepository).findById(99L);
    }

    // -------------------- MODIFY --------------------

    @Test
    void modify_shouldReturnUpdatedOutDto_whenExists() throws Exception {
        CategoriaInDto in = CategoriaInDto.builder()
                .activa(false)
                .costePromedio(5f)
                .descripcion("nuevo")
                .iconoUrl(null)
                .nombre("X")
                .ordenPrioridad(2)
                .build();

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria1));
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(inv -> inv.getArgument(0));

        CategoriaOutDto out = categoriaService.modify(1L, in);

        assertEquals(1L, out.getId());
        assertEquals("X", out.getNombre());
        assertEquals(2, out.getOrdenPrioridad());
        assertFalse(out.isActiva());

        verify(categoriaRepository).findById(1L);
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void modify_shouldThrow_whenNotFound() {
        CategoriaInDto in = CategoriaInDto.builder()
                .activa(true).costePromedio(0).nombre("X").ordenPrioridad(0)
                .build();

        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoriaNotFoundException.class, () -> categoriaService.modify(99L, in));
        verify(categoriaRepository).findById(99L);
        verify(categoriaRepository, never()).save(any());
    }

    // -------------------- DELETE --------------------

    @Test
    void delete_shouldDelete_whenExists() throws Exception {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria1));

        categoriaService.delete(1L);

        verify(categoriaRepository).findById(1L);
        verify(categoriaRepository).delete(categoria1);
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoriaNotFoundException.class, () -> categoriaService.delete(99L));
        verify(categoriaRepository).findById(99L);
        verify(categoriaRepository, never()).delete(any());
    }
}
