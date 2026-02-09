package com.svalero.rutea.service;

import com.svalero.rutea.domain.PuntoInteres;
import com.svalero.rutea.domain.Ruta;
import com.svalero.rutea.domain.Usuario;
import com.svalero.rutea.dto.RutaInDto;
import com.svalero.rutea.dto.RutaOutDto;
import com.svalero.rutea.exception.PuntoInteresNotFoundException;
import com.svalero.rutea.exception.RutaNotFoundException;
import com.svalero.rutea.exception.UsuarioNotFoundException;
import com.svalero.rutea.repository.PuntoInteresRepository;
import com.svalero.rutea.repository.RutaRepository;
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
class RutaServiceTest {

    @Mock private RutaRepository rutaRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PuntoInteresRepository puntoInteresRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks private RutaService rutaService;

    private Usuario usuario;
    private PuntoInteres p1;
    private PuntoInteres p2;
    private Ruta ruta1;
    private Ruta ruta2;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder().id(10L).username("borja").build();

        p1 = PuntoInteres.builder().id(100L).nombre("Parque").build();
        p2 = PuntoInteres.builder().id(200L).nombre("Mirador").build();

        ruta1 = Ruta.builder()
                .id(1L)
                .dificultad("facil")
                .distanciaKm(5.5f)
                .duracionMinutos(60)
                .fechaRealizacion(LocalDate.now())
                .publica(true)
                .titulo("Paseo por el parque")
                .usuario(usuario)
                .puntos(List.of(p1, p2))
                .build();

