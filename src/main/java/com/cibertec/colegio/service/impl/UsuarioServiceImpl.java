package com.cibertec.colegio.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cibertec.colegio.model.Rol;
import com.cibertec.colegio.model.Usuario;
import com.cibertec.colegio.repository.UsuarioRepository;
import com.cibertec.colegio.service.CodigoService;
import com.cibertec.colegio.service.RolService;
import com.cibertec.colegio.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;
    private final RolService rolService;
    private final CodigoService codigoService;

    @Override
    public Usuario guardarUsuario(Usuario objUsuario) {
        if (objUsuario.getCodigo() == null) {
            objUsuario.setCodigo(codigoService.siguienteUsuario());
        }
        objUsuario.setClave(passwordEncoder.encode(objUsuario.getClave()));
        return usuarioRepositorio.save(objUsuario);
    }

    @Override
    public List<Usuario> listartodosUsuario() {
        return usuarioRepositorio.findAll();
    }

    @Override
    public Usuario buscarByUsuario(String username) {
        return usuarioRepositorio.findByUsername(username).orElse(null);
    }

    @Override
    public Usuario obtenerPorId(Integer id) {
        return usuarioRepositorio.findById(id).orElse(null);
    }

    @Override
    public Usuario actualizarUsuario(Integer id, String nombres, String apellidos, String username,
                                     String password, Integer rolId) {
        Usuario usuario = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!usuario.getUsername().equals(username)) {
            Usuario existente = buscarByUsuario(username);
            if (existente != null && !existente.getId().equals(id)) {
                throw new IllegalArgumentException("El nombre de usuario ya está en uso");
            }
        }

        Rol rol = rolService.buscarById(rolId);
        if (rol == null) {
            throw new IllegalArgumentException("Rol no válido");
        }

        usuario.setNombres(nombres);
        usuario.setApellidos(apellidos);
        usuario.setUsername(username);
        usuario.setRol(rol);

        if (password != null && !password.isBlank()) {
            usuario.setClave(passwordEncoder.encode(password));
        }

        return usuarioRepositorio.save(usuario);
    }

    @Override
    public void eliminarUsuario(Integer id) {
        usuarioRepositorio.deleteById(id);
    }
}
