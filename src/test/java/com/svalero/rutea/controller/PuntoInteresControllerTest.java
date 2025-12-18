package com.svalero.rutea.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.rutea.domain.PuntoInteres;
import com.svalero.rutea.exception.ResourceNotFoundException;
import com.svalero.rutea.service.PuntoInteresService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PuntoInteresController.class)
class PuntoInteresControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private PuntoInteresService puntoService;
    @Autowired private ObjectMapper objectMapper;

    // Caso 20X
    @Test
    void testGetById_Success() throws Exception {
        PuntoInteres punto = new PuntoInteres();
        punto.setId(1L);
        when(puntoService.findById(1L)).thenReturn(punto);
        mockMvc.perform(get("/api/puntos/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // Caso 404
    @Test
    void testGetById_NotFound() throws Exception {
        when(puntoService.findById(99L)).thenThrow(new ResourceNotFoundException("No existe"));
        mockMvc.perform(get("/api/puntos/99").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // Caso 400 (Falta el campo obligatorio 'latitud')
    @Test
    void testAdd_BadRequest() throws Exception {
        PuntoInteres puntoMalo = new PuntoInteres();
        puntoMalo.setNombre("Parque");
        puntoMalo.setLongitud(1.0); // Falta Latitud (requerido con @NotNull)
        
        mockMvc.perform(post("/api/puntos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(puntoMalo)))
                .andExpect(status().isBadRequest());
    }
}