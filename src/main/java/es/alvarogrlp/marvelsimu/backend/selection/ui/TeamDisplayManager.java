package es.alvarogrlp.marvelsimu.backend.selection.ui;

import javafx.scene.layout.VBox;

import java.util.List;
import java.util.ArrayList;

import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import eu.iamgio.animated.transition.container.AnimatedVBox;

/**
 * Clase encargada de gestionar la visualización de los equipos
 */
public class TeamDisplayManager {
    
    // Listas para almacenar los equipos del jugador y la IA
    private List<PersonajeModel> playerTeam;
    private List<PersonajeModel> aiTeam;
    
    /**
     * Constructor
     */
    public TeamDisplayManager() {
        playerTeam = new ArrayList<>();
        aiTeam = new ArrayList<>();
    }
    
    /**
     * Posiciona los contenedores de equipos en la pantalla
     * @param playerTeamBox Contenedor del equipo del jugador
     * @param aiTeamBox Contenedor del equipo de la IA
     */
    public void positionTeamContainers(VBox playerTeamBox, VBox aiTeamBox) {
        if (playerTeamBox != null) {
            playerTeamBox.setLayoutX(14.0);
            playerTeamBox.setLayoutY(79.0);
        }
        
        if (aiTeamBox != null) {
            aiTeamBox.setLayoutX(674.0);
            aiTeamBox.setLayoutY(79.0);
            aiTeamBox.setVisible(true);
        }
    }
    
    /**
     * Elimina un personaje del equipo
     * @param character Personaje a eliminar
     * @param isPlayerTeam Si es del equipo del jugador
     * @param uiManager Gestor de UI para actualizar la interfaz
     * @return true si se eliminó correctamente
     */
    public boolean removeCharacterFromTeam(PersonajeModel character, boolean isPlayerTeam, SelectionUIManager uiManager) {
        if (character == null) {
            return false;
        }

        List<PersonajeModel> targetTeam = isPlayerTeam ? playerTeam : aiTeam;
        AnimatedVBox targetContainer = isPlayerTeam ? uiManager.getPlayerTeamContainer() : uiManager.getAITeamContainer();
        
        // Buscar el índice del personaje en el equipo
        int index = -1;
        for (int i = 0; i < targetTeam.size(); i++) {
            if (targetTeam.get(i).getNombreCodigo().equals(character.getNombreCodigo())) {
                index = i;
                break;
            }
        }
        
        // Si se encontró, eliminarlo
        if (index >= 0) {
            targetTeam.remove(index);
            
            // Encontrar la tarjeta correspondiente en el contenedor visual
            if (index < targetContainer.getChildren().size()) {
                // Crear referencia al elemento a eliminar
                VBox characterCard = (VBox) targetContainer.getChildren().get(index);
                
                // Eliminar la tarjeta del contenedor visual con animación
                uiManager.removeCharacterFromDisplay(characterCard, isPlayerTeam);
                
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Añade un personaje al equipo
     * @param character Personaje a añadir
     * @param isPlayerTeam Si es para el equipo del jugador
     * @return true si se añadió correctamente
     */
    public boolean addCharacterToTeam(PersonajeModel character, boolean isPlayerTeam) {
        if (character == null) {
            return false;
        }
        
        List<PersonajeModel> targetTeam = isPlayerTeam ? playerTeam : aiTeam;
        
        // Verificar si ya está en el equipo
        for (PersonajeModel existingChar : targetTeam) {
            if (existingChar.getNombreCodigo().equals(character.getNombreCodigo())) {
                return false; // Ya está en el equipo
            }
        }
        
        // Añadir al equipo
        targetTeam.add(character);
        return true;
    }
    
    /**
     * Verifica si un personaje está en un equipo
     * @param character Personaje a verificar
     * @param isPlayerTeam Si se debe verificar el equipo del jugador
     * @return true si el personaje está en el equipo
     */
    public boolean isCharacterInTeam(PersonajeModel character, boolean isPlayerTeam) {
        if (character == null) {
            return false;
        }
        
        List<PersonajeModel> targetTeam = isPlayerTeam ? playerTeam : aiTeam;
        
        for (PersonajeModel existingChar : targetTeam) {
            if (existingChar.getNombreCodigo().equals(character.getNombreCodigo())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Obtiene el tamaño actual del equipo
     * @param isPlayerTeam Si es el equipo del jugador
     * @return Número de personajes en el equipo
     */
    public int getTeamSize(boolean isPlayerTeam) {
        return isPlayerTeam ? playerTeam.size() : aiTeam.size();
    }
    
    /**
     * Verifica si se puede añadir más personajes al equipo
     * @param isPlayerTeam Si es el equipo del jugador
     * @return true si se puede añadir más personajes
     */
    public boolean canAddToTeam(boolean isPlayerTeam) {
        int maxTeamSize = 3; // Tamaño máximo de equipo
        return getTeamSize(isPlayerTeam) < maxTeamSize;
    }
    
    /**
     * Obtiene la lista de personajes de un equipo
     * @param isPlayerTeam Si es el equipo del jugador
     * @return Lista de personajes
     */
    public List<PersonajeModel> getTeam(boolean isPlayerTeam) {
        return isPlayerTeam ? playerTeam : aiTeam;
    }
    
    /**
     * Limpia ambos equipos
     */
    public void clearTeams() {
        playerTeam.clear();
        aiTeam.clear();
    }
}