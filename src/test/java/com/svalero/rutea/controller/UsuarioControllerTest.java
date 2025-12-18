package com.svalero.rutea.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.rutea.domain.Usuario;
import com.svalero.rutea.exception.ResourceNotFoundException;
import com.svalero.rutea.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class) // Solo cargamos el controlador de Usuario
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc; // Simula las peticiones HTTP (Postman)

    @MockBean
    private UsuarioService usuarioService; // Simulamos el servicio

    @Autowired
    private ObjectMapper objectMapper; // Convierte objetos Java a JSON

    // 1. TEST CASO ÉXITO (200 OK)
    @Test
    void testGetUsuarioById_Success() throws Exception {
        // GIVEN: El servicio devuelve un usuario correcto
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("turistaCheck");
        
        when(usuarioService.findById(1L)).thenReturn(usuario);

        // WHEN & THEN: Llamamos a la API y esperamos un 200 OK y el nombre correcto
        mockMvc.perform(get("/api/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("turistaCheck"));
    }

    // 2. TEST CASO NO ENCONTRADO (404 NOT FOUND)
    @Test
    void testGetUsuarioById_NotFound() throws Exception {
        // GIVEN: El servicio lanza la excepción de "No encontrado"
        when(usuarioService.findById(99L)).thenThrow(new ResourceNotFoundException("Usuario no existe"));

        // WHEN & THEN: Esperamos un código 404
        mockMvc.perform(get("/api/usuarios/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // 3. TEST CASO DATOS INVÁLIDOS (400 BAD REQUEST)
    @Test
    void testCreateUsuario_BadRequest() throws Exception {
        // GIVEN: Un usuario sin email y con nombre muy corto (inválido)
        Usuario usuarioMalo = new Usuario();
        usuarioMalo.setUsername("yo"); // Mal: min 4 letras
        // Mal: falta email
        
        // WHEN & THEN: Al enviarlo, esperamos que salte la validación (400)
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioMalo)))
                .andExpect(status().isBadRequest());
    }
}