package es.alvarogrlp.marvelsimu.backend.util;

import es.alvarogrlp.marvelsimu.backend.model.UsuarioModel;

/**
 * Clase que gestiona la información de la sesión actual del usuario.
 * Permite mantener los datos del usuario logueado entre diferentes pantallas.
 */
public class SessionManager {
    private static UsuarioModel usuarioActual;
    
    /**
     * Establece el usuario actual de la sesión
     * @param usuario El modelo de usuario que ha iniciado sesión
     */
    public static void setUsuarioActual(UsuarioModel usuario) {
        usuarioActual = usuario;
    }
    
    /**
     * Obtiene el usuario actual de la sesión
     * @return El modelo de usuario actual o null si no hay sesión
     */
    public static UsuarioModel getUsuarioActual() {
        return usuarioActual;
    }
    
    /**
     * Cierra la sesión actual
     */
    public static void cerrarSesion() {
        usuarioActual = null;
    }
}