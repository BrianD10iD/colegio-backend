package com.cibertec.colegio.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cibertec.colegio.model.Rol;
import com.cibertec.colegio.repository.RolRepository;
import com.cibertec.colegio.service.RolService;

@Service

public class RolServiceImpl  implements RolService{
	


    private final RolRepository rolRepository;

    public RolServiceImpl(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    public List<Rol> listarTodosRol() {
        return rolRepository.findAll();
    }

    @Override
    public Rol buscarById(Integer id) {
        return rolRepository.findById(id).orElse(null);
    }
}
