package com.cibertec.colegio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cibertec.colegio.model.Alumno;

public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    Optional<Alumno> findByDni(String dni);

    long countByTutor_Id(Integer tutorId);

    long countByGrado_Id(Integer gradoId);

    List<Alumno> findAllByOrderByCodigoAsc();
}
