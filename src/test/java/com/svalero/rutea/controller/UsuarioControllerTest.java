package com.svalero.rutea.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalero.rutea.dto.UsuarioInDto;
import com.svalero.rutea.dto.UsuarioOutDto;
import com.svalero.rutea.exception.UsuarioNotFoundException;
import com.svalero.rutea.service.UsuarioService;
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

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private UsuarioService usuarioService;

    // -------------------- GET ALL --------------------

    @Test
    void getAll_shouldReturn200() throws Exception {
        when(usuarioService.findAll(eq(true), eq(3), eq("bor")))
                .thenReturn(List.of(new UsuarioOutDto(
                        1L, "a@a.com", true, LocalDate.now(), 3, "borja"
                )));

        mockMvc.perform(get("/usuarios")
                        .param("premium", "true")
                        .param("nivelExperiencia", "3")
                        .param("username", "bor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("borja"));
    }

    @Test
    void getAll_shouldReturn400_whenBadQueryParam() throws Exception {
        mockMvc.perform(get("/usuarios").param("nivelExperiencia", "abc"))
                .andExpect(status().isBadRequest());
        // Este 400 lo devuelve Spring por conversión de tipos (no tu ErrorResponse),
        // por eso NO comprobamos $.code aquí.
    }

    // -------------------- GET BY ID --------------------

    @Test
    void getById_shouldReturn200() throws Exception {
        when(usuarioService.findById(1L))
                .thenReturn(new UsuarioOutDto(1L, "a@a.com", false, LocalDate.now(), 0, "borja"));

        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("a@a.com"));
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {
        when(usuarioService.findById(99L)).thenThrow(new UsuarioNotFoundException());

        mockMvc.perform(get("/usuarios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void getById_shouldReturn400_whenBadId() throws Exception {
        mockMvc.perform(get("/usuarios/abc"))
                .andExpect(status().isBadRequest());
    }

    // -------------------- POST --------------------

    @Test
    void post_shouldReturn201() throws Exception {
        UsuarioInDto in = new UsuarioInDto(
                "a@a.com", true, LocalDate.now(), 2, "123456", "borja"
        );

        UsuarioOutDto out = new UsuarioOutDto(
                1L, "a@a.com", true, in.getFechaRegistro(), 2, "borja"
        );

        when(usuarioService.add(any(UsuarioInDto.class))).thenReturn(out);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("borja"));
    }

    @Test
    void post_shouldReturn400_whenInvalidBody() throws Exception {
        // inválido: email vacío, email no válido, fechaRegistro null, password corta, username vacío
        UsuarioInDto bad = new UsuarioInDto(
                "no-es-email", false, null, -1, "123", ""
        );

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.fechaRegistro").exists())
                .andExpect(jsonPath("$.errors.password").exists())
                .andExpect(jsonPath("$.errors.username").exists());
    }

    // -------------------- PUT --------------------

    @Test
    void put_shouldReturn200() throws Exception {
        UsuarioInDto in = new UsuarioInDto(
                "b@b.com", false, LocalDate.now(), 5, "123456", "newuser"
        );

        UsuarioOutDto out = new UsuarioOutDto(
                1L, "b@b.com", false, in.getFechaRegistro(), 5, "newuser"
        );

        when(usuarioService.modify(eq(1L), any(UsuarioInDto.class))).thenReturn(out);

        mockMvc.perform(put("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    void put_shouldReturn404_whenNotFound() throws Exception {
        UsuarioInDto in = new UsuarioInDto(
                "b@b.com", false, LocalDate.now(), 5, "123456", "newuser"
        );

        when(usuarioService.modify(eq(99L), any(UsuarioInDto.class))).thenThrow(new UsuarioNotFoundException());

        mockMvc.perform(put("/usuarios/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void put_shouldReturn400_whenInvalidBody() throws Exception {
        UsuarioInDto bad = new UsuarioInDto(
                "", false, null, -2, "1", ""
        );

        mockMvc.perform(put("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.fechaRegistro").exists())
                .andExpect(jsonPath("$.errors.password").exists())
                .andExpect(jsonPath("$.errors.username").exists());
    }

    // -------------------- DELETE --------------------

    @Test
    void delete_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_shouldReturn404_whenNotFound() throws Exception {
        doThrow(new UsuarioNotFoundException()).when(usuarioService).delete(99L);

        mockMvc.perform(delete("/usuarios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void delete_shouldReturn400_whenBadId() throws Exception {
        mockMvc.perform(delete("/usuarios/abc"))
                .andExpect(status().isBadRequest());
    }
}
