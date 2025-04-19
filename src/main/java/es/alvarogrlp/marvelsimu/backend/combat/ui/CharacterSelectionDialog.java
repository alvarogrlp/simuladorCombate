package es.alvarogrlp.marvelsimu.backend.combat.ui;

import java.util.List;
import java.util.function.Consumer;

import es.alvarogrlp.marvelsimu.backend.model.PersonajeModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class CharacterSelectionDialog {
    
    private AnchorPane rootPane;
    
    public CharacterSelectionDialog(AnchorPane rootPane) {
        this.rootPane = rootPane;
    }
    
    public void showDialog(
            List<PersonajeModel> characters, 
            int currentCharacterIndex, 
            Consumer<Integer> onCharacterSelected) {
        
        HBox selectionContainer = new HBox(10);
        selectionContainer.setAlignment(Pos.CENTER);
        selectionContainer.getStyleClass().add("character-selection-container");
        
        VBox completeContainer = new VBox(20);
        completeContainer.setAlignment(Pos.CENTER);
        completeContainer.getStyleClass().add("selection-dialog");
        
        // Mostrar personajes disponibles
        boolean hasAvailableCharacters = false;
        
        for (int i = 0; i < characters.size(); i++) {
            PersonajeModel character = characters.get(i);
            
            VBox characterOption = new VBox(5);
            characterOption.setAlignment(Pos.CENTER);
            characterOption.setPadding(new Insets(10));
            
            ImageView characterImage = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(character.getImagenMiniatura())));
            characterImage.setFitWidth(70);
            characterImage.setFitHeight(70);
            characterImage.setPreserveRatio(true);
            
            Label nameLabel = new Label(character.getNombre());
            nameLabel.getStyleClass().add("character-name-small");
            
            characterOption.getChildren().addAll(characterImage, nameLabel);
            
            final int index = i;
            if (index == currentCharacterIndex || character.isDerrotado()) {
                characterOption.setOpacity(0.5);
                characterOption.getStyleClass().add("character-disabled");
            } else {
                hasAvailableCharacters = true;
                characterOption.getStyleClass().add("character-selectable");
                characterOption.setOnMouseClicked(event -> {
                    rootPane.getChildren().remove(completeContainer);
                    onCharacterSelected.accept(index);
                });
            }
            
            selectionContainer.getChildren().add(characterOption);
        }
        
        Button cancelButton = new Button("Cancelar");
        cancelButton.getStyleClass().add("back-button");
        
        cancelButton.setOnAction(event -> {
            rootPane.getChildren().remove(completeContainer);
        });
        
        completeContainer.getChildren().addAll(selectionContainer, cancelButton);
        
        // Solo mostrar si hay personajes disponibles para seleccionar
        if (hasAvailableCharacters) {
            AnchorPane.setTopAnchor(completeContainer, 300.0);
            AnchorPane.setLeftAnchor(completeContainer, 250.0);
            AnchorPane.setRightAnchor(completeContainer, 250.0);
            
            rootPane.getChildren().add(completeContainer);
        }
    }
    
    public void showForceDialog(
            List<PersonajeModel> characters, 
            int currentCharacterIndex, 
            Consumer<Integer> onCharacterSelected) {
        
        HBox selectionContainer = new HBox(10);
        selectionContainer.setAlignment(Pos.CENTER);
        selectionContainer.getStyleClass().add("character-selection-container");
        
        VBox completeContainer = new VBox(20);
        completeContainer.setAlignment(Pos.CENTER);
        completeContainer.getStyleClass().add("selection-dialog");
        
        Text selectionText = new Text("Debes seleccionar otro personaje para continuar");
        selectionText.getStyleClass().add("selection-text");
        selectionText.setTextAlignment(TextAlignment.CENTER);
        
        completeContainer.getChildren().add(selectionText);
        
        final int[] availableCharacters = {0};
        
        for (int i = 0; i < characters.size(); i++) {
            PersonajeModel character = characters.get(i);
            
            VBox characterOption = new VBox(5);
            characterOption.setAlignment(Pos.CENTER);
            characterOption.setPadding(new Insets(10));
            
            ImageView characterImage = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(character.getImagenMiniatura())));
            characterImage.setFitWidth(70);
            characterImage.setFitHeight(70);
            characterImage.setPreserveRatio(true);
            
            Label nameLabel = new Label(character.getNombre());
            nameLabel.getStyleClass().add("character-name-small");
            
            characterOption.getChildren().addAll(characterImage, nameLabel);
            
            final int index = i;
            if (index == currentCharacterIndex || character.isDerrotado()) {
                characterOption.setOpacity(0.5);
                characterOption.getStyleClass().add("character-disabled");
            } else {
                availableCharacters[0]++;
                
                characterOption.getStyleClass().add("character-selectable");
                characterOption.setOnMouseClicked(event -> {
                    rootPane.getChildren().remove(completeContainer);
                    onCharacterSelected.accept(index);
                });
            }
            
            selectionContainer.getChildren().add(characterOption);
        }
        
        completeContainer.getChildren().add(selectionContainer);
        
        if (availableCharacters[0] > 0) {
            AnchorPane.setTopAnchor(completeContainer, 300.0);
            AnchorPane.setLeftAnchor(completeContainer, 250.0);
            AnchorPane.setRightAnchor(completeContainer, 250.0);
            
            rootPane.getChildren().add(completeContainer);
        }
    }
}