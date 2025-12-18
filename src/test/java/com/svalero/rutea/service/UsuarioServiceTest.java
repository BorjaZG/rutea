package com.svalero.rutea.service;

import com.svalero.rutea.domain.Usuario;
import com.svalero.rutea.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository; // Simulamos el repo

    @InjectMocks
    private UsuarioService usuarioService; // El servicio usará el repo simulado

    private Usuario usuarioPrueba;

    @BeforeEach
    void setUp() {
        // Preparamos un usuario de prueba antes de cada test
        usuarioPrueba = new Usuario();
        usuarioPrueba.setId(1L);
        usuarioPrueba.setUsername("tester");
        usuarioPrueba.setEmail("test@test.com");
        usuarioPrueba.setPassword("1234");
        usuarioPrueba.setFechaRegistro(LocalDate.now());
    }

    @Test
    void testFindById() {
        // 1. GIVEN (Dado que...)
        // Le decimos al mock: "Cuando te pidan el ID 1, devuelve el usuarioPrueba"
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPrueba));

        // 2. WHEN (Cuando...)
        Usuario resultado = usuarioService.findById(1L);

        // 3. THEN (Entonces...)
        assertNotNull(resultado);
        assertEquals("tester", resultado.getUsername());
        verify(usuarioRepository, times(1)).findById(1L); // Verificamos que se llamó al repo
    }

    @Test
    void testSave() {
        // 1. GIVEN
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioPrueba);

        // 2. WHEN
        Usuario resultado = usuarioService.save(usuarioPrueba);

        // 3. THEN
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }
}