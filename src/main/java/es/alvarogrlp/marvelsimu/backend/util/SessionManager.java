package es.alvarogrlp.marvelsimu.backend.util;

import es.alvarogrlp.marvelsimu.backend.model.UsuarioModel;
import es.alvarogrlp.marvelsimu.backend.selection.logic.SelectionManager;

/**
 * Clase que gestiona la información de la sesión actual del usuario.
 * Permite mantener los datos del usuario logueado entre diferentes pantallas.
 */
public class SessionManager {
    // Instancia única - patrón Singleton
    private static SessionManager instance;
    
    // Datos de la sesión
    private UsuarioModel usuarioActual;
    private SelectionManager selectionManager;
    
    // Constructor privado para el patrón Singleton
    private SessionManager() {
        // Inicialización vacía
    }
    
    /**
     * Obtiene la instancia única de SessionManager
     * @return La instancia de SessionManager
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Establece el usuario actual de la sesión
     * @param usuario El modelo de usuario que ha iniciado sesión
     */
    public static void setUsuarioActual(UsuarioModel usuario) {
        getInstance().usuarioActual = usuario;
    }
    
    /**
     * Obtiene el usuario actual de la sesión
     * @return El modelo de usuario actual o null si no hay sesión
     */
    public static UsuarioModel getUsuarioActual() {
        return getInstance().usuarioActual;
    }
    
    /**
     * Establece el SelectionManager para la sesión actual
     * @param manager El SelectionManager a guardar
     */
    public static void setSelectionManager(SelectionManager manager) {
        getInstance().selectionManager = manager;
    }
    
    /**
     * Obtiene el SelectionManager de la sesión actual
     * @return El SelectionManager o null si no está configurado
     */
    public static SelectionManager getSelectionManager() {
        return getInstance().selectionManager;
    }
    
    /**
     * Cierra la sesión actual, eliminando todos los datos
     */
    public static void cerrarSesion() {
        getInstance().usuarioActual = null;
        getInstance().selectionManager = null;
    }
}