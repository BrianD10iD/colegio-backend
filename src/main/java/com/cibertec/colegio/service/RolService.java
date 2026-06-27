package com.cibertec.colegio.service;

import java.util.List;
import com.cibertec.colegio.model.Rol;



public interface RolService {
	
	public List<Rol> listarTodosRol();
	
	public Rol buscarById(Integer id);
	
	

}
