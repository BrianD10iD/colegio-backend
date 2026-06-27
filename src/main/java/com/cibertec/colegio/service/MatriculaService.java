package com.cibertec.colegio.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.colegio.model.Alumno;
import com.cibertec.colegio.model.Grado;
import com.cibertec.colegio.model.Matricula;
import com.cibertec.colegio.model.Tutor;
import com.cibertec.colegio.repository.AlumnoRepository;
import com.cibertec.colegio.repository.MatriculaRepository;

@Service
public class MatriculaService {

    public static final String ESTADO_ACTIVA = "ACTIVA";
    public static final String ESTADO_ANULADA = "ANULADA";

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private GradoService gradoService;

    @Autowired
    private TutorService tutorService;

    @Autowired
    private CodigoService codigoService;

    public List<Matricula> listarTodas() {
        return matriculaRepository.findAllByOrderByCodigoAsc();
    }

    public Matricula obtenerPorId(Long id) {
        return matriculaRepository.findById(id).orElse(null);
    }

    @Transactional
    public Matricula registrarMatricula(Alumno alumno, Integer gradoId, Integer tutorId, String anioEscolar) {
        String dni = validarDni(alumno.getDni());
        alumno.setDni(dni);

        if (alumnoRepository.findByDni(dni).isPresent()) {
            throw new IllegalArgumentException("Ya existe un alumno registrado con ese DNI.");
        }

        Grado grado = gradoService.obtenerPorId(gradoId);
        Tutor tutor = tutorService.obtenerPorId(tutorId);

        if (grado == null || tutor == null) {
            throw new IllegalArgumentException("Grado o tutor no válido");
        }

        if (matriculaRepository.existsByAlumnoDniAndEstado(dni, ESTADO_ACTIVA)) {
            throw new IllegalArgumentException("Ya existe una matrícula activa para este DNI");
        }

        alumno.setGrado(grado);
        alumno.setTutor(tutor);
        if (alumno.getCodigo() == null) {
            alumno.setCodigo(codigoService.siguienteAlumno());
        }
        Alumno alumnoGuardado = alumnoRepository.save(alumno);

        Matricula matricula = new Matricula();
        matricula.setCodigo(codigoService.siguienteMatricula());
        matricula.setAlumno(alumnoGuardado);
        matricula.setGrado(grado);
        matricula.setTutor(tutor);
        matricula.setFechaMatricula(LocalDate.now());
        matricula.setAnioEscolar(anioEscolar);
        matricula.setEstado(ESTADO_ACTIVA);

        return matriculaRepository.save(matricula);
    }

    @Transactional
    public Matricula actualizarMatricula(Long id, Integer gradoId, Integer tutorId, String anioEscolar) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Matrícula no encontrada"));

        if (ESTADO_ANULADA.equals(matricula.getEstado())) {
            throw new IllegalArgumentException("No se puede modificar una matrícula anulada");
        }

        Grado grado = gradoService.obtenerPorId(gradoId);
        Tutor tutor = tutorService.obtenerPorId(tutorId);

        if (grado == null || tutor == null) {
            throw new IllegalArgumentException("Grado o tutor no válido");
        }

        Alumno alumno = matricula.getAlumno();
        alumno.setGrado(grado);
        alumno.setTutor(tutor);
        alumnoRepository.save(alumno);

        matricula.setGrado(grado);
        matricula.setTutor(tutor);
        matricula.setAnioEscolar(anioEscolar);

        return matriculaRepository.save(matricula);
    }

    @Transactional
    public Matricula anularMatricula(Long id) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Matrícula no encontrada"));

        matricula.setEstado(ESTADO_ANULADA);
        return matriculaRepository.save(matricula);
    }

    public void eliminar(Long id) {
        matriculaRepository.deleteById(id);
    }

    private String validarDni(String dni) {
        if (dni == null) {
            throw new IllegalArgumentException("El DNI es obligatorio.");
        }
        String normalizado = dni.replaceAll("\\D", "");
        if (normalizado.length() != 8) {
            throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos.");
        }
        return normalizado;
    }
}
