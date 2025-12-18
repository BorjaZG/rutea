package com.svalero.rutea.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.rutea.domain.Resena;
import com.svalero.rutea.exception.ResourceNotFoundException;
import com.svalero.rutea.service.ResenaService;
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

@WebMvcTest(ResenaController.class)
class ResenaControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private ResenaService resenaService;
    @Autowired private ObjectMapper objectMapper;

    // Caso 20X
    @Test
    void testGetById_Success() throws Exception {
        Resena resena = new Resena();
        resena.setId(1L);
        when(resenaService.findById(1L)).thenReturn(resena);
        mockMvc.perform(get("/api/resenas/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // Caso 404
    @Test
    void testGetById_NotFound() throws Exception {
        when(resenaService.findById(99L)).thenThrow(new ResourceNotFoundException("No existe"));
        mockMvc.perform(get("/api/resenas/99").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // Caso 400 (Comentario muy corto - min 10 chars)
    @Test
    void testAdd_BadRequest() throws Exception {
        Resena resenaMala = new Resena();
        resenaMala.setComentario("Malo"); // Solo 4 chars
        resenaMala.setValoracion(1); 
        
        mockMvc.perform(post("/api/resenas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resenaMala)))
                .andExpect(status().isBadRequest());
    }
}