package com.cibertec.colegio.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cibertec.colegio.model.Usuario;
import com.cibertec.colegio.repository.UsuarioRepository;

@Service
public class UsuarioDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    UsuarioDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getUsername())
                .password(usuario.getClave())
                .roles(mapRol(usuario.getRol().getNombre()))
                .build();
    }

    private String mapRol(String nombreRol) {
        if (nombreRol == null) {
            return "AUXILIAR";
        }
        return switch (nombreRol.trim().toLowerCase()) {
            case "administrador", "admin" -> "ADMIN";
            case "auxiliar" -> "AUXILIAR";
            default -> nombreRol.trim().toUpperCase();
        };
    }
}
