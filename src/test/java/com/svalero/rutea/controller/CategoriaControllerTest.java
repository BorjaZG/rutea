package com.svalero.rutea.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.rutea.domain.Categoria;
import com.svalero.rutea.exception.ResourceNotFoundException;
import com.svalero.rutea.service.CategoriaService;
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

@WebMvcTest(CategoriaController.class)
class CategoriaControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private CategoriaService categoriaService;
    @Autowired private ObjectMapper objectMapper;

    // Caso 20X
    @Test
    void testGetById_Success() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        when(categoriaService.findById(1L)).thenReturn(categoria);
        mockMvc.perform(get("/api/categorias/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // Caso 404
    @Test
    void testGetById_NotFound() throws Exception {
        when(categoriaService.findById(99L)).thenThrow(new ResourceNotFoundException("No existe"));
        mockMvc.perform(get("/api/categorias/99").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // Caso 400 (Falta el campo obligatorio 'nombre')
    @Test
    void testAdd_BadRequest() throws Exception {
        Categoria categoriaMala = new Categoria(); 
        categoriaMala.setNombre(""); // Obligatorio y no puede estar vacío
        
        mockMvc.perform(post("/api/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoriaMala)))
                .andExpect(status().isBadRequest());
    }
}