package es.alvarogrlp.marvelsimu.backend.selection.logic;

import java.util.ArrayList;
import java.util.List;

import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import es.alvarogrlp.marvelsimu.backend.selection.ui.SelectionUIManager;
import javafx.scene.control.Button;

/**
 * Clase encargada de gestionar la construcción de equipos
 */
public class TeamBuilder {
    
    private final int maxTeamSize;
    private List<PersonajeModel> playerTeam;
    private List<PersonajeModel> aiTeam;
    
    /**
     * Constructor
     * @param maxTeamSize Tamaño máximo de equipo
     */
    public TeamBuilder(int maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
        this.playerTeam = new ArrayList<>();
        this.aiTeam = new ArrayList<>();
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
            // Primero eliminamos del modelo
            targetTeam.remove(index);
            
            // Luego animamos la salida en la UI y actualizamos la visualización
            uiManager.animateRemoveCharacterCard(index, isPlayerTeam);
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Verifica si se puede añadir más personajes al equipo del jugador
     * @return true si se puede añadir más personajes
     */
    public boolean canAddToPlayerTeam() {
        return playerTeam.size() < maxTeamSize;
    }
    
    /**
     * Verifica si se puede añadir más personajes al equipo de la IA
     * @return true si se puede añadir más personajes
     */
    public boolean canAddToAITeam() {
        return aiTeam.size() < maxTeamSize;
    }
    
    /**
     * Añade un personaje al equipo
     * @param character Personaje a añadir
     * @param sourceButton Botón original del personaje
     * @param isPlayerTeam Si es para el equipo del jugador
     * @param uiManager Gestor de UI para actualizar la interfaz
     * @return true si se añadió correctamente
     */
    public boolean addCharacterToTeam(PersonajeModel character, Button sourceButton, boolean isPlayerTeam, SelectionUIManager uiManager) {
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
        
        // Verificar si hay espacio
        if (targetTeam.size() >= maxTeamSize) {
            return false; // Equipo lleno
        }
        
        // Añadir al equipo
        targetTeam.add(character);
        
        // Añadir a la visualización
        uiManager.addCharacterToTeamDisplay(character, sourceButton, isPlayerTeam);
        
        return true;
    }
    
    /**
     * Verifica si el equipo del jugador está completo
     * @return true si el equipo del jugador tiene todos los personajes
     */
    public boolean isPlayerTeamComplete() {
        return playerTeam.size() == maxTeamSize;
    }
    
    /**
     * Verifica si el equipo de la IA está completo
     * @return true si el equipo de la IA tiene todos los personajes
     */
    public boolean isAITeamComplete() {
        return aiTeam.size() == maxTeamSize;
    }
    
    /**
     * Prepara un equipo para el combate
     * @param team Equipo a preparar
     * @return Equipo preparado (clonado)
     */
    public List<PersonajeModel> prepareTeam(List<PersonajeModel> team) {
        List<PersonajeModel> preparedTeam = new ArrayList<>();
        
        for (PersonajeModel character : team) {
            // Clonar el personaje para no modificar el original
            PersonajeModel clone = cloneCharacter(character);
            
            // Inicializar valores para el combate
            clone.setVidaActual(clone.getVida());
            
            // Asegurarse de que los usos de habilidades están inicializados
            if (clone.getHabilidad1Usos() <= 0) {
                clone.setHabilidad1Usos(3); // Valor por defecto si no está definido
            }
            
            if (clone.getHabilidad2Usos() <= 0) {
                clone.setHabilidad2Usos(2); // Valor por defecto si no está definido
            }
            
            // Asegurarse de que los tipos de ataque estén inicializados
            if (clone.getAtaqueMeleeTipo() == null || clone.getAtaqueMeleeTipo().isEmpty()) {
                clone.setAtaqueMeleeTipo("fisico"); // Valor por defecto
            }
            
            if (clone.getAtaqueLejanoTipo() == null || clone.getAtaqueLejanoTipo().isEmpty()) {
                clone.setAtaqueLejanoTipo("fisico"); // Valor por defecto
            }
            
            if (clone.getHabilidad1Tipo() == null || clone.getHabilidad1Tipo().isEmpty()) {
                clone.setHabilidad1Tipo("magico"); // Valor por defecto
            }
            
            if (clone.getHabilidad2Tipo() == null || clone.getHabilidad2Tipo().isEmpty()) {
                clone.setHabilidad2Tipo("magico"); // Valor por defecto
            }
            
            preparedTeam.add(clone);
        }
        
        return preparedTeam;
    }
    
    /**
     * Crea una copia del personaje
     * @param original Personaje original
     * @return Copia del personaje
     */
    private PersonajeModel cloneCharacter(PersonajeModel original) {
        PersonajeModel clone = new PersonajeModel();
        
        // Copiar todos los valores
        clone.setId(original.getId());
        clone.setNombreCodigo(original.getNombreCodigo());
        clone.setNombre(original.getNombre());
        clone.setDescripcion(original.getDescripcion());
        
        // Imágenes
        clone.setImagenCombate(original.getImagenCombate());
        clone.setImagenMiniatura(original.getImagenMiniatura());
        clone.setImagenCompleta(original.getImagenCompleta());
        
        // Estadísticas básicas
        clone.setVida(original.getVida());
        clone.setFuerza(original.getFuerza());
        clone.setVelocidad(original.getVelocidad());
        clone.setResistencia(original.getResistencia());
        clone.setPoderMagico(original.getPoderMagico());
        
        // Ataques - nombres, valores y TIPOS
        clone.setAtaqueMeleeNombre(original.getAtaqueMeleeNombre());
        clone.setAtaqueMelee(original.getAtaqueMelee());
        clone.setAtaqueMeleeTipo(original.getAtaqueMeleeTipo());
        
        clone.setAtaqueLejanoNombre(original.getAtaqueLejanoNombre());
        clone.setAtaqueLejano(original.getAtaqueLejano());
        clone.setAtaqueLejanoTipo(original.getAtaqueLejanoTipo());
        
        // Defensas
        clone.setResistenciaFisica(original.getResistenciaFisica());
        clone.setResistenciaMagica(original.getResistenciaMagica());
        clone.setEvasion(original.getEvasion());
        
        // Pasiva COMPLETA
        clone.setPasivaNombre(original.getPasivaNombre());
        clone.setPasivaDescripcion(original.getPasivaDescripcion());
        clone.setPasivaTipo(original.getPasivaTipo());
        clone.setPasivaValor(original.getPasivaValor());
        
        // Habilidades COMPLETAS con tipos y usos
        clone.setHabilidad1Nombre(original.getHabilidad1Nombre());
        clone.setHabilidad1Tipo(original.getHabilidad1Tipo());
        clone.setHabilidad1Poder(original.getHabilidad1Poder());
        clone.setHabilidad1Usos(original.getHabilidad1Usos());
        
        clone.setHabilidad2Nombre(original.getHabilidad2Nombre());
        clone.setHabilidad2Tipo(original.getHabilidad2Tipo());
        clone.setHabilidad2Poder(original.getHabilidad2Poder());
        clone.setHabilidad2Usos(original.getHabilidad2Usos());
        
        // Extras para combate
        clone.setProbabilidadCritico(original.getProbabilidadCritico());
        clone.setMultiplicadorCritico(original.getMultiplicadorCritico());
        
        return clone;
    }
    
    /**
     * Obtiene la lista de personajes del equipo del jugador
     * @return Lista de personajes
     */
    public List<PersonajeModel> getPlayerTeam() {
        return playerTeam;
    }
    
    /**
     * Obtiene la lista de personajes del equipo de la IA
     * @return Lista de personajes
     */
    public List<PersonajeModel> getAITeam() {
        return aiTeam;
    }
    
    /**
     * Limpia ambos equipos
     */
    public void clearTeams() {
        playerTeam.clear();
        aiTeam.clear();
    }
    
    /**
     * Obtiene el tamaño actual del equipo
     * @param isPlayerTeam Si es el equipo del jugador
     * @return Número de personajes en el equipo
     */
    public int getTeamSize(boolean isPlayerTeam) {
        return isPlayerTeam ? playerTeam.size() : aiTeam.size();
    }
}