package com.svalero.rutea.controller;

import com.svalero.rutea.dto.RutaInDto;
import com.svalero.rutea.dto.RutaOutDto;
import com.svalero.rutea.exception.PuntoInteresNotFoundException;
import com.svalero.rutea.exception.RutaNotFoundException;
import com.svalero.rutea.exception.UsuarioNotFoundException;
import com.svalero.rutea.service.RutaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class RutaController {

    private static final Logger logger = LoggerFactory.getLogger(RutaController.class);

    @Autowired
    private RutaService rutaService;

    @GetMapping("/v1/rutas")
    public ResponseEntity<List<RutaOutDto>> getAll(
            @RequestParam(required = false, defaultValue = "") String dificultad,
            @RequestParam(required = false) Boolean publica,
            @RequestParam(required = false, defaultValue = "") String titulo
    ) {
        logger.debug("GET /v1/rutas - Filtros: dificultad={}, publica={}, titulo={}",
                dificultad, publica, titulo);
        return ResponseEntity.ok(rutaService.findAll(dificultad, publica, titulo));
    }

    @GetMapping("/v1/rutas/{id}")
    public ResponseEntity<RutaOutDto> get(@PathVariable long id) throws RutaNotFoundException {
        logger.debug("GET /v1/rutas/{}", id);
        return ResponseEntity.ok(rutaService.findById(id));
    }

    @PostMapping("/v1/rutas")
    public ResponseEntity<RutaOutDto> add(@Valid @RequestBody RutaInDto dto)
            throws UsuarioNotFoundException, PuntoInteresNotFoundException {
        logger.debug("POST /v1/rutas - {}", dto.getTitulo());
        return new ResponseEntity<>(rutaService.add(dto), HttpStatus.CREATED);
    }

    @PutMapping("/v1/rutas/{id}")
    public ResponseEntity<RutaOutDto> modify(@PathVariable long id, @Valid @RequestBody RutaInDto dto)
            throws RutaNotFoundException, UsuarioNotFoundException, PuntoInteresNotFoundException {
        logger.debug("PUT /v1/rutas/{}", id);
        return ResponseEntity.ok(rutaService.modify(id, dto));
    }

    @PatchMapping("/v1/rutas/{id}")
    public ResponseEntity<RutaOutDto> patch(
            @PathVariable long id,
            @RequestBody Map<String, Object> updates)
            throws RutaNotFoundException, UsuarioNotFoundException, PuntoInteresNotFoundException {
        logger.debug("PATCH /v1/rutas/{} - Campos: {}", id, updates.keySet());
        return ResponseEntity.ok(rutaService.patch(id, updates));
    }

    @DeleteMapping("/v1/rutas/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws RutaNotFoundException {
        logger.debug("DELETE /v1/rutas/{}", id);
        rutaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
