package es.alvarogrlp.marvelsimu.backend.combat.logic;

import java.util.ArrayList;
import java.util.List;

import es.alvarogrlp.marvelsimu.backend.combat.animation.CombatAnimationManager;
import es.alvarogrlp.marvelsimu.backend.combat.ui.CombatUIManager;
import es.alvarogrlp.marvelsimu.backend.combat.ui.MessageDisplayManager;
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
    
    public CombatManager(
            AnchorPane rootPane, 
            List<PersonajeModel> playerCharacters, 
            List<PersonajeModel> aiCharacters) {
        
        this.rootPane = rootPane;
        this.playerCharacters = new ArrayList<>(playerCharacters);
        this.aiCharacters = new ArrayList<>(aiCharacters);
        this.playerCharacterIndex = 0;
        this.aiCharacterIndex = 0;
        
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
            p.inicializarUsosHabilidades();
            
            // Verificar si falta algún tipo de ataque
            if (p.getAtaqueMeleeTipo() == null) p.setAtaqueMeleeTipo("fisico");
            if (p.getAtaqueLejanoTipo() == null) p.setAtaqueLejanoTipo("fisico");
            if (p.getHabilidad1Tipo() == null) p.setHabilidad1Tipo("magico");
            if (p.getHabilidad2Tipo() == null) p.setHabilidad2Tipo("magico");
        }
        
        for (PersonajeModel p : this.aiCharacters) {
            p.inicializarVida();
            p.inicializarUsosHabilidades();
            
            // Verificar si falta algún tipo de ataque
            if (p.getAtaqueMeleeTipo() == null) p.setAtaqueMeleeTipo("fisico");
            if (p.getAtaqueLejanoTipo() == null) p.setAtaqueLejanoTipo("fisico");
            if (p.getHabilidad1Tipo() == null) p.setHabilidad1Tipo("magico");
            if (p.getHabilidad2Tipo() == null) p.setHabilidad2Tipo("magico");
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
        
        // Verificar usos de habilidad
        if ((attackType.equals("habilidad1") || attackType.equals("habilidad2")) 
                && !attacker.tieneUsosDisponibles(attackType)) {
            messageManager.displayMessage("¡No quedan usos para esta habilidad!", true, 
                    () -> turnManager.finishPlayerTurn(false));
            return;
        }
        
        // Consumir uso si es habilidad
        if (attackType.equals("habilidad1") || attackType.equals("habilidad2")) {
            attacker.consumirUsoHabilidad(attackType);
        }
        
        // Calcular daño y crítico
        boolean isCritical = attacker.esGolpeCritico();
        String attackTypeCode = attacker.getTipoAtaque(attackType);
        int attackPower = attacker.getPoderAtaque(attackType);
        
        // Obtener el nombre real del ataque
        String attackName = attacker.getNombreAtaque(attackType);
        
        // Mostrar mensaje de ataque con el nombre real
        String attackMessage = attacker.getNombre() + " usa " + attackName;
        if (isCritical) {
            attackMessage += " ¡GOLPE CRÍTICO!";
        }
        
        // Mensaje especial para daño verdadero
        boolean isTrueDamage = "daño_verdadero".equals(attackTypeCode);
        if (isTrueDamage) {
            attackMessage += " (¡Daño Verdadero!)";
        }
        
        messageManager.displayMessage(attackMessage, true);
        
        // Animar ataque
        animationManager.animatePlayerAttack(attackType, () -> {
            int previousHealth = defender.getVidaActual();
            
            // Aplicar daño
            boolean defeated = defender.recibirDaño(attackPower, attackTypeCode, isCritical);
            
            // Calcular daño real
            int realDamage = previousHealth - defender.getVidaActual();
            
            processAttackResult(defender, realDamage, isCritical, isTrueDamage, defeated, true);
        });
    }
    
    private void processAttackResult(
            PersonajeModel defender, 
            int damage, 
            boolean isCritical, 
            boolean isTrueDamage, 
            boolean defeated,
            boolean isPlayerAttack) {
        
        // Mostrar efectos visuales según el resultado
        if (damage == 0) {
            if (Math.random() * 100 < defender.getEvasion()) {
                // Mostrar efecto de evasión
                animationManager.showEvasionEffect(defender, isPlayerAttack);
                
                // Mensaje de evasión (probabilidad reducida)
                if (Math.random() < 0.5) {
                    messageManager.displayMessage(defender.getNombre() + " esquiva el ataque!", isPlayerAttack);
                }
            }
        } else {
            // Comprobar pasivas de reducción de daño
            boolean hasReductionPassive = "reduccion".equals(defender.getPasivaTipo()) || 
                                         "barrera".equals(defender.getPasivaTipo()) || 
                                         "armadura".equals(defender.getPasivaTipo());
            
            if (hasReductionPassive && Math.random() * 100 < defender.getPasivaValor()) {
                // Mostrar efecto de reducción
                animationManager.showDamageReductionEffect(defender, isPlayerAttack);
                
                // Mensaje de reducción (probabilidad reducida)
                if (Math.random() < 0.3) {
                    messageManager.displayMessage("Daño reducido", isPlayerAttack);
                }
            }
            
            // Mostrar texto de daño
            animationManager.showDamageText(damage, isPlayerAttack, isCritical, isTrueDamage);
        }
        
        // Efecto especial para daño verdadero
        if (isTrueDamage) {
            animationManager.showTrueDamageEffect(defender, isPlayerAttack);
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
    
    private void handleCharacterDefeat(PersonajeModel defeated, boolean isPlayerAttack) {
        // Ocultar el personaje derrotado
        if (isPlayerAttack) {
            uiManager.hideAICharacter();
        } else {
            uiManager.hidePlayerCharacter();
        }
        
        // Mensaje de derrota
        messageManager.displayMessage(defeated.getNombre() + " ha sido derrotado!", isPlayerAttack, () -> {
            if (isPlayerAttack) {
                // Si la IA fue derrotada
                boolean allAIDefeated = aiCharacters.stream().allMatch(PersonajeModel::isDerrotado);
                
                if (allAIDefeated) {
                    endCombat(true);
                } else {
                    changeAICharacter();
                }
            } else {
                // Si el jugador fue derrotado
                boolean allPlayerDefeated = playerCharacters.stream().allMatch(PersonajeModel::isDerrotado);
                
                if (allPlayerDefeated) {
                    endCombat(false);
                } else {
                    uiManager.showForceCharacterSelection(this);
                }
            }
        });
    }
    
    public void aiTurn() {
        // Obtener personajes actuales
        PersonajeModel attacker = aiCharacters.get(aiCharacterIndex);
        PersonajeModel defender = playerCharacters.get(playerCharacterIndex);
        
        // Regeneración si aplica
        if ("regeneracion".equals(attacker.getPasivaTipo())) {
            int regenerationAmount = (attacker.getVida() * attacker.getPasivaValor()) / 100;
            attacker.regenerar(regenerationAmount);
            animationManager.showRegenerationEffect(attacker, false);
            
            if (Math.random() < 0.5) {
                messageManager.displayMessage(attacker.getNombre() + " regenera " + 
                        regenerationAmount + " puntos de vida", false);
            }
        }
        
        // Seleccionar el mejor ataque
        String attackType = aiSelector.selectBestAttack(attacker, defender);
        
        // Si es una habilidad, consumir un uso
        if (attackType.equals("habilidad1") || attackType.equals("habilidad2")) {
            attacker.consumirUsoHabilidad(attackType);
        }
        
        String attackName = attacker.getNombreAtaque(attackType);
        
        // Mostrar mensaje de ataque
        messageManager.displayMessage(attacker.getNombre() + " usa " + attackName, false);
        
        // Animar ataque
        animationManager.animateAIAttack(attackType, () -> {
            boolean isCritical = attacker.esGolpeCritico();
            int attackPower = attacker.getPoderAtaque(attackType);
            String attackTypeCode = attacker.getTipoAtaque(attackType);
            
            int previousHealth = defender.getVidaActual();
            
            // Aplicar daño
            boolean defeated = defender.recibirDaño(attackPower, attackTypeCode, isCritical);
            
            // Calcular daño real
            int realDamage = previousHealth - defender.getVidaActual();
            
            // Determinar si es daño verdadero
            boolean isTrueDamage = "daño_verdadero".equals(attackTypeCode);
            
            processAttackResult(defender, realDamage, isCritical, isTrueDamage, defeated, false);
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
}