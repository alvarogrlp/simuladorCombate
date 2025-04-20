package es.alvarogrlp.marvelsimu.backend.selection.util;

import java.util.List;

import es.alvarogrlp.marvelsimu.backend.model.PasivaModel;
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
        if (attackType == null) return "Desconocido";
        
        switch (attackType.toLowerCase()) {
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
            case "acc":
                return "Ataque Cuerpo a Cuerpo";
            case "aad":
                return "Ataque a Distancia";
            case "habilidad_mas_poderosa":
                return "Habilidad Poderosa";
            case "habilidad_caracteristica":
                return "Habilidad Característica";
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
        // Verificar si el personaje tiene pasivas
        List<PasivaModel> pasivas = character.getPasivas();
        if (pasivas == null || pasivas.isEmpty()) {
            return "Ninguna";
        }
        
        // Tomar la primera pasiva para mostrar (podemos mejorar esto más adelante)
        PasivaModel pasiva = pasivas.get(0);
        
        // Si no hay un tipo de efecto definido, devolver una descripción genérica
        if (pasiva.getEfectoTipo() == null) {
            return pasiva.getNombre() + " - " + pasiva.getDescripcion();
        }
        
        // Formatear según el tipo de efecto
        switch (pasiva.getEfectoTipo()) {
            case "reduce_damage_pct":
                return "Reduce daño recibido en " + pasiva.getEfectoValor() + "%";
            case "shield_pct":
                return "Escudo que absorbe " + pasiva.getEfectoValor() + "% del daño";
            case "heal_pct":
                return "Regenera " + pasiva.getEfectoValor() + "% de vida por turno";
            case "counter_pct":
                return pasiva.getEfectoValor() + "% prob. de contraatacar";
            case "critical_chance_pct":
                return "Aumenta prob. crítico en " + pasiva.getEfectoValor() + "%";
            case "dodge_chance_pct":
                return pasiva.getEfectoValor() + "% prob. de esquivar ataques";
            default:
                return pasiva.getNombre() + " - " + pasiva.getDescripcion();
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