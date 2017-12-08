package purpsuite;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import purpsuite.controller.MainPane;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view/main.fxml"));
        //Parent root = fxmlLoader.load();
        primaryStage.setTitle("PurpSuite");
        primaryStage.setScene(new Scene(MainPane.getInstance(), 800, 600));
        primaryStage.setOnCloseRequest((e)->{
            try {MainPane.getInstance().exit();} catch (Exception ex) {}
            System.exit(0);
        });
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
