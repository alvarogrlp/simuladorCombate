package es.alvarogrlp.marvelsimu.backend.util;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import es.alvarogrlp.marvelsimu.backend.config.ConfigManager;
import es.alvarogrlp.marvelsimu.backend.config.ThemeManager;
import javafx.scene.Scene;
import javafx.scene.image.Image;

/**
 * Utilidad para mostrar alertas con el estilo de la aplicación
 */
public class AlertUtils {

    /**
     * Aplica el estilo de la aplicación a una alerta.
     * @param alert La alerta a estilizar
     */
    public static void aplicarEstilo(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        
        // Aplicar las clases CSS
        dialogPane.getStyleClass().add("dialog-pane");
        
        // Aplicar la hoja de estilos según el tema actual
        String currentTheme = ConfigManager.ConfigProperties.getProperty("theme", "dark");
        String cssPath = "dark".equals(currentTheme) ? 
                "/es/alvarogrlp/marvelsimu/dark-mode.css" : 
                "/es/alvarogrlp/marvelsimu/light-mode.css";
        
        // Aplicar la hoja de estilos
        dialogPane.getStylesheets().add(
                AlertUtils.class.getResource(cssPath).toExternalForm());
        
        // Opcional: Añadir el icono de la aplicación a la ventana de alerta
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        try {
            stage.getIcons().add(new Image(
                    AlertUtils.class.getResourceAsStream("/images/icono.png")));
        } catch (Exception e) {
            // Si no se puede cargar el icono, no hacer nada
            System.err.println("No se pudo cargar el icono para la alerta");
        }
    }
    
    /**
     * Crea y muestra una alerta con el estilo de la aplicación.
     * @param tipo Tipo de alerta (INFORMATION, WARNING, ERROR, CONFIRMATION)
     * @param titulo Título de la alerta
     * @param cabecera Cabecera de la alerta (puede ser null)
     * @param mensaje Mensaje de la alerta
     * @return La alerta creada
     */
    public static Alert mostrarAlerta(Alert.AlertType tipo, String titulo, String cabecera, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecera);
        alert.setContentText(mensaje);
        
        aplicarEstilo(alert);
        alert.showAndWait();
        
        return alert;
    }
    
    /**
     * Método simplificado para mostrar alertas informativas.
     * @param titulo Título de la alerta
     * @param mensaje Mensaje de la alerta
     */
    public static void mostrarInfo(String titulo, String mensaje) {
        mostrarAlerta(Alert.AlertType.INFORMATION, titulo, null, mensaje);
    }
    
    /**
     * Método simplificado para mostrar alertas de error.
     * @param titulo Título de la alerta
     * @param mensaje Mensaje de la alerta
     */
    public static void mostrarError(String titulo, String mensaje) {
        mostrarAlerta(Alert.AlertType.ERROR, titulo, null, mensaje);
    }
    
    /**
     * Método simplificado para mostrar alertas de advertencia.
     * @param titulo Título de la alerta
     * @param mensaje Mensaje de la alerta
     */
    public static void mostrarAdvertencia(String titulo, String mensaje) {
        mostrarAlerta(Alert.AlertType.WARNING, titulo, null, mensaje);
    }
    
    /**
     * Método simplificado para mostrar alertas de confirmación.
     * @param titulo Título de la alerta
     * @param mensaje Mensaje de la alerta
     * @return true si el usuario confirma, false en caso contrario
     */
    public static boolean mostrarConfirmacion(String titulo, String mensaje) {
        Alert alert = mostrarAlerta(Alert.AlertType.CONFIRMATION, titulo, null, mensaje);
        return alert.getResult() == javafx.scene.control.ButtonType.OK;
    }
}