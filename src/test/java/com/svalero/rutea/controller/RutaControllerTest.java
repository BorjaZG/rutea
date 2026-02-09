package com.svalero.rutea.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.rutea.dto.RutaInDto;
import com.svalero.rutea.dto.RutaOutDto;
import com.svalero.rutea.exception.PuntoInteresNotFoundException;
import com.svalero.rutea.exception.RutaNotFoundException;
import com.svalero.rutea.exception.UsuarioNotFoundException;
import com.svalero.rutea.service.RutaService;
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

@WebMvcTest(RutaController.class)
class RutaControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private RutaService rutaService;

    // -------------------- GET ALL --------------------

    @Test
    void getAll_shouldReturn200() throws Exception {
        when(rutaService.findAll(eq("facil"), eq(true), eq("paseo")))
                .thenReturn(List.of(new RutaOutDto(
                        1L, "facil", 5.5f, 60, LocalDate.now(), true, "Paseo por el parque", 10L, List.of(100L, 200L)
                )));

        mockMvc.perform(get("/rutas")
                        .param("dificultad", "facil")
                        .param("publica", "true")
                        .param("titulo", "paseo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].publica").value(true))
                .andExpect(jsonPath("$[0].usuarioId").value(10));
    }

    @Test
    void getAll_shouldReturn400_whenBadQueryParam() throws Exception {
        mockMvc.perform(get("/rutas").param("publica", "abc"))
                .andExpect(status().isBadRequest());
        // Este 400 lo genera Spring por conversión de tipos (no tus @ExceptionHandler),
        // por eso NO comprobamos $.code aquí.
    }

    // -------------------- GET BY ID --------------------

    @Test
    void getById_shouldReturn200() throws Exception {
        when(rutaService.findById(1L))
                .thenReturn(new RutaOutDto(
                        1L, "media", 10f, 120, LocalDate.now(), false, "Ruta bonita", 10L, List.of()
                ));

        mockMvc.perform(get("/rutas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Ruta bonita"));
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {
        when(rutaService.findById(99L)).thenThrow(new RutaNotFoundException());

        mockMvc.perform(get("/rutas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void getById_shouldReturn400_whenBadId() throws Exception {
        mockMvc.perform(get("/rutas/abc"))
                .andExpect(status().isBadRequest());
    }

    // -------------------- POST --------------------

    @Test
    void post_shouldReturn201() throws Exception {
        RutaInDto in = new RutaInDto(
                "facil",
                5.5f,
                60,
                LocalDate.now(),
                true,
                "Paseo por el parque",
                10L,
                List.of(100L, 200L)
        );

        RutaOutDto out = new RutaOutDto(
                1L, "facil", 5.5f, 60, in.getFechaRealizacion(), true, "Paseo por el parque", 10L, List.of(100L, 200L)
        );

        when(rutaService.add(any(RutaInDto.class))).thenReturn(out);

        mockMvc.perform(post("/rutas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Paseo por el parque"));
    }

    @Test
    void post_shouldReturn400_whenInvalidBody() throws Exception {
        // inválido: titulo blank, fechaRealizacion null, usuarioId null, distancia negativa, duracion negativa...
        RutaInDto bad = new RutaInDto(
                "x",
                -1f,
                -5,
                null,
                true,
                "",
                null,
                List.of()
        );

        mockMvc.perform(post("/rutas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors.titulo").exists())
                .andExpect(jsonPath("$.errors.fechaRealizacion").exists())
                .andExpect(jsonPath("$.errors.usuarioId").exists());
    }

    @Test
    void post_shouldReturn404_whenUsuarioNotFound() throws Exception {
        RutaInDto in = new RutaInDto(
                "facil", 1f, 10, LocalDate.now(), true, "Ruta", 999L, List.of()
        );

        when(rutaService.add(any(RutaInDto.class))).thenThrow(new UsuarioNotFoundException());

        mockMvc.perform(post("/rutas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void post_shouldReturn404_whenPuntoInteresNotFound() throws Exception {
        RutaInDto in = new RutaInDto(
                "facil", 1f, 10, LocalDate.now(), true, "Ruta", 10L, List.of(999L)
        );

        when(rutaService.add(any(RutaInDto.class))).thenThrow(new PuntoInteresNotFoundException());

        mockMvc.perform(post("/rutas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    // -------------------- PUT --------------------

    @Test
    void put_shouldReturn200() throws Exception {
        RutaInDto in = new RutaInDto(
                "dificil", 12f, 180, LocalDate.now(), false, "Ruta nueva", 10L, List.of(100L)
        );

        RutaOutDto out = new RutaOutDto(
                1L, "dificil", 12f, 180, in.getFechaRealizacion(), false, "Ruta nueva", 10L, List.of(100L)
        );

        when(rutaService.modify(eq(1L), any(RutaInDto.class))).thenReturn(out);

        mockMvc.perform(put("/rutas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.dificultad").value("dificil"));
    }

    @Test
    void put_shouldReturn404_whenRutaNotFound() throws Exception {
        RutaInDto in = new RutaInDto(
                "x", 1f, 1, LocalDate.now(), true, "Ruta", 10L, List.of()
        );

        when(rutaService.modify(eq(99L), any(RutaInDto.class))).thenThrow(new RutaNotFoundException());

        mockMvc.perform(put("/rutas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void put_shouldReturn404_whenUsuarioNotFound() throws Exception {
        RutaInDto in = new RutaInDto(
                "x", 1f, 1, LocalDate.now(), true, "Ruta", 999L, List.of()
        );

        when(rutaService.modify(eq(1L), any(RutaInDto.class))).thenThrow(new UsuarioNotFoundException());

        mockMvc.perform(put("/rutas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void put_shouldReturn404_whenPuntoInteresNotFound() throws Exception {
        RutaInDto in = new RutaInDto(
                "x", 1f, 1, LocalDate.now(), true, "Ruta", 10L, List.of(999L)
        );

        when(rutaService.modify(eq(1L), any(RutaInDto.class))).thenThrow(new PuntoInteresNotFoundException());

        mockMvc.perform(put("/rutas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void put_shouldReturn400_whenInvalidBody() throws Exception {
        RutaInDto bad = new RutaInDto(
                null, -1f, -1, null, true, "", null, List.of()
        );

        mockMvc.perform(put("/rutas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors.titulo").exists())
                .andExpect(jsonPath("$.errors.fechaRealizacion").exists())
                .andExpect(jsonPath("$.errors.usuarioId").exists());
    }

    // -------------------- DELETE --------------------

    @Test
    void delete_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/rutas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_shouldReturn404_whenNotFound() throws Exception {
        doThrow(new RutaNotFoundException()).when(rutaService).delete(99L);

        mockMvc.perform(delete("/rutas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void delete_shouldReturn400_whenBadId() throws Exception {
        mockMvc.perform(delete("/rutas/abc"))
                .andExpect(status().isBadRequest());
    }
}
