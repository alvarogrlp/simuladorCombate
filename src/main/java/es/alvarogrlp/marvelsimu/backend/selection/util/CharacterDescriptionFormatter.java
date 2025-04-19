package es.alvarogrlp.marvelsimu.backend.selection.util;

import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * Clase utilitaria para formatear descripciones de personajes
 */
public class CharacterDescriptionFormatter {
    
    /**
     * Obtiene una descripción del tipo de ataque
     * @param attackType Tipo de ataque
     * @return Descripción formateada
     */
    public String getAttackTypeDescription(String attackType) {
        switch (attackType) {
            case "fisico":
                return "Tipo: Físico";
            case "magico":
                return "Tipo: Mágico";
            case "energia":
                return "Tipo: Energía";
            case "daño_verdadero":
                return "Tipo: Daño Verdadero";
            case "fisico_penetrante":
                return "Tipo: Físico Penetrante";
            default:
                return "Tipo: " + attackType;
        }
    }
    
    /**
     * Obtiene una descripción legible de la pasiva
     * @param character Personaje con la pasiva
     * @return Descripción formateada
     */
    public String getPassiveDescription(PersonajeModel character) {
        String type = character.getPasivaTipo();
        int value = character.getPasivaValor();
        
        switch (type) {
            case "reduccion":
                return value + "% prob. de reducir daño";
            case "barrera":
                return value + "% prob. de activar barrera";
            case "armadura":
                return value + "% prob. de reducir daño";
            case "regeneracion":
                return "Regenera " + value + "% de vida por turno";
            case "bloqueo":
                return value + "% prob. de bloquear ataque";
            default:
                return "Ninguna";
        }
    }
    
    /**
     * Crea una fila de estadística para el panel de información
     * @param name Nombre de la estadística
     * @param value Valor de la estadística
     * @return HBox con la fila formateada
     */
    public HBox createStatRow(String name, int value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        
        Text nameText = new Text(name + ":");
        nameText.getStyleClass().add("stat-name-row");
        
        Text valueText = new Text(String.valueOf(value));
        valueText.getStyleClass().add("stat-value-row");
        
        row.getChildren().addAll(nameText, valueText);
        return row;
    }
}