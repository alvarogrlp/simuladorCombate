package es.alvarogrlp.marvelsimu.backend.selection.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import es.alvarogrlp.marvelsimu.backend.model.AtaqueModel;
import es.alvarogrlp.marvelsimu.backend.model.PasivaModel;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeServiceModel;
import es.alvarogrlp.marvelsimu.backend.selection.ui.SelectionUIManager;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

/**
 * Clase principal que gestiona la lógica de selección de personajes
 */
public class SelectionManager {
    
    private final int MAX_TEAM_SIZE = 3;
    private int selectionsCount = 0;
    
    private PersonajeModel currentCharacter;
    private Button currentButton;
    private Button oldButton;
    
    private Map<String, PersonajeModel> charactersMap = new HashMap<>();
    private Map<String, PersonajeModel> transformationsMap = new HashMap<>();
    private TeamBuilder teamBuilder;
    private SelectionUIManager uiManager;
    
    /**
     * Mapa para almacenar los botones dinámicos y sus personajes asociados
     */
    private Map<Button, PersonajeModel> dynamicButtonsMap = new HashMap<>();
    
    /**
     * Constructor
     * @param rootPane Panel raíz para la UI
     */
    public SelectionManager(AnchorPane rootPane) {
        this.teamBuilder = new TeamBuilder(MAX_TEAM_SIZE);
        this.uiManager = new SelectionUIManager(rootPane, this);
        loadCharacters();
    }
    
    /**
     * Inicializa la interfaz de usuario
     */
    public void initializeUI() {
        uiManager.setupUI();
    }
    
