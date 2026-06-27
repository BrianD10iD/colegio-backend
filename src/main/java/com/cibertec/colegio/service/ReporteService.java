package com.cibertec.colegio.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.colegio.model.Alumno;
import com.cibertec.colegio.model.Grado;
import com.cibertec.colegio.model.Matricula;
import com.cibertec.colegio.model.Tutor;
import com.cibertec.colegio.repository.AlumnoRepository;
import com.cibertec.colegio.repository.GradoRepository;
import com.cibertec.colegio.repository.MatriculaRepository;
import com.cibertec.colegio.repository.TutorRepository;

@Service
public class ReporteService {

    private final AlumnoRepository alumnoRepository;

    private TutorRepository tutorRepository;

    private GradoRepository gradoRepository;

    private MatriculaRepository matriculaRepository;

    ReporteService(AlumnoRepository alumnoRepository, TutorRepository tutorRepository, GradoRepository gradoRepository, MatriculaRepository matriculaRepository) {
        this.alumnoRepository = alumnoRepository;
        this.tutorRepository = tutorRepository;
        this.gradoRepository = gradoRepository;
        this.matriculaRepository = matriculaRepository;
    }

    @Transactional(readOnly = true)
    public byte[] generarReporteAlumnos() throws IOException {
        List<Alumno> alumnos = alumnoRepository.findAll();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reporte de Alumnos");
            
            // Crear estilo para el encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            
            // Crear fila de encabezado
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Código", "Nombres", "Apellidos", "DNI", "Fecha Nacimiento", "Grado", "Sección", "Tutor"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Llenar datos
            int rowNum = 1;
            for (Alumno alumno : alumnos) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(textoCodigo(alumno.getCodigo()));
                row.createCell(1).setCellValue(alumno.getNombres());
                row.createCell(2).setCellValue(alumno.getApellidos());
                row.createCell(3).setCellValue(alumno.getDni());
                row.createCell(4).setCellValue(alumno.getFechaNacimiento() != null ? 
                    alumno.getFechaNacimiento().toString() : "");
                row.createCell(5).setCellValue(alumno.getGrado() != null ? 
                    alumno.getGrado().getNombre() : "");
                row.createCell(6).setCellValue(alumno.getGrado() != null ? 
                    alumno.getGrado().getSeccion() : "");
                row.createCell(7).setCellValue(alumno.getTutor() != null ? 
                    alumno.getTutor().getNombres() + " " + alumno.getTutor().getApellidos() : "");
            }
            
