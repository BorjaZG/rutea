package com.svalero.rutea.controller;

import com.svalero.rutea.domain.PuntoInteres;
import com.svalero.rutea.service.PuntoInteresService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/puntos")
@CrossOrigin
public class PuntoInteresController {

    @Autowired private PuntoInteresService puntoService;

    @GetMapping
    public ResponseEntity<List<PuntoInteres>> getAll(
            @RequestParam(required = false) Boolean abierto,
            @RequestParam(required = false) Float minPuntuacion) {
        if (abierto != null && minPuntuacion != null) {
            return new ResponseEntity<>(puntoService.filtrarPuntos(abierto, minPuntuacion), HttpStatus.OK);
        }
        return new ResponseEntity<>(puntoService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PuntoInteres> getById(@PathVariable long id) {
        return new ResponseEntity<>(puntoService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PuntoInteres> add(@Valid @RequestBody PuntoInteres punto) {
        return new ResponseEntity<>(puntoService.save(punto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PuntoInteres> update(@PathVariable long id, @Valid @RequestBody PuntoInteres punto) {
        return new ResponseEntity<>(puntoService.update(id, punto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        puntoService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}