        ruta2 = Ruta.builder()
                .id(2L)
                .dificultad("dificil")
                .distanciaKm(12f)
                .duracionMinutos(180)
                .fechaRealizacion(LocalDate.now())
                .publica(false)
                .titulo("Subida al monte")
                .usuario(null)
                .puntos(List.of())
                .build();
    }

    // -------------------- ADD --------------------

    @Test
    void add_shouldReturnOutDto_whenUsuarioAndPuntosExist() throws Exception {
        RutaInDto in = new RutaInDto(
                "facil", 5.5f, 60, LocalDate.now(), true, "Paseo por el parque",
                10L, List.of(100L, 200L)
        );

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(puntoInteresRepository.findById(100L)).thenReturn(Optional.of(p1));
        when(puntoInteresRepository.findById(200L)).thenReturn(Optional.of(p2));

        Ruta mapped = Ruta.builder().build();
        when(modelMapper.map(in, Ruta.class)).thenReturn(mapped);

        when(rutaRepository.save(any(Ruta.class))).thenReturn(ruta1);

        RutaOutDto mappedOut = new RutaOutDto(
                1L, "facil", 5.5f, 60, ruta1.getFechaRealizacion(), true, "Paseo por el parque", 10L, List.of(100L, 200L)
        );
        when(modelMapper.map(any(Ruta.class), eq(RutaOutDto.class))).thenReturn(mappedOut);

        RutaOutDto out = rutaService.add(in);

        assertNotNull(out);
        assertEquals(1L, out.getId());
        assertEquals(10L, out.getUsuarioId());
        assertEquals(List.of(100L, 200L), out.getPuntosIds());

        ArgumentCaptor<Ruta> captor = ArgumentCaptor.forClass(Ruta.class);
        verify(rutaRepository).save(captor.capture());
        assertSame(usuario, captor.getValue().getUsuario());
        assertEquals(2, captor.getValue().getPuntos().size());

        verify(usuarioRepository).findById(10L);
        verify(puntoInteresRepository).findById(100L);
        verify(puntoInteresRepository).findById(200L);
        verify(modelMapper).map(in, Ruta.class);
        verify(modelMapper).map(any(Ruta.class), eq(RutaOutDto.class));
    }

    @Test
    void add_shouldThrow_whenUsuarioNotFound() {
        RutaInDto in = new RutaInDto(
                "x", 1f, 1, LocalDate.now(), true, "Ruta", 999L, List.of()
        );

        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> rutaService.add(in));

        verify(usuarioRepository).findById(999L);
        verify(rutaRepository, never()).save(any());
        verify(modelMapper, never()).map(any(), eq(Ruta.class));
        verifyNoInteractions(puntoInteresRepository);
    }

    @Test
    void add_shouldThrow_whenAnyPuntoNotFound() {
        RutaInDto in = new RutaInDto(
                "x", 1f, 1, LocalDate.now(), true, "Ruta", 10L, List.of(999L)
        );

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));

        // ✅ IMPORTANTE: evitar NPE
        when(modelMapper.map(in, Ruta.class)).thenReturn(new Ruta());

        when(puntoInteresRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(PuntoInteresNotFoundException.class, () -> rutaService.add(in));

        verify(usuarioRepository).findById(10L);
        verify(modelMapper).map(in, Ruta.class);
        verify(puntoInteresRepository).findById(999L);
        verify(rutaRepository, never()).save(any());
    }

    // -------------------- FIND BY ID --------------------

    @Test
    void findById_shouldReturnOutDto_whenExists() throws Exception {
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(ruta1));

        RutaOutDto mappedOut = new RutaOutDto(
                1L, "facil", 5.5f, 60, ruta1.getFechaRealizacion(), true, "Paseo por el parque", 10L, List.of(100L, 200L)
        );
        when(modelMapper.map(any(Ruta.class), eq(RutaOutDto.class))).thenReturn(mappedOut);

        RutaOutDto out = rutaService.findById(1L);

        assertEquals(1L, out.getId());
        assertEquals(10L, out.getUsuarioId());
        assertEquals(List.of(100L, 200L), out.getPuntosIds());

        verify(rutaRepository).findById(1L);
        verify(modelMapper).map(any(Ruta.class), eq(RutaOutDto.class));
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        when(rutaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RutaNotFoundException.class, () -> rutaService.findById(999L));

        verify(rutaRepository).findById(999L);
        verifyNoInteractions(modelMapper);
    }

    // -------------------- MODIFY --------------------

    @Test
    void modify_shouldReturnUpdatedOutDto_whenExistsAndRelationsExist() throws Exception {
        RutaInDto in = new RutaInDto(
                "dificil", 12f, 180, LocalDate.now(), false, "Ruta nueva", 10L, List.of(100L)
        );

        when(rutaRepository.findById(1L)).thenReturn(Optional.of(ruta1));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(puntoInteresRepository.findById(100L)).thenReturn(Optional.of(p1));

        doAnswer(inv -> {
            RutaInDto src = inv.getArgument(0);
            Ruta dest = inv.getArgument(1);
            dest.setDificultad(src.getDificultad());
            dest.setDistanciaKm(src.getDistanciaKm());
            dest.setDuracionMinutos(src.getDuracionMinutos());
            dest.setFechaRealizacion(src.getFechaRealizacion());
            dest.setPublica(src.isPublica());
            dest.setTitulo(src.getTitulo());
            return null;
        }).when(modelMapper).map(any(RutaInDto.class), any(Ruta.class));

        when(rutaRepository.save(any(Ruta.class))).thenAnswer(inv -> inv.getArgument(0));

        RutaOutDto mappedOut = new RutaOutDto(
                1L, "dificil", 12f, 180, in.getFechaRealizacion(), false, "Ruta nueva", 10L, List.of(100L)
        );
        when(modelMapper.map(any(Ruta.class), eq(RutaOutDto.class))).thenReturn(mappedOut);

        RutaOutDto out = rutaService.modify(1L, in);

        assertEquals(1L, out.getId());
        assertEquals("Ruta nueva", out.getTitulo());
        assertFalse(out.isPublica());
        assertEquals(List.of(100L), out.getPuntosIds());

        verify(rutaRepository).findById(1L);
        verify(usuarioRepository).findById(10L);
        verify(puntoInteresRepository).findById(100L);
        verify(rutaRepository).save(any(Ruta.class));
    }

    @Test
    void modify_shouldThrow_whenRutaNotFound() {
        RutaInDto in = new RutaInDto(
                "x", 1f, 1, LocalDate.now(), true, "Ruta", 10L, List.of()
        );

        when(rutaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RutaNotFoundException.class, () -> rutaService.modify(999L, in));

        verify(rutaRepository).findById(999L);
        verify(rutaRepository, never()).save(any());
        verifyNoInteractions(usuarioRepository);
        verifyNoInteractions(puntoInteresRepository);
    }

    @Test
    void modify_shouldThrow_whenUsuarioNotFound() {
        RutaInDto in = new RutaInDto(
                "x", 1f, 1, LocalDate.now(), true, "Ruta", 999L, List.of()
        );

        when(rutaRepository.findById(1L)).thenReturn(Optional.of(ruta1));
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> rutaService.modify(1L, in));

        verify(rutaRepository).findById(1L);
        verify(usuarioRepository).findById(999L);
        verify(rutaRepository, never()).save(any());
        verifyNoInteractions(puntoInteresRepository);
        verify(modelMapper, never()).map(any(), any(Ruta.class));
    }

    @Test
    void modify_shouldThrow_whenAnyPuntoNotFound() {
        RutaInDto in = new RutaInDto(
                "x", 1f, 1, LocalDate.now(), true, "Ruta", 10L, List.of(999L)
        );

        when(rutaRepository.findById(1L)).thenReturn(Optional.of(ruta1));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(puntoInteresRepository.findById(999L)).thenReturn(Optional.empty());

        // ✅ el map(dto, existing) se llama antes de fetchPuntos
        doNothing().when(modelMapper).map(any(RutaInDto.class), any(Ruta.class));

        assertThrows(PuntoInteresNotFoundException.class, () -> rutaService.modify(1L, in));

        verify(rutaRepository).findById(1L);
        verify(usuarioRepository).findById(10L);
        verify(modelMapper).map(any(RutaInDto.class), any(Ruta.class));
        verify(puntoInteresRepository).findById(999L);
        verify(rutaRepository, never()).save(any());
    }

    // -------------------- DELETE --------------------

    @Test
    void delete_shouldDelete_whenExists() throws Exception {
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(ruta1));

        rutaService.delete(1L);

        verify(rutaRepository).findById(1L);
        verify(rutaRepository).delete(ruta1);
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(rutaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RutaNotFoundException.class, () -> rutaService.delete(999L));

        verify(rutaRepository).findById(999L);
        verify(rutaRepository, never()).delete(any());
    }

    // -------------------- FIND ALL (FILTROS + COMPLETAR IDS) --------------------

    @Test
    void findAll_shouldFilterAndFillUsuarioIdAndPuntosIds() {
        when(rutaRepository.findAll()).thenReturn(List.of(ruta1, ruta2));

        when(modelMapper.map(anyList(), ArgumentMatchers.<Type>any()))
                .thenAnswer(inv -> {
                    List<?> input = inv.getArgument(0);
                    if (input.size() == 1) {
                        return List.of(new RutaOutDto(
                                1L, "facil", 5.5f, 60, ruta1.getFechaRealizacion(),
                                true, "Paseo por el parque", null, null
                        ));
                    }
                    // por si en algún caso entra con 2
                    return List.of(
                            new RutaOutDto(1L, "facil", 5.5f, 60, ruta1.getFechaRealizacion(), true, "Paseo por el parque", null, null),
                            new RutaOutDto(2L, "dificil", 12f, 180, ruta2.getFechaRealizacion(), false, "Subida al monte", null, null)
                    );
                });

        List<RutaOutDto> result = rutaService.findAll("fac", true, "parque");

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(10L, result.get(0).getUsuarioId());
        assertEquals(List.of(100L, 200L), result.get(0).getPuntosIds());

        verify(rutaRepository).findAll();
        verify(modelMapper).map(anyList(), ArgumentMatchers.<Type>any());
    }
}
