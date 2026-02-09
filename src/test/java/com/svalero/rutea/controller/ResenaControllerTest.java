package com.svalero.rutea.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.rutea.dto.ResenaInDto;
import com.svalero.rutea.dto.ResenaOutDto;
import com.svalero.rutea.exception.PuntoInteresNotFoundException;
import com.svalero.rutea.exception.ResenaNotFoundException;
import com.svalero.rutea.exception.UsuarioNotFoundException;
import com.svalero.rutea.service.ResenaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ResenaController.class)
class ResenaControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private ResenaService resenaService;

    // -------------------- GET ALL --------------------

    @Test
    void getAll_shouldReturn200() throws Exception {
        when(resenaService.findAll(eq(true), eq(10), eq(5)))
                .thenReturn(List.of(new ResenaOutDto(
                        1L, "Muy bien", true, LocalDate.now(), 10, "Genial", 5, 2L, 3L
                )));

        mockMvc.perform(get("/resenas")
                        .param("editada", "true")
                        .param("likes", "10")
                        .param("valoracion", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].valoracion").value(5));
    }

    @Test
    void getAll_shouldReturn400_whenBadQueryParam() throws Exception {
        mockMvc.perform(get("/resenas").param("likes", "abc"))
                .andExpect(status().isBadRequest());
        // Nota: este 400 lo produce Spring por conversión de tipo, no tus @ExceptionHandler,
        // por eso NO comprobamos $.code aquí.
    }

    // -------------------- GET BY ID --------------------

    @Test
    void getById_shouldReturn200() throws Exception {
        when(resenaService.findById(1L))
                .thenReturn(new ResenaOutDto(
                        1L, "Comentario", false, LocalDate.now(), 0, null, 4, 10L, 20L
                ));

        mockMvc.perform(get("/resenas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.valoracion").value(4));
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {
        when(resenaService.findById(99L)).thenThrow(new ResenaNotFoundException());

        mockMvc.perform(get("/resenas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void getById_shouldReturn400_whenBadId() throws Exception {
        mockMvc.perform(get("/resenas/abc"))
                .andExpect(status().isBadRequest());
    }

    // -------------------- POST --------------------

    @Test
    void post_shouldReturn201() throws Exception {
        ResenaInDto in = new ResenaInDto(
                "Muy buena", false, LocalDate.now(), 0, "Título", 5, 10L, 20L
        );

        ResenaOutDto out = new ResenaOutDto(
                1L, "Muy buena", false, in.getFechaPublicacion(), 0, "Título", 5, 10L, 20L
        );

        when(resenaService.add(any(ResenaInDto.class))).thenReturn(out);

        mockMvc.perform(post("/resenas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.comentario").value("Muy buena"));
    }

    @Test
    void post_shouldReturn400_whenInvalidBody() throws Exception {
        // inválido: comentario blank, valoracion fuera de rango, puntoId/usuarioId null
        ResenaInDto bad = new ResenaInDto(
                "", false, null, -1, null, 6, null, null
        );

        mockMvc.perform(post("/resenas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors.comentario").exists())
                .andExpect(jsonPath("$.errors.puntoId").exists())
                .andExpect(jsonPath("$.errors.usuarioId").exists());
    }

    @Test
    void post_shouldReturn404_whenPuntoNotFound() throws Exception {
        ResenaInDto in = new ResenaInDto(
                "Ok", false, null, 0, null, 4, 999L, 20L
        );

        when(resenaService.add(any(ResenaInDto.class))).thenThrow(new PuntoInteresNotFoundException());

        mockMvc.perform(post("/resenas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void post_shouldReturn404_whenUsuarioNotFound() throws Exception {
        ResenaInDto in = new ResenaInDto(
                "Ok", false, null, 0, null, 4, 10L, 999L
        );

        when(resenaService.add(any(ResenaInDto.class))).thenThrow(new UsuarioNotFoundException());

        mockMvc.perform(post("/resenas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    // -------------------- PUT --------------------

    @Test
    void put_shouldReturn200() throws Exception {
        ResenaInDto in = new ResenaInDto(
                "Editada", true, LocalDate.now(), 5, "Nuevo", 3, 10L, 20L
        );

        ResenaOutDto out = new ResenaOutDto(
                1L, "Editada", true, in.getFechaPublicacion(), 5, "Nuevo", 3, 10L, 20L
        );

        when(resenaService.modify(eq(1L), any(ResenaInDto.class))).thenReturn(out);

        mockMvc.perform(put("/resenas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.editada").value(true));
    }

    @Test
    void put_shouldReturn404_whenResenaNotFound() throws Exception {
        ResenaInDto in = new ResenaInDto(
                "X", false, null, 0, null, 4, 10L, 20L
        );

        when(resenaService.modify(eq(99L), any(ResenaInDto.class))).thenThrow(new ResenaNotFoundException());

        mockMvc.perform(put("/resenas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void put_shouldReturn404_whenPuntoNotFound() throws Exception {
        ResenaInDto in = new ResenaInDto(
                "X", false, null, 0, null, 4, 999L, 20L
        );

        when(resenaService.modify(eq(1L), any(ResenaInDto.class))).thenThrow(new PuntoInteresNotFoundException());

        mockMvc.perform(put("/resenas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void put_shouldReturn404_whenUsuarioNotFound() throws Exception {
        ResenaInDto in = new ResenaInDto(
                "X", false, null, 0, null, 4, 10L, 999L
        );

        when(resenaService.modify(eq(1L), any(ResenaInDto.class))).thenThrow(new UsuarioNotFoundException());

        mockMvc.perform(put("/resenas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void put_shouldReturn400_whenInvalidBody() throws Exception {
        ResenaInDto bad = new ResenaInDto(
                "", false, null, -5, "t", 0, null, null
        );

        mockMvc.perform(put("/resenas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors.comentario").exists())
                .andExpect(jsonPath("$.errors.puntoId").exists())
                .andExpect(jsonPath("$.errors.usuarioId").exists());
    }

    // -------------------- DELETE --------------------

    @Test
    void delete_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/resenas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_shouldReturn404_whenNotFound() throws Exception {
        doThrow(new ResenaNotFoundException()).when(resenaService).delete(99L);

        mockMvc.perform(delete("/resenas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void delete_shouldReturn400_whenBadId() throws Exception {
        mockMvc.perform(delete("/resenas/abc"))
                .andExpect(status().isBadRequest());
    }
}
