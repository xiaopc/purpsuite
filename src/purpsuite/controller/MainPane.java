package purpsuite.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Date;

public class MainPane extends GridPane {
    @FXML
    public static MainPane mainPane;
    LoggerController loggerController = new LoggerController();
    ProxyController proxyController = new ProxyController();

    Callback<Class<?>, Object> controllerFactory = type -> {
        if (type == LoggerController.class) {
            return loggerController ;
        } else if (type == ProxyController.class) {
            return proxyController ;
        }
        else {
            try {
                return type.newInstance();
            } catch (Exception exc) {
                exc.printStackTrace();
                throw new RuntimeException(exc);
            }
        }
    };

    public MainPane() throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/purpsuite/view/main.fxml"));
        loader.setControllerFactory(controllerFactory);
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }
    public static MainPane getInstance() throws IOException{
        if (mainPane == null) {
            mainPane = new MainPane();
        }
        return mainPane;
    }

    public void AddLog(Date date, String info){
        loggerController.addNewItem(date, info);
    }
    public void exit() { proxyController.exit(); }
}