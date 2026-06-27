package com.cibertec.colegio.service;

import java.util.List;

import com.cibertec.colegio.model.Usuario;

public interface UsuarioService {

    Usuario guardarUsuario(Usuario usuario);

    List<Usuario> listartodosUsuario();

    Usuario buscarByUsuario(String username);

    Usuario obtenerPorId(Integer id);

    Usuario actualizarUsuario(Integer id, String nombres, String apellidos, String username,
                              String password, Integer rolId);

    void eliminarUsuario(Integer id);
}
