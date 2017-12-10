package purpsuite;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import purpsuite.controller.MainPane;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("PurpSuite");
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
        primaryStage.setScene(new Scene(MainPane.getInstance(), 1000, 700));
        primaryStage.setResizable(false);
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
