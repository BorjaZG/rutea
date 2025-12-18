package com.svalero.rutea.controller;

import com.svalero.rutea.domain.Ruta;
import com.svalero.rutea.service.RutaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rutas")
@CrossOrigin
public class RutaController {

    @Autowired private RutaService rutaService;

    @GetMapping
    public ResponseEntity<List<Ruta>> getAll(@RequestParam(required = false) String dificultad) {
        if (dificultad != null) {
            return new ResponseEntity<>(rutaService.filtrarRutas(dificultad), HttpStatus.OK);
        }
        return new ResponseEntity<>(rutaService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ruta> getById(@PathVariable long id) {
        return new ResponseEntity<>(rutaService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Ruta> add(@Valid @RequestBody Ruta ruta) {
        return new ResponseEntity<>(rutaService.save(ruta), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ruta> update(@PathVariable long id, @Valid @RequestBody Ruta ruta) {
        return new ResponseEntity<>(rutaService.update(id, ruta), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        rutaService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}