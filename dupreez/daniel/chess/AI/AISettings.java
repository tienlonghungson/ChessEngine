package dupreez.daniel.chess.AI;

import dupreez.daniel.chess.Launcher;
import dupreez.daniel.chess.Player.ComputerPlayer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class AISettings
{
    public String name;
    public String description;
    public int depth;
    public boolean alphaBetaPrunning;
    public boolean iterativeDeepening;
    public boolean useTimelimit;
    public int timelimit;
    public int iterativeDeepeningStartDepth;
    public boolean killerHeuristic;
    public boolean transpositionTable;
    public boolean moveOrdering;
    public int checkForCheckmateDepth;
    public int checkForStalemateDepth;
    public boolean addRandomness;

    private static AISettings getAISettings(String filePath)
    {
        HashMap<String, String> rawSettings = new HashMap<>();

        try
        {
            Scanner in = new Scanner(new File(filePath));
            while(in.hasNextLine())
            {
                String[] rawData = in.nextLine().split(":\\s+");
                rawSettings.put(rawData[0], rawData[1]);
            }
        }
        catch (FileNotFoundException e)
        {
            System.err.println("INVALID AISETTINGS FILE!");
            System.out.println(filePath);
            System.exit(-1);
        }

        AISettings settings = new AISettings();

        for (String key: rawSettings.keySet())
        {
            String value = rawSettings.get(key);
            switch (key)
            {
                case "name":
                    settings.name = value;
                    break;
                case "description":
                    settings.description = value;
                    break;
                case "depth":
                    settings.depth = Integer.parseInt(value);
                    break;
                case "alphaBetaPrunning":
                    settings.alphaBetaPrunning = Boolean.parseBoolean(value);
                    break;
                case "iterativeDeepening":
                    settings.iterativeDeepening = Boolean.parseBoolean(value);
                    break;
                case "timelimit":
                    if (value.equalsIgnoreCase("unlimited"))
                        settings.useTimelimit = false;
                    else
                    {
                        settings.useTimelimit = true;
                        settings.timelimit = Integer.parseInt(value);
                    }
                    break;
                case "iterativeDeepeningStartDepth":
                    settings.iterativeDeepeningStartDepth = Integer.parseInt(value);
                    break;
                case "killerHeuristic":
                    settings.killerHeuristic = Boolean.parseBoolean(value);
                    break;
                case "transpositionTable":
                    settings.transpositionTable = Boolean.parseBoolean(value);
                    break;
                case "moveOrdering":
                    settings.moveOrdering = Boolean.parseBoolean(value);
                    break;
                case "checkForCheckmateDepth":
                    settings.checkForCheckmateDepth = Integer.parseInt(value);
                    break;
                case "checkForStalemateDepth":
                    settings.checkForStalemateDepth = Integer.parseInt(value);
                    break;
                case "addRandomness":
                    settings.addRandomness = Boolean.parseBoolean(value);
                    break;
            }
        }

        return settings;
    }

    public static AISettings chooseAISettings()
    {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("AI Selection");

        String aiPath = Launcher.filePath.getAbsolutePath() + "/AIs";

        LinkedList<String> files = getAIFiles(aiPath);
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        for (String file: files)
            choiceBox.getItems().add(file.substring(0, file.length() - 3));
        choiceBox.setValue(files.getFirst().substring(0, files.getFirst().length() - 3));

        Button button = new Button("Done");
        button.setOnAction(e -> {
            if(choiceBox.getValue() != null)
                window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(choiceBox, button);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout);
        window.setScene(scene);

        window.showAndWait();

        return getAISettings(aiPath + "/" + choiceBox.getValue() + ".ai");
    }

    private static LinkedList<String> getAIFiles(String path)
    {
        File savesPath = new File(path);
        LinkedList<String> aiFiles = new LinkedList<>();
        for (File file: savesPath.listFiles())
        {
            if(file.isFile() && file.getName().contains(".ai"))
                aiFiles.add(file.getName());
        }
        return aiFiles;
    }
}
