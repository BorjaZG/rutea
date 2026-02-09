package com.svalero.rutea.service;

import com.svalero.rutea.domain.PuntoInteres;
import com.svalero.rutea.domain.Resena;
import com.svalero.rutea.domain.Usuario;
import com.svalero.rutea.dto.ResenaInDto;
import com.svalero.rutea.dto.ResenaOutDto;
import com.svalero.rutea.exception.PuntoInteresNotFoundException;
import com.svalero.rutea.exception.ResenaNotFoundException;
import com.svalero.rutea.exception.UsuarioNotFoundException;
import com.svalero.rutea.repository.PuntoInteresRepository;
import com.svalero.rutea.repository.ResenaRepository;
import com.svalero.rutea.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResenaServiceTest {

    @Mock private ResenaRepository resenaRepository;
    @Mock private PuntoInteresRepository puntoInteresRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks private ResenaService resenaService;

    private PuntoInteres punto;
    private Usuario usuario;
    private Resena r1;
    private Resena r2;

    @BeforeEach
    void setUp() {
        punto = PuntoInteres.builder().id(10L).nombre("Parque").build();
        usuario = Usuario.builder().id(20L).username("borja").build();

        r1 = Resena.builder()
                .id(1L)
                .comentario("Muy bien")
                .editada(false)
                .fechaPublicacion(LocalDate.now())
                .likes(10)
                .titulo("Genial")
                .valoracion(5)
                .punto(punto)
                .usuario(usuario)
                .build();

        r2 = Resena.builder()
                .id(2L)
                .comentario("Normal")
                .editada(true)
                .fechaPublicacion(LocalDate.now())
                .likes(0)
                .titulo(null)
                .valoracion(3)
                .punto(null)
                .usuario(null)
                .build();
    }

    // -------------------- ADD --------------------

    @Test
    void add_shouldReturnOutDto_whenPuntoAndUsuarioExist() throws Exception {
        ResenaInDto in = new ResenaInDto(
                "Muy bien", false, LocalDate.now(), 10, "Genial", 5, 10L, 20L
        );

        when(puntoInteresRepository.findById(10L)).thenReturn(Optional.of(punto));
        when(usuarioRepository.findById(20L)).thenReturn(Optional.of(usuario));

        Resena mapped = Resena.builder().build();
        when(modelMapper.map(in, Resena.class)).thenReturn(mapped);

        when(resenaRepository.save(any(Resena.class))).thenReturn(r1);

        ResenaOutDto mappedOut = new ResenaOutDto(
                1L, "Muy bien", false, r1.getFechaPublicacion(), 10, "Genial", 5, 10L, 20L
        );
        when(modelMapper.map(r1, ResenaOutDto.class)).thenReturn(mappedOut);

        ResenaOutDto out = resenaService.add(in);

        assertNotNull(out);
        assertEquals(1L, out.getId());
        assertEquals(10L, out.getPuntoId());
        assertEquals(20L, out.getUsuarioId());

        ArgumentCaptor<Resena> captor = ArgumentCaptor.forClass(Resena.class);
        verify(resenaRepository).save(captor.capture());
        assertSame(punto, captor.getValue().getPunto());
        assertSame(usuario, captor.getValue().getUsuario());

        verify(puntoInteresRepository).findById(10L);
        verify(usuarioRepository).findById(20L);
        verify(modelMapper).map(in, Resena.class);
        verify(modelMapper).map(r1, ResenaOutDto.class);
    }

    @Test
    void add_shouldThrow_whenPuntoNotFound() {
        ResenaInDto in = new ResenaInDto(
                "X", false, null, 0, null, 4, 999L, 20L
        );

        when(puntoInteresRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PuntoInteresNotFoundException.class, () -> resenaService.add(in));

        verify(puntoInteresRepository).findById(999L);
        verify(usuarioRepository, never()).findById(anyLong());
        verify(resenaRepository, never()).save(any());
    }

    @Test
    void add_shouldThrow_whenUsuarioNotFound() {
        ResenaInDto in = new ResenaInDto(
                "X", false, null, 0, null, 4, 10L, 999L
        );

        when(puntoInteresRepository.findById(10L)).thenReturn(Optional.of(punto));
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> resenaService.add(in));

        verify(puntoInteresRepository).findById(10L);
        verify(usuarioRepository).findById(999L);
        verify(resenaRepository, never()).save(any());
        verify(modelMapper, never()).map(any(), eq(Resena.class));
    }

    // -------------------- FIND BY ID --------------------

    @Test
    void findById_shouldReturnOutDto_whenExists() throws Exception {
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(r1));

        ResenaOutDto mappedOut = new ResenaOutDto(
                1L, "Muy bien", false, r1.getFechaPublicacion(), 10, "Genial", 5, 10L, 20L
        );
        when(modelMapper.map(r1, ResenaOutDto.class)).thenReturn(mappedOut);

        ResenaOutDto out = resenaService.findById(1L);

        assertEquals(1L, out.getId());
        assertEquals(5, out.getValoracion());
        assertEquals(10L, out.getPuntoId());
        assertEquals(20L, out.getUsuarioId());

        verify(resenaRepository).findById(1L);
        verify(modelMapper).map(r1, ResenaOutDto.class);
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        when(resenaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResenaNotFoundException.class, () -> resenaService.findById(999L));

        verify(resenaRepository).findById(999L);
        verifyNoInteractions(modelMapper);
    }

    // -------------------- MODIFY --------------------

    @Test
    void modify_shouldReturnUpdatedOutDto_whenExistsAndRelationsExist() throws Exception {
        ResenaInDto in = new ResenaInDto(
                "Editada", true, LocalDate.now(), 5, "Nuevo", 3, 10L, 20L
        );

        when(resenaRepository.findById(1L)).thenReturn(Optional.of(r1));
        when(puntoInteresRepository.findById(10L)).thenReturn(Optional.of(punto));
        when(usuarioRepository.findById(20L)).thenReturn(Optional.of(usuario));

        // modelMapper.map(dto, existing) => void
        doAnswer(inv -> {
            ResenaInDto src = inv.getArgument(0);
            Resena dest = inv.getArgument(1);
            dest.setComentario(src.getComentario());
            dest.setEditada(src.isEditada());
            dest.setFechaPublicacion(src.getFechaPublicacion());
            dest.setLikes(src.getLikes());
            dest.setTitulo(src.getTitulo());
            dest.setValoracion(src.getValoracion());
            return null;
        }).when(modelMapper).map(any(ResenaInDto.class), any(Resena.class));

        when(resenaRepository.save(any(Resena.class))).thenAnswer(inv -> inv.getArgument(0));

        ResenaOutDto mappedOut = new ResenaOutDto(
                1L, "Editada", true, in.getFechaPublicacion(), 5, "Nuevo", 3, 10L, 20L
        );
        when(modelMapper.map(any(Resena.class), eq(ResenaOutDto.class))).thenReturn(mappedOut);

        ResenaOutDto out = resenaService.modify(1L, in);

        assertEquals(1L, out.getId());
        assertEquals("Editada", out.getComentario());
        assertTrue(out.isEditada());
        assertEquals(10L, out.getPuntoId());
        assertEquals(20L, out.getUsuarioId());

        verify(resenaRepository).findById(1L);
        verify(puntoInteresRepository).findById(10L);
        verify(usuarioRepository).findById(20L);
        verify(modelMapper).map(any(ResenaInDto.class), any(Resena.class));
        verify(resenaRepository).save(any(Resena.class));
    }

    @Test
    void modify_shouldThrow_whenResenaNotFound() {
        ResenaInDto in = new ResenaInDto(
                "X", false, null, 0, null, 4, 10L, 20L
        );

        when(resenaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResenaNotFoundException.class, () -> resenaService.modify(999L, in));

        verify(resenaRepository).findById(999L);
        verify(resenaRepository, never()).save(any());
        verify(puntoInteresRepository, never()).findById(anyLong());
        verify(usuarioRepository, never()).findById(anyLong());
    }

    @Test
    void modify_shouldThrow_whenPuntoNotFound() {
        ResenaInDto in = new ResenaInDto(
                "X", false, null, 0, null, 4, 999L, 20L
        );

        when(resenaRepository.findById(1L)).thenReturn(Optional.of(r1));
        when(puntoInteresRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PuntoInteresNotFoundException.class, () -> resenaService.modify(1L, in));

        verify(resenaRepository).findById(1L);
        verify(puntoInteresRepository).findById(999L);
        verify(usuarioRepository, never()).findById(anyLong());
        verify(resenaRepository, never()).save(any());
        verify(modelMapper, never()).map(any(), any(Resena.class));
    }

    @Test
    void modify_shouldThrow_whenUsuarioNotFound() {
        ResenaInDto in = new ResenaInDto(
                "X", false, null, 0, null, 4, 10L, 999L
        );

        when(resenaRepository.findById(1L)).thenReturn(Optional.of(r1));
        when(puntoInteresRepository.findById(10L)).thenReturn(Optional.of(punto));
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> resenaService.modify(1L, in));

        verify(resenaRepository).findById(1L);
        verify(puntoInteresRepository).findById(10L);
        verify(usuarioRepository).findById(999L);
        verify(resenaRepository, never()).save(any());
        verify(modelMapper, never()).map(any(), any(Resena.class));
    }

    // -------------------- DELETE --------------------

    @Test
    void delete_shouldDelete_whenExists() throws Exception {
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(r1));

        resenaService.delete(1L);

        verify(resenaRepository).findById(1L);
        verify(resenaRepository).delete(r1);
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(resenaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResenaNotFoundException.class, () -> resenaService.delete(999L));

        verify(resenaRepository).findById(999L);
        verify(resenaRepository, never()).delete(any());
    }

    // -------------------- FIND ALL (FILTROS EN MEMORIA) --------------------

    @Test
    void findAll_shouldFilterByEditadaLikesValoracion() {
        when(resenaRepository.findAll()).thenReturn(List.of(r1, r2));

        List<ResenaOutDto> mapped = List.of(
                new ResenaOutDto(1L, "Muy bien", false, r1.getFechaPublicacion(), 10, "Genial", 5, 10L, 20L)
        );

        when(modelMapper.map(anyList(), ArgumentMatchers.<Type>any())).thenReturn(mapped);

        List<ResenaOutDto> result = resenaService.findAll(false, 10, 5);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());

        verify(resenaRepository).findAll();
        verify(modelMapper).map(anyList(), ArgumentMatchers.<Type>any());
    }
}
