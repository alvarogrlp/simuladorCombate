package es.alvarogrlp.marvelsimu.backend.selection.ui;

import java.io.InputStream;
import java.util.List;

import es.alvarogrlp.marvelsimu.backend.model.AtaqueModel;
import es.alvarogrlp.marvelsimu.backend.model.PasivaModel;
import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import es.alvarogrlp.marvelsimu.backend.selection.logic.SelectionManager;
import es.alvarogrlp.marvelsimu.backend.selection.util.CharacterDescriptionFormatter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
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
        VBox panel = new VBox(8);
        panel.setPrefWidth(520);
        panel.setPrefHeight(650);
        panel.getStyleClass().add("character-info-container");
        panel.setPadding(new Insets(15));
        
        // Crear header con botón de cierre
        HBox header = createHeaderWithCloseButton(character.getNombre());
        
        // Crear un ScrollPane para poder desplazarse por el contenido
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("info-scrollpane");
        
        // Contenedor para el contenido desplazable
        VBox contentBox = new VBox(12);
        contentBox.setPadding(new Insets(5, 10, 10, 10));
        contentBox.setStyle("-fx-background-color: transparent;");
        contentBox.setPrefWidth(480);
        contentBox.setMaxWidth(480);
        
        // Añadir imagen y estadísticas básicas
        HBox characterBasicInfo = createCharacterImageAndBasicStats(character);
        
        // Crear contenido resto del panel
        VBox characterDetails = createCharacterDetails(character);
        contentBox.getChildren().addAll(characterBasicInfo, characterDetails);
        
        // Configurar el ScrollPane
        scrollPane.setContent(contentBox);
        
        // Botón de selección con nuevo estilo
        Button selectButton = new Button("SELECCIONAR");
        selectButton.getStyleClass().add("select-button");
        selectButton.setOnAction(e -> selectionManager.confirmSelection());
        
        // Añadir componentes al panel principal
        panel.getChildren().addAll(header, scrollPane, selectButton);
        
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
     * Crea un panel con la imagen y estadísticas básicas
     */
    private HBox createCharacterImageAndBasicStats(PersonajeModel character) {
        HBox container = new HBox(15);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(10, 0, 20, 0));
        
        // Cargar imagen del personaje
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
                    
                    // Aplicar efecto de borde a la imagen
                    charImage.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 150, 255, 0.6), 10, 0.5, 0, 0);");
                }
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen de personaje: " + e.getMessage());
        }
        
        // Contenedor para las estadísticas principales
        VBox statsContainer = new VBox(15);
        statsContainer.setAlignment(Pos.CENTER);
        
        // Crear las cajas de estadísticas principales - CORREGIDO
        HBox mainStatsBox = new HBox(10);
        mainStatsBox.setAlignment(Pos.CENTER);
        
        // Usamos las propiedades que sí existen en PersonajeModel
        VBox vida = createStatBox("VIDA", character.getVida());
        VBox fuerza = createStatBox("FUE", character.getFuerza());
        VBox velocidad = createStatBox("VEL", character.getVelocidad());
        mainStatsBox.getChildren().addAll(vida, fuerza, velocidad);
        
        // Segunda fila de estadísticas - CORREGIDO
        HBox secondaryStatsBox = new HBox(10);
        secondaryStatsBox.setAlignment(Pos.CENTER);
        
        // Mostramos poder y otros valores derivados o calculados
        VBox poder = createStatBox("POD", character.getPoder());
        
        // Podemos mostrar valores de ataque como estadísticas adicionales
        AtaqueModel ataqueCC = character.getAtaquePorTipo("ACC");
        int valorAtaqueCC = ataqueCC != null ? ataqueCC.getDanoBase() : 0;
        VBox ataqueBox = createStatBox("ATQ", valorAtaqueCC);
        
        // Para la tercera estadística, podemos usar un valor derivado o un espacio en blanco
        VBox vidaActual = createStatBox("ACT", character.getVidaActual() > 0 ? 
                                              character.getVidaActual() : 
                                              character.getVida());
        
        secondaryStatsBox.getChildren().addAll(poder, ataqueBox, vidaActual);
        
        // Añadir las cajas de estadísticas al contenedor
        statsContainer.getChildren().addAll(mainStatsBox, secondaryStatsBox);
        
        // Añadir imagen y estadísticas al contenedor principal
        container.getChildren().addAll(charImage, statsContainer);
        
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
     * Crea una miniatura para el personaje - versión simplificada
     */
    private ImageView createCharacterThumbnail(PersonajeModel character) {
        ImageView miniatura = new ImageView();
        miniatura.setFitWidth(100);
        miniatura.setFitHeight(100);
        miniatura.setPreserveRatio(true);
        
        try {
            // Simplemente cargar la imagen directamente de la ruta proporcionada
            InputStream is = getClass().getClassLoader().getResourceAsStream(character.getImagenMiniatura());
            if (is != null) {
                Image image = new Image(is);
                if (!image.isError()) {
                    miniatura.setImage(image);
                    return miniatura;
                }
            }
            
            // Si la imagen no se encuentra, usar la imagen por defecto
            System.err.println("No se pudo cargar la miniatura: " + character.getImagenMiniatura());
            InputStream defaultIs = getClass().getClassLoader().getResourceAsStream("images/Personajes/random.png");
            if (defaultIs != null) {
                miniatura.setImage(new Image(defaultIs));
            }
        } catch (Exception e) {
            System.err.println("Error al crear la miniatura: " + e.getMessage());
            try {
                InputStream defaultIs = getClass().getClassLoader().getResourceAsStream("images/Personajes/random.png");
                if (defaultIs != null) {
                    miniatura.setImage(new Image(defaultIs));
                }
            } catch (Exception ex) {
                // No se puede hacer nada más
            }
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
            formatter.createStatRow("PODER", character.getPoder())
        );
        
        return statsBox;
    }
    
    /**
     * Crea los detalles del personaje
     */
    private VBox createCharacterDetails(PersonajeModel character) {
        VBox characterDetails = new VBox(15);
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
        
        // Sección de pasivas
        VBox pasivasSection = createPasivasSection(character);
        
        // Sección de ataques 
        VBox attacksSection = createAttacksSection(character);
        
        // Añadir todos los componentes al contenedor de detalles
        characterDetails.getChildren().addAll(miniStatContainer, pasivasSection, attacksSection);
        
        return characterDetails;
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
        
        // Descripción de la pasiva
        Text descripcionText = new Text(pasiva.getDescripcion());
        descripcionText.getStyleClass().add("stat-text");
        descripcionText.setWrappingWidth(500);
        
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
        
        // Daño base
        Text danoText = new Text("Daño base: " + ataque.getDanoBase());
        danoText.getStyleClass().add("attack-power");
        
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