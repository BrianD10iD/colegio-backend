package com.cibertec.colegio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cibertec.colegio.model.Matricula;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    boolean existsByAlumnoDniAndEstado(String dni, String estado);

    List<Matricula> findAllByOrderByCodigoAsc();
}
