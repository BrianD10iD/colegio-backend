package com.cibertec.colegio.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.colegio.config.JwtService;
import com.cibertec.colegio.model.Usuario;
import com.cibertec.colegio.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final UsuarioRepository usuarioRepository;

    AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        Usuario usuario = usuarioRepository.findByUsername(request.username()).orElseThrow();
        String role = mapRol(usuario.getRol().getNombre());
        String token = jwtService.generateToken(usuario.getUsername(), role);

        return ResponseEntity.ok(new AuthResponse(token, usuario.getUsername(), role));
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

    public record LoginRequest(String username, String password) {}

    public record AuthResponse(String token, String username, String role) {}
}
