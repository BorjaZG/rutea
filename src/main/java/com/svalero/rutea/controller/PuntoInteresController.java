package com.svalero.rutea.controller;

import com.svalero.rutea.dto.PuntoInteresInDto;
import com.svalero.rutea.dto.PuntoInteresOutDto;
import com.svalero.rutea.exception.CategoriaNotFoundException;
import com.svalero.rutea.exception.PuntoInteresNotFoundException;
import com.svalero.rutea.service.PuntoInteresService;
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
public class PuntoInteresController {

    private static final Logger logger = LoggerFactory.getLogger(PuntoInteresController.class);

    @Autowired
    private PuntoInteresService puntoInteresService;

    @GetMapping("/puntos")
    public ResponseEntity<List<PuntoInteresOutDto>> getAll(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Boolean abiertoActualmente,
            @RequestParam(required = false) Boolean abierto,
            @RequestParam(required = false, defaultValue = "") String nombre,
            @RequestParam(required = false) Float puntuacionMedia
    ) {
        logger.debug("GET /puntos - Filtros: categoriaId={}, abiertoActualmente={}, nombre={}, puntuacionMedia={}",
                categoriaId, abiertoActualmente != null ? abiertoActualmente : abierto, nombre, puntuacionMedia);
        Boolean abiertoFinal = abiertoActualmente != null ? abiertoActualmente : abierto;
        return ResponseEntity.ok(puntoInteresService.findAll(categoriaId, abiertoFinal, nombre, puntuacionMedia));
    }

    @GetMapping("/puntos/{id}")
    public ResponseEntity<PuntoInteresOutDto> get(@PathVariable long id) throws PuntoInteresNotFoundException {
        logger.debug("GET /puntos/{}", id);
        return ResponseEntity.ok(puntoInteresService.findById(id));
    }

    @PostMapping("/puntos")
    public ResponseEntity<PuntoInteresOutDto> add(@Valid @RequestBody PuntoInteresInDto dto)
            throws CategoriaNotFoundException {
        logger.debug("POST /puntos - {}", dto.getNombre());
        return new ResponseEntity<>(puntoInteresService.add(dto), HttpStatus.CREATED);
    }

    @PutMapping("/puntos/{id}")
    public ResponseEntity<PuntoInteresOutDto> modify(@PathVariable long id, @Valid @RequestBody PuntoInteresInDto dto)
            throws PuntoInteresNotFoundException, CategoriaNotFoundException {
        logger.debug("PUT /puntos/{}", id);
        return ResponseEntity.ok(puntoInteresService.modify(id, dto));
    }

    @PatchMapping("/puntos/{id}")
    public ResponseEntity<PuntoInteresOutDto> patch(
            @PathVariable long id,
            @RequestBody Map<String, Object> updates)
            throws PuntoInteresNotFoundException, CategoriaNotFoundException {
        logger.debug("PATCH /puntos/{} - Campos: {}", id, updates.keySet());
        return ResponseEntity.ok(puntoInteresService.patch(id, updates));
    }

    @DeleteMapping("/puntos/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws PuntoInteresNotFoundException {
        logger.debug("DELETE /puntos/{}", id);
        puntoInteresService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
