package com.cibertec.colegio.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.colegio.model.Grado;
import com.cibertec.colegio.service.GradoService;

@RestController
@RequestMapping("/api/grado")
public class GradoController {

    private final GradoService gradoService;

    GradoController(GradoService gradoService) {
        this.gradoService = gradoService;
    }

    @GetMapping
    public List<Grado> listar() {
        return gradoService.listarTodos();
    }

    @PostMapping
    public Grado crear(@RequestBody Grado grado) {
        return gradoService.guardar(grado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Grado> obtener(@PathVariable Integer id) {
        Grado grado = gradoService.obtenerPorId(id);
        if (grado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(grado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Grado> actualizar(@PathVariable Integer id, @RequestBody Grado grado) {
        Grado existente = gradoService.obtenerPorId(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        grado.setId(id);
        grado.setCodigo(existente.getCodigo());
        return ResponseEntity.ok(gradoService.guardar(grado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        gradoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
