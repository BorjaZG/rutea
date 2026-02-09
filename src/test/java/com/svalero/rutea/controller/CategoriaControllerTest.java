package com.svalero.rutea.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.rutea.dto.CategoriaInDto;
import com.svalero.rutea.dto.CategoriaOutDto;
import com.svalero.rutea.exception.CategoriaNotFoundException;
import com.svalero.rutea.exception.GlobalExceptionHandler;
import com.svalero.rutea.service.CategoriaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
@Import(GlobalExceptionHandler.class)
class CategoriaControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private CategoriaService categoriaService;

    // -------------------- GET ALL --------------------

    @Test
    void getAll_shouldReturn200() throws Exception {
        when(categoriaService.findAll(eq(true), eq("mon"), eq(1)))
                .thenReturn(List.of(CategoriaOutDto.builder().id(1L).nombre("Montaña").activa(true).ordenPrioridad(1).build()));

        mockMvc.perform(get("/categorias")
                        .param("activa", "true")
                        .param("nombre", "mon")
                        .param("ordenPrioridad", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getAll_shouldReturn400_whenBadQueryParam() throws Exception {
        mockMvc.perform(get("/categorias").param("ordenPrioridad", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    // -------------------- GET BY ID --------------------

    @Test
    void getById_shouldReturn200() throws Exception {
        when(categoriaService.findById(1L))
                .thenReturn(CategoriaOutDto.builder().id(1L).nombre("Montaña").build());

        mockMvc.perform(get("/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getById_shouldReturn404() throws Exception {
        when(categoriaService.findById(99L)).thenThrow(new CategoriaNotFoundException());

        mockMvc.perform(get("/categorias/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void getById_shouldReturn400_whenBadId() throws Exception {
        mockMvc.perform(get("/categorias/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    // -------------------- POST --------------------

    @Test
    void post_shouldReturn201() throws Exception {
        CategoriaInDto in = CategoriaInDto.builder()
                .activa(true)
                .costePromedio(0)
                .nombre("Montaña")
                .ordenPrioridad(1)
                .build();

        CategoriaOutDto out = CategoriaOutDto.builder()
                .id(1L)
                .activa(true)
                .costePromedio(0)
                .nombre("Montaña")
                .ordenPrioridad(1)
                .build();

        when(categoriaService.add(any(CategoriaInDto.class))).thenReturn(out);

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void post_shouldReturn400_whenInvalidBody() throws Exception {
        CategoriaInDto bad = CategoriaInDto.builder()
                .nombre("") // @NotBlank
                .ordenPrioridad(0)
                .costePromedio(0)
                .build();

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors.nombre").exists());
    }

    // -------------------- PUT --------------------

    @Test
    void put_shouldReturn200() throws Exception {
        CategoriaInDto in = CategoriaInDto.builder()
                .activa(true)
                .costePromedio(0)
                .nombre("X")
                .ordenPrioridad(1)
                .build();

        CategoriaOutDto out = CategoriaOutDto.builder()
                .id(1L)
                .activa(true)
                .costePromedio(0)
                .nombre("X")
                .ordenPrioridad(1)
                .build();

        when(categoriaService.modify(eq(1L), any(CategoriaInDto.class))).thenReturn(out);

        mockMvc.perform(put("/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void put_shouldReturn404() throws Exception {
        CategoriaInDto in = CategoriaInDto.builder()
                .activa(true).costePromedio(0).nombre("X").ordenPrioridad(1)
                .build();

        when(categoriaService.modify(eq(99L), any(CategoriaInDto.class))).thenThrow(new CategoriaNotFoundException());

        mockMvc.perform(put("/categorias/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void put_shouldReturn400_whenInvalidBody() throws Exception {
        CategoriaInDto bad = CategoriaInDto.builder()
                .nombre("") // @NotBlank
                .ordenPrioridad(0)
                .costePromedio(0)
                .build();

        mockMvc.perform(put("/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors.nombre").exists());
    }

    @Test
    void put_shouldReturn400_whenBadId() throws Exception {
        CategoriaInDto in = CategoriaInDto.builder()
                .activa(true).costePromedio(0).nombre("X").ordenPrioridad(1)
                .build();

        mockMvc.perform(put("/categorias/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    // -------------------- DELETE --------------------

    @Test
    void delete_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/categorias/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_shouldReturn404() throws Exception {
        doThrow(new CategoriaNotFoundException()).when(categoriaService).delete(99L);

        mockMvc.perform(delete("/categorias/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void delete_shouldReturn400_whenBadId() throws Exception {
        mockMvc.perform(delete("/categorias/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }
}
