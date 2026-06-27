package com.cibertec.colegio.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.cibertec.colegio.model.Tutor;
import com.cibertec.colegio.repository.TutorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TutorService {

    private static final int DNI_LENGTH = 8;
    private static final int PHONE_LENGTH = 9;

    private final TutorRepository tutorRepository;
    private final CodigoService codigoService;

    public List<Tutor> listarTodos() {
        return tutorRepository.findAllByOrderByCodigoAsc();
    }

    public Tutor guardar(Tutor tutor) {
        String dni = validarDni(tutor.getDni());
        tutor.setDni(dni);
        tutor.setTelefono(validarTelefono(tutor.getTelefono()));

        tutorRepository.findByDni(dni).ifPresent(existente -> {
            if (tutor.getId() == null || !Objects.equals(existente.getId(), tutor.getId())) {
                throw new IllegalArgumentException("Ya existe un tutor registrado con ese DNI.");
            }
        });

        if (tutor.getCodigo() == null) {
            tutor.setCodigo(codigoService.siguienteTutor());
        }
        return tutorRepository.save(tutor);
    }

    public Tutor obtenerPorId(Integer id) {
        return tutorRepository.findById(id).orElse(null);
    }

    public void eliminar(Integer id) {
        tutorRepository.deleteById(id);
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

    private String validarTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException("El teléfono es obligatorio.");
        }
        String normalizado = telefono.replaceAll("\\D", "");
        if (normalizado.length() != PHONE_LENGTH) {
            throw new IllegalArgumentException("El teléfono debe tener exactamente 9 dígitos.");
        }
        return normalizado;
    }
}
