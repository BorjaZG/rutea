package com.svalero.rutea.service;

import com.svalero.rutea.domain.Usuario;
import com.svalero.rutea.dto.UsuarioInDto;
import com.svalero.rutea.dto.UsuarioOutDto;
import com.svalero.rutea.exception.UsuarioNotFoundException;
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
class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks private UsuarioService usuarioService;

    private Usuario u1;
    private Usuario u2;

    @BeforeEach
    void setUp() {
        u1 = Usuario.builder()
                .id(1L)
                .email("a@a.com")
                .esPremium(true)
                .fechaRegistro(LocalDate.now())
                .nivelExperiencia(3)
                .password("123456")
                .username("borja")
                .build();

        u2 = Usuario.builder()
                .id(2L)
                .email("b@b.com")
                .esPremium(false)
                .fechaRegistro(LocalDate.now())
                .nivelExperiencia(0)
                .password("123456")
                .username("ana")
                .build();
    }

    // -------------------- ADD --------------------

    @Test
    void add_shouldReturnOutDto() {
        UsuarioInDto in = new UsuarioInDto(
                "a@a.com", true, LocalDate.now(), 3, "123456", "borja"
        );

        Usuario mapped = Usuario.builder().build();
        when(modelMapper.map(in, Usuario.class)).thenReturn(mapped);

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(u1);

        UsuarioOutDto outMapped = new UsuarioOutDto(
                1L, "a@a.com", true, u1.getFechaRegistro(), 3, "borja"
        );
        when(modelMapper.map(u1, UsuarioOutDto.class)).thenReturn(outMapped);

        UsuarioOutDto out = usuarioService.add(in);

        assertNotNull(out);
        assertEquals(1L, out.getId());
        assertEquals("borja", out.getUsername());

        verify(modelMapper).map(in, Usuario.class);
        verify(usuarioRepository).save(any(Usuario.class));
        verify(modelMapper).map(u1, UsuarioOutDto.class);
    }

    // -------------------- FIND ALL (FILTROS REPO) --------------------

    @Test
    void findAll_shouldUse3Filters_whenPremiumNivelUsernamePresent() {
        when(usuarioRepository.findByEsPremiumAndNivelExperienciaAndUsernameContainingIgnoreCase(true, 3, "bor"))
                .thenReturn(List.of(u1));

        when(modelMapper.map(anyList(), ArgumentMatchers.<Type>any()))
                .thenReturn(List.of(new UsuarioOutDto(1L, "a@a.com", true, u1.getFechaRegistro(), 3, "borja")));

        List<UsuarioOutDto> result = usuarioService.findAll(true, 3, "bor");

        assertEquals(1, result.size());
        verify(usuarioRepository).findByEsPremiumAndNivelExperienciaAndUsernameContainingIgnoreCase(true, 3, "bor");
    }

    @Test
    void findAll_shouldUse2Filters_whenPremiumAndNivelPresent() {
        when(usuarioRepository.findByEsPremiumAndNivelExperiencia(true, 3)).thenReturn(List.of(u1));
        when(modelMapper.map(anyList(), ArgumentMatchers.<Type>any()))
                .thenReturn(List.of(new UsuarioOutDto(1L, "a@a.com", true, u1.getFechaRegistro(), 3, "borja")));

        List<UsuarioOutDto> result = usuarioService.findAll(true, 3, "");

        assertEquals(1, result.size());
        verify(usuarioRepository).findByEsPremiumAndNivelExperiencia(true, 3);
    }

    @Test
    void findAll_shouldUse2Filters_whenPremiumAndUsernamePresent() {
        when(usuarioRepository.findByEsPremiumAndUsernameContainingIgnoreCase(true, "bor")).thenReturn(List.of(u1));
        when(modelMapper.map(anyList(), ArgumentMatchers.<Type>any()))
                .thenReturn(List.of(new UsuarioOutDto(1L, "a@a.com", true, u1.getFechaRegistro(), 3, "borja")));

        List<UsuarioOutDto> result = usuarioService.findAll(true, null, "bor");

        assertEquals(1, result.size());
        verify(usuarioRepository).findByEsPremiumAndUsernameContainingIgnoreCase(true, "bor");
    }

    @Test
    void findAll_shouldUse2Filters_whenNivelAndUsernamePresent() {
        when(usuarioRepository.findByNivelExperienciaAndUsernameContainingIgnoreCase(3, "bor")).thenReturn(List.of(u1));
        when(modelMapper.map(anyList(), ArgumentMatchers.<Type>any()))
                .thenReturn(List.of(new UsuarioOutDto(1L, "a@a.com", true, u1.getFechaRegistro(), 3, "borja")));

        List<UsuarioOutDto> result = usuarioService.findAll(null, 3, "bor");

        assertEquals(1, result.size());
        verify(usuarioRepository).findByNivelExperienciaAndUsernameContainingIgnoreCase(3, "bor");
    }

    @Test
    void findAll_shouldUse1Filter_whenOnlyPremiumPresent() {
        when(usuarioRepository.findByEsPremium(true)).thenReturn(List.of(u1));
        when(modelMapper.map(anyList(), ArgumentMatchers.<Type>any()))
                .thenReturn(List.of(new UsuarioOutDto(1L, "a@a.com", true, u1.getFechaRegistro(), 3, "borja")));

        List<UsuarioOutDto> result = usuarioService.findAll(true, null, "");

        assertEquals(1, result.size());
        verify(usuarioRepository).findByEsPremium(true);
    }

    @Test
    void findAll_shouldUse1Filter_whenOnlyNivelPresent() {
        when(usuarioRepository.findByNivelExperiencia(0)).thenReturn(List.of(u2));
        when(modelMapper.map(anyList(), ArgumentMatchers.<Type>any()))
                .thenReturn(List.of(new UsuarioOutDto(2L, "b@b.com", false, u2.getFechaRegistro(), 0, "ana")));

        List<UsuarioOutDto> result = usuarioService.findAll(null, 0, "");

        assertEquals(1, result.size());
        verify(usuarioRepository).findByNivelExperiencia(0);
    }

    @Test
    void findAll_shouldUse1Filter_whenOnlyUsernamePresent() {
        when(usuarioRepository.findByUsernameContainingIgnoreCase("an")).thenReturn(List.of(u2));
        when(modelMapper.map(anyList(), ArgumentMatchers.<Type>any()))
                .thenReturn(List.of(new UsuarioOutDto(2L, "b@b.com", false, u2.getFechaRegistro(), 0, "ana")));

        List<UsuarioOutDto> result = usuarioService.findAll(null, null, "an");

        assertEquals(1, result.size());
        verify(usuarioRepository).findByUsernameContainingIgnoreCase("an");
    }

    @Test
    void findAll_shouldReturnAll_whenNoFilters() {
        when(usuarioRepository.findAll()).thenReturn(List.of(u1, u2));

        when(modelMapper.map(anyList(), ArgumentMatchers.<Type>any()))
                .thenReturn(List.of(
                        new UsuarioOutDto(1L, "a@a.com", true, u1.getFechaRegistro(), 3, "borja"),
                        new UsuarioOutDto(2L, "b@b.com", false, u2.getFechaRegistro(), 0, "ana")
                ));

        List<UsuarioOutDto> result = usuarioService.findAll(null, null, "");

        assertEquals(2, result.size());
        verify(usuarioRepository).findAll();
    }

    // -------------------- FIND BY ID --------------------

    @Test
    void findById_shouldReturnOutDto_whenExists() throws Exception {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u1));

        UsuarioOutDto mappedOut = new UsuarioOutDto(1L, "a@a.com", true, u1.getFechaRegistro(), 3, "borja");
        when(modelMapper.map(u1, UsuarioOutDto.class)).thenReturn(mappedOut);

        UsuarioOutDto out = usuarioService.findById(1L);

        assertEquals(1L, out.getId());
        assertEquals("borja", out.getUsername());

        verify(usuarioRepository).findById(1L);
        verify(modelMapper).map(u1, UsuarioOutDto.class);
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> usuarioService.findById(99L));

        verify(usuarioRepository).findById(99L);
        verifyNoInteractions(modelMapper);
    }

    // -------------------- MODIFY --------------------

    @Test
    void modify_shouldReturnUpdatedOutDto_whenExists() throws Exception {
        UsuarioInDto in = new UsuarioInDto(
                "new@new.com", false, LocalDate.now(), 5, "123456", "newuser"
        );

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u1));

        doAnswer(inv -> {
            UsuarioInDto src = inv.getArgument(0);
            Usuario dest = inv.getArgument(1);
            dest.setEmail(src.getEmail());
            dest.setEsPremium(src.isEsPremium());
            dest.setFechaRegistro(src.getFechaRegistro());
            dest.setNivelExperiencia(src.getNivelExperiencia());
            dest.setPassword(src.getPassword());
            dest.setUsername(src.getUsername());
            return null;
        }).when(modelMapper).map(any(UsuarioInDto.class), any(Usuario.class));

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioOutDto mappedOut = new UsuarioOutDto(1L, "new@new.com", false, in.getFechaRegistro(), 5, "newuser");
        when(modelMapper.map(any(Usuario.class), eq(UsuarioOutDto.class))).thenReturn(mappedOut);

        UsuarioOutDto out = usuarioService.modify(1L, in);

        assertEquals(1L, out.getId());
        assertEquals("newuser", out.getUsername());
        assertEquals("new@new.com", out.getEmail());

        verify(usuarioRepository).findById(1L);
        verify(modelMapper).map(any(UsuarioInDto.class), any(Usuario.class));
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void modify_shouldThrow_whenNotFound() {
        UsuarioInDto in = new UsuarioInDto(
                "x@x.com", false, LocalDate.now(), 0, "123456", "x"
        );

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> usuarioService.modify(99L, in));

        verify(usuarioRepository).findById(99L);
        verify(usuarioRepository, never()).save(any());
        verify(modelMapper, never()).map(any(), any(Usuario.class));
    }

    // -------------------- DELETE --------------------

    @Test
    void delete_shouldDelete_whenExists() throws Exception {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u1));

        usuarioService.delete(1L);

        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).delete(u1);
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () -> usuarioService.delete(99L));

        verify(usuarioRepository).findById(99L);
        verify(usuarioRepository, never()).delete(any());
    }
}
