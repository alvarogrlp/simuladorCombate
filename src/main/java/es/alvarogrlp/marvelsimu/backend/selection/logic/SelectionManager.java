package es.alvarogrlp.marvelsimu.backend.selection.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import es.alvarogrlp.marvelsimu.backend.selection.ui.SelectionUIManager;
import es.alvarogrlp.marvelsimu.backend.util.DatabaseUtil;
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
    private List<PersonajeModel> playerTeam = new ArrayList<>();
    private List<PersonajeModel> aiTeam = new ArrayList<>();
    
    private TeamBuilder teamBuilder;
    private SelectionUIManager uiManager;
    
    // Añadir un mapa para almacenar botones dinámicos y sus personajes asociados
    private Map<Button, PersonajeModel> dynamicButtonsMap = new HashMap<>();
    
    /**
     * Constructor
     * @param rootPane Panel raíz para la UI
     */
    public SelectionManager(AnchorPane rootPane) {
        this.teamBuilder = new TeamBuilder(MAX_TEAM_SIZE);
        this.uiManager = new SelectionUIManager(rootPane, this);
        loadCharactersFromDatabase();
    }
    
    /**
     * Inicializa la interfaz de usuario
     */
    public void initializeUI() {
        uiManager.setupUI();
    }
    
    /**
     * Carga los personajes desde la base de datos
     */
    private void loadCharactersFromDatabase() {
        try {
            DatabaseUtil.initializePool();
            charactersMap = DatabaseUtil.loadAllCharacters();
            System.out.println("Personajes cargados de la BD: " + charactersMap.size());
            
            // Si no se cargaron personajes, crear algunos por defecto
            if (charactersMap.isEmpty()) {
                System.err.println("¡ADVERTENCIA! No se encontraron personajes en la base de datos. Creando personajes por defecto.");
                createDefaultCharacters();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al cargar personajes: " + e.getMessage());
            
            // Crear personajes por defecto en caso de error
            createDefaultCharacters();
        }
    }
    
    /**
     * Crea personajes por defecto en caso de que no se puedan cargar de la BD
     */
    private void createDefaultCharacters() {
        // Captain America
        PersonajeModel captain = new PersonajeModel();
        captain.setNombreCodigo("captain-america");
        captain.setNombre("Captain America");
        captain.setVida(850);
        captain.setFuerza(80);
        captain.setVelocidad(70);
        captain.setResistencia(85);
        captain.setPoderMagico(10);
        captain.setAtaqueMeleeNombre("Golpe de Escudo");
        captain.setAtaqueMelee(65);
        captain.setAtaqueLejanoNombre("Lanzamiento de Escudo");
        captain.setAtaqueLejano(55);
        captain.setResistenciaFisica(40);
        captain.setResistenciaMagica(20);
        captain.setEvasion(25);
        captain.setPasivaTipo("armadura");
        captain.setPasivaValor(30);
        captain.setHabilidad1Nombre("Escudo Indestructible");
        captain.setHabilidad1Tipo("fisico");
        captain.setHabilidad1Poder(120);
        captain.setHabilidad2Nombre("¡Vengadores Unidos!");
        captain.setHabilidad2Tipo("fisico");
        captain.setHabilidad2Poder(180);
        
        // Hulk
        PersonajeModel hulk = new PersonajeModel();
        hulk.setNombreCodigo("hulk");
        hulk.setNombre("Hulk");
        hulk.setVida(1200);
        hulk.setFuerza(100);
        hulk.setVelocidad(60);
        hulk.setResistencia(90);
        hulk.setPoderMagico(5);
        hulk.setAtaqueMeleeNombre("Puños de Furia");
        hulk.setAtaqueMelee(85);
        hulk.setAtaqueLejanoNombre("Aplauso Sónico");
        hulk.setAtaqueLejano(70);
        hulk.setResistenciaFisica(50);
        hulk.setResistenciaMagica(10);
        hulk.setEvasion(10);
        hulk.setPasivaTipo("regeneracion");
        hulk.setPasivaValor(5);
        hulk.setHabilidad1Nombre("Salto Aplastante");
        hulk.setHabilidad1Tipo("fisico");
        hulk.setHabilidad1Poder(150);
        hulk.setHabilidad2Nombre("Ira Incontrolable");
        hulk.setHabilidad2Tipo("fisico_penetrante");
        hulk.setHabilidad2Poder(200);
        
        // Iron Man
        PersonajeModel ironman = new PersonajeModel();
        ironman.setNombreCodigo("ironman");
        ironman.setNombre("Iron Man");
        ironman.setVida(750);
        ironman.setFuerza(70);
        ironman.setVelocidad(85);
        ironman.setResistencia(75);
        ironman.setPoderMagico(60);
        ironman.setAtaqueMeleeNombre("Puño Repulsor");
        ironman.setAtaqueMelee(60);
        ironman.setAtaqueLejanoNombre("Rayo Repulsor");
        ironman.setAtaqueLejano(75);
        ironman.setResistenciaFisica(40);
        ironman.setResistenciaMagica(30);
        ironman.setEvasion(30);
        ironman.setPasivaTipo("barrera");
        ironman.setPasivaValor(25);
        ironman.setHabilidad1Nombre("Misiles Inteligentes");
        ironman.setHabilidad1Tipo("energia");
        ironman.setHabilidad1Poder(140);
        ironman.setHabilidad2Nombre("Unirrayo");
        ironman.setHabilidad2Tipo("energia");
        ironman.setHabilidad2Poder(190);
        
        // Agregar al mapa
        charactersMap.put(captain.getNombreCodigo(), captain);
        charactersMap.put(hulk.getNombreCodigo(), hulk);
        charactersMap.put(ironman.getNombreCodigo(), ironman);
        
        // Agregar más personajes conforme se necesiten
        
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
            // Eliminar del equipo
            if (teamBuilder.removeCharacterFromTeam(currentCharacter, true, uiManager)) {
                // Habilitar su botón para poder seleccionarlo nuevamente
                Button button = findCharacterButton(currentCharacter);
                if (button != null) {
                    button.setDisable(false);
                }
                
                // Mensaje de confirmación
                uiManager.showInfoMessage(currentCharacter.getNombre() + " eliminado de tu equipo");
                
                // Limpiar selección actual
                currentCharacter = null;
                currentButton = null;
                
                // Actualizar estado del botón de luchar
                updateFightButtonState();
                
                return;
            }
        } else if (isInAITeam) {
            // Eliminar del equipo de IA
            if (teamBuilder.removeCharacterFromTeam(currentCharacter, false, uiManager)) {
                // Habilitar su botón para poder seleccionarlo nuevamente
                Button button = findCharacterButton(currentCharacter);
                if (button != null) {
                    button.setDisable(false);
                }
                
                // Mensaje de confirmación
                uiManager.showInfoMessage(currentCharacter.getNombre() + " eliminado del equipo IA");
                
                // Limpiar selección actual
                currentCharacter = null;
                currentButton = null;
                
                // Actualizar estado del botón de luchar
                updateFightButtonState();
                
                return;
            }
        }
        
        // Determinar a qué equipo añadir el personaje
        boolean addToPlayerTeam = !teamBuilder.isPlayerTeamComplete();
        
        // Si el equipo del jugador está lleno, intentar añadir al equipo de IA
        if (addToPlayerTeam) {
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
            } else {
                uiManager.showErrorMessage("Tu equipo está completo (3/3)");
            }
        } else {
            // Intentar añadir al equipo de IA
            if (teamBuilder.canAddToAITeam()) {
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
                uiManager.showErrorMessage("El equipo IA está completo (3/3)");
            }
        }
    }
    
    /**
     * Encuentra el botón correspondiente a un personaje
     */
    private Button findCharacterButton(PersonajeModel character) {
        if (character == null) return null;
        
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
     * Elimina un personaje del equipo
     * @param character Personaje a eliminar
     * @param sourceButton Botón original del personaje (se habilitará nuevamente)
     * @param isPlayerTeam Si es del equipo del jugador
     * @return true si se eliminó correctamente
     */
    public boolean removeCharacterFromTeam(PersonajeModel character, Button sourceButton, boolean isPlayerTeam) {
        if (character == null) {
            return false;
        }

        // Eliminar personaje del equipo usando TeamBuilder
        if (teamBuilder.removeCharacterFromTeam(character, isPlayerTeam, uiManager)) {
            // Si se encontró y eliminó, habilitar su botón original
            if (sourceButton != null) {
                sourceButton.setDisable(false);
            } else {
                // Si no se proporcionó el botón, intentar encontrarlo
                Button button = findCharacterButton(character);
                if (button != null) {
                    button.setDisable(false);
                }
            }
            
            // Actualizar estado del botón de luchar
            updateFightButtonState();
            
            return true;
        }
        
        return false;
    }

    /**
     * Sobrecarga del método para mantener compatibilidad
     * @param character Personaje a eliminar
     * @param isPlayerTeam Si es del equipo del jugador
     * @return true si se eliminó correctamente
     */
    public boolean removeCharacterFromTeam(PersonajeModel character, boolean isPlayerTeam) {
        return removeCharacterFromTeam(character, null, isPlayerTeam);
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
     * Verifica si el equipo del jugador está completo
     * @return true si el equipo tiene el número máximo de personajes
     */
    public boolean isPlayerTeamComplete() {
        return teamBuilder.isPlayerTeamComplete();
    }

    /**
     * Verifica si el equipo de la IA está completo
     * @return true si el equipo tiene el número máximo de personajes
     */
    public boolean isAITeamComplete() {
        return teamBuilder.isAITeamComplete();
    }
    
    /**
     * Maneja la eliminación de un personaje (separado de la UI)
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
     * Obtiene el gestor de UI
     * @return El gestor de UI
     */
    public SelectionUIManager getUIManager() {
        return uiManager;
    }
    
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
     * Selecciona un personaje aleatorio de entre los disponibles
     */
    public void selectRandomCharacter() {
        // Obtener todos los personajes disponibles (no seleccionados)
        Map<Button, PersonajeModel> buttonCharacterMap = new HashMap<>();
        
        // Acceder a los botones a través del uiManager
        Button btnCaptain = uiManager.getCaptainButton();
        Button btnHulk = uiManager.getHulkButton();
        Button btnIronMan = uiManager.getIronManButton();
        Button btnSpiderMan = uiManager.getSpiderManButton();
        Button btnDrStrange = uiManager.getDrStrangeButton();
        Button btnMagik = uiManager.getMagikButton();
        
        // Agregar botones y personajes al mapa
        if (btnCaptain != null && !btnCaptain.isDisable()) {
            PersonajeModel character = findCharacterByIdOrName(charactersMap, "4", "Captain America");
            if (character != null) {
                buttonCharacterMap.put(btnCaptain, character);
            }
        }
        
        if (btnHulk != null && !btnHulk.isDisable()) {
            PersonajeModel character = findCharacterByIdOrName(charactersMap, "1", "Hulk");
            if (character != null) {
                buttonCharacterMap.put(btnHulk, character);
            }
        }
        
        if (btnIronMan != null && !btnIronMan.isDisable()) {
            PersonajeModel character = findCharacterByIdOrName(charactersMap, "3", "Iron Man");
            if (character != null) {
                buttonCharacterMap.put(btnIronMan, character);
            }
        }
        
        if (btnSpiderMan != null && !btnSpiderMan.isDisable()) {
            PersonajeModel character = findCharacterByIdOrName(charactersMap, "2", "Spider-Man");
            if (character != null) {
                buttonCharacterMap.put(btnSpiderMan, character);
            }
        }
        
        if (btnDrStrange != null && !btnDrStrange.isDisable()) {
            PersonajeModel character = findCharacterByIdOrName(charactersMap, "5", "Doctor Strange");
            if (character != null) {
                buttonCharacterMap.put(btnDrStrange, character);
            }
        }
        
        if (btnMagik != null && !btnMagik.isDisable()) {
            PersonajeModel character = findCharacterByIdOrName(charactersMap, "6", "Magik");
            if (character != null) {
                buttonCharacterMap.put(btnMagik, character);
            }
        }
        
        // Añadir todos los botones disponibles adicionales que se han cargado dinámicamente
        for (Map.Entry<Button, PersonajeModel> entry : dynamicButtonsMap.entrySet()) {
            if (!entry.getKey().isDisable()) {
                buttonCharacterMap.put(entry.getKey(), entry.getValue());
            }
        }
        
        // Si no hay personajes disponibles, mostrar mensaje y salir
        if (buttonCharacterMap.isEmpty()) {
            uiManager.showErrorMessage("No hay más personajes disponibles para seleccionar");
            return;
        }
        
        // Seleccionar un personaje aleatoriamente
        int randomIndex = (int)(Math.random() * buttonCharacterMap.size());
        
        // Convertir el mapa a lista para acceder por índice
        List<Map.Entry<Button, PersonajeModel>> entries = new ArrayList<>(buttonCharacterMap.entrySet());
        Map.Entry<Button, PersonajeModel> selectedEntry = entries.get(randomIndex);
        
        Button selectedButton = selectedEntry.getKey();
        PersonajeModel selectedCharacter = selectedEntry.getValue();
        
        // Determinar a qué equipo añadir el personaje
        boolean addToPlayerTeam = !teamBuilder.isPlayerTeamComplete();
        
        // Añadir al equipo correspondiente
        if (addToPlayerTeam) {
            if (teamBuilder.canAddToPlayerTeam()) {
                if (teamBuilder.addCharacterToTeam(selectedCharacter, selectedButton, true, uiManager)) {
                    // Deshabilitar el botón
                    selectedButton.setDisable(true);
                    
                    // Mensaje de confirmación
                    uiManager.showInfoMessage(selectedCharacter.getNombre() + " añadido aleatoriamente a tu equipo");
                    
                    // Actualizar estado del botón de luchar
                    updateFightButtonState();
                }
            } else {
                // Si el equipo del jugador está lleno, intentar añadir al equipo de la IA
                if (teamBuilder.canAddToAITeam()) {
                    if (teamBuilder.addCharacterToTeam(selectedCharacter, selectedButton, false, uiManager)) {
                        // Deshabilitar el botón
                        selectedButton.setDisable(true);
                        
                        // Mensaje de confirmación
                        uiManager.showInfoMessage(selectedCharacter.getNombre() + " añadido aleatoriamente al equipo IA");
                        
                        // Actualizar estado del botón de luchar
                        updateFightButtonState();
                    }
                } else {
                    uiManager.showErrorMessage("Ambos equipos están completos");
                }
            }
        } else {
            // Añadir al equipo IA directamente
            if (teamBuilder.canAddToAITeam()) {
                if (teamBuilder.addCharacterToTeam(selectedCharacter, selectedButton, false, uiManager)) {
                    // Deshabilitar el botón
                    selectedButton.setDisable(true);
                    
                    // Mensaje de confirmación
                    uiManager.showInfoMessage(selectedCharacter.getNombre() + " añadido aleatoriamente al equipo IA");
                    
                    // Actualizar estado del botón de luchar
                    updateFightButtonState();
                }
            } else {
                uiManager.showErrorMessage("El equipo IA está completo");
            }
        }
    }
    
    /**
     * Busca un personaje por ID o nombre en el mapa de personajes
     * @param characterMap Mapa de personajes
     * @param id ID a buscar
     * @param name Nombre a buscar como fallback
     * @return PersonajeModel encontrado o null
     */
    private PersonajeModel findCharacterByIdOrName(Map<String, PersonajeModel> characterMap, String id, String name) {
        // Intentar primero por ID
        PersonajeModel character = characterMap.get(id);
        
        // Si no se encuentra, buscar por nombre
        if (character == null) {
            for (PersonajeModel p : characterMap.values()) {
                if (p.getNombre().equalsIgnoreCase(name) || 
                    p.getNombreCodigo().equalsIgnoreCase(name.toLowerCase().replace(" ", "-"))) {
                    return p;
                }
            }
        }
        
        return character;
    }
    
    /**
     * Registra un botón dinámico con su personaje asociado
     */
    public void registerDynamicButton(Button button, PersonajeModel character) {
        dynamicButtonsMap.put(button, character);
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
     * Getter para el mapa de botones dinámicos
     * @return Mapa de botones y sus personajes asociados
     */
    public Map<Button, PersonajeModel> getDynamicButtonsMap() {
        return dynamicButtonsMap;
    }
}