    /**
     * Carga todos los personajes desde la base de datos
     */
    private void loadCharacters() {
        try {
            // Obtener todos los personajes
            PersonajeServiceModel personajeService = new PersonajeServiceModel();
            List<PersonajeModel> allCharacters = personajeService.obtenerTodosPersonajes();
            
            // Lista de transformaciones que deben ser excluidas del mapa principal
            List<String> transformationCodes = List.of(
                "magik_darkchild", 
                "thanos_gauntlet"
            );
            
            // Filtrar sólo los personajes jugables (no transformaciones)
            for (PersonajeModel character : allCharacters) {
                String codigo = character.getNombreCodigo().toLowerCase();
                
                if (!character.isEsTransformacion() && !transformationCodes.contains(codigo)) {
                    // Personaje jugable normal
                    charactersMap.put(character.getNombreCodigo(), character);
                } else {
                    // Transformación - guardar en mapa separado
                    transformationsMap.put(character.getNombreCodigo(), character);
                    character.setEsTransformacion(true); // Asegurar que esté marcado
                }
            }
            
            System.out.println("Se cargaron " + charactersMap.size() + " personajes jugables");
            System.out.println("Se cargaron " + transformationsMap.size() + " transformaciones");
            
        } catch (Exception e) {
            System.err.println("Error cargando personajes: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Crea personajes por defecto en caso de que no se puedan cargar de la BD
     * Se adapta para usar el nuevo modelo con ataques y pasivas
     */
    private void createDefaultCharacters() {
        // Captain America
        PersonajeModel captain = new PersonajeModel();
        captain.setNombreCodigo("captain-america");
        captain.setNombre("Captain America");
        captain.setVida(850);
        captain.setFuerza(80);
        captain.setVelocidad(70);
        
        // Crear ataques de Captain America
        List<AtaqueModel> captainAtaques = new ArrayList<>();
        
        AtaqueModel captainCC = new AtaqueModel();
        captainCC.setTipoAtaqueClave("ACC");
        captainCC.setNombre("Golpe de Escudo");
        captainCC.setDanoBase(65);
        captainAtaques.add(captainCC);
        
        AtaqueModel captainAD = new AtaqueModel();
        captainAD.setTipoAtaqueClave("AAD");
        captainAD.setNombre("Lanzamiento de Escudo");
        captainAD.setDanoBase(55);
        captainAtaques.add(captainAD);
        
        AtaqueModel captainH1 = new AtaqueModel();
        captainH1.setTipoAtaqueClave("habilidad_mas_poderosa");
        captainH1.setNombre("Escudo Indestructible");
        captainH1.setDanoBase(120);
        captainH1.setUsosMaximos(3);
        captainH1.setCooldownTurnos(2);
        captainAtaques.add(captainH1);
        
        AtaqueModel captainH2 = new AtaqueModel();
        captainH2.setTipoAtaqueClave("habilidad_caracteristica");
        captainH2.setNombre("¡Vengadores Unidos!");
        captainH2.setDanoBase(180);
        captainH2.setUsosMaximos(2);
        captainH2.setCooldownTurnos(3);
        captainAtaques.add(captainH2);
        
        captain.setAtaques(captainAtaques);
        
        // Crear pasiva de Captain America
        List<PasivaModel> captainPasivas = new ArrayList<>();
        
        PasivaModel captainPasiva = new PasivaModel();
        captainPasiva.setNombre("Voluntad Inquebrantable");
        captainPasiva.setDescripcion("Reduce el daño recibido en un 30%");
        captainPasiva.setTriggerTipo("on_damage_taken");
        captainPasiva.setEfectoTipo("reduce_damage_pct");
        captainPasiva.setEfectoValor(30);
        captainPasivas.add(captainPasiva);
        
        captain.setPasivas(captainPasivas);
        
        // Hulk
        PersonajeModel hulk = new PersonajeModel();
        hulk.setNombreCodigo("hulk");
        hulk.setNombre("Hulk");
        hulk.setVida(1200);
        hulk.setFuerza(100);
        hulk.setVelocidad(60);
        
        // Crear ataques de Hulk
        List<AtaqueModel> hulkAtaques = new ArrayList<>();
        
        AtaqueModel hulkCC = new AtaqueModel();
        hulkCC.setTipoAtaqueClave("ACC");
        hulkCC.setNombre("Puños de Furia");
        hulkCC.setDanoBase(85);
        hulkAtaques.add(hulkCC);
        
        AtaqueModel hulkAD = new AtaqueModel();
        hulkAD.setTipoAtaqueClave("AAD");
        hulkAD.setNombre("Aplauso Sónico");
        hulkAD.setDanoBase(70);
        hulkAtaques.add(hulkAD);
        
        AtaqueModel hulkH1 = new AtaqueModel();
        hulkH1.setTipoAtaqueClave("habilidad_mas_poderosa");
        hulkH1.setNombre("Salto Aplastante");
        hulkH1.setDanoBase(150);
        hulkH1.setUsosMaximos(3);
        hulkH1.setCooldownTurnos(2);
        hulkAtaques.add(hulkH1);
        
        AtaqueModel hulkH2 = new AtaqueModel();
        hulkH2.setTipoAtaqueClave("habilidad_caracteristica");
        hulkH2.setNombre("Ira Incontrolable");
        hulkH2.setDanoBase(200);
        hulkH2.setUsosMaximos(2);
        hulkH2.setCooldownTurnos(4);
        hulkAtaques.add(hulkH2);
        
        hulk.setAtaques(hulkAtaques);
        
        // Crear pasiva de Hulk
        List<PasivaModel> hulkPasivas = new ArrayList<>();
        
        PasivaModel hulkPasiva = new PasivaModel();
        hulkPasiva.setNombre("Regeneración Gamma");
        hulkPasiva.setDescripcion("Recupera un 5% de vida al inicio de cada turno");
        hulkPasiva.setTriggerTipo("on_turn_start");
        hulkPasiva.setEfectoTipo("heal_pct");
        hulkPasiva.setEfectoValor(5);
        hulkPasivas.add(hulkPasiva);
        
        hulk.setPasivas(hulkPasivas);
        
        // Iron Man
        PersonajeModel ironman = new PersonajeModel();
        ironman.setNombreCodigo("ironman");
        ironman.setNombre("Iron Man");
        ironman.setVida(750);
        ironman.setFuerza(70);
        ironman.setVelocidad(85);
        ironman.setPoder(400);
        
        // Crear ataques de Iron Man
        List<AtaqueModel> ironmanAtaques = new ArrayList<>();
        
        AtaqueModel ironmanCC = new AtaqueModel();
        ironmanCC.setTipoAtaqueClave("ACC");
        ironmanCC.setNombre("Puño Repulsor");
        ironmanCC.setDanoBase(60);
        ironmanAtaques.add(ironmanCC);
        
        AtaqueModel ironmanAD = new AtaqueModel();
        ironmanAD.setTipoAtaqueClave("AAD");
        ironmanAD.setNombre("Rayo Repulsor");
        ironmanAD.setDanoBase(75);
        ironmanAtaques.add(ironmanAD);
        
        AtaqueModel ironmanH1 = new AtaqueModel();
        ironmanH1.setTipoAtaqueClave("habilidad_mas_poderosa");
        ironmanH1.setNombre("Misiles Inteligentes");
        ironmanH1.setDanoBase(140);
        ironmanH1.setUsosMaximos(3);
        ironmanH1.setCooldownTurnos(2);
        ironmanAtaques.add(ironmanH1);
        
        AtaqueModel ironmanH2 = new AtaqueModel();
        ironmanH2.setTipoAtaqueClave("habilidad_caracteristica");
        ironmanH2.setNombre("Unirrayo");
        ironmanH2.setDanoBase(190);
        ironmanH2.setUsosMaximos(2);
        ironmanH2.setCooldownTurnos(3);
        ironmanAtaques.add(ironmanH2);
        
        ironman.setAtaques(ironmanAtaques);
        
        // Crear pasiva de Iron Man
        List<PasivaModel> ironmanPasivas = new ArrayList<>();
        
        PasivaModel ironmanPasiva = new PasivaModel();
        ironmanPasiva.setNombre("Escudo de Energía");
        ironmanPasiva.setDescripcion("Al inicio del combate, obtiene un escudo que absorbe el 25% del daño");
        ironmanPasiva.setTriggerTipo("on_start_combat");
        ironmanPasiva.setEfectoTipo("shield_pct");
        ironmanPasiva.setEfectoValor(25);
        ironmanPasivas.add(ironmanPasiva);
        
        ironman.setPasivas(ironmanPasivas);
        
        // Agregar las imágenes a los personajes
        captain.setImagenMiniatura("images/Personajes/captain-america.png");
        captain.setImagenCombate("images/Ingame/captain-america-ingame.png");
        
        hulk.setImagenMiniatura("images/Personajes/hulk.png");
        hulk.setImagenCombate("images/Ingame/hulk-ingame.png");
        
        ironman.setImagenMiniatura("images/Personajes/ironman.png");
        ironman.setImagenCombate("images/Ingame/ironman-ingame.png");
        
        // Agregar al mapa
        charactersMap.put(captain.getNombreCodigo(), captain);
        charactersMap.put(hulk.getNombreCodigo(), hulk);
        charactersMap.put(ironman.getNombreCodigo(), ironman);
        
        System.out.println("Se crearon " + charactersMap.size() + " personajes por defecto");
    }
    
    /**
     * Selecciona un personaje cuando se hace clic en su botón
     */
    public void selectCharacter(PersonajeModel character, Button button) {
        try {
            // Limpiar resaltado de todos los botones
            uiManager.clearAllButtonsHighlight();
            
            // Guardar referencias a la selección actual
            currentCharacter = character;
            currentButton = button;
            oldButton = button;
            
            // Resaltar el botón seleccionado
            button.getStyleClass().add("selected-character");
            
            // Mostrar información del personaje
            uiManager.showCharacterInfo(character);
        } catch (Exception e) {
            System.err.println("Error en selectCharacter: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Maneja la confirmación de selección (botón "SELECCIONAR" en el panel de información)
     */
    public void confirmSelection() {
        // Verificar que hay un personaje seleccionado
        if (currentCharacter == null) {
            return;
        }
        
        // Ocultar el panel de información
        uiManager.hideCharacterInfo();
        
        // Verificar si el personaje ya está en algún equipo
        boolean isInPlayerTeam = teamBuilder.isCharacterInTeam(currentCharacter, true);
        boolean isInAITeam = teamBuilder.isCharacterInTeam(currentCharacter, false);
        
        // Si ya está en algún equipo, eliminarlo primero
        if (isInPlayerTeam) {
            // Crear una copia local para evitar NullPointerException después de limpiar currentCharacter
            String nombrePersonaje = currentCharacter.getNombre();
            
            if (teamBuilder.removeCharacterFromTeam(currentCharacter, true, uiManager)) {
                // Limpiar resaltado del botón solo si la eliminación fue exitosa
                uiManager.clearButtonHighlight(currentButton);
                
                // Restaurar estado del botón
                if (currentButton != null) {
                    currentButton.setDisable(false);
                }
                
                // Limpiar selección actual
                currentCharacter = null;
                currentButton = null;
                
                // Verificar estado del botón de luchar
                updateFightButtonState();
                
                // Mensaje de confirmación de eliminación
                uiManager.showInfoMessage(nombrePersonaje + " eliminado de tu equipo");
            }
            
            return;
        } else if (isInAITeam) {
            // Crear una copia local para evitar NullPointerException después de limpiar currentCharacter
            String nombrePersonaje = currentCharacter.getNombre();
            
            if (teamBuilder.removeCharacterFromTeam(currentCharacter, false, uiManager)) {
                // Limpiar resaltado del botón solo si la eliminación fue exitosa
                uiManager.clearButtonHighlight(currentButton);
                
                // Restaurar estado del botón
                if (currentButton != null) {
                    currentButton.setDisable(false);
                }
                
                // Limpiar selección actual
                currentCharacter = null;
                currentButton = null;
                
                // Verificar estado del botón de luchar
                updateFightButtonState();
                
                // Mensaje de confirmación de eliminación
                uiManager.showInfoMessage(nombrePersonaje + " eliminado del equipo IA");
            }
            
            return;
        }
        
        // Aquí está el cambio: SIEMPRE intentar añadir al equipo del jugador si hay espacio,
        // sin importar si el equipo de la IA tiene espacio o no
        if (teamBuilder.canAddToPlayerTeam()) {
            if (teamBuilder.addCharacterToTeam(currentCharacter, currentButton, true, uiManager)) {
                // Deshabilitar su botón para evitar seleccionarlo de nuevo
                currentButton.setDisable(true);
                
                // Mensaje de confirmación
                uiManager.showInfoMessage(currentCharacter.getNombre() + " añadido a tu equipo");
                
                // Limpiar selección actual
                currentCharacter = null;
                currentButton = null;
                
                // Actualizar estado del botón de luchar
                updateFightButtonState();
            }
        } else if (teamBuilder.canAddToAITeam()) {
            // Solo si el equipo del jugador está lleno, añadir al equipo de IA
            if (teamBuilder.addCharacterToTeam(currentCharacter, currentButton, false, uiManager)) {
                // Deshabilitar su botón para evitar seleccionarlo de nuevo
                currentButton.setDisable(true);
                
                // Mensaje de confirmación
                uiManager.showInfoMessage(currentCharacter.getNombre() + " añadido al equipo IA");
                
                // Limpiar selección actual
                currentCharacter = null;
                currentButton = null;
                
                // Actualizar estado del botón de luchar
                updateFightButtonState();
            }
        } else {
            uiManager.showErrorMessage("Ambos equipos están completos (3/3)");
        }
    }
    
    /**
     * Encuentra el botón correspondiente a un personaje
     * @param character Personaje a buscar
     * @return El botón correspondiente o null si no se encuentra
     */
    public Button findCharacterButton(PersonajeModel character) {
        if (character == null) return null;
        
        // Primero buscar en los botones dinámicos
        for (Map.Entry<Button, PersonajeModel> entry : dynamicButtonsMap.entrySet()) {
            if (entry.getValue().getNombreCodigo().equals(character.getNombreCodigo())) {
                return entry.getKey();
            }
        }
        
        // Si no se encuentra, buscar en los botones predefinidos
        String nombre = character.getNombre().toLowerCase();
        
        if (nombre.contains("captain") || nombre.contains("america")) {
            return uiManager.getCaptainButton();
        } else if (nombre.contains("hulk")) {
            return uiManager.getHulkButton();
        } else if (nombre.contains("iron") || nombre.contains("stark")) {
            return uiManager.getIronManButton();
        } else if (nombre.contains("spider") || nombre.contains("parker")) {
            return uiManager.getSpiderManButton();
        } else if (nombre.contains("strange") || nombre.contains("doctor")) {
            return uiManager.getDrStrangeButton();
        } else if (nombre.contains("magik")) {
            return uiManager.getMagikButton();
        }
        
        return null;
    }
    
    /**
     * Deselecciona el personaje actual
     */
    public void deselectCurrentCharacter() {
        // Verificar que hay un personaje seleccionado
        if (currentCharacter == null) {
            return;
        }
        
        // Asegurarse de que la UI se actualice correctamente
        uiManager.hideCharacterInfo();
        uiManager.clearAllButtonsHighlight();
        
        // Limpiar selección actual
        Button oldButton = currentButton;
        currentCharacter = null;
        currentButton = null;
        
        // Restaurar estado del botón si es necesario
        if (oldButton != null) {
            oldButton.setDisable(false);
        }
    }
    
    /**
     * Prepara los equipos para el combate
     * @return True si la preparación fue exitosa
     */
    public boolean prepareTeamsForCombat() {
        // Verificar que ambos equipos estén completos
        if (!teamBuilder.isPlayerTeamComplete() || !teamBuilder.isAITeamComplete()) {
            uiManager.showErrorMessage("Ambos equipos deben estar completos antes de iniciar el combate");
            return false;
        }
        
        // Preparar los equipos para el combate
        List<PersonajeModel> preparedPlayerTeam = teamBuilder.prepareTeam(teamBuilder.getPlayerTeam());
        List<PersonajeModel> preparedAITeam = teamBuilder.prepareTeam(teamBuilder.getAITeam());
        
        // Log para verificar la preparación
        System.out.println("Personajes jugador enviados: " + preparedPlayerTeam.stream()
                          .map(p -> p.getNombre())
                          .reduce("", (a, b) -> a.isEmpty() ? b : a + ", " + b));
                          
        System.out.println("Personajes IA enviados: " + preparedAITeam.stream()
                          .map(p -> p.getNombre())
                          .reduce("", (a, b) -> a.isEmpty() ? b : a + ", " + b));
        
        // Pasar los personajes preparados al controlador de combate
        es.alvarogrlp.marvelsimu.backend.controller.CombateController.setPersonajesSeleccionados(preparedPlayerTeam, preparedAITeam);
        
        return true;
    }
    
    /**
     * Maneja la adición o eliminación de un personaje al equipo
     * @param character Personaje a añadir
     */
    public void addCharacterToTeam(PersonajeModel character) {
        // Ocultar el panel de información
        uiManager.hideCharacterInfo();
        
        // Verificar si el personaje ya está en algún equipo
        boolean isInPlayerTeam = teamBuilder.isCharacterInTeam(character, true);
        boolean isInAITeam = teamBuilder.isCharacterInTeam(character, false);
        
        // Si ya está en algún equipo, eliminarlo primero
        if (isInPlayerTeam) {
            if (teamBuilder.removeCharacterFromTeam(character, true, uiManager)) {
                // Limpiar resaltado del botón solo si la eliminación fue exitosa
                uiManager.clearButtonHighlight(currentButton);
                
                // Restaurar estado del botón
                if (currentButton != null) {
                    currentButton.setDisable(false);
                }
                
                // Limpiar selección actual
                currentCharacter = null;
                currentButton = null;
                
                // Verificar estado del botón de luchar
                updateFightButtonState();
                
                // Mensaje de confirmación de eliminación
                uiManager.showInfoMessage(character.getNombre() + " eliminado de tu equipo");
                return;
            }
        } else if (isInAITeam) {
            if (teamBuilder.removeCharacterFromTeam(character, false, uiManager)) {
                // Limpiar resaltado del botón solo si la eliminación fue exitosa
                uiManager.clearButtonHighlight(currentButton);
                
                // Restaurar estado del botón
                if (currentButton != null) {
                    currentButton.setDisable(false);
                }
                
                // Limpiar selección actual
                currentCharacter = null;
                currentButton = null;
                
                // Verificar estado del botón de luchar
                updateFightButtonState();
                
                // Mensaje de confirmación de eliminación
                uiManager.showInfoMessage(character.getNombre() + " eliminado del equipo IA");
                return;
            }
        }
        
        // Determinar a qué equipo añadir el personaje
        boolean addToPlayerTeam = !teamBuilder.isPlayerTeamComplete();
        
        // Si el equipo del jugador está lleno, intentar añadir al equipo de IA
        if (addToPlayerTeam) {
            if (teamBuilder.canAddToPlayerTeam()) {
                teamBuilder.addCharacterToTeam(character, currentButton, true, uiManager);
                uiManager.showInfoMessage(character.getNombre() + " añadido a tu equipo");
                
                // Actualizar estado del botón
                currentButton.setDisable(true);
                
                // Limpiar selección actual
                currentCharacter = null;
                currentButton = null;
                
                // Verificar estado del botón de luchar
                updateFightButtonState();
            } else {
                uiManager.showErrorMessage("Tu equipo está completo (3/3)");
            }
        } else {
            // Intentar añadir al equipo de IA
            if (teamBuilder.canAddToAITeam()) {
                teamBuilder.addCharacterToTeam(character, currentButton, false, uiManager);
                uiManager.showInfoMessage(character.getNombre() + " añadido al equipo IA");
                
                // Actualizar estado del botón
                currentButton.setDisable(true);
                
                // Limpiar selección actual
                currentCharacter = null;
                currentButton = null;
                
                // Verificar estado del botón de luchar
                updateFightButtonState();
            } else {
                uiManager.showErrorMessage("El equipo IA está completo (3/3)");
            }
        }
    }
    
    /**
     * Actualiza el estado del botón de luchar según si hay personajes suficientes
     */
    private void updateFightButtonState() {
        if (teamBuilder.isPlayerTeamComplete() && teamBuilder.isAITeamComplete()) {
            uiManager.enableFightButton();
        } else {
            uiManager.disableFightButton();
        }
    }
    
    /**
     * Maneja la eliminación de un personaje sin actualizar la UI directamente
     * @param character Personaje a eliminar
     * @param isPlayerTeam Si es del equipo del jugador (true) o IA (false)
     */
    public void handleCharacterRemoval(PersonajeModel character, boolean isPlayerTeam) {
        if (character == null) return;
        
        // Actualizar el modelo de datos directamente
        List<PersonajeModel> team = isPlayerTeam ?
                                   teamBuilder.getPlayerTeam() :
                                   teamBuilder.getAITeam();
        
        // Eliminar personaje del equipo
        team.removeIf(p -> p.getNombreCodigo().equals(character.getNombreCodigo()));
        
        // Mostrar mensaje específico según el equipo
        String mensaje = character.getNombre() + " eliminado del " +
                        (isPlayerTeam ? "equipo del jugador" : "equipo de la IA");
        uiManager.showInfoMessage(mensaje);
        
        // Encontrar y habilitar el botón original
        Button button = findCharacterButton(character);
        if (button != null) {
            button.setDisable(false);
        }
        
        // Actualizar estado del botón de luchar
        updateFightButtonState();
    }

    /**
     * Elimina un personaje del equipo
     * @param character Personaje a eliminar
     * @param isPlayerTeam Si es del equipo del jugador (true) o IA (false)
     * @return true si se eliminó correctamente
     */
    public boolean removeCharacterFromTeam(PersonajeModel character, boolean isPlayerTeam) {
        if (character == null) {
            return false;
        }
        
        // Buscar el botón asociado primero
        Button button = findCharacterButton(character);
        
        // Intentar eliminarlo del equipo
        boolean removed = teamBuilder.removeCharacterFromTeam(character, isPlayerTeam, uiManager);
        
        if (removed) {
            // Reactivar el botón si se ha encontrado
            if (button != null) {
                button.setDisable(false);
            }
            
            // Actualizar estado del botón de luchar
            updateFightButtonState();
        }
        
        return removed;
    }
    
    /**
     * Verifica si el equipo del jugador está completo
     * @return true si el equipo del jugador tiene el número máximo de personajes
     */
    public boolean isPlayerTeamComplete() {
        return teamBuilder.isPlayerTeamComplete();
    }

    /**
     * Verifica si el equipo de la IA está completo
     * @return true si el equipo de la IA tiene el número máximo de personajes
     */
    public boolean isAITeamComplete() {
        return teamBuilder.isAITeamComplete();
    }
    
    /**
     * Selecciona un personaje aleatorio cuando se hace clic en el botón de selección aleatoria
     */
    public void selectRandomCharacter() {
        try {
            // Obtener todos los personajes disponibles que no estén en ningún equipo
            List<PersonajeModel> availableCharacters = new ArrayList<>();
            
            for (PersonajeModel character : charactersMap.values()) {
                // Verificar si ya está en algún equipo
                boolean isInPlayerTeam = teamBuilder.isCharacterInTeam(character, true);
                boolean isInAITeam = teamBuilder.isCharacterInTeam(character, false);
                
                // Si no está en ningún equipo, añadirlo a la lista de disponibles
                if (!isInPlayerTeam && !isInAITeam) {
                    availableCharacters.add(character);
                }
            }
            
            // Si no hay personajes disponibles, mostrar un mensaje y salir
            if (availableCharacters.isEmpty()) {
                uiManager.showErrorMessage("No hay personajes disponibles para seleccionar");
                return;
            }
            
            // Seleccionar un personaje aleatorio
            Random random = new Random();
            PersonajeModel randomCharacter = availableCharacters.get(random.nextInt(availableCharacters.size()));
            
            // Encontrar el botón correspondiente
            Button button = null;
            for (Map.Entry<Button, PersonajeModel> entry : dynamicButtonsMap.entrySet()) {
                if (entry.getValue().getNombreCodigo().equals(randomCharacter.getNombreCodigo())) {
                    button = entry.getKey();
                    break;
                }
            }
            
            // Si encontramos el botón, seleccionar el personaje
            if (button != null) {
                selectCharacter(randomCharacter, button);
            } else {
                // Buscar por los botones específicos
                button = findCharacterButton(randomCharacter);
                if (button != null) {
                    selectCharacter(randomCharacter, button);
                } else {
                    uiManager.showErrorMessage("No se pudo encontrar el botón para el personaje aleatorio");
                }
            }
        } catch (Exception e) {
            System.err.println("Error al seleccionar personaje aleatorio: " + e.getMessage());
            e.printStackTrace();
            uiManager.showErrorMessage("Error al seleccionar personaje aleatorio");
        }
    }
    
    /**
     * Añade un personaje aleatorio directamente al equipo
     */
    public void addRandomCharacterToTeam() {
        // Obtener personajes disponibles (que no estén en ningún equipo)
        List<PersonajeModel> availableCharacters = new ArrayList<>();
        
        for (PersonajeModel character : charactersMap.values()) {
            // Solo incluir personajes no transformaciones que no estén en ningún equipo
            if (!teamBuilder.isCharacterInTeam(character, true) && 
                !teamBuilder.isCharacterInTeam(character, false) && 
                !character.isEsTransformacion()) {
                availableCharacters.add(character);
            }
        }
        
        // Si no hay personajes disponibles, mostrar mensaje y salir
        if (availableCharacters.isEmpty()) {
            uiManager.showErrorMessage("No hay personajes disponibles");
            return;
        }
        
        // Seleccionar personaje aleatorio
        Random random = new Random();
        PersonajeModel randomCharacter = availableCharacters.get(random.nextInt(availableCharacters.size()));
        
        // Encontrar el botón correspondiente
        Button button = null;
        for (Map.Entry<Button, PersonajeModel> entry : dynamicButtonsMap.entrySet()) {
            if (entry.getValue().getNombreCodigo().equals(randomCharacter.getNombreCodigo())) {
                button = entry.getKey();
                break;
            }
        }
        
        // Agregar al equipo del jugador o IA según disponibilidad
        if (button != null) {
            if (teamBuilder.canAddToPlayerTeam()) {
                teamBuilder.addCharacterToTeam(randomCharacter, button, true, uiManager);
                button.setDisable(true);
            } else if (teamBuilder.canAddToAITeam()) {
                teamBuilder.addCharacterToTeam(randomCharacter, button, false, uiManager);
                button.setDisable(true);
            } else {
                uiManager.showErrorMessage("Ambos equipos están completos");
            }
            
            // Actualizar estado del botón de luchar
            updateFightButtonState();
        }
    }
    
    // Resto de métodos sin cambios... (seleccionRandomCharacter, registerDynamicButton, etc.)
    
    // Getters y setters
    
    public Map<String, PersonajeModel> getCharactersMap() {
        return charactersMap;
    }
    
    public int getMaxTeamSize() {
        return MAX_TEAM_SIZE;
    }
    
    public int getSelectionsCount() {
        return selectionsCount;
    }
    
    public PersonajeModel getCurrentCharacter() {
        return currentCharacter;
    }
    
    public Button getCurrentButton() {
        return currentButton;
    }
    
    /**
     * Obtiene el gestor de UI
     * @return El gestor de UI
     */
    public SelectionUIManager getUIManager() {
        return uiManager;
    }
    
    /**
     * Registra un botón dinámico con su personaje asociado
     * @param button Botón a registrar
     * @param character Personaje asociado al botón
     */
    public void registerDynamicButton(Button button, PersonajeModel character) {
        if (button != null && character != null) {
            dynamicButtonsMap.put(button, character);
        }
    }

    /**
     * Obtiene el mapa de botones dinámicos
     * @return Mapa con los botones y sus personajes asociados
     */
    public Map<Button, PersonajeModel> getDynamicButtonsMap() {
        return dynamicButtonsMap;
    }
    
    /**
     * Limpia el resaltado de todos los botones de personajes
     */
    public void clearAllButtonsHighlight() {
        try {
            // Usar el mapa de botones dinámicos directamente
            for (Button button : dynamicButtonsMap.keySet()) {
                if (button != null) {
                    button.getStyleClass().remove("selected-character");
                }
            }
            
            // Limpiar también los botones específicos que podemos obtener del uiManager
            if (uiManager != null) {
                clearButtonHighlightIfExists(uiManager.getCaptainButton());
                clearButtonHighlightIfExists(uiManager.getHulkButton());
                clearButtonHighlightIfExists(uiManager.getIronManButton());
                clearButtonHighlightIfExists(uiManager.getSpiderManButton());
                clearButtonHighlightIfExists(uiManager.getDrStrangeButton());
                clearButtonHighlightIfExists(uiManager.getMagikButton());
                clearButtonHighlightIfExists(uiManager.getRandomButton());
            }
            
        } catch (Exception e) {
            System.err.println("Error al limpiar resaltado de botones: " + e.getMessage());
        }
    }

    /**
     * Limpia el resaltado de un botón específico si existe
     */
    private void clearButtonHighlightIfExists(Button button) {
        if (button != null) {
            button.getStyleClass().remove("selected-character");
        }
    }
    
    /**
     * Obtiene el mapa de transformaciones
     * @return Mapa con las transformaciones y sus personajes asociados
     */
    public Map<String, PersonajeModel> getTransformationsMap() {
        return transformationsMap;
    }

    /**
     * Obtiene el TeamBuilder
     * @return El TeamBuilder actual
     */
    public TeamBuilder getTeamBuilder() {
        return teamBuilder;
    }

    /**
     * Oculta el panel de información del personaje
     */
    public void hideCharacterInfo() {
        if (uiManager != null) {
            uiManager.hideCharacterInfo();
        }
    }
}