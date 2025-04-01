package es.alvarogrlp.marvelsimu.backend.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import es.alvarogrlp.marvelsimu.backend.model.abtrastas.Conexion;

public class UsuarioServiceModel extends Conexion {

    public UsuarioServiceModel() {
    }
    
    public UsuarioServiceModel(String unaRutaArchivoBD) throws SQLException {
        super(unaRutaArchivoBD);
    }
    
    public ArrayList<UsuarioModel> obtenerUsuarios() throws SQLException {
        String sql = "SELECT * FROM Usuario";
        return obtenerUsuario(sql);
    }

    public ArrayList<UsuarioModel> obtenerUsuario(String sql) throws SQLException {
        ArrayList<UsuarioModel> usuarios = new ArrayList<UsuarioModel>();
        try {
            PreparedStatement sentencia = getConnection().prepareStatement(sql);
            ResultSet resultado = sentencia.executeQuery();

            while (resultado.next()) {
                String nombreStr = resultado.getString("nombre");
                String contraseniaStr = resultado.getString("contrasenia");
                String emailStr = resultado.getString("email");
                UsuarioModel usuarioModel = new UsuarioModel(emailStr, nombreStr, contraseniaStr);
                usuarios.add(usuarioModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cerrar();
        }
        return usuarios;
    }

    public UsuarioModel obtenerCredencialesUsuario(String dato) {
        try {
            String sql = "SELECT * FROM Usuario " + "where email='" + dato + "'";
            ArrayList<UsuarioModel> usuarios = obtenerUsuario(sql);
            if (usuarios.isEmpty()) {
                sql = "SELECT * FROM Usuario " + "where nombre='" + dato + "'";
                usuarios = obtenerUsuario(sql);
            }
            if (usuarios.isEmpty()) {
                return null;
            }
            return usuarios.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public boolean agregarUsuario(UsuarioModel usuario) throws SQLException {
        if (usuario == null) {
            return false;
        }
        ArrayList<UsuarioModel> usuarios = obtenerUsuarios();
        String sql = "INSERT  INTO usuario (nombre,email,contrasenia) VALUES ('" + usuario.getNombre() + "', '"
                + usuario.getEmail() + "', '" + usuario.getContrasenia() + "')";

        if (usuarios.contains(usuario)) {
            return false;
        }

        try {
            PreparedStatement sentencia = getConnection().prepareStatement(sql);
            sentencia.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cerrar();
        }
        return true;
    }
}
