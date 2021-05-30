package src.view.board;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class PlayView {
     public static Stage getPlayView(String TITLE,Scene scene){
         Stage window = new Stage();
         window.setTitle(TITLE);
         window.setResizable(false);
         window.setScene(scene);
         return window;
     }
}
