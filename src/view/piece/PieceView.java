package src.view.piece;

import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import src.chess.piece.Piece;
import src.position.Position;
import src.view.board.ActiveBoardView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PieceView extends ImageView{
    public static final long TRANSITION_TIME = 400; //400

    public void updatePosition(Position position){//}, boolean isVisual) {
        TranslateTransition tf = new TranslateTransition(Duration.millis(TRANSITION_TIME),this);
        tf.setToX(position.getCol() * ActiveBoardView.SPOT_WIDTH);
        tf.setToY(position.getRow() * ActiveBoardView.SPOT_WIDTH);
        tf.play();
    }


    public void kill() {
        //TODO this method hasn't been test
        setVisible(false);
    }

    public void revive() {
        //TODO this method hasn't been test
        setVisible(true);
    }
    public void setupIcon(String filePath, Piece piece) {
        filePath += piece.isWhite() ? "/White_" : "/Black_";
        filePath += piece.getName() + ".png";

        try {
            Image image = new Image(new FileInputStream(filePath));
            setImage(image);
            setFitHeight(ActiveBoardView.SPOT_WIDTH);
            setFitWidth(ActiveBoardView.SPOT_WIDTH);
            setPreserveRatio(true);

            setTranslateX(piece.getPosition().getCol() * ActiveBoardView.SPOT_WIDTH);
            setTranslateY(piece.getPosition().getRow() * ActiveBoardView.SPOT_WIDTH);
        } catch(FileNotFoundException e) {
            System.err.printf("NO SUCH FILE \"%s\"%n", filePath);
        }
    }
}
