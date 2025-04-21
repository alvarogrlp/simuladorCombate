package es.alvarogrlp.marvelsimu.backend.selection.ui;

import java.io.InputStream;
import java.util.List;

import es.alvarogrlp.marvelsimu.backend.model.AtaqueModel;
import es.alvarogrlp.marvelsimu.backend.model.PasivaModel;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import es.alvarogrlp.marvelsimu.backend.selection.logic.SelectionManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
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
    
    public CharacterInfoPanel(SelectionManager selectionManager) {
        this.selectionManager = selectionManager;
    }
    
    /**
     * Crea un panel con información detallada del personaje
     * @param character Personaje a mostrar
     * @return Panel de información
     */
    public VBox createInfoPanel(PersonajeModel character) {
        // Contenedor principal - Aumentar ancho para compensar la barra de desplazamiento
        VBox panel = new VBox(10);
        panel.setPrefWidth(580);  
        panel.setPrefHeight(650);
        panel.getStyleClass().add("character-info-container");
        panel.setPadding(new Insets(20));
        
        // Crear header con botón de cierre
        HBox header = createHeaderWithCloseButton(character.getNombre());
        
        // Crear un ScrollPane solo con scroll vertical
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER); // Desactivar scroll horizontal
        scrollPane.getStyleClass().add("info-scrollpane");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        // Contenedor para el contenido desplazable - AJUSTADO PARA LA BARRA DE DESPLAZAMIENTO
        VBox contentBox = new VBox(16);  
        contentBox.setPadding(new Insets(5, 15, 5, 5)); // Aumentar padding derecho
        contentBox.setStyle("-fx-background-color: transparent;");
        contentBox.setPrefWidth(520);  // Reducido para dejar espacio a la barra
        contentBox.setMaxWidth(520);   // Reducido para dejar espacio a la barra
        
        // SIMPLIFICADO: Solo mantener los stats principales con números grandes
        VBox mainStats = createMainStatsBox(character);
        
        // Crear secciones para pasivas y ataques
        VBox pasivasSection = createPasivasSection(character);
        VBox attacksSection = createAttacksSection(character);
        
        // Añadir componentes al contenedor principal en orden
        contentBox.getChildren().addAll(mainStats, pasivasSection, attacksSection);
        
        // Configurar el ScrollPane
        scrollPane.setContent(contentBox);
        
        // Botón de selección con nuevo estilo
        Button selectButton = new Button("SELECCIONAR");
        selectButton.getStyleClass().add("select-button");
        selectButton.setOnAction(e -> selectionManager.confirmSelection());
        selectButton.setPrefWidth(200);  // Ancho fijo para el botón
        
        // Centrar el botón
        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().add(selectButton);
        buttonContainer.setPadding(new Insets(10, 0, 5, 0));
        
        // Añadir componentes al panel principal
        panel.getChildren().addAll(header, scrollPane, buttonContainer);
        
        return panel;
    }
    
    /**
     * Crea el encabezado con el botón de cierre
     */
    private HBox createHeaderWithCloseButton(String characterName) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 0, 10, 0));
        header.setSpacing(10);
        
        // Título con el nombre del personaje
        Text nameText = new Text(characterName.toUpperCase());
        nameText.getStyleClass().add("character-name");
        HBox.setHgrow(nameText, Priority.ALWAYS);
        
        // Botón de cierre mejorado
        Button closeButton = new Button("X");
        closeButton.getStyleClass().add("close-button");
        closeButton.setOnAction(e -> selectionManager.hideCharacterInfo());
        
        // Añadir elementos al header
        header.getChildren().addAll(nameText, closeButton);
        
        return header;
    }
    
    /**
     * Crea la caja principal con las estadísticas grandes
     */
    private VBox createMainStatsBox(PersonajeModel character) {
        VBox container = new VBox(12);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(5));
        
        // Cargar imagen del personaje centrada
        ImageView charImage = new ImageView();
        charImage.setFitHeight(180);
        charImage.setFitWidth(140);
        charImage.setPreserveRatio(true);
        
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(character.getImagenMiniatura());
            if (is != null) {
                Image image = new Image(is);
                if (!image.isError()) {
                    charImage.setImage(image);
                    charImage.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 150, 255, 0.6), 10, 0.5, 0, 0);");
                }
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen de personaje: " + e.getMessage());
        }
        
        // Contenedor para la imagen
        HBox imageContainer = new HBox();
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.getChildren().add(charImage);
        
        // Estadísticas principales en formato grande
        HBox mainStatsBox = new HBox(15);
        mainStatsBox.setAlignment(Pos.CENTER);
        mainStatsBox.setPadding(new Insets(10, 0, 0, 0));
        
        VBox vida = createStatBox("VIDA", character.getVida());
        VBox fuerza = createStatBox("FUE", character.getFuerza());
        VBox velocidad = createStatBox("VEL", character.getVelocidad());
        VBox poder = createStatBox("POD", character.getPoder());
        
        mainStatsBox.getChildren().addAll(vida, fuerza, velocidad, poder);
        
        // Añadir ambos a un contenedor
        container.getChildren().addAll(imageContainer, mainStatsBox);
        
        return container;
    }
    
    /**
     * Crea una caja para una estadística individual
     */
    private VBox createStatBox(String statName, int statValue) {
        VBox statBox = new VBox(3);
        statBox.setAlignment(Pos.CENTER);
        statBox.getStyleClass().add("stat-box");
        statBox.setPrefSize(85, 70);
        
        Text valueText = new Text(String.valueOf(statValue));
        valueText.getStyleClass().add("stat-value");
        
        Text nameText = new Text(statName);
        nameText.getStyleClass().add("stat-name");
        
        statBox.getChildren().addAll(valueText, nameText);
        
        return statBox;
    }
    
    /**
     * Crea la sección de pasivas del personaje
     */
    private VBox createPasivasSection(PersonajeModel character) {
        VBox pasivasSection = new VBox(8);
        pasivasSection.setAlignment(Pos.CENTER);
        
        // Título de la sección
        Text pasivasTitle = new Text("HABILIDADES PASIVAS");
        pasivasTitle.getStyleClass().add("section-title");
        pasivasSection.getChildren().add(pasivasTitle);
        
        List<PasivaModel> pasivas = character.getPasivas();
        if (pasivas != null && !pasivas.isEmpty()) {
            for (PasivaModel pasiva : pasivas) {
                VBox pasivaBox = createPasivaBox(pasiva);
                pasivasSection.getChildren().add(pasivaBox);
            }
        } else {
            // Mostrar mensaje si no hay pasivas
            Text noPasivas = new Text("Este personaje no tiene habilidades pasivas registradas");
            noPasivas.getStyleClass().add("stat-text");
            noPasivas.setWrappingWidth(500);
            pasivasSection.getChildren().add(noPasivas);
        }
        
        return pasivasSection;
    }
    
    /**
     * Crea un box para mostrar información de una pasiva
     */
    private VBox createPasivaBox(PasivaModel pasiva) {
        VBox pasivaBox = new VBox(5);
        pasivaBox.setAlignment(Pos.CENTER_LEFT);
        pasivaBox.setPadding(new Insets(5, 10, 5, 10));
        pasivaBox.setStyle(
            "-fx-background-color: rgba(40, 60, 100, 0.3);" +
            "-fx-background-radius: 5px;" +
            "-fx-border-color: rgba(100, 150, 255, 0.4);" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 5px;"
        );
        
        // Nombre de la pasiva
        Text nombreText = new Text(pasiva.getNombre());
        nombreText.getStyleClass().add("ability-name");
        
        // Descripción de la pasiva - Ajustado ancho para la barra de desplazamiento
        Text descripcionText = new Text(pasiva.getDescripcion());
        descripcionText.getStyleClass().add("ability-description");
        descripcionText.setWrappingWidth(480); // Reducido de 500 a 480
        
        // Información adicional (tipo de trigger, cooldown, etc.)
        StringBuilder infoBuilder = new StringBuilder();
        if (pasiva.getUsosMaximos() > 0) {
            infoBuilder.append("Usos máximos: ").append(pasiva.getUsosMaximos()).append(" | ");
        }
        if (pasiva.getCooldownTurnos() > 0) {
            infoBuilder.append("Cooldown: ").append(pasiva.getCooldownTurnos()).append(" turnos");
        }
        
        if (infoBuilder.length() > 0) {
            Text infoText = new Text(infoBuilder.toString());
            infoText.getStyleClass().add("ability-type");
            pasivaBox.getChildren().addAll(nombreText, descripcionText, infoText);
        } else {
            pasivaBox.getChildren().addAll(nombreText, descripcionText);
        }
        
        return pasivaBox;
    }
    
    /**
     * Crea la sección de ataques y habilidades
     */
    private VBox createAttacksSection(PersonajeModel character) {
        VBox attacksSection = new VBox(12);
        attacksSection.setAlignment(Pos.CENTER);
        
        // Título de la sección
        Text attacksTitle = new Text("ATAQUES Y HABILIDADES");
        attacksTitle.getStyleClass().add("section-title");
        attacksSection.getChildren().add(attacksTitle);
        
        List<AtaqueModel> ataques = character.getAtaques();
        if (ataques != null && !ataques.isEmpty()) {
            for (AtaqueModel ataque : ataques) {
                VBox ataqueBox = createAtaqueBox(ataque);
                attacksSection.getChildren().add(ataqueBox);
            }
        } else {
            // Mostrar mensaje si no hay ataques
            Text noAtaques = new Text("Este personaje no tiene ataques registrados");
            noAtaques.getStyleClass().add("stat-text");
            noAtaques.setWrappingWidth(500);
            attacksSection.getChildren().add(noAtaques);
        }
        
        return attacksSection;
    }
    
    /**
     * Crea un box para mostrar información de un ataque
     */
    private VBox createAtaqueBox(AtaqueModel ataque) {
        VBox ataqueBox = new VBox(5);
        ataqueBox.setAlignment(Pos.CENTER_LEFT);
        ataqueBox.setPadding(new Insets(5, 10, 5, 10));
        ataqueBox.setStyle(
            "-fx-background-color: rgba(40, 60, 100, 0.3);" +
            "-fx-background-radius: 5px;" +
            "-fx-border-color: rgba(100, 150, 255, 0.4);" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 5px;"
        );
        
        // Nombre y tipo del ataque
        Text nombreText = new Text(ataque.getNombre() + " (" + formatTipoAtaque(ataque.getTipoAtaqueClave()) + ")");
        nombreText.getStyleClass().add("attack-name");
        
        // Daño base - Aplicar clase CSS correcta para que se muestre en color adecuado
        Text danoText = new Text("Daño base: " + ataque.getDanoBase());
        danoText.getStyleClass().add("attack-damage"); // Usar attack-damage en lugar de attack-power
        
        // Información adicional (usos, cooldown)
        StringBuilder infoBuilder = new StringBuilder();
        if (ataque.getUsosMaximos() > 0) {
            infoBuilder.append("Usos máximos: ").append(ataque.getUsosMaximos()).append(" | ");
        }
        if (ataque.getCooldownTurnos() > 0) {
            infoBuilder.append("Cooldown: ").append(ataque.getCooldownTurnos()).append(" turnos");
        }
        
        if (infoBuilder.length() > 0) {
            Text infoText = new Text(infoBuilder.toString());
            infoText.getStyleClass().add("ability-type");
            ataqueBox.getChildren().addAll(nombreText, danoText, infoText);
        } else {
            ataqueBox.getChildren().addAll(nombreText, danoText);
        }
        
        return ataqueBox;
    }
    
    /**
     * Formatea el tipo de ataque para su visualización
     */
    private String formatTipoAtaque(String tipoAtaque) {
        if (tipoAtaque == null) return "Desconocido";
        
        switch (tipoAtaque.toLowerCase()) {
            case "acc":
                return "Ataque Cuerpo a Cuerpo";
            case "aad":
                return "Ataque a Distancia";
            case "habilidad_mas_poderosa":
                return "Habilidad Más Poderosa";
            case "habilidad_caracteristica":
                return "Habilidad Característica";
            default:
                return tipoAtaque;
        }
    }
}