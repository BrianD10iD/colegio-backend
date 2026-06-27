package com.cibertec.colegio.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.cibertec.colegio.model.Alumno;
import com.cibertec.colegio.repository.AlumnoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlumnoService {

    private static final int DNI_LENGTH = 8;
    private final AlumnoRepository alumnoRepository;
    private final CodigoService codigoService;

    public List<Alumno> listarTodos() {
        return alumnoRepository.findAllByOrderByCodigoAsc();
    }

    public Alumno guardar(Alumno alumno) {
        String dni = validarDni(alumno.getDni());
        alumno.setDni(dni);

        alumnoRepository.findByDni(dni).ifPresent(existente -> {
            if (alumno.getId() == null || !Objects.equals(existente.getId(), alumno.getId())) {
                throw new IllegalArgumentException("Ya existe un alumno registrado con ese DNI.");
            }
        });

        if (alumno.getCodigo() == null) {
            alumno.setCodigo(codigoService.siguienteAlumno());
        }
        return alumnoRepository.save(alumno);
    }

    public Alumno obtenerPorId(Long id) {
        return alumnoRepository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        alumnoRepository.deleteById(id);
    }

    private String validarDni(String dni) {
        if (dni == null) {
            throw new IllegalArgumentException("El DNI es obligatorio.");
        }
        String normalizado = dni.replaceAll("\\D", "");
        if (normalizado.length() != DNI_LENGTH) {
            throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos.");
        }
        return normalizado;
    }
}
