package com.cibertec.colegio.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.colegio.model.Rol;
import com.cibertec.colegio.service.RolService;

@RestController
@RequestMapping("/api/rol")
public class RolRestController {

    private final RolService rolService;

    RolRestController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    public List<Rol> listar() {
        return rolService.listarTodosRol();
    }
}
