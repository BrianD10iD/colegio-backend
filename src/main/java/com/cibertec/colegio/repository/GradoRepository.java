package com.cibertec.colegio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cibertec.colegio.model.Grado;

public interface GradoRepository extends JpaRepository<Grado, Integer> {

    List<Grado> findAllByOrderByNombreAscSeccionAsc();
}
