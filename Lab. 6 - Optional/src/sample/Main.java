package sample;

import PainterLogic.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Deschidem fisierul fxml corespunzator aplicatiei, ce contine datele despre fereastra generate de SceneBuilder.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("myPainter.fxml"));
        Parent root = loader.load();
        //Setam stage-ul in controller (clasa ce se ocupa cu logica din spatele aplicatiei) pentru a avea
        //acces la el si din controller (lucru necesar pt afisarea dialogurilor pt deschiderea/salvarea unui fisier.
        Controller controller = loader.getController();
        controller.setStage(primaryStage);
        //Setam proprietatile stage-ului (numele ferestrei, dimensiunea ferestrei), dupa care o afisam.
        primaryStage.setTitle("My Painter");
        primaryStage.setScene(new Scene(root, 900, 500));
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }

}
