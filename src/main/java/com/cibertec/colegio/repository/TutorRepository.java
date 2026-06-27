package com.cibertec.colegio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cibertec.colegio.model.Tutor;

public interface TutorRepository extends JpaRepository<Tutor, Integer> {

    Optional<Tutor> findByDni(String dni);

    List<Tutor> findAllByOrderByCodigoAsc();
}
