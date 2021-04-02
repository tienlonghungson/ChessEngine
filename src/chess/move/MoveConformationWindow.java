package src.chess.move;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.LinkedList;

public class MoveConformationWindow {
    private static Move finalMove;

    public static Move display(String title, String message, LinkedList<Move> moves) {
        Stage window = new Stage();

        // APPLICATION_MODAL : Defines a modal window that blocks events from being delivered to any other application window.
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);
        label.setTextAlignment(TextAlignment.CENTER);

        ChoiceBox<Move> moveChoiceBox = new ChoiceBox<>();
        moveChoiceBox.getItems().addAll(moves);

        Button button = new Button("Done");
        button.setOnAction(e -> {
            finalMove = moveChoiceBox.getValue();
            window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, moveChoiceBox, button);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return finalMove;
    }
}
