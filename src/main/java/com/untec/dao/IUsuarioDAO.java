package com.untec.dao;

import com.untec.model.Usuario;

public interface IUsuarioDAO {
    // Validar el login
    Usuario validar(String email, String password);
}