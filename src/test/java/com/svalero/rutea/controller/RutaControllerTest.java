package com.svalero.rutea.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.rutea.domain.Ruta;
import com.svalero.rutea.exception.ResourceNotFoundException;
import com.svalero.rutea.service.RutaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RutaController.class)
class RutaControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private RutaService rutaService;
    @Autowired private ObjectMapper objectMapper;

    // Caso 20X
    @Test
    void testGetById_Success() throws Exception {
        Ruta ruta = new Ruta();
        ruta.setId(1L);
        when(rutaService.findById(1L)).thenReturn(ruta);
        mockMvc.perform(get("/api/rutas/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // Caso 404
    @Test
    void testGetById_NotFound() throws Exception {
        when(rutaService.findById(99L)).thenThrow(new ResourceNotFoundException("No existe"));
        mockMvc.perform(get("/api/rutas/99").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // Caso 400 (Falta el campo obligatorio 'titulo')
    @Test
    void testAdd_BadRequest() throws Exception {
        Ruta rutaMala = new Ruta();
        rutaMala.setFechaRealizacion(LocalDate.now()); // Falta Título (obligatorio)
        
        mockMvc.perform(post("/api/rutas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rutaMala)))
                .andExpect(status().isBadRequest());
    }
}