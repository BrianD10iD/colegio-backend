package com.cibertec.colegio.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class CodigoService {

    private static final String SCHEMA = "public";

    public static final String PREF_ALUMNO = "ALU";
    public static final String PREF_MATRICULA = "MAT";
    public static final String PREF_TUTOR = "TUT";
    public static final String PREF_GRADO = "REP";
    public static final String PREF_USUARIO = "USE";

    public static final String SEQ_ALUMNO = "seq_alumno_codigo";
    public static final String SEQ_TUTOR = "seq_tutor_codigo";
    public static final String SEQ_GRADO = "seq_grado_codigo";
    public static final String SEQ_MATRICULA = "seq_matricula_codigo";
    public static final String SEQ_USUARIO = "seq_usuario_codigo";

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void inicializarSecuencias() {
        asegurarEsquemaPublico();
        crearSecuencia(SEQ_ALUMNO);
        crearSecuencia(SEQ_TUTOR);
        crearSecuencia(SEQ_GRADO);
        crearSecuencia(SEQ_MATRICULA);
        crearSecuencia(SEQ_USUARIO);
    }

    @Transactional
    public String siguienteAlumno() {
        return siguiente(SEQ_ALUMNO, "alumno", PREF_ALUMNO);
    }

    @Transactional
    public String siguienteTutor() {
        return siguiente(SEQ_TUTOR, "tutor", PREF_TUTOR);
    }

    @Transactional
    public String siguienteGrado() {
        return siguiente(SEQ_GRADO, "grado", PREF_GRADO);
    }

    @Transactional
    public String siguienteMatricula() {
        return siguiente(SEQ_MATRICULA, "matricula", PREF_MATRICULA);
    }

    @Transactional
    public String siguienteUsuario() {
        return siguiente(SEQ_USUARIO, "usuario", PREF_USUARIO);
    }

    private String siguiente(String secuencia, String tabla, String prefijo) {
        long max = maxNumeroCodigo(tabla, prefijo);
        long next = nextVal(secuencia);
        if (next <= max) {
            entityManager.createNativeQuery("SELECT setval('" + seq(secuencia) + "', " + max + ", true)")
                    .getSingleResult();
            next = nextVal(secuencia);
        }
        return format(prefijo, next);
    }

    @Transactional
    public void sincronizarSecuencia(String secuencia, String tabla, String prefijo) {
        long max = maxNumeroCodigo(tabla, prefijo);
        long current = currentSeqValue(secuencia);
        long target = Math.max(max, current);
        entityManager.createNativeQuery("SELECT setval('" + seq(secuencia) + "', " + target + ", true)")
                .getSingleResult();
    }

    public String format(String prefijo, long numero) {
        return String.format("%s-%05d", prefijo, numero);
    }

    public long extraerNumero(String codigo, String prefijo) {
        if (codigo == null || codigo.isBlank()) {
            return 0;
        }
        if (codigo.matches("\\d+")) {
            return Long.parseLong(codigo);
        }
        String esperado = prefijo + "-";
        if (codigo.startsWith(esperado)) {
            return Long.parseLong(codigo.substring(esperado.length()));
        }
        return 0;
    }

    public String normalizarCodigo(String codigo, String prefijo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }
        if (codigo.startsWith(prefijo + "-")) {
            long numero = extraerNumero(codigo, prefijo);
            return format(prefijo, numero);
        }
        if (codigo.matches("\\d+")) {
            return format(prefijo, Long.parseLong(codigo));
        }
        return codigo;
    }

    @SuppressWarnings("unchecked")
    private long maxNumeroCodigo(String tabla, String prefijo) {
        List<Object> codigos = entityManager.createNativeQuery("SELECT codigo FROM " + tbl(tabla)).getResultList();
        long max = 0;
        for (Object codigo : codigos) {
            if (codigo == null) {
                continue;
            }
            long numero = extraerNumero(codigo.toString(), prefijo);
            if (numero > max) {
                max = numero;
            }
        }
        return max;
    }

    private void asegurarEsquemaPublico() {
        entityManager.createNativeQuery("CREATE SCHEMA IF NOT EXISTS " + SCHEMA).executeUpdate();
    }

    private void crearSecuencia(String secuencia) {
        entityManager.createNativeQuery(
                "CREATE SEQUENCE IF NOT EXISTS " + seq(secuencia) + " START WITH 1 INCREMENT BY 1")
                .executeUpdate();
    }

    private long currentSeqValue(String secuencia) {
        Number valor = (Number) entityManager
                .createNativeQuery("SELECT last_value FROM " + seq(secuencia))
                .getSingleResult();
        return valor.longValue();
    }

    private long nextVal(String secuencia) {
        Number valor = (Number) entityManager
                .createNativeQuery("SELECT nextval('" + seq(secuencia) + "')")
                .getSingleResult();
        return valor.longValue();
    }

    private String seq(String secuencia) {
        return SCHEMA + "." + secuencia;
    }

    private String tbl(String tabla) {
        return SCHEMA + "." + tabla;
    }
}
