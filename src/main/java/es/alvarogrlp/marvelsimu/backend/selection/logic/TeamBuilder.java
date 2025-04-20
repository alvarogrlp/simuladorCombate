package es.alvarogrlp.marvelsimu.backend.selection.logic;

import java.util.ArrayList;
import java.util.List;

import es.alvarogrlp.marvelsimu.backend.model.AtaqueModel;
import es.alvarogrlp.marvelsimu.backend.model.PasivaModel;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import es.alvarogrlp.marvelsimu.backend.selection.ui.SelectionUIManager;
import javafx.animation.FadeTransition;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

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
     * Elimina un personaje del equipo de manera limpia
     * @param character Personaje a eliminar
     * @param isPlayerTeam Si es del equipo del jugador
     * @param uiManager Gestor de UI para actualizar la interfaz
     * @return true si se eliminó correctamente
     */
    public boolean removeCharacterFromTeam(PersonajeModel character, boolean isPlayerTeam, SelectionUIManager uiManager) {
        if (character == null || uiManager == null) {
            return false;
        }

        List<PersonajeModel> targetTeam = isPlayerTeam ? playerTeam : aiTeam;
        
        // Verificar si el equipo está vacío
        if (targetTeam.isEmpty()) {
            return false;
        }
        
        // Buscar el índice del personaje en el equipo
        int index = -1;
        for (int i = 0; i < targetTeam.size(); i++) {
            PersonajeModel existing = targetTeam.get(i);
            if (existing != null && existing.getNombreCodigo().equals(character.getNombreCodigo())) {
                index = i;
                break;
            }
        }
        
        // Si se encontró, eliminarlo
        if (index >= 0) {
            // Primero guardar una copia del personaje para evitar problemas de referencia
            PersonajeModel removedCharacter = targetTeam.get(index);
            
            // Eliminar del modelo de datos
            targetTeam.remove(index);
            
            try {
                // Luego animamos la salida en la UI y actualizamos la visualización
                uiManager.animateRemoveCharacterCard(index, isPlayerTeam);
                
                // Mostrar mensaje de eliminación
                String equipo = isPlayerTeam ? "tu equipo" : "equipo IA";
                uiManager.showInfoMessage(removedCharacter.getNombre() + " eliminado de " + equipo);
                
                // Actualizar el estado del botón de lucha
                uiManager.updateFightButtonState();
                
                return true;
            } catch (Exception e) {
                // En caso de error en la UI, asegurarnos de que el modelo siga siendo consistente
                System.err.println("Error al eliminar personaje de la UI: " + e.getMessage());
                
                // Forzar actualización de la UI sin animación
                try {
                    uiManager.forceUpdateTeamDisplay(isPlayerTeam);
                } catch (Exception ex) {
                    System.err.println("Error al forzar actualización de la UI: " + ex.getMessage());
                }
                
                return true; // El personaje se eliminó del modelo, aunque haya fallado la UI
            }
        }
        
        return false; // No se encontró el personaje
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
            // Usar el método clonar de PersonajeModel en lugar de nuestro propio método
            PersonajeModel clone = character.clonar();
            
            // Inicializar la vida y el estado de combate
            clone.inicializarVida();
            
            // Verificar que todos los ataques y pasivas tienen valores válidos
            validarYConfigurarAtaques(clone);
            validarYConfigurarPasivas(clone);
            
            preparedTeam.add(clone);
        }
        
        return preparedTeam;
    }
    
    /**
     * Valida y configura los ataques de un personaje
     * @param personaje Personaje a validar
     */
    private void validarYConfigurarAtaques(PersonajeModel personaje) {
        List<AtaqueModel> ataques = personaje.getAtaques();
        
        // Si no hay ataques, crear ataques por defecto basados en los atributos antiguos
        if (ataques == null || ataques.isEmpty()) {
            ataques = new ArrayList<>();
            
            // Crear ataques básicos por defecto
            AtaqueModel ataqueCC = new AtaqueModel();
            ataqueCC.setPersonajeId(personaje.getId());
            ataqueCC.setTipoAtaqueClave("ACC");
            ataqueCC.setNombre("Ataque Cuerpo a Cuerpo");
            ataqueCC.setDanoBase(personaje.getFuerza() / 2);
            ataqueCC.resetearEstadoCombate();
            ataques.add(ataqueCC);
            
            AtaqueModel ataqueAD = new AtaqueModel();
            ataqueAD.setPersonajeId(personaje.getId());
            ataqueAD.setTipoAtaqueClave("AAD");
            ataqueAD.setNombre("Ataque a Distancia");
            ataqueAD.setDanoBase(personaje.getFuerza() / 3);
            ataqueAD.resetearEstadoCombate();
            ataques.add(ataqueAD);
            
            AtaqueModel habilidad1 = new AtaqueModel();
            habilidad1.setPersonajeId(personaje.getId());
            habilidad1.setTipoAtaqueClave("habilidad_mas_poderosa");
            habilidad1.setNombre("Habilidad Especial");
            habilidad1.setDanoBase(personaje.getFuerza() + personaje.getPoder() / 4);
            habilidad1.setUsosMaximos(3);
            habilidad1.setCooldownTurnos(1);
            habilidad1.resetearEstadoCombate();
            ataques.add(habilidad1);
            
            AtaqueModel habilidad2 = new AtaqueModel();
            habilidad2.setPersonajeId(personaje.getId());
            habilidad2.setTipoAtaqueClave("habilidad_caracteristica");
            habilidad2.setNombre("Habilidad Definitiva");
            habilidad2.setDanoBase(personaje.getFuerza() + personaje.getPoder() / 2);
            habilidad2.setUsosMaximos(1);
            habilidad2.setCooldownTurnos(3);
            habilidad2.resetearEstadoCombate();
            ataques.add(habilidad2);
            
            personaje.setAtaques(ataques);
        } else {
            // Asegurarse de que todos los ataques estén inicializados para el combate
            for (AtaqueModel ataque : ataques) {
                ataque.resetearEstadoCombate();
            }
        }
    }
    
    /**
     * Valida y configura las pasivas de un personaje
     * @param personaje Personaje a validar
     */
    private void validarYConfigurarPasivas(PersonajeModel personaje) {
        List<PasivaModel> pasivas = personaje.getPasivas();
        
        // Si no hay pasivas, crear una pasiva por defecto
        if (pasivas == null || pasivas.isEmpty()) {
            pasivas = new ArrayList<>();
            
            // Crear una pasiva básica basada en los atributos del personaje
            PasivaModel pasiva = new PasivaModel();
            pasiva.setPersonajeId(personaje.getId());
            
            // La pasiva dependerá del atributo más alto
            if (personaje.getPoder() > personaje.getFuerza() && personaje.getPoder() > personaje.getVelocidad()) {
                // Personaje enfocado en poder - Pasiva de escudo
                pasiva.setNombre("Barrera Mágica");
                pasiva.setDescripcion("Al inicio del combate, el personaje obtiene un escudo que reduce el daño");
                pasiva.setTriggerTipo("on_start_combat");
                pasiva.setEfectoTipo("shield_pct");
                pasiva.setEfectoValor(20);
            } else if (personaje.getVelocidad() > personaje.getFuerza()) {
                // Personaje enfocado en velocidad - Pasiva de evasión
                pasiva.setNombre("Reflejos Rápidos");
                pasiva.setDescripcion("Tiene probabilidad de esquivar ataques enemigos");
                pasiva.setTriggerTipo("on_damage_taken");
                pasiva.setEfectoTipo("dodge_chance_pct");
                pasiva.setEfectoValor(15);
            } else {
                // Personaje enfocado en fuerza - Pasiva de contraataque
                pasiva.setNombre("Contraataque");
                pasiva.setDescripcion("Al recibir daño, tiene posibilidad de contraatacar");
                pasiva.setTriggerTipo("on_damage_taken");
                pasiva.setEfectoTipo("counter_pct");
                pasiva.setEfectoValor(25);
            }
            
            pasiva.resetearEstadoCombate();
            pasivas.add(pasiva);
            personaje.setPasivas(pasivas);
        } else {
            // Asegurarse de que todas las pasivas estén inicializadas para el combate
            for (PasivaModel pasiva : pasivas) {
                pasiva.resetearEstadoCombate();
            }
        }
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