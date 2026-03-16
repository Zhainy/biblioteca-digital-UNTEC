package com.untec.dao;

import com.untec.model.Usuario;
import com.untec.utils.Conexion;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioDAOImpl implements IUsuarioDAO {
    private static final Logger LOGGER = Logger.getLogger(UsuarioDAOImpl.class.getName());

    @Override
    public Usuario validar(String email, String password) {
        Usuario usuario = null;
        String sql = "SELECT id, nombre, email, password FROM usuarios WHERE email = ?";

        try (Connection conn = Conexion.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && credencialesValidas(password, rs.getString("password"))) {
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setEmail(rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al validar usuario con email " + email, e);
        }
        return usuario;
    }

    private boolean credencialesValidas(String passwordIngresada, String passwordGuardada) {
        if (passwordGuardada == null || passwordGuardada.isBlank()) {
            return false;
        }

        if (passwordGuardada.startsWith("$2a$") || passwordGuardada.startsWith("$2b$") || passwordGuardada.startsWith("$2y$")) {
            try {
                return BCrypt.checkpw(passwordIngresada, passwordGuardada);
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.WARNING, "Hash BCrypt invalido detectado para un usuario", e);
                return false;
            }
        }

        // Compatibilidad temporal para registros antiguos en texto plano.
        LOGGER.warning("Se detecto password en texto plano. Migra este usuario a BCrypt.");
        return passwordGuardada.equals(passwordIngresada);
    }
}