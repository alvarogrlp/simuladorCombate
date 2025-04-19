package es.alvarogrlp.marvelsimu.backend.selection.ui;

import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import es.alvarogrlp.marvelsimu.backend.selection.logic.SelectionManager;
import es.alvarogrlp.marvelsimu.backend.selection.util.CharacterDescriptionFormatter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Clase encargada de crear y gestionar el panel de información de un personaje
 */
public class CharacterInfoPanel {
    
    private SelectionManager selectionManager;
    private CharacterDescriptionFormatter formatter;
    
    public CharacterInfoPanel(SelectionManager selectionManager) {
        this.selectionManager = selectionManager;
        this.formatter = new CharacterDescriptionFormatter();
    }
    
    /**
     * Crea un panel con información detallada del personaje
     * @param character Personaje a mostrar
     * @return Panel de información
     */
    public VBox createInfoPanel(PersonajeModel character) {
        // Contenedor principal
        VBox panel = new VBox(10);
        panel.setPrefWidth(450);
        panel.setPrefHeight(500);
        panel.getStyleClass().add("character-info-container");
        panel.setPadding(new Insets(15));
        
        // Aplicar estilos explícitamente en caso de que la hoja de estilos no cargue
        panel.setStyle(
            "-fx-background-color: rgba(20, 30, 50, 0.9);" +
            "-fx-background-radius: 15px;" +
            "-fx-border-color: rgba(100, 150, 255, 0.6);" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 15px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.8), 10, 0, 0, 0);"
        );
        
        // Crear header con botón de cierre
        HBox header = createHeaderWithCloseButton(character.getNombre());
        
        // Resto del contenido del panel
        VBox characterDetails = createCharacterDetails(character);
        
        // Botón de seleccionar - Crear un contenedor para centrarlo
        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.CENTER);
        
        Button selectButton = new Button("SELECCIONAR");
        selectButton.getStyleClass().add("select-button");
        
        // Aplicar estilo explícito al botón también
        selectButton.setStyle(
            "-fx-background-color: rgba(0, 150, 0, 0.8);" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 16px;" +
            "-fx-border-color: rgba(0, 200, 0, 0.9);" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-padding: 8px 16px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 5, 0, 0, 2);"
        );
        
        selectButton.setPrefWidth(180);
        selectButton.setPrefHeight(35);
        selectButton.setOnAction(e -> selectionManager.confirmSelection());
        
        // Agregar efecto hover al botón
        selectButton.setOnMouseEntered(e -> {
            selectButton.setStyle(
                "-fx-background-color: rgba(0, 180, 0, 0.9);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 16px;" +
                "-fx-border-color: rgba(0, 200, 0, 0.9);" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;" +
                "-fx-padding: 8px 16px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 5, 0, 0, 2);" +
                "-fx-cursor: hand;" +
                "-fx-scale-x: 1.05;" +
                "-fx-scale-y: 1.05;"
            );
        });
        
        selectButton.setOnMouseExited(e -> {
            selectButton.setStyle(
                "-fx-background-color: rgba(0, 150, 0, 0.8);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 16px;" +
                "-fx-border-color: rgba(0, 200, 0, 0.9);" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;" +
                "-fx-padding: 8px 16px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 5, 0, 0, 2);" +
                "-fx-scale-x: 1.0;" +
                "-fx-scale-y: 1.0;"
            );
        });
        
        // Añadir el botón al contenedor centrado
        buttonContainer.getChildren().add(selectButton);
        
        // Añadir componentes al panel
        panel.getChildren().addAll(header, characterDetails, buttonContainer);
        
        return panel;
    }
    
    /**
     * Crea el encabezado con botón de cierre
     */
    private HBox createHeaderWithCloseButton(String characterName) {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPrefWidth(420);
        
        // Botón para cerrar la ventana
        Button closeButton = new Button("X");
        closeButton.getStyleClass().add("close-button");
        
        // Simplificar la funcionalidad del botón
        closeButton.setOnAction(e -> selectionManager.deselectCurrentCharacter());
        
        // Añadir título y botón de cerrar al header
        Text nameText = new Text(characterName);
        nameText.getStyleClass().add("character-name");
        
        header.getChildren().addAll(closeButton, nameText);
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(nameText, Priority.ALWAYS);
        
        return header;
    }
    
    /**
     * Crea la miniatura del personaje
     */
    private ImageView createCharacterThumbnail(PersonajeModel character) {
        ImageView miniatura = new ImageView();
        try {
            Image img = new Image(getClass().getClassLoader().getResourceAsStream(character.getImagenMiniatura()));
            miniatura.setImage(img);
            miniatura.setFitHeight(120);
            miniatura.setPreserveRatio(true);
            miniatura.getStyleClass().add("character-mini");
        } catch (Exception e) {
            System.err.println("Error cargando miniatura: " + e.getMessage());
        }
        return miniatura;
    }
    
    /**
     * Crea el panel de estadísticas básicas
     */
    private VBox createStatsPanel(PersonajeModel character) {
        VBox statsBox = new VBox(8);
        statsBox.setAlignment(Pos.CENTER_LEFT);
        statsBox.setPadding(new Insets(3));
        
        // Agregar estadísticas
        statsBox.getChildren().addAll(
            formatter.createStatRow("VIDA", character.getVida()),
            formatter.createStatRow("FUERZA", character.getFuerza()),
            formatter.createStatRow("VELOCIDAD", character.getVelocidad()),
            formatter.createStatRow("RESIST.", character.getResistencia()),
            formatter.createStatRow("MAGIA", character.getPoderMagico())
        );
        
        return statsBox;
    }
    
    /**
     * Crea el panel de defensas y resistencias
     */
    private VBox createDefensesPanel(PersonajeModel character) {
        VBox defensasBox = new VBox(8);
        defensasBox.setAlignment(Pos.CENTER);
        defensasBox.setPadding(new Insets(3, 0, 5, 0));
        
        // Texto de título para resistencias
        Text tituloResistencias = new Text("RESISTENCIAS");
        tituloResistencias.getStyleClass().add("section-title");
        
        // HBox para resistencias
        HBox resistenciasHBox = new HBox(10);
        resistenciasHBox.setAlignment(Pos.CENTER);
        
        Text txtResistenciaFisica = new Text("Resistencia Física: " + character.getResistenciaFisica() + "%");
        txtResistenciaFisica.getStyleClass().add("stat-text");
        
        Text txtResistenciaMagica = new Text("Resistencia Mágica: " + character.getResistenciaMagica() + "%");
        txtResistenciaMagica.getStyleClass().add("stat-text");
        
        resistenciasHBox.getChildren().addAll(txtResistenciaFisica, txtResistenciaMagica);
        
        // HBox para evasión y pasiva
        HBox evasionPasivaHBox = new HBox(10);
        evasionPasivaHBox.setAlignment(Pos.CENTER);
        
        Text txtEvasion = new Text("Evasión: " + character.getEvasion() + "%");
        txtEvasion.getStyleClass().add("stat-text");
        
        Text txtPasiva = new Text("Pasiva: " + formatter.getPassiveDescription(character));
        txtPasiva.getStyleClass().add("stat-text");
        txtPasiva.setWrappingWidth(300);
        
        evasionPasivaHBox.getChildren().addAll(txtEvasion, txtPasiva);
        
        // Añadir elementos al box de defensas
        defensasBox.getChildren().addAll(tituloResistencias, resistenciasHBox, evasionPasivaHBox);
        
        return defensasBox;
    }
    
    /**
     * Crea el panel de ataques y habilidades
     */
    private VBox createAttacksPanel(PersonajeModel character) {
        VBox attacksSection = new VBox(8);
        attacksSection.setAlignment(Pos.CENTER);
        
        // Ataques básicos
        Text attacksTitle = new Text("ATAQUES");
        attacksTitle.getStyleClass().add("section-title");
        
        HBox basicAttacksContainer = new HBox(20);
        basicAttacksContainer.setAlignment(Pos.CENTER);
        
        // Ataque melee
        VBox meleeAttackBox = new VBox(3);
        meleeAttackBox.setAlignment(Pos.CENTER);
        Text meleeAttackName = new Text(character.getAtaqueMeleeNombre());
        meleeAttackName.getStyleClass().add("attack-name");
        Text meleeAttackDamage = new Text("Daño: " + character.getAtaqueMelee());
        meleeAttackDamage.getStyleClass().add("attack-power");
        meleeAttackBox.getChildren().addAll(meleeAttackName, meleeAttackDamage);
        
        // Ataque a distancia
        VBox rangedAttackBox = new VBox(3);
        rangedAttackBox.setAlignment(Pos.CENTER);
        Text rangedAttackName = new Text(character.getAtaqueLejanoNombre());
        rangedAttackName.getStyleClass().add("attack-name");
        Text rangedAttackDamage = new Text("Daño: " + character.getAtaqueLejano());
        rangedAttackDamage.getStyleClass().add("attack-power");
        rangedAttackBox.getChildren().addAll(rangedAttackName, rangedAttackDamage);
        
        basicAttacksContainer.getChildren().addAll(meleeAttackBox, rangedAttackBox);
        
        // Habilidades especiales
        Text abilitiesTitle = new Text("HABILIDADES (USOS LIMITADOS)");
        abilitiesTitle.getStyleClass().add("section-title");
        
        HBox abilitiesContainer = new HBox(20);
        abilitiesContainer.setAlignment(Pos.CENTER);
        
        // Primera habilidad
        VBox ability1Box = createAbilityBox(
            character.getHabilidad1Nombre(),
            formatter.getAttackTypeDescription(character.getHabilidad1Tipo()),
            "Daño: " + character.getHabilidad1Poder() + " (3 usos)"
        );
        
        // Segunda habilidad
        VBox ability2Box = createAbilityBox(
            character.getHabilidad2Nombre(),
            formatter.getAttackTypeDescription(character.getHabilidad2Tipo()),
            "Daño: " + character.getHabilidad2Poder() + " (2 usos)"
        );
        
        abilitiesContainer.getChildren().addAll(ability1Box, ability2Box);
        
        // Añadir todos los elementos a la sección de ataques
        attacksSection.getChildren().addAll(attacksTitle, basicAttacksContainer, abilitiesTitle, abilitiesContainer);
        
        return attacksSection;
    }
    
    /**
     * Crea un box para mostrar información de una habilidad
     */
    private VBox createAbilityBox(String name, String type, String damage) {
        VBox abilityBox = new VBox(3);
        abilityBox.setAlignment(Pos.CENTER);
        
        Text nameText = new Text(name);
        nameText.getStyleClass().add("ability-name");
        
        Text typeText = new Text(type);
        typeText.getStyleClass().add("ability-type");
        
        Text damageText = new Text(damage);
        damageText.getStyleClass().add("ability-power");
        
        abilityBox.getChildren().addAll(nameText, typeText, damageText);
        
        return abilityBox;
    }
    
    /**
     * Crea los detalles del personaje
     */
    private VBox createCharacterDetails(PersonajeModel character) {
        VBox characterDetails = new VBox(10);
        characterDetails.setAlignment(Pos.TOP_CENTER);
        
        // Contenedor para miniatura y estadísticas
        HBox miniStatContainer = new HBox(15);
        miniStatContainer.setAlignment(Pos.CENTER);
        miniStatContainer.setPadding(new Insets(5));
        
        // Miniatura del personaje
        ImageView miniatura = createCharacterThumbnail(character);
        
        // Panel de estadísticas básicas
        VBox statsBox = createStatsPanel(character);
        
        miniStatContainer.getChildren().addAll(miniatura, statsBox);
        
        // Panel de defensas y resistencias
        VBox defensasBox = createDefensesPanel(character);
        
        // Panel de ataques y habilidades
        VBox attacksSection = createAttacksPanel(character);
        
        // Añadir todos los componentes al contenedor de detalles
        characterDetails.getChildren().addAll(miniStatContainer, defensasBox, attacksSection);
        
        return characterDetails;
    }
}