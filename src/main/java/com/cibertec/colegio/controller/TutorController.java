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

import com.cibertec.colegio.model.Tutor;
import com.cibertec.colegio.service.TutorService;

@RestController
@RequestMapping("/api/tutor")
public class TutorController {

    private final TutorService tutorService;

    TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @GetMapping
    public List<Tutor> listar() {
        return tutorService.listarTodos();
    }

    @PostMapping
    public Tutor crear(@RequestBody Tutor tutor) {
        return tutorService.guardar(tutor);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tutor> obtener(@PathVariable Integer id) {
        Tutor tutor = tutorService.obtenerPorId(id);
        if (tutor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tutor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tutor> actualizar(@PathVariable Integer id, @RequestBody Tutor tutor) {
        Tutor existente = tutorService.obtenerPorId(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        tutor.setId(id);
        tutor.setCodigo(existente.getCodigo());
        return ResponseEntity.ok(tutorService.guardar(tutor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        tutorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
