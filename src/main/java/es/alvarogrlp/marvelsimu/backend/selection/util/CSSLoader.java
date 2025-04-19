package es.alvarogrlp.marvelsimu.backend.selection.util;

import javafx.scene.Parent;
import java.net.URL;

/**
 * Utilidad para cargar archivos CSS de forma segura
 */
public class CSSLoader {
    
    /**
     * Carga un archivo CSS en un nodo Parent
     * @param parent Nodo donde cargar el CSS
     * @param cssPath Ruta del archivo CSS
     * @return true si se cargó correctamente, false en caso contrario
     */
    public static boolean loadCSS(Parent parent, String cssPath) {
        try {
            // Intentar diferentes formas de cargar el CSS
            URL cssResource = CSSLoader.class.getResource(cssPath);
            
            // Si no se encuentra, intentar con una ruta alternativa
            if (cssResource == null) {
                cssResource = CSSLoader.class.getClassLoader().getResource(cssPath.substring(1));
            }
            
            // Si sigue sin encontrarse, intentar con otra ruta
            if (cssResource == null && cssPath.startsWith("/")) {
                cssResource = CSSLoader.class.getResource(cssPath.substring(1));
            }
            
            if (cssResource != null) {
                parent.getStylesheets().add(cssResource.toExternalForm());
                System.out.println("CSS cargado correctamente: " + cssPath);
                return true;
            } else {
                System.err.println("No se encontró el archivo CSS en la ruta: " + cssPath);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error al cargar el CSS: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Aplica estilos inline como fallback
     * @param parent Nodo donde aplicar los estilos
     * @param styles Estilos a aplicar
     */
    public static void applyInlineStyles(Parent parent, String styles) {
        parent.setStyle(styles);
    }
}