package es.alvarogrlp.marvelsimu.backend.combat.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.alvarogrlp.marvelsimu.backend.combat.animation.CombatAnimationManager;
import es.alvarogrlp.marvelsimu.backend.combat.model.CombatMessage;
import es.alvarogrlp.marvelsimu.backend.combat.ui.CombatUIManager;
import es.alvarogrlp.marvelsimu.backend.combat.ui.MessageDisplayManager;
import es.alvarogrlp.marvelsimu.backend.model.AtaqueModel;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CombatManager {
    
    private List<PersonajeModel> playerCharacters;
    private List<PersonajeModel> aiCharacters;
    private int playerCharacterIndex;
    private int aiCharacterIndex;
    private boolean combatFinished = false;
    
    private CombatUIManager uiManager;
    private CombatAnimationManager animationManager;
    private TurnManager turnManager;
    private MessageDisplayManager messageManager;
    private AIActionSelector aiSelector;
    private AnchorPane rootPane;
    private es.alvarogrlp.marvelsimu.backend.selection.logic.SelectionManager selectionManager;
    private final AbilityManager abilityManager;
    
    // Mapa para recordar la forma base de cada transformación
    private Map<PersonajeModel, PersonajeModel> originalFormMap = new HashMap<>();
    private Map<PersonajeModel, Integer> originalHealthMap = new HashMap<>();

    public CombatManager(
            AnchorPane rootPane, 
            List<PersonajeModel> playerCharacters, 
            List<PersonajeModel> aiCharacters,
            es.alvarogrlp.marvelsimu.backend.selection.logic.SelectionManager selectionManager) {
        
        this.rootPane = rootPane;
        this.playerCharacters = new ArrayList<>(playerCharacters);
        this.aiCharacters = new ArrayList<>(aiCharacters);
        this.playerCharacterIndex = 0;
        this.aiCharacterIndex = 0;
        this.selectionManager = selectionManager;
        
        // Inicializar managers
        this.uiManager = new CombatUIManager(rootPane);
        this.animationManager = new CombatAnimationManager(rootPane, uiManager);
        this.messageManager = new MessageDisplayManager(rootPane);
        this.turnManager = new TurnManager(this, messageManager);
        this.aiSelector = new AIActionSelector();
        this.abilityManager = new AbilityManager(this);
        
        // Guardar una referencia a esta instancia para acceder desde la UI
        rootPane.setUserData(this);
        
        // Inicializar vidas y habilidades
        initializeCharacters();
    }
    
    private void initializeCharacters() {
        // Inicializar cada personaje
        for (PersonajeModel p : this.playerCharacters) {
            p.inicializarVida();
            
            // No usar métodos inexistentes
            // Solo reiniciar los ataques usando el nuevo modelo
            for (AtaqueModel ataque : p.getAtaques()) {
                ataque.resetearEstadoCombate();
            }
        }
        
        for (PersonajeModel p : this.aiCharacters) {
            p.inicializarVida();
            
            // No usar métodos inexistentes
            // Solo reiniciar los ataques usando el nuevo modelo
            for (AtaqueModel ataque : p.getAtaques()) {
                ataque.resetearEstadoCombate();
            }
        }
        
        // Actualizar la UI
        uiManager.updateCharacterViews(
            playerCharacters.get(playerCharacterIndex),
            aiCharacters.get(aiCharacterIndex),
            playerCharacters,
            aiCharacters,
            playerCharacterIndex,
            aiCharacterIndex
        );
    }
    
    public void playerAttack(String attackType) {
        // Verificar que sea el turno del jugador
        if (!turnManager.isPlayerTurn()) {
            return;
        }

        // Deshabilitar inmediatamente los controles para prevenir múltiples clics
        uiManager.disablePlayerControls();
        
        // Obtener personajes actuales
        PersonajeModel attacker = playerCharacters.get(playerCharacterIndex);
        PersonajeModel defender = aiCharacters.get(aiCharacterIndex);
        
        // Obtener el ataque según el tipo seleccionado
        AtaqueModel ataque = null;
        
        switch (attackType) {
            case "habilidad1": {
                CombatMessage msg = abilityManager.habUnoMagik(attacker, defender);
                messageManager.displayMessage(
                  msg.getText(),
                  msg.getType() == CombatMessage.MessageType.ABILITY
                );
                turnManager.finishPlayerTurn(msg.isSuccess());
                return;
            }
            case "habilidad2": {
                CombatMessage msg = abilityManager.habDosMagik(attacker, defender);
                messageManager.displayMessage(msg.getText(),
                    msg.getType() == CombatMessage.MessageType.ABILITY);
                turnManager.finishPlayerTurn(msg.isSuccess());
                return;
            }
            case "melee":
                ataque = attacker.getAtaquePorTipo("ACC");
                break;
            case "lejano":
                ataque = attacker.getAtaquePorTipo("AAD");
                break;
        }
        
        // Verificar disponibilidad del ataque
        boolean puedeUsar = true;
        if (ataque != null) {
            puedeUsar = ataque.estaDisponible();
        }
        
        if (!puedeUsar) {
            messageManager.displayMessage("¡Ataque no disponible!", true, 
                    () -> {
                        uiManager.enablePlayerControls();
                        turnManager.finishPlayerTurn(false);
                    });
            return;
        }
        
        // Consumir uso si el ataque existe
        if (ataque != null) {
            ataque.consumirUso();
        }
        
        // Obtener daño base del ataque
        int danioBase = 100; // Valor por defecto
        final String attackName; // Ahora es FINAL para usarse en la lambda
        
        if (ataque != null) {
            danioBase = ataque.getDanoBase();
            attackName = ataque.getNombre();
        } else {
            attackName = "Ataque"; // Asignación en bloque else para mantenerla final
        }
        
        // Mensaje de ataque
        messageManager.displayMessage(attacker.getNombre() + " usa " + attackName, true);
        
        // Variables finales para la lambda
        final int danioBaseFinal = danioBase;
        
        // Animar ataque
        animationManager.animatePlayerAttack(attackType, () -> {
            // Obtener vida antes del ataque
            int previousHealth = defender.getVidaActual();
            
            // Calcular daño con la nueva fórmula
            int damageToInflict = DamageCalculator.calcularDano(
                danioBaseFinal, 
                attacker.getPoder(), 
                defender.getPoder()
            );
            
            // Aplicar daño
            defender.setVidaActual(defender.getVidaActual() - damageToInflict);
            boolean defeated = defender.getVidaActual() <= 0;
            
            // Asegurar que la vida no baje de 0
            if (defeated) {
                defender.setVidaActual(0);
            }
            
            // Calcular daño real
            int realDamage = previousHealth - defender.getVidaActual();
            
            // Procesar resultado del ataque
            processAttackResult(defender, realDamage, defeated, true);
        });
    }

    /**
     * Procesa el resultado de un ataque
     */
    private void processAttackResult(
            PersonajeModel defender, 
            int damage, 
            boolean defeated,
            boolean isPlayerAttack) {
        
        // Mostrar efectos visuales según el resultado
        if (damage > 0) {
            // Mostrar texto de daño
            animationManager.showDamageText(damage, isPlayerAttack, false, false);
        }
        
        // Actualizar UI
        uiManager.updateCharacterHealth(
            playerCharacters.get(playerCharacterIndex),
            aiCharacters.get(aiCharacterIndex)
        );
        
        // Si el personaje fue derrotado
        if (defeated) {
            handleCharacterDefeat(defender, isPlayerAttack);
        } else {
            // Continuar con el siguiente turno
            if (isPlayerAttack) {
                // Terminar el turno del jugador explícitamente
                turnManager.finishPlayerTurn(true);
            } else {
                // Terminar el turno de la IA
                turnManager.finishAITurn();
            }
        }
    }

    /**
     * Maneja el turno de la IA
     */
    public void aiTurn() {
        // Asegurarnos de que no es el turno del jugador
        if (turnManager.isPlayerTurn()) {
            System.err.println("ERROR: Intentando ejecutar AI Turn durante el turno del jugador");
            return;
        }
        
        System.out.println("Iniciando turno de la IA...");
        
        // Obtener personajes actuales
        PersonajeModel attacker = aiCharacters.get(aiCharacterIndex);
        PersonajeModel defender = playerCharacters.get(playerCharacterIndex);
        
        // Actualizar cooldowns de ataques
        for (AtaqueModel ataque : attacker.getAtaques()) {
            ataque.finalizarTurno();
        }
        
        // Seleccionar el mejor ataque
        String attackType = aiSelector.selectBestAttack(attacker, defender);
        System.out.println("IA seleccionó ataque: " + attackType);
        
        // Obtener el ataque según el tipo seleccionado
        AtaqueModel ataque = null;
        
        switch (attackType) {
            case "habilidad1": {
                CombatMessage msg = abilityManager.habUnoThanos(attacker, defender);
                messageManager.displayMessage(msg.getText(), false);
                turnManager.finishAITurn();
                return;
            }
            case "habilidad2": {
                CombatMessage msg = abilityManager.habDosThanos(attacker, defender);
                messageManager.displayMessage(msg.getText(), false);
                turnManager.finishAITurn();
                return;
            }
            case "melee":
                ataque = attacker.getAtaquePorTipo("ACC");
                break;
            case "lejano":
                ataque = attacker.getAtaquePorTipo("AAD");
                break;
        }
        
        // Consumir uso si el ataque existe
        if (ataque != null) {
            ataque.consumirUso();
        }
        
        // Obtener daño base del ataque
        int danioBase = 100; // Valor por defecto
        final String attackName; // Ahora es FINAL para usarse en la lambda
        
        if (ataque != null) {
            danioBase = ataque.getDanoBase();
            attackName = ataque.getNombre();
        } else {
            attackName = "Ataque"; // Asignación en bloque else para mantenerla final
        }
        
        // Mostrar mensaje de ataque
        messageManager.displayMessage(attacker.getNombre() + " usa " + attackName, false);
        
        // Variables finales para la lambda
        final int danioBaseFinal = danioBase;
        
        // Animar ataque
        animationManager.animateAIAttack(attackType, () -> {
            System.out.println("Ejecutando daño de IA: " + attackName);
            
            // Obtener vida antes del ataque
            int previousHealth = defender.getVidaActual();
            
            // Calcular daño con la nueva fórmula
            int damageToInflict = DamageCalculator.calcularDano(
                danioBaseFinal, 
                attacker.getPoder(), 
                defender.getPoder()
            );
            
            System.out.println("Daño calculado: " + damageToInflict + " (base: " + danioBaseFinal + ")");
            
            // Aplicar daño
            defender.setVidaActual(defender.getVidaActual() - damageToInflict);
            boolean defeated = defender.getVidaActual() <= 0;
            
            // Asegurar que la vida no baje de 0
            if (defeated) {
                defender.setVidaActual(0);
            }
            
            // Calcular daño real
            int realDamage = previousHealth - defender.getVidaActual();
            
            // Procesar resultado del ataque
            processAttackResult(defender, realDamage, defeated, false);
        });
    }
    
    public void changePlayerCharacter(int index) {
        if (index >= 0 && index < playerCharacters.size() && 
            index != playerCharacterIndex && 
            !playerCharacters.get(index).isDerrotado()) {
            
            // Deshabilitar controles durante la transición
            uiManager.disablePlayerControls();
            
            playerCharacterIndex = index;
            uiManager.hidePlayerCharacter();
            
            messageManager.displayMessage("¡Has cambiado a " + 
                playerCharacters.get(playerCharacterIndex).getNombre() + "!", true, () -> {
                    uiManager.updateCharacterViews(
                        playerCharacters.get(playerCharacterIndex),
                        aiCharacters.get(aiCharacterIndex),
                        playerCharacters,
                        aiCharacters,
                        playerCharacterIndex,
                        aiCharacterIndex
                    );
                    
                    uiManager.showPlayerCharacter();
                    turnManager.startAITurn();
                });
        }
    }
    
    /**
     * Cambia al siguiente personaje de la IA que no esté derrotado
     */
    private void changeAICharacter() {
        int originalIndex = aiCharacterIndex;
        
        // Buscar el siguiente personaje no derrotado, comenzando desde el siguiente índice
        do {
            aiCharacterIndex = (aiCharacterIndex + 1) % aiCharacters.size();
        } while (aiCharacters.get(aiCharacterIndex).isDerrotado() && aiCharacterIndex != originalIndex);
        
        // Verificar si realmente encontramos un personaje no derrotado
        if (!aiCharacters.get(aiCharacterIndex).isDerrotado()) {
            // El personaje ya está oculto, por lo que preparamos el cambio directamente
            
            messageManager.displayMessage("La IA cambia a " + 
                aiCharacters.get(aiCharacterIndex).getNombre(), false, () -> {
                    // Actualizar la interfaz de usuario
                    uiManager.updateCharacterViews(
                        playerCharacters.get(playerCharacterIndex),
                        aiCharacters.get(aiCharacterIndex),
                        playerCharacters,
                        aiCharacters,
                        playerCharacterIndex,
                        aiCharacterIndex
                    );
                    
                    uiManager.showAICharacter();
                    
                    // Habilitar controles de jugador después de mostrar el nuevo personaje
                    uiManager.enablePlayerControls();
                    
                    // Continuar con el turno del jugador
                    turnManager.startPlayerTurn();
                });
        } else {
            // Este caso no debería ocurrir, pero por seguridad
            System.err.println("Error: No hay personajes disponibles en el equipo de la IA");
            endCombat(true); // Victoria del jugador por error
        }
    }
    
    public void endCombat(boolean playerWon) {
        combatFinished = true;
        handleCombatEnd(playerWon);
    }
    
    /**
     * Verifica si el combate ha terminado
     */
    public boolean isCombatFinished() {
        return combatFinished;
    }
    
    /**
     * Gestiona el final del combate y muestra las opciones correspondientes
     */
    public void handleCombatEnd(boolean playerWon) {
        // Marcar que el combate ha terminado
        combatFinished = true;
        
        // Crear overlay oscuro para cubrir toda la pantalla
        AnchorPane overlay = new AnchorPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        overlay.setPrefWidth(896);
        overlay.setPrefHeight(810);
        AnchorPane.setTopAnchor(overlay, 0.0);
        AnchorPane.setLeftAnchor(overlay, 0.0);
        AnchorPane.setRightAnchor(overlay, 0.0);
        AnchorPane.setBottomAnchor(overlay, 0.0);
        
        // Contenedor para el mensaje y el botón
        VBox messageContainer = new VBox(30); // 30px de separación vertical
        messageContainer.setAlignment(Pos.CENTER);
        messageContainer.setMaxWidth(700);
        
        // Texto grande de victoria/derrota
        Text resultText = new Text(playerWon ? "¡VICTORIA!" : "DERROTA");
        resultText.setFont(Font.font("System", FontWeight.BOLD, 72));
        resultText.setFill(playerWon ? Color.GOLD : Color.FIREBRICK);
        resultText.setStroke(Color.BLACK);
        resultText.setStrokeWidth(2);
        resultText.setTextAlignment(TextAlignment.CENTER);
        
        // Crear nuevo botón de volver específico para el final del combate
        Button volverButton = new Button("Volver a Selección");
        volverButton.setId("btnVolverFinal");
        volverButton.setPrefWidth(250);
        volverButton.setPrefHeight(60);
        volverButton.setFont(Font.font("System", FontWeight.BOLD, 18));
        volverButton.setStyle(
            "-fx-background-color: #4a7ba7;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 5px;" +
            "-fx-border-color: #2a5b87;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 5px;" +
            "-fx-cursor: hand;"
        );
        
        // Añadir elementos al contenedor
        messageContainer.getChildren().addAll(resultText, volverButton);
        
        // Posicionar el contenedor en el centro de la pantalla
        AnchorPane.setTopAnchor(messageContainer, 300.0);
        AnchorPane.setLeftAnchor(messageContainer, 98.0); // (896 - 700) / 2 = 98
        AnchorPane.setRightAnchor(messageContainer, 98.0);
        
        // Añadir el overlay y el contenedor a la escena
        overlay.getChildren().add(messageContainer);
        rootPane.getChildren().add(overlay);
        
        // Iniciar con opacidad 0 para animación
        overlay.setOpacity(0);
        
        // Configurar el comportamiento del botón
        volverButton.setOnAction(e -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(
                        getClass().getResource("/es/alvarogrlp/marvelsimu/seleccionPersonajes.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 896, 810);
                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.setTitle("Selección de Personajes");
                stage.setScene(scene);
                stage.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        // Desactivar todos los demás botones
        disableAllButtons();
        
        // Animar la entrada del overlay con los elementos
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), overlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        // Animar el mensaje de victoria
        ScaleTransition scaleText = new ScaleTransition(Duration.millis(800), resultText);
        scaleText.setFromX(0.5);
        scaleText.setFromY(0.5);
        scaleText.setToX(1.0);
        scaleText.setToY(1.0);
        
        // Animar el botón
        FadeTransition fadeButton = new FadeTransition(Duration.millis(800), volverButton);
        fadeButton.setFromValue(0);
        fadeButton.setToValue(1);
        fadeButton.setDelay(Duration.millis(500));
        
        ScaleTransition scaleButton = new ScaleTransition(Duration.millis(800), volverButton);
        scaleButton.setFromX(0.8);
        scaleButton.setFromY(0.8);
        scaleButton.setToX(1.0);
        scaleButton.setToY(1.0);
        scaleButton.setDelay(Duration.millis(500));
        
        // Ejecutar animaciones
        ParallelTransition animation = new ParallelTransition(
            fadeIn, scaleText, fadeButton, scaleButton
        );
        animation.play();
    }
    
    /**
     * Deshabilita todos los botones de la interfaz de combate
     */
    private void disableAllButtons() {
        // Obtener todos los botones del combate
        Button atacarButton = (Button) rootPane.lookup("#atacarButton");
        Button cambiarButton = (Button) rootPane.lookup("#cambiarButton");
        Button volverButton = (Button) rootPane.lookup("#btnVolver");
        
        // Deshabilitar contenedor de ataques
        Node attackContainer = rootPane.lookup("#attackContainer");
        if (attackContainer != null) {
            attackContainer.setVisible(false);
        }
        
        // Deshabilitar todos los botones
        if (atacarButton != null) {
            atacarButton.setDisable(true);
            atacarButton.setVisible(false);
        }
        
        if (cambiarButton != null) {
            cambiarButton.setDisable(true);
            cambiarButton.setVisible(false);
        }
        
        if (volverButton != null) {
            volverButton.setDisable(true);
            volverButton.setVisible(false);
        }
    }
    
    // Getters para acceder a los personajes y sus índices
    public PersonajeModel getCurrentPlayerCharacter() {
        return playerCharacters.get(playerCharacterIndex);
    }
    
    public PersonajeModel getCurrentAICharacter() {
        return aiCharacters.get(aiCharacterIndex);
    }
    
    public List<PersonajeModel> getPlayerCharacters() {
        return playerCharacters;
    }
    
    public List<PersonajeModel> getAICharacters() {
        return aiCharacters;
    }
    
    public int getPlayerCharacterIndex() {
        return playerCharacterIndex;
    }
    
    public int getAICharacterIndex() {
        return aiCharacterIndex;
    }
    
    public CombatUIManager getUIManager() {
        return uiManager;
    }
    
    public TurnManager getTurnManager() {
        return turnManager;
    }
    
    /**
     * Permite acceder al AbilityManager desde la UI u otros componentes
     */
    public AbilityManager getAbilityManager() {
        return abilityManager;
    }
    
    /**
     * Maneja la derrota de un personaje y determina las consecuencias
     * @param defeated El personaje derrotado
     * @param isPlayerAttack Indica si fue el jugador quien realizó el ataque
     */
    private void handleCharacterDefeat(PersonajeModel defeated, boolean isPlayerAttack) {
        // Marcar al personaje como derrotado
        defeated.setDerrotado(true);
        
        // Animar la derrota
        if (isPlayerAttack) {
            // La IA perdió un personaje
            animationManager.animateDefeat(defeated, false, () -> {
                // Comprobar si todos los personajes de la IA están derrotados
                boolean allAIDefeated = true;
                for (PersonajeModel aiChar : aiCharacters) {
                    if (!aiChar.isDerrotado()) {
                        allAIDefeated = false;
                        break;
                    }
                }
                
                // Si todos los personajes de la IA están derrotados, victoria del jugador
                if (allAIDefeated) {
                    endCombat(true); 
                } else {
                    // Cambiar al siguiente personaje de la IA
                    uiManager.hideAICharacter();
                    changeAICharacter();
                }
            });
        } else {
            // El jugador perdió un personaje
            animationManager.animateDefeat(defeated, true, () -> {
                // Comprobar si todos los personajes del jugador están derrotados
                boolean allPlayerDefeated = true;
                for (PersonajeModel playerChar : playerCharacters) {
                    if (!playerChar.isDerrotado()) {
                        allPlayerDefeated = false;
                        break;
                    }
                }
                
                // Si todos los personajes del jugador están derrotados, victoria de la IA
                if (allPlayerDefeated) {
                    endCombat(false); // Victoria de la IA
                } else {
                    // Mostrar diálogo para seleccionar el siguiente personaje
                    uiManager.hidePlayerCharacter(); // Ocultar personaje derrotado
                    showCharacterSelectionDialog();
                }
            });
        }
    }
    
    /**
     * Muestra un diálogo para seleccionar un nuevo personaje cuando el actual es derrotado
     */
    private void showCharacterSelectionDialog() {
        // Crear contenedor para el diálogo
        VBox dialogContainer = new VBox(15);
        dialogContainer.setAlignment(Pos.CENTER);
        dialogContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 20px; -fx-background-radius: 10px;");
        dialogContainer.setMaxWidth(400);
        dialogContainer.setMaxHeight(500);
        
        // Título del diálogo
        Text titleText = new Text("Selecciona tu próximo personaje");
        titleText.setFill(Color.WHITE);
        titleText.setFont(Font.font("System", FontWeight.BOLD, 18));
        
        // Añadir título al contenedor
        dialogContainer.getChildren().add(titleText);
        
        // Crear botones para cada personaje disponible
        for (int i = 0; i < playerCharacters.size(); i++) {
            PersonajeModel character = playerCharacters.get(i);
            
            // Saltarse personajes derrotados y el actual
            if (character.isDerrotado() || i == playerCharacterIndex) {
                continue;
            }
            
            // Crear botón para el personaje
            Button characterButton = new Button(character.getNombre());
            characterButton.setPrefWidth(300);
            characterButton.setPrefHeight(50);
            characterButton.setStyle(
                "-fx-background-color: #4a7ba7;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 5px;" +
                "-fx-border-color: #2a5b87;" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 5px;" +
                "-fx-cursor: hand;"
            );
            
            // Configurar acción del botón (cambiar al personaje seleccionado)
            final int index = i;
            characterButton.setOnAction(e -> {
                // Remover el diálogo
                rootPane.getChildren().remove(dialogContainer);
                
                // Cambiar al personaje seleccionado
                playerCharacterIndex = index;
                
                // Actualizar vistas y continuar con el combate
                uiManager.hidePlayerCharacter();
                
                messageManager.displayMessage("¡Has cambiado a " + 
                    playerCharacters.get(playerCharacterIndex).getNombre() + "!", true, () -> {
                        uiManager.updateCharacterViews(
                            playerCharacters.get(playerCharacterIndex),
                            aiCharacters.get(aiCharacterIndex),
                            playerCharacters,
                            aiCharacters,
                            playerCharacterIndex,
                            aiCharacterIndex
                        );
                        
                        uiManager.showPlayerCharacter();
                        turnManager.finishAITurn();
                    });
            });
            
            // Añadir botón al contenedor
            dialogContainer.getChildren().add(characterButton);
        }
        
        // Posicionar el diálogo en el centro de la pantalla
        AnchorPane.setTopAnchor(dialogContainer, 200.0);
        AnchorPane.setLeftAnchor(dialogContainer, 248.0); // (896 - 400) / 2 = 248
        AnchorPane.setRightAnchor(dialogContainer, 248.0);
        
        // Añadir el diálogo a la escena con animación
        dialogContainer.setScaleX(0.5);
        dialogContainer.setScaleY(0.5);
        dialogContainer.setOpacity(0);
        rootPane.getChildren().add(dialogContainer);
        
        // Animar la aparición del diálogo
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), dialogContainer);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), dialogContainer);
        fadeIn.setToValue(1.0);
        
        // Ejecutar animaciones
        ParallelTransition animation = new ParallelTransition(scaleIn, fadeIn);
        animation.play();
    }
    
    /**
     * Aplica una transformación a un personaje
     * @param character Personaje a transformar
     * @param transformationCode Código de la transformación
     * @return true si la transformación fue exitosa
     */
    public boolean applyTransformation(PersonajeModel character, String transformationCode) {
        // Obtener la transformación base desde el selectionManager
        PersonajeModel template = selectionManager.getTransformationsMap().get(transformationCode);
        if (template == null) {
            System.err.println("Transformación no encontrada: " + transformationCode);
            return false;
        }

        // Clonar la forma transformada
        PersonajeModel transformed = template.clonar();

        // Guardar referencia a la forma original y su vida actual
        originalFormMap.put(transformed, character);
        originalHealthMap.put(transformed, character.getVidaActual());

        // Ajustar stats de la forma transformada
        transformed.setPersonajeBaseId(character.getId());
        transformed.setNombre(character.getNombre() + " → " + template.getNombre());
        transformed.setVidaActual(transformed.getVida());

        // Reemplazar en el equipo según índice
        if (character == playerCharacters.get(playerCharacterIndex)) {
            playerCharacters.set(playerCharacterIndex, transformed);
        } else if (character == aiCharacters.get(aiCharacterIndex)) {
            aiCharacters.set(aiCharacterIndex, transformed);
        } else {
            // Buscar en resto del equipo
            for (int i = 0; i < playerCharacters.size(); i++) {
                if (playerCharacters.get(i) == character) {
                    playerCharacters.set(i, transformed);
                    break;
                }
            }
            for (int i = 0; i < aiCharacters.size(); i++) {
                if (aiCharacters.get(i) == character) {
                    aiCharacters.set(i, transformed);
                    break;
                }
            }
        }

        // Actualizar UI con el nuevo modelo
        uiManager.updateCharacterViews(
            playerCharacters.get(playerCharacterIndex),
            aiCharacters.get(aiCharacterIndex),
            playerCharacters,
            aiCharacters,
            playerCharacterIndex,
            aiCharacterIndex
        );

        return true;
    }

    /**
     * Revierte la transformación y devuelve la forma original
     */
    public void revertTransformation(PersonajeModel transformed) {
        PersonajeModel original = originalFormMap.remove(transformed);
        if (original == null) return;

        // Restaurar la vida que tenía el original antes de transformarse
        Integer savedHp = originalHealthMap.remove(transformed);
        if (savedHp != null) {
            original.setVidaActual(savedHp);
        }

        // Reemplazar de nuevo en el equipo
        if (transformed == playerCharacters.get(playerCharacterIndex)) {
            playerCharacters.set(playerCharacterIndex, original);
        } else if (transformed == aiCharacters.get(aiCharacterIndex)) {
            aiCharacters.set(aiCharacterIndex, original);
        } else {
            for (int i = 0; i < playerCharacters.size(); i++) {
                if (playerCharacters.get(i) == transformed) {
                    playerCharacters.set(i, original);
                    break;
                }
            }
            for (int i = 0; i < aiCharacters.size(); i++) {
                if (aiCharacters.get(i) == transformed) {
                    aiCharacters.set(i, original);
                    break;
                }
            }
        }

        // Actualizar la vista
        uiManager.updateCharacterViews(
            playerCharacters.get(playerCharacterIndex),
            aiCharacters.get(aiCharacterIndex),
            playerCharacters,
            aiCharacters,
            playerCharacterIndex,
            aiCharacterIndex
        );
    }
}