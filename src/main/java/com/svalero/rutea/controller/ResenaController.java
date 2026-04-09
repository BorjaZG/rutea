package com.svalero.rutea.controller;

import com.svalero.rutea.dto.ResenaInDto;
import com.svalero.rutea.dto.ResenaOutDto;
import com.svalero.rutea.exception.PuntoInteresNotFoundException;
import com.svalero.rutea.exception.ResenaNotFoundException;
import com.svalero.rutea.exception.UsuarioNotFoundException;
import com.svalero.rutea.service.ResenaService;
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
public class ResenaController {

    private static final Logger logger = LoggerFactory.getLogger(ResenaController.class);

    @Autowired
    private ResenaService resenaService;

    @GetMapping("/resenas")
    public ResponseEntity<List<ResenaOutDto>> getAll(
            @RequestParam(required = false) Boolean editada,
            @RequestParam(required = false) Integer likes,
            @RequestParam(required = false) Integer valoracion
    ) {
        logger.debug("GET /resenas - Filtros: editada={}, likes={}, valoracion={}",
                editada, likes, valoracion);
        return ResponseEntity.ok(resenaService.findAll(editada, likes, valoracion));
    }

    @GetMapping("/resenas/{id}")
    public ResponseEntity<ResenaOutDto> get(@PathVariable long id) throws ResenaNotFoundException {
        logger.debug("GET /resenas/{}", id);
        return ResponseEntity.ok(resenaService.findById(id));
    }

    @PostMapping("/resenas")
    public ResponseEntity<ResenaOutDto> add(@Valid @RequestBody ResenaInDto dto)
            throws PuntoInteresNotFoundException, UsuarioNotFoundException {
        logger.debug("POST /resenas - valoracion: {}", dto.getValoracion());
        return new ResponseEntity<>(resenaService.add(dto), HttpStatus.CREATED);
    }

    @PutMapping("/resenas/{id}")
    public ResponseEntity<ResenaOutDto> modify(@PathVariable long id, @Valid @RequestBody ResenaInDto dto)
            throws ResenaNotFoundException, PuntoInteresNotFoundException, UsuarioNotFoundException {
        logger.debug("PUT /resenas/{}", id);
        return ResponseEntity.ok(resenaService.modify(id, dto));
    }

    @PatchMapping("/resenas/{id}")
    public ResponseEntity<ResenaOutDto> patch(
            @PathVariable long id,
            @RequestBody Map<String, Object> updates)
            throws ResenaNotFoundException, PuntoInteresNotFoundException, UsuarioNotFoundException {
        logger.debug("PATCH /resenas/{} - Campos: {}", id, updates.keySet());
        return ResponseEntity.ok(resenaService.patch(id, updates));
    }

    @DeleteMapping("/resenas/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws ResenaNotFoundException {
        logger.debug("DELETE /resenas/{}", id);
        resenaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
