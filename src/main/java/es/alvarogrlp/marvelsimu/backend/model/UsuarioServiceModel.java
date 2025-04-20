package es.alvarogrlp.marvelsimu.backend.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import es.alvarogrlp.marvelsimu.backend.model.abtrastas.Conexion;

public class UsuarioServiceModel extends Conexion {

    private static final String DATABASE_PATH = "src/main/resources/marvelSimu.db";
    
    public UsuarioServiceModel() throws SQLException {
        super(DATABASE_PATH);
    }
    
    public UsuarioServiceModel(String rutaArchivoBD) throws SQLException {
        super(rutaArchivoBD);
    }
    
    public ArrayList<UsuarioModel> obtenerUsuarios() throws SQLException {
        String sql = "SELECT * FROM usuario";
        return obtenerUsuario(sql);
    }

    public ArrayList<UsuarioModel> obtenerUsuario(String sql) throws SQLException {
        ArrayList<UsuarioModel> usuarios = new ArrayList<UsuarioModel>();
        try {
            PreparedStatement sentencia = getConnection().prepareStatement(sql);
            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                String nombreStr = resultado.getString("nombre");
                String passwordStr = resultado.getString("password_hash");
                String emailStr = resultado.getString("email");
                UsuarioModel usuarioModel = new UsuarioModel(nombreStr, emailStr, passwordStr);
                usuarios.add(usuarioModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cerrar();
        }
        return usuarios;
    }

    public UsuarioModel obtenerCredencialesUsuario(String dato) throws SQLException {
        try {
            String sql = "SELECT * FROM usuario WHERE email=?";
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setString(1, dato);
            
            ResultSet resultado = stmt.executeQuery();
            if (resultado.next()) {
                String nombreStr = resultado.getString("nombre");
                String passwordStr = resultado.getString("password_hash");
                String emailStr = resultado.getString("email");
                return new UsuarioModel(nombreStr, emailStr, passwordStr);
            }
            
            // Si no encontró por email, buscar por nombre
            sql = "SELECT * FROM usuario WHERE nombre=?";
            stmt = getConnection().prepareStatement(sql);
            stmt.setString(1, dato);
            
            resultado = stmt.executeQuery();
            if (resultado.next()) {
                String nombreStr = resultado.getString("nombre");
                String passwordStr = resultado.getString("password_hash");
                String emailStr = resultado.getString("email");
                return new UsuarioModel(nombreStr, emailStr, passwordStr);
            }
            
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            cerrar();
        }
    }

    public boolean agregarUsuario(UsuarioModel usuario) throws SQLException {
        if (usuario == null) {
            return false;
        }
        
        // Verificar que no exista ya
        if (existeUsuario(usuario.getEmail())) {
            return false;
        }

        try {
            String sql = "INSERT INTO usuario (nombre, email, password_hash) VALUES (?, ?, ?)";
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getContrasenia());
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            cerrar();
        }
    }
    
    private boolean existeUsuario(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario WHERE email = ?";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } finally {
            cerrar();
        }
    }
    
    public boolean eliminarUsuario(UsuarioModel usuario) throws SQLException {
        if (usuario == null) {
            return false;
        }
        String sql = "DELETE FROM usuario WHERE email = ?";
        try {
            PreparedStatement sentencia = getConnection().prepareStatement(sql);
            sentencia.setString(1, usuario.getEmail());
            int filasAfectadas = sentencia.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            cerrar();
        }
    }
    
    public boolean actualizarUsuario(UsuarioModel usuario) throws SQLException {
        if (usuario == null) {
            return false;
        }
        String sql = "UPDATE usuario SET nombre = ?, password_hash = ? WHERE email = ?";
        try {
            PreparedStatement sentencia = getConnection().prepareStatement(sql);
            sentencia.setString(1, usuario.getNombre());
            sentencia.setString(2, usuario.getContrasenia());
            sentencia.setString(3, usuario.getEmail());
            int filasAfectadas = sentencia.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            cerrar();
        }
    }
}
