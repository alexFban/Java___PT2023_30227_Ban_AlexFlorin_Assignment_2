import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SimulationFrame extends Application{
    GridPane root = new GridPane();
    TextField timeLim = new TextField();
    TextField maxProsTime = new TextField();
    TextField minProsTime = new TextField();
    TextField maxArrTime = new TextField();
    TextField minArrTime = new TextField();
    TextField queueNr = new TextField();
    TextField clientNr = new TextField();
    Button startBtn = new Button("Start sim");
    Text time = new Text("Time limit:");
    Text pros = new Text("Serving:");
    Text arr = new Text("Arriving:");
    Text min1 = new Text("Min:");
    Text max1 = new Text("Max:");
    Text min2 = new Text("Min:");
    Text max2 = new Text("Max:");
    Text qAmount = new Text("Queue Amount:");
    Text cAmount = new Text("Client Amount:");
    Text isFinished = new Text("Simulation Complete");
    Button done = new Button("Close");
    public static List<String> visualData;
    Thread t;
    static Stage visual = new Stage();
    static GridPane visualRoot = new GridPane();
    static Text printText = new Text("");
    static Scene scene = new Scene(visualRoot, 400, 300);
    public SimulationFrame(){}
    public static void getData(){
        visualData.add(SimulationManager.dataToPrint);
    }
    public void printLiveData(){

        int i=0;
        while(t.isAlive()){
            if(i<visualData.size()){
                final int point = i;
                Platform.runLater(() -> {
                    scene.setRoot(visualRoot);
                    visual.setScene(scene);
                    visual.show();
                    //printText.setText(printText.getText() + visualData.get(i) + "\n");
                    //visualRoot.getChildren().clear();
                    visualRoot.getChildren().clear();
                    visualRoot.add(new Text(visualData.get(point) + "\n"), 0, 0);
                    //scene.setRoot(visualRoot);
                    System.out.println(visualData.get(point) + "\n");
                });
                i++;
            }
        }
        Platform.runLater(() -> {
            visualRoot.add(isFinished, 0, 1);
            visualRoot.add(done, 0, 2);
        });
    }
    public void setFields(){
        timeLim.setPrefWidth(40);
        minProsTime.setPrefWidth(40);
        maxProsTime.setPrefWidth(40);
        minArrTime.setPrefWidth(40);
        maxArrTime.setPrefWidth(40);
        queueNr.setPrefWidth(40);
        clientNr.setPrefWidth(40);
        root.add(time, 0, 0);
        root.add(pros, 0, 1);
        root.add(arr, 0, 2);
        root.add(qAmount, 0, 3);
        root.add(cAmount, 0, 4);
        root.add(min1, 1, 1);
        root.add(max1, 3, 1);
        root.add(min2, 1, 2);
        root.add(max2, 3, 2);
        root.add(timeLim, 1, 0, 4, 1);
        root.add(minProsTime, 2, 1);
        root.add(maxProsTime, 4, 1);
        root.add(minArrTime, 2, 2);
        root.add(maxArrTime, 4, 2);
        root.add(queueNr, 1, 3, 4, 1);
        root.add(clientNr, 1, 4, 4, 1);
        root.add(startBtn, 1, 5, 2, 1);
    }
    @Override
    public void start(Stage stage) throws Exception {
        root.setAlignment(Pos.CENTER);
        root.setHgap(5);
        root.setVgap(10);
        setFields();
        startBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                visualData = new ArrayList<String>();
                visualData.add("");
                t = new Thread(() -> SimulationManager.main(new String[]{timeLim.getText(), minProsTime.getText(),
                        maxProsTime.getText(), minArrTime.getText(), maxArrTime.getText(), queueNr.getText(),
                        clientNr.getText()}));
                t.start();
                new Thread(() -> printLiveData()).start();
                stage.close();
            }
        });
        done.setOnAction(e -> {visual.close(); System.exit(0);});
        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Queue Simulator");
        stage.show();
    }
    public static void main(String[] args){launch();}
}
