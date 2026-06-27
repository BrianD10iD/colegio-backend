package com.cibertec.colegio.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.colegio.service.ReporteService;

@RestController
@RequestMapping("/api/reportes")
public class ReporteRestController {

    private final ReporteService reporteService;

    ReporteRestController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/matriculas/excel")
    public ResponseEntity<byte[]> matriculas() {
        return excelResponse(reporteService::generarReporteMatriculas, "reporte_matriculas");
    }

    @GetMapping("/alumnos/excel")
    public ResponseEntity<byte[]> alumnos() {
        return excelResponse(reporteService::generarReporteAlumnos, "reporte_alumnos");
    }

    @GetMapping("/tutores/excel")
    public ResponseEntity<byte[]> tutores() {
        return excelResponse(reporteService::generarReporteTutores, "reporte_tutores");
    }

    @GetMapping("/grados/excel")
    public ResponseEntity<byte[]> grados() {
        return excelResponse(reporteService::generarReporteGrados, "reporte_grados");
    }

    @GetMapping("/completo/excel")
    public ResponseEntity<byte[]> completo() {
        return excelResponse(reporteService::generarReporteCompleto, "reporte_completo_colegio");
    }

    private ResponseEntity<byte[]> excelResponse(ExcelSupplier supplier, String prefix) {
        try {
            byte[] data = supplier.get();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment",
                    prefix + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @FunctionalInterface
    private interface ExcelSupplier {
        byte[] get() throws IOException;
    }
}
