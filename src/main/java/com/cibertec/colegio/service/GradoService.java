package com.cibertec.colegio.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.cibertec.colegio.model.Grado;
import com.cibertec.colegio.repository.GradoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GradoService {

    private final GradoRepository gradoRepository;
    private final CodigoService codigoService;

    public List<Grado> listarTodos() {
        return gradoRepository.findAllByOrderByNombreAscSeccionAsc();
    }

    public Grado guardar(Grado grado) {
        if (grado.getNombre() == null || grado.getNombre().isBlank()
                || grado.getSeccion() == null || grado.getSeccion().isBlank()) {
            throw new IllegalArgumentException("Complete nombre y sección del grado.");
        }

        String nombre = grado.getNombre().trim();
        String seccion = grado.getSeccion().trim();
        grado.setNombre(nombre);
        grado.setSeccion(seccion);

        gradoRepository.findAll().stream()
                .filter(g -> nombre.equalsIgnoreCase(g.getNombre()) && seccion.equalsIgnoreCase(g.getSeccion()))
                .findFirst()
                .ifPresent(existente -> {
                    if (grado.getId() == null || !Objects.equals(existente.getId(), grado.getId())) {
                        throw new IllegalArgumentException("Ya existe un grado con ese nombre y sección.");
                    }
                });

        if (grado.getCodigo() == null) {
            grado.setCodigo(codigoService.siguienteGrado());
        }
        return gradoRepository.save(grado);
    }

    public Grado obtenerPorId(Integer id) {
        return gradoRepository.findById(id).orElse(null);
    }

    public void eliminar(Integer id) {
        gradoRepository.deleteById(id);
    }
}
