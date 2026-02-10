package com.svalero.rutea.service;

import com.svalero.rutea.domain.Categoria;
import com.svalero.rutea.domain.PuntoInteres;
import com.svalero.rutea.dto.PuntoInteresInDto;
import com.svalero.rutea.dto.PuntoInteresOutDto;
import com.svalero.rutea.exception.CategoriaNotFoundException;
import com.svalero.rutea.exception.PuntoInteresNotFoundException;
import com.svalero.rutea.repository.CategoriaRepository;
import com.svalero.rutea.repository.PuntoInteresRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PuntoInteresServiceTest {

    @Mock private PuntoInteresRepository puntoInteresRepository;
    @Mock private CategoriaRepository categoriaRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks private PuntoInteresService puntoInteresService;

    private Categoria cat1;
    private PuntoInteres p1;
    private PuntoInteres p2;

    @BeforeEach
    void setUp() {
        cat1 = Categoria.builder().id(1L).nombre("Naturaleza").build();

        p1 = PuntoInteres.builder()
                .id(10L)
                .abiertoActualmente(true)
                .fechaCreacion(LocalDateTime.now())
                .latitud(41.0)
                .longitud(-0.8)
                .nombre("Parque Grande")
                .puntuacionMedia(4.5f)
                .categoria(cat1)
                .build();

        p2 = PuntoInteres.builder()
                .id(20L)
                .abiertoActualmente(false)
                .fechaCreacion(LocalDateTime.now())
                .latitud(40.0)
                .longitud(-1.0)
                .nombre("Museo")
                .puntuacionMedia(3.0f)
                .categoria(null)
                .build();
    }

    // -------------------- ADD --------------------

    @Test
    void add_shouldReturnOutDto_whenCategoriaExists() throws Exception {
        PuntoInteresInDto in = new PuntoInteresInDto(
                true, p1.getFechaCreacion(), 41.0, -0.8, "Parque Grande", 4.5f, 1L
        );

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(cat1));

        PuntoInteres mapped = PuntoInteres.builder().build();
        when(modelMapper.map(in, PuntoInteres.class)).thenReturn(mapped);

        // ✅ Cambio: ahora puede llamar saveAndFlush
        when(puntoInteresRepository.save(any(PuntoInteres.class))).thenReturn(p1);

        // ✅ CORREGIDO: 9 parámetros (añadido categoriaNombre)
        PuntoInteresOutDto mappedOut = new PuntoInteresOutDto(
                10L, true, p1.getFechaCreacion(), 41.0, -0.8, "Parque Grande", 4.5f, 1L, "Naturaleza"
        );
        when(modelMapper.map(p1, PuntoInteresOutDto.class)).thenReturn(mappedOut);

        PuntoInteresOutDto out = puntoInteresService.add(in);

        assertNotNull(out);
        assertEquals(10L, out.getId());
        assertEquals(1L, out.getCategoriaId());

        ArgumentCaptor<PuntoInteres> captor = ArgumentCaptor.forClass(PuntoInteres.class);
        verify(puntoInteresRepository).save(captor.capture());
        assertSame(cat1, captor.getValue().getCategoria());

        verify(categoriaRepository).findById(1L);
        verify(modelMapper).map(in, PuntoInteres.class);
        verify(modelMapper).map(p1, PuntoInteresOutDto.class);
    }

    @Test
    void add_shouldThrow_whenCategoriaNotFound() {
        PuntoInteresInDto in = new PuntoInteresInDto(
                true, null, 41.0, -0.8, "X", 4.0f, 99L
        );

        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoriaNotFoundException.class, () -> puntoInteresService.add(in));

        verify(categoriaRepository).findById(99L);
        verify(puntoInteresRepository, never()).save(any());
        verify(modelMapper, never()).map(any(), eq(PuntoInteres.class));
    }

    // -------------------- FIND BY ID --------------------

    @Test
    void findById_shouldReturnOutDto_whenExists() throws Exception {
        when(puntoInteresRepository.findById(10L)).thenReturn(Optional.of(p1));

        // ✅ CORREGIDO: 9 parámetros (añadido categoriaNombre)
        PuntoInteresOutDto mappedOut = new PuntoInteresOutDto(
                10L, true, p1.getFechaCreacion(), 41.0, -0.8, "Parque Grande", 4.5f, 1L, "Naturaleza"
        );
        when(modelMapper.map(p1, PuntoInteresOutDto.class)).thenReturn(mappedOut);

        PuntoInteresOutDto out = puntoInteresService.findById(10L);

        assertEquals(10L, out.getId());
        assertEquals("Parque Grande", out.getNombre());
        assertEquals(1L, out.getCategoriaId());

        verify(puntoInteresRepository).findById(10L);
        verify(modelMapper).map(p1, PuntoInteresOutDto.class);
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        when(puntoInteresRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PuntoInteresNotFoundException.class, () -> puntoInteresService.findById(999L));
        verify(puntoInteresRepository).findById(999L);
        verifyNoInteractions(modelMapper);
    }

    // -------------------- MODIFY --------------------

    @Test
    void modify_shouldReturnUpdatedOutDto_whenExistsAndCategoriaExists() throws Exception {
        PuntoInteresInDto in = new PuntoInteresInDto(
                false, null, 40.0, -0.7, "Nuevo", 3.5f, 1L
        );

        when(puntoInteresRepository.findById(10L)).thenReturn(Optional.of(p1));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(cat1));

        // modelMapper.map(dto, existing) => void
        doAnswer(inv -> {
            PuntoInteresInDto src = inv.getArgument(0);
            PuntoInteres dest = inv.getArgument(1);
            dest.setAbiertoActualmente(src.isAbiertoActualmente());
            dest.setLatitud(src.getLatitud());
            dest.setLongitud(src.getLongitud());
            dest.setNombre(src.getNombre());
            dest.setPuntuacionMedia(src.getPuntuacionMedia());
            dest.setFechaCreacion(src.getFechaCreacion());
            return null;
        }).when(modelMapper).map(any(PuntoInteresInDto.class), any(PuntoInteres.class));

        when(puntoInteresRepository.save(any(PuntoInteres.class))).thenAnswer(inv -> inv.getArgument(0));

        // ✅ CORREGIDO: 9 parámetros (añadido categoriaNombre)
        PuntoInteresOutDto mappedOut = new PuntoInteresOutDto(
                10L, false, null, 40.0, -0.7, "Nuevo", 3.5f, 1L, "Naturaleza"
        );
        when(modelMapper.map(any(PuntoInteres.class), eq(PuntoInteresOutDto.class))).thenReturn(mappedOut);

        PuntoInteresOutDto out = puntoInteresService.modify(10L, in);

        assertEquals(10L, out.getId());
        assertEquals("Nuevo", out.getNombre());
        assertFalse(out.isAbiertoActualmente());
        assertEquals(1L, out.getCategoriaId());

        verify(puntoInteresRepository).findById(10L);
        verify(categoriaRepository).findById(1L);
        verify(modelMapper).map(any(PuntoInteresInDto.class), any(PuntoInteres.class));
        verify(puntoInteresRepository).save(any(PuntoInteres.class));
    }

    @Test
    void modify_shouldThrow_whenPuntoNotFound() {
        PuntoInteresInDto in = new PuntoInteresInDto(
                true, null, 41.0, -0.8, "X", 4.0f, 1L
        );

        when(puntoInteresRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PuntoInteresNotFoundException.class, () -> puntoInteresService.modify(999L, in));

        verify(puntoInteresRepository).findById(999L);
        verify(puntoInteresRepository, never()).save(any());
        verify(categoriaRepository, never()).findById(anyLong());
    }

    @Test
    void modify_shouldThrow_whenCategoriaNotFound() {
        PuntoInteresInDto in = new PuntoInteresInDto(
                true, null, 41.0, -0.8, "X", 4.0f, 99L
        );

        when(puntoInteresRepository.findById(10L)).thenReturn(Optional.of(p1));
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoriaNotFoundException.class, () -> puntoInteresService.modify(10L, in));

        verify(puntoInteresRepository).findById(10L);
        verify(categoriaRepository).findById(99L);
        verify(puntoInteresRepository, never()).save(any());
        verify(modelMapper, never()).map(any(PuntoInteresInDto.class), any(PuntoInteres.class));
    }

    // -------------------- DELETE --------------------

    @Test
    void delete_shouldDelete_whenExists() throws Exception {
        when(puntoInteresRepository.findById(10L)).thenReturn(Optional.of(p1));

        puntoInteresService.delete(10L);

        verify(puntoInteresRepository).findById(10L);
        verify(puntoInteresRepository).delete(p1);
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(puntoInteresRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PuntoInteresNotFoundException.class, () -> puntoInteresService.delete(999L));

        verify(puntoInteresRepository).findById(999L);
        verify(puntoInteresRepository, never()).delete(any());
    }

    // -------------------- FIND ALL (FILTROS EN MEMORIA) --------------------

    @Test
    void findAll_shouldFilterByCategoriaAbiertoNombrePuntuacion() {
        when(puntoInteresRepository.findAll()).thenReturn(List.of(p1, p2));

        // ✅ CORREGIDO: 9 parámetros (añadido categoriaNombre)
        List<PuntoInteresOutDto> mappedList = List.of(
                new PuntoInteresOutDto(10L, true, p1.getFechaCreacion(), 41.0, -0.8, "Parque Grande", 4.5f, 1L, "Naturaleza")
        );
        when(modelMapper.map(anyList(), ArgumentMatchers.<java.lang.reflect.Type>any()))
                .thenReturn(mappedList);

        List<PuntoInteresOutDto> result = puntoInteresService.findAll(
                1L, true, "parque", 4.5f
        );

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());

        verify(puntoInteresRepository).findAll();
        verify(modelMapper).map(anyList(), ArgumentMatchers.<java.lang.reflect.Type>any());
    }
}