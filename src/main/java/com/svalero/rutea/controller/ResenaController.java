package com.svalero.rutea.controller;

import com.svalero.rutea.domain.Resena;
import com.svalero.rutea.service.ResenaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/resenas")
@CrossOrigin
public class ResenaController {

    @Autowired private ResenaService resenaService;

    @GetMapping
    public ResponseEntity<List<Resena>> getAll(@RequestParam(required = false) Integer valoracion) {
        if (valoracion != null) {
            return new ResponseEntity<>(resenaService.filtrarResenas(valoracion), HttpStatus.OK);
        }
        return new ResponseEntity<>(resenaService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resena> getById(@PathVariable long id) {
        return new ResponseEntity<>(resenaService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Resena> add(@Valid @RequestBody Resena resena) {
        return new ResponseEntity<>(resenaService.save(resena), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resena> update(@PathVariable long id, @Valid @RequestBody Resena resena) {
        return new ResponseEntity<>(resenaService.update(id, resena), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        resenaService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}