package com.svalero.rutea.controller;

import com.svalero.rutea.dto.RutaInDtoV2;
import com.svalero.rutea.dto.RutaOutDtoV2;
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

@RestController
public class RutaControllerV2 {

    private static final Logger logger = LoggerFactory.getLogger(RutaControllerV2.class);

    @Autowired
    private RutaService rutaService;

    @GetMapping("/v2/rutas")
    public ResponseEntity<List<RutaOutDtoV2>> getAll(
            @RequestParam(required = false, defaultValue = "") String dificultad,
            @RequestParam(required = false) Boolean publica,
            @RequestParam(required = false, defaultValue = "") String titulo
    ) {
        logger.debug("GET /v2/rutas - Filtros: dificultad={}, publica={}, titulo={}",
                dificultad, publica, titulo);
        return ResponseEntity.ok(rutaService.findAllV2(dificultad, publica, titulo));
    }

    @GetMapping("/v2/rutas/{id}")
    public ResponseEntity<RutaOutDtoV2> get(@PathVariable long id) throws RutaNotFoundException {
        logger.debug("GET /v2/rutas/{}", id);
        return ResponseEntity.ok(rutaService.findByIdV2(id));
    }

    @PostMapping("/v2/rutas")
    public ResponseEntity<RutaOutDtoV2> add(@Valid @RequestBody RutaInDtoV2 dto)
            throws UsuarioNotFoundException, PuntoInteresNotFoundException {
        logger.debug("POST /v2/rutas - {}", dto.getTitulo());
        return new ResponseEntity<>(rutaService.addV2(dto), HttpStatus.CREATED);
    }

    @PutMapping("/v2/rutas/{id}")
    public ResponseEntity<RutaOutDtoV2> modify(@PathVariable long id, @Valid @RequestBody RutaInDtoV2 dto)
            throws RutaNotFoundException, UsuarioNotFoundException, PuntoInteresNotFoundException {
        logger.debug("PUT /v2/rutas/{}", id);
        return ResponseEntity.ok(rutaService.modifyV2(id, dto));
    }

    @DeleteMapping("/v2/rutas/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws RutaNotFoundException {
        logger.debug("DELETE /v2/rutas/{}", id);
        rutaService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
