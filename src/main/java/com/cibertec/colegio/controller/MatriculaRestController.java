package com.cibertec.colegio.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.colegio.model.Alumno;
import com.cibertec.colegio.model.Matricula;
import com.cibertec.colegio.service.MatriculaService;

@RestController
@RequestMapping("/api/matricula")
public class MatriculaRestController {

    private final MatriculaService matriculaService;

    MatriculaRestController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @GetMapping
    public List<Matricula> listar() {
        return matriculaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Matricula> obtener(@PathVariable Long id) {
        Matricula matricula = matriculaService.obtenerPorId(id);
        if (matricula == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(matricula);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody MatriculaRequest request) {
        try {
            Alumno alumno = request.toAlumno();
            Matricula matricula = matriculaService.registrarMatricula(
                    alumno, request.gradoId(), request.tutorId(), request.anioEscolar());
            return ResponseEntity.status(HttpStatus.CREATED).body(matricula);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody MatriculaUpdateRequest request) {
        try {
            Matricula matricula = matriculaService.actualizarMatricula(
                    id, request.gradoId(), request.tutorId(), request.anioEscolar());
            return ResponseEntity.ok(matricula);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", ex.getMessage()));
        }
    }

    @PutMapping("/{id}/anular")
    public ResponseEntity<?> anular(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(matriculaService.anularMatricula(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        matriculaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    public record MatriculaRequest(
            String nombres,
            String apellidos,
            String dni,
            String fechaNacimiento,
            Integer gradoId,
            Integer tutorId,
            String anioEscolar) {

        Alumno toAlumno() {
            Alumno alumno = new Alumno();
            alumno.setNombres(nombres);
            alumno.setApellidos(apellidos);
            alumno.setDni(dni);
            if (fechaNacimiento == null || fechaNacimiento.isBlank()) {
                throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");
            }
            try {
                alumno.setFechaNacimiento(java.time.LocalDate.parse(fechaNacimiento));
            } catch (java.time.format.DateTimeParseException ex) {
                throw new IllegalArgumentException("La fecha de nacimiento no es válida.");
            }
            return alumno;
        }
    }

    public record MatriculaUpdateRequest(Integer gradoId, Integer tutorId, String anioEscolar) {}
}