            // Ajustar ancho de columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Agregar información de generación
            Row infoRow = sheet.createRow(rowNum + 2);
            infoRow.createCell(0).setCellValue("Generado el: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    @Transactional(readOnly = true)
    public byte[] generarReporteMatriculas() throws IOException {
        List<Matricula> matriculas = matriculaRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reporte de Matrículas");
            CellStyle headerStyle = crearEstiloEncabezado(workbook, IndexedColors.DARK_TEAL);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Código", "Alumno", "DNI", "Grado", "Sección", "Tutor", "Año Escolar", "Fecha", "Estado"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Matricula matricula : matriculas) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(textoCodigo(matricula.getCodigo()));
                row.createCell(1).setCellValue(matricula.getAlumno().getNombres() + " " + matricula.getAlumno().getApellidos());
                row.createCell(2).setCellValue(matricula.getAlumno().getDni());
                row.createCell(3).setCellValue(matricula.getGrado().getNombre());
                row.createCell(4).setCellValue(matricula.getGrado().getSeccion());
                row.createCell(5).setCellValue(matricula.getTutor().getNombres() + " " + matricula.getTutor().getApellidos());
                row.createCell(6).setCellValue(matricula.getAnioEscolar());
                row.createCell(7).setCellValue(matricula.getFechaMatricula().toString());
                row.createCell(8).setCellValue(matricula.getEstado());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    @Transactional(readOnly = true)
    public byte[] generarReporteTutores() throws IOException {
        List<Tutor> tutores = tutorRepository.findAll();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reporte de Tutores");
            
            // Crear estilo para el encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            
            // Crear fila de encabezado
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Código", "Nombres", "Apellidos", "DNI", "Teléfono", "Email", "Dirección", "Parentesco", "Alumnos a Cargo"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Llenar datos
            int rowNum = 1;
            for (Tutor tutor : tutores) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(textoCodigo(tutor.getCodigo()));
                row.createCell(1).setCellValue(tutor.getNombres());
                row.createCell(2).setCellValue(tutor.getApellidos());
                row.createCell(3).setCellValue(tutor.getDni());
                row.createCell(4).setCellValue(tutor.getTelefono());
                row.createCell(5).setCellValue(tutor.getEmail());
                row.createCell(6).setCellValue(tutor.getDireccion());
                row.createCell(7).setCellValue(tutor.getParentesco());
                row.createCell(8).setCellValue(alumnoRepository.countByTutor_Id(tutor.getId()));
            }
            
            // Ajustar ancho de columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Agregar información de generación
            Row infoRow = sheet.createRow(rowNum + 2);
            infoRow.createCell(0).setCellValue("Generado el: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    @Transactional(readOnly = true)
    public byte[] generarReporteGrados() throws IOException {
        List<Grado> grados = gradoRepository.findAll();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reporte de Grados");
            
            // Crear estilo para el encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            
            // Crear fila de encabezado
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Código", "Nombre del Grado", "Sección", "Cantidad de Alumnos"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Llenar datos
            int rowNum = 1;
            for (Grado grado : grados) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(textoCodigo(grado.getCodigo()));
                row.createCell(1).setCellValue(grado.getNombre());
                row.createCell(2).setCellValue(grado.getSeccion());
                row.createCell(3).setCellValue(alumnoRepository.countByGrado_Id(grado.getId()));
            }
            
            // Ajustar ancho de columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Agregar información de generación
            Row infoRow = sheet.createRow(rowNum + 2);
            infoRow.createCell(0).setCellValue("Generado el: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    @Transactional(readOnly = true)
    public byte[] generarReporteCompleto() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            
            // Hoja de Alumnos
            crearHojaAlumnos(workbook);
            
            // Hoja de Tutores
            crearHojaTutores(workbook);
            
            // Hoja de Grados
            crearHojaGrados(workbook);

            // Hoja de Matrículas
            crearHojaMatriculas(workbook);

            // Hoja de Resumen
            crearHojaResumen(workbook);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void crearHojaAlumnos(Workbook workbook) {
        List<Alumno> alumnos = alumnoRepository.findAll();
        Sheet sheet = workbook.createSheet("Alumnos");
        
        // Estilo de encabezado
        CellStyle headerStyle = crearEstiloEncabezado(workbook, IndexedColors.DARK_BLUE);
        
        // Encabezados
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Código", "Nombres", "Apellidos", "DNI", "Fecha Nacimiento", "Grado", "Sección", "Tutor"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Datos
        int rowNum = 1;
        for (Alumno alumno : alumnos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(textoCodigo(alumno.getCodigo()));
            row.createCell(1).setCellValue(alumno.getNombres());
            row.createCell(2).setCellValue(alumno.getApellidos());
            row.createCell(3).setCellValue(alumno.getDni());
            row.createCell(4).setCellValue(alumno.getFechaNacimiento() != null ? 
                alumno.getFechaNacimiento().toString() : "");
            row.createCell(5).setCellValue(alumno.getGrado() != null ? 
                alumno.getGrado().getNombre() : "");
            row.createCell(6).setCellValue(alumno.getGrado() != null ? 
                alumno.getGrado().getSeccion() : "");
            row.createCell(7).setCellValue(alumno.getTutor() != null ? 
                alumno.getTutor().getNombres() + " " + alumno.getTutor().getApellidos() : "");
        }
        
        // Ajustar columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void crearHojaTutores(Workbook workbook) {
        List<Tutor> tutores = tutorRepository.findAll();
        Sheet sheet = workbook.createSheet("Tutores");
        
        CellStyle headerStyle = crearEstiloEncabezado(workbook, IndexedColors.DARK_GREEN);
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Código", "Nombres", "Apellidos", "DNI", "Teléfono", "Email", "Dirección", "Parentesco"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        for (Tutor tutor : tutores) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(textoCodigo(tutor.getCodigo()));
            row.createCell(1).setCellValue(tutor.getNombres());
            row.createCell(2).setCellValue(tutor.getApellidos());
            row.createCell(3).setCellValue(tutor.getDni());
            row.createCell(4).setCellValue(tutor.getTelefono());
            row.createCell(5).setCellValue(tutor.getEmail());
            row.createCell(6).setCellValue(tutor.getDireccion());
            row.createCell(7).setCellValue(tutor.getParentesco());
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void crearHojaGrados(Workbook workbook) {
        List<Grado> grados = gradoRepository.findAll();
        Sheet sheet = workbook.createSheet("Grados");
        
        CellStyle headerStyle = crearEstiloEncabezado(workbook, IndexedColors.DARK_RED);
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Código", "Nombre del Grado", "Sección", "Cantidad de Alumnos"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        for (Grado grado : grados) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(textoCodigo(grado.getCodigo()));
            row.createCell(1).setCellValue(grado.getNombre());
            row.createCell(2).setCellValue(grado.getSeccion());
            row.createCell(3).setCellValue(alumnoRepository.countByGrado_Id(grado.getId()));
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void crearHojaMatriculas(Workbook workbook) {
        List<Matricula> matriculas = matriculaRepository.findAll();
        Sheet sheet = workbook.createSheet("Matrículas");
        CellStyle headerStyle = crearEstiloEncabezado(workbook, IndexedColors.DARK_TEAL);

        Row headerRow = sheet.createRow(0);
        String[] headers = {"Código", "Alumno", "DNI", "Grado", "Tutor", "Año Escolar", "Fecha", "Estado"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (Matricula matricula : matriculas) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(textoCodigo(matricula.getCodigo()));
            row.createCell(1).setCellValue(matricula.getAlumno().getNombres() + " " + matricula.getAlumno().getApellidos());
            row.createCell(2).setCellValue(matricula.getAlumno().getDni());
            row.createCell(3).setCellValue(matricula.getGrado().getNombre() + " - " + matricula.getGrado().getSeccion());
            row.createCell(4).setCellValue(matricula.getTutor().getNombres() + " " + matricula.getTutor().getApellidos());
            row.createCell(5).setCellValue(matricula.getAnioEscolar());
            row.createCell(6).setCellValue(matricula.getFechaMatricula().toString());
            row.createCell(7).setCellValue(matricula.getEstado());
        }
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void crearHojaResumen(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Resumen");
        
        CellStyle headerStyle = crearEstiloEncabezado(workbook, IndexedColors.DARK_TEAL);
        CellStyle dataStyle = workbook.createCellStyle();
        Font dataFont = workbook.createFont();
        dataFont.setBold(true);
        dataStyle.setFont(dataFont);
        
        // Título
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("RESUMEN ESTADÍSTICO - COLEGIO CARL SAGAN");
        titleCell.setCellStyle(headerStyle);
        
        // Estadísticas
        int rowNum = 2;
        
        // Total de alumnos
        Row alumnosRow = sheet.createRow(rowNum++);
        alumnosRow.createCell(0).setCellValue("Total de Alumnos:");
        Cell alumnosCell = alumnosRow.createCell(1);
        alumnosCell.setCellValue(alumnoRepository.count());
        alumnosCell.setCellStyle(dataStyle);
        
        // Total de tutores
        Row tutoresRow = sheet.createRow(rowNum++);
        tutoresRow.createCell(0).setCellValue("Total de Tutores:");
        Cell tutoresCell = tutoresRow.createCell(1);
        tutoresCell.setCellValue(tutorRepository.count());
        tutoresCell.setCellStyle(dataStyle);
        
        // Total de grados
        Row gradosRow = sheet.createRow(rowNum++);
        gradosRow.createCell(0).setCellValue("Total de Grados:");
        Cell gradosCell = gradosRow.createCell(1);
        gradosCell.setCellValue(gradoRepository.count());
        gradosCell.setCellStyle(dataStyle);
        
        // Total de matrículas
        Row matriculasRow = sheet.createRow(rowNum++);
        matriculasRow.createCell(0).setCellValue("Total de Matrículas:");
        Cell matriculasCell = matriculasRow.createCell(1);
        matriculasCell.setCellValue(matriculaRepository.count());
        matriculasCell.setCellStyle(dataStyle);

        // Fecha de generación
        Row fechaRow = sheet.createRow(rowNum + 2);
        fechaRow.createCell(0).setCellValue("Fecha de Generación:");
        fechaRow.createCell(1).setCellValue(LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        
        // Ajustar columnas
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private String textoCodigo(String codigo) {
        return codigo != null ? codigo : "";
    }

    private CellStyle crearEstiloEncabezado(Workbook workbook, IndexedColors color) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(color.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        return headerStyle;
    }
}
