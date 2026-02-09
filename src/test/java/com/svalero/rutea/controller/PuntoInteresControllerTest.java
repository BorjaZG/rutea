package com.svalero.rutea.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.rutea.dto.PuntoInteresInDto;
import com.svalero.rutea.dto.PuntoInteresOutDto;
import com.svalero.rutea.exception.CategoriaNotFoundException;
import com.svalero.rutea.exception.PuntoInteresNotFoundException;
import com.svalero.rutea.service.PuntoInteresService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PuntoInteresController.class)
class PuntoInteresControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private PuntoInteresService puntoInteresService;

    // -------------------- GET ALL --------------------

    @Test
    void getAll_shouldReturn200_withNewParam_abiertoActualmente() throws Exception {
        when(puntoInteresService.findAll(eq(1L), eq(true), eq("par"), eq(4.5f)))
                .thenReturn(List.of(new PuntoInteresOutDto(
                        10L, true, LocalDateTime.now(), 41.0, -0.8, "Parque", 4.5f, 1L
                )));

        mockMvc.perform(get("/puntos")
                        .param("categoriaId", "1")
                        .param("abiertoActualmente", "true")
                        .param("nombre", "par")
                        .param("puntuacionMedia", "4.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].categoriaId").value(1))
                .andExpect(jsonPath("$[0].abiertoActualmente").value(true));
    }

    @Test
    void getAll_shouldReturn200_usingLegacyParam_abierto_whenAbiertoActualmenteMissing() throws Exception {
        // Si no viene abiertoActualmente, el controller usa "abierto"
        when(puntoInteresService.findAll(isNull(), eq(false), eq(""), isNull()))
                .thenReturn(List.of(new PuntoInteresOutDto(
                        1L, false, null, 0, 0, "X", 0f, null
                )));

        mockMvc.perform(get("/puntos")
                        .param("abierto", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].abiertoActualmente").value(false));
    }

    @Test
    void getAll_shouldReturn400_whenBadQueryParam() throws Exception {
        mockMvc.perform(get("/puntos").param("puntuacionMedia", "abc"))
                .andExpect(status().isBadRequest());
        // Aquí Spring devuelve 400 por conversión de tipos (no entra en tus @ExceptionHandler)
        // Si quieres un cuerpo unificado, habría que manejar MethodArgumentTypeMismatchException.
    }

    // -------------------- GET BY ID --------------------

    @Test
    void getById_shouldReturn200() throws Exception {
        when(puntoInteresService.findById(1L))
                .thenReturn(new PuntoInteresOutDto(
                        1L, true, null, 41.0, -0.8, "Parque", 4.2f, 3L
                ));

        mockMvc.perform(get("/puntos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Parque"));
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {
        when(puntoInteresService.findById(99L)).thenThrow(new PuntoInteresNotFoundException());

        mockMvc.perform(get("/puntos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void getById_shouldReturn400_whenBadId() throws Exception {
        mockMvc.perform(get("/puntos/abc"))
                .andExpect(status().isBadRequest());
        // Igual que antes: este 400 es de Spring por type mismatch (no tu ErrorResponse)
    }

    // -------------------- POST --------------------

    @Test
    void post_shouldReturn201() throws Exception {
        PuntoInteresInDto in = new PuntoInteresInDto(
                true,
                LocalDateTime.now(),
                41.0,
                -0.8,
                "Parque",
                4.5f,
                1L
        );

        PuntoInteresOutDto out = new PuntoInteresOutDto(
                10L, true, in.getFechaCreacion(), 41.0, -0.8, "Parque", 4.5f, 1L
        );

        when(puntoInteresService.add(any(PuntoInteresInDto.class))).thenReturn(out);

        mockMvc.perform(post("/puntos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("Parque"));
    }

    @Test
    void post_shouldReturn400_whenInvalidBody() throws Exception {
        // inválido: nombre blank, categoriaId null, latitud fuera rango...
        PuntoInteresInDto bad = new PuntoInteresInDto(
                true, null, 200.0, -999.0, "", 6.0f, null
        );

        mockMvc.perform(post("/puntos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors.nombre").exists())
                .andExpect(jsonPath("$.errors.categoriaId").exists());
    }

    @Test
    void post_shouldReturn404_whenCategoriaNotFound() throws Exception {
        PuntoInteresInDto in = new PuntoInteresInDto(
                true, null, 41.0, -0.8, "Parque", 4.0f, 999L
        );

        when(puntoInteresService.add(any(PuntoInteresInDto.class))).thenThrow(new CategoriaNotFoundException());

        mockMvc.perform(post("/puntos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    // -------------------- PUT --------------------

    @Test
    void put_shouldReturn200() throws Exception {
        PuntoInteresInDto in = new PuntoInteresInDto(
                false, null, 40.0, -0.7, "Nuevo", 3.5f, 2L
        );

        PuntoInteresOutDto out = new PuntoInteresOutDto(
                1L, false, null, 40.0, -0.7, "Nuevo", 3.5f, 2L
        );

        when(puntoInteresService.modify(eq(1L), any(PuntoInteresInDto.class))).thenReturn(out);

        mockMvc.perform(put("/puntos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Nuevo"));
    }

    @Test
    void put_shouldReturn404_whenPuntoNotFound() throws Exception {
        PuntoInteresInDto in = new PuntoInteresInDto(
                true, null, 41.0, -0.8, "Parque", 4.0f, 1L
        );

        when(puntoInteresService.modify(eq(99L), any(PuntoInteresInDto.class)))
                .thenThrow(new PuntoInteresNotFoundException());

        mockMvc.perform(put("/puntos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void put_shouldReturn404_whenCategoriaNotFound() throws Exception {
        PuntoInteresInDto in = new PuntoInteresInDto(
                true, null, 41.0, -0.8, "Parque", 4.0f, 999L
        );

        when(puntoInteresService.modify(eq(1L), any(PuntoInteresInDto.class)))
                .thenThrow(new CategoriaNotFoundException());

        mockMvc.perform(put("/puntos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void put_shouldReturn400_whenInvalidBody() throws Exception {
        PuntoInteresInDto bad = new PuntoInteresInDto(
                true, null, 1000.0, 0.0, "", -1.0f, null
        );

        mockMvc.perform(put("/puntos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors.nombre").exists())
                .andExpect(jsonPath("$.errors.categoriaId").exists());
    }

    // -------------------- DELETE --------------------

    @Test
    void delete_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/puntos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_shouldReturn404_whenNotFound() throws Exception {
        doThrow(new PuntoInteresNotFoundException()).when(puntoInteresService).delete(99L);

        mockMvc.perform(delete("/puntos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void delete_shouldReturn400_whenBadId() throws Exception {
        mockMvc.perform(delete("/puntos/abc"))
                .andExpect(status().isBadRequest());
    }
}
