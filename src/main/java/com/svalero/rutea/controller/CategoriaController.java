package com.svalero.rutea.controller;

import com.svalero.rutea.domain.Categoria;
import com.svalero.rutea.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin
public class CategoriaController {

    @Autowired private CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<Categoria>> getAll(
            @RequestParam(required = false) Boolean activa,
            @RequestParam(required = false) Float costeMax) {
        if (activa != null && costeMax != null) {
            return new ResponseEntity<>(categoriaService.filtrarCategorias(activa, costeMax), HttpStatus.OK);
        }
        return new ResponseEntity<>(categoriaService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getById(@PathVariable long id) {
        return new ResponseEntity<>(categoriaService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Categoria> add(@Valid @RequestBody Categoria categoria) {
        return new ResponseEntity<>(categoriaService.save(categoria), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> update(@PathVariable long id, @Valid @RequestBody Categoria categoria) {
        return new ResponseEntity<>(categoriaService.update(id, categoria), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        categoriaService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}