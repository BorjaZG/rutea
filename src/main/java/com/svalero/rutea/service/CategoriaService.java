package com.svalero.rutea.service;

import com.svalero.rutea.domain.Categoria;
import com.svalero.rutea.exception.ResourceNotFoundException;
import com.svalero.rutea.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoriaService {

    @Autowired private CategoriaRepository categoriaRepository;

    public List<Categoria> findAll() {
        return (List<Categoria>) categoriaRepository.findAll();
    }

    public List<Categoria> filtrarCategorias(boolean activa, float costeMax) {
        return categoriaRepository.findByActivaAndCostePromedioLessThan(activa, costeMax);
    }

    public Categoria findById(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
    }

    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public Categoria update(Long id, Categoria nuevaCategoria) {
        Categoria categoria = findById(id);
        categoria.setNombre(nuevaCategoria.getNombre());
        categoria.setDescripcion(nuevaCategoria.getDescripcion());
        categoria.setIconoUrl(nuevaCategoria.getIconoUrl());
        categoria.setOrdenPrioridad(nuevaCategoria.getOrdenPrioridad());
        categoria.setActiva(nuevaCategoria.isActiva());
        categoria.setCostePromedio(nuevaCategoria.getCostePromedio());
        return categoriaRepository.save(categoria);
    }

    public void delete(Long id) {
        Categoria categoria = findById(id);
        categoriaRepository.delete(categoria);
    }
}