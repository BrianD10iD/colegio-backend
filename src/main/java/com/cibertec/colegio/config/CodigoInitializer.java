package com.cibertec.colegio.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.colegio.service.CodigoService;

@Component
@Order(1)
public class CodigoInitializer implements CommandLineRunner {

    private final CodigoService codigoService;

    CodigoInitializer(CodigoService codigoService) {
        this.codigoService = codigoService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        codigoService.inicializarSecuencias();
        codigoService.sincronizarSecuencia(CodigoService.SEQ_ALUMNO, "alumno", CodigoService.PREF_ALUMNO);
        codigoService.sincronizarSecuencia(CodigoService.SEQ_TUTOR, "tutor", CodigoService.PREF_TUTOR);
        codigoService.sincronizarSecuencia(CodigoService.SEQ_GRADO, "grado", CodigoService.PREF_GRADO);
        codigoService.sincronizarSecuencia(CodigoService.SEQ_MATRICULA, "matricula", CodigoService.PREF_MATRICULA);
        codigoService.sincronizarSecuencia(CodigoService.SEQ_USUARIO, "usuario", CodigoService.PREF_USUARIO);
    }
}
