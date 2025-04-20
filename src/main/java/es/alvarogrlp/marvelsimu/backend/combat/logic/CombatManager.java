package es.alvarogrlp.marvelsimu.backend.combat.logic;

import java.util.ArrayList;
import java.util.List;

import es.alvarogrlp.marvelsimu.backend.combat.animation.CombatAnimationManager;
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
        
        // Inicializar vidas y habilidades
        initializeCharacters();
        
        // Guardar una referencia a esta instancia para acceder desde la UI
        rootPane.setUserData(this);
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
            case "melee":
                ataque = attacker.getAtaquePorTipo("ACC");
                break;
            case "lejano":
                ataque = attacker.getAtaquePorTipo("AAD");
                break;
            case "habilidad1":
                ataque = attacker.getAtaquePorTipo("habilidad_mas_poderosa");
                break;
            case "habilidad2":
                ataque = attacker.getAtaquePorTipo("habilidad_caracteristica");
                break;
        }
        
        // Verificar disponibilidad del ataque
        boolean puedeUsar = true;
        if (ataque != null) {
            puedeUsar = ataque.estaDisponible();
        }
        
        if (!puedeUsar) {
            messageManager.displayMessage("¡Ataque no disponible!", true, 
                    () -> turnManager.finishPlayerTurn(false));
            return;
        }
        
        // Consumir uso si el ataque existe
        if (ataque != null) {
            ataque.consumirUso();
        }
        
        // Calcular poder de ataque y nombre
        int multiplicadorAtaque = 1;
        String attackName = "Ataque";
        
        if (ataque != null) {
            // Usar danoBase como multiplicador
            multiplicadorAtaque = ataque.getDanoBase() / 50; // Factor de escala
            if (multiplicadorAtaque < 1) multiplicadorAtaque = 1;
            
            attackName = ataque.getNombre();
        }
        
        // Mensaje de ataque
        messageManager.displayMessage(attacker.getNombre() + " usa " + attackName, true);
        
        // Factor final para el cálculo de daño
        final int factorAtaque = multiplicadorAtaque;
        
        // Animar ataque
        animationManager.animatePlayerAttack(attackType, () -> {
            // Obtener vida antes del ataque
            int previousHealth = defender.getVidaActual();
            
            // Calcular daño con el nuevo sistema
            int damageToInflict = DamageCalculator.calcularDano(
                attacker.getFuerza() * factorAtaque, 
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
                turnManager.startAITurn();
            } else {
                turnManager.finishAITurn();
            }
        }
    }
    
    public void aiTurn() {
        // Obtener personajes actuales
        PersonajeModel attacker = aiCharacters.get(aiCharacterIndex);
        PersonajeModel defender = playerCharacters.get(playerCharacterIndex);
        
        // Actualizar cooldowns de ataques
        for (AtaqueModel ataque : attacker.getAtaques()) {
            ataque.finalizarTurno();
        }
        
        // Seleccionar el mejor ataque
        String attackType = aiSelector.selectBestAttack(attacker, defender);
        
        // Obtener el ataque según el tipo seleccionado
        AtaqueModel ataque = null;
        
        switch (attackType) {
            case "melee":
                ataque = attacker.getAtaquePorTipo("ACC");
                break;
            case "lejano":
                ataque = attacker.getAtaquePorTipo("AAD");
                break;
            case "habilidad1":
                ataque = attacker.getAtaquePorTipo("habilidad_mas_poderosa");
                break;
            case "habilidad2":
                ataque = attacker.getAtaquePorTipo("habilidad_caracteristica");
                break;
        }
        
        // Consumir uso si el ataque existe
        if (ataque != null) {
            ataque.consumirUso();
        }
        
        // Calcular poder de ataque y nombre
        int multiplicadorAtaque = 1;
        String attackName = "Ataque";
        
        if (ataque != null) {
            // Usar danoBase como multiplicador
            multiplicadorAtaque = ataque.getDanoBase() / 50; // Factor de escala
            if (multiplicadorAtaque < 1) multiplicadorAtaque = 1;
            
            attackName = ataque.getNombre();
        }
        
        // Mostrar mensaje de ataque
        messageManager.displayMessage(attacker.getNombre() + " usa " + attackName, false);
        
        // Factor final para el cálculo de daño
        final int factorAtaque = multiplicadorAtaque;
        
        // Animar ataque
        animationManager.animateAIAttack(attackType, () -> {
            // Obtener vida antes del ataque
            int previousHealth = defender.getVidaActual();
            
            // Calcular daño con el nuevo sistema
            int damageToInflict = DamageCalculator.calcularDano(
                attacker.getFuerza() * factorAtaque, 
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
    
    private void changeAICharacter() {
        int originalIndex = aiCharacterIndex;
        aiCharacterIndex = (aiCharacterIndex + 1) % aiCharacters.size();
        
        while (aiCharacters.get(aiCharacterIndex).isDerrotado() && aiCharacterIndex != originalIndex) {
            aiCharacterIndex = (aiCharacterIndex + 1) % aiCharacters.size();
        }
        
        messageManager.displayMessage("La IA cambia a " + 
            aiCharacters.get(aiCharacterIndex).getNombre() + "!", false, () -> {
                uiManager.updateCharacterViews(
                    playerCharacters.get(playerCharacterIndex),
                    aiCharacters.get(aiCharacterIndex),
                    playerCharacters,
                    aiCharacters,
                    playerCharacterIndex,
                    aiCharacterIndex
                );
                
                uiManager.showAICharacter();
                turnManager.finishAITurn();
            });
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
     * Maneja la derrota de un personaje y determina las consecuencias
     * @param defeated El personaje derrotado
     * @param isPlayerAttack Indica si fue el jugador quien realizó el ataque
     */
    private void handleCharacterDefeat(PersonajeModel defeated, boolean isPlayerAttack) {
        // Marcar al personaje como derrotado
        defeated.setDerrotado(true);
        
        // Animar la derrota
        if (isPlayerAttack) {
            // Si es un personaje de la IA el derrotado
            animationManager.animateDefeat(defeated, false, () -> {
                // Mostrar mensaje de derrota
                messageManager.displayMessage("¡" + defeated.getNombre() + " ha sido derrotado!", true, () -> {
                    // Verificar si todos los personajes de la IA están derrotados
                    boolean allAIDefeated = true;
                    for (PersonajeModel aiChar : aiCharacters) {
                        if (!aiChar.isDerrotado()) {
                            allAIDefeated = false;
                            break;
                        }
                    }
                    
                    // Si todos los personajes de la IA están derrotados, victoria del jugador
                    if (allAIDefeated) {
                        endCombat(true); // Victoria del jugador
                    } else {
                        // Cambiar al siguiente personaje de la IA
                        changeAICharacter();
                    }
                });
            });
        } else {
            // Si es un personaje del jugador el derrotado
            animationManager.animateDefeat(defeated, true, () -> {
                // Mostrar mensaje de derrota
                messageManager.displayMessage("¡" + defeated.getNombre() + " ha sido derrotado!", false, () -> {
                    // Verificar si todos los personajes del jugador están derrotados
                    boolean allPlayerDefeated = true;
                    for (PersonajeModel playerChar : playerCharacters) {
                        if (!playerChar.isDerrotado()) {
                            allPlayerDefeated = false;
                            break;
                        }
                    }
                    
                    // Si todos los personajes del jugador están derrotados, victoria de la IA
                    if (allPlayerDefeated) {
                        endCombat(false); // Derrota del jugador
                    } else {
                        // Mostrar diálogo para seleccionar el siguiente personaje
                        showCharacterSelectionDialog();
                    }
                });
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
        // Obtener la transformación desde el mapa
        PersonajeModel transformation = selectionManager.getTransformationsMap().get(transformationCode);
        
        if (transformation == null) {
            System.err.println("Transformación no encontrada: " + transformationCode);
            return false;
        }
        
        // Guardar referencia al personaje original
        int originalId = character.getId();
        String originalName = character.getNombre();
        
        // Clonar la transformación
        PersonajeModel transformedCharacter = transformation.clonar();
        
        // Mantener algunos datos del personaje original
        transformedCharacter.setPersonajeBaseId(originalId);
        transformedCharacter.setNombre(originalName + " (" + transformation.getNombre() + ")");
        
        // Reemplazar el personaje en el equipo
        if (character == playerCharacters.get(playerCharacterIndex)) {
            // Es el personaje activo del jugador
            playerCharacters.set(playerCharacterIndex, transformedCharacter);
            // Actualizar UI
            uiManager.updateCharacterViews(
                transformedCharacter, 
                aiCharacters.get(aiCharacterIndex),
                playerCharacters,
                aiCharacters,
                playerCharacterIndex,
                aiCharacterIndex
            );
        } else if (character == aiCharacters.get(aiCharacterIndex)) {
            // Es el personaje activo de la IA
            aiCharacters.set(aiCharacterIndex, transformedCharacter);
            // Actualizar UI
            uiManager.updateCharacterViews(
                playerCharacters.get(playerCharacterIndex),
                transformedCharacter,
                playerCharacters,
                aiCharacters,
                playerCharacterIndex,
                aiCharacterIndex
            );
        } else {
            // Buscar en todo el equipo
            for (int i = 0; i < playerCharacters.size(); i++) {
                if (playerCharacters.get(i) == character) {
                    playerCharacters.set(i, transformedCharacter);
                    break;
                }
            }
            for (int i = 0; i < aiCharacters.size(); i++) {
                if (aiCharacters.get(i) == character) {
                    aiCharacters.set(i, transformedCharacter);
                    break;
                }
            }
        }
        
        return true;
    }
}