package purpsuite.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Date;

public class LoggerController {
    public class LogItem{
        private final SimpleObjectProperty<Date> logDate;
        private final SimpleStringProperty Info;

        public LogItem(Date logDate, String info) {
            this.logDate = new SimpleObjectProperty<Date>(logDate);
            Info = new SimpleStringProperty(info);
        }
        public String getLogDate() {
            return logDate.get().toString();
        }

        public void setLogDate(Date logDate) {
            this.logDate.set(logDate);
        }

        public String getInfo() {
            return Info.get();
        }

        public void setInfo(String info) {
            this.Info.set(info);
        }
    }

    @FXML
    private TableView<LogItem> LoggerTable;
    @FXML
    private TableColumn<LogItem,String> LoggerTableTime;
    @FXML
    private TableColumn<LogItem,String> LoggerTableInfo;

    private ObservableList<LogItem> LogData = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        LoggerTableTime.setCellValueFactory(new PropertyValueFactory<LogItem,String>("logDate"));
        LoggerTableInfo.setCellValueFactory(new PropertyValueFactory<LogItem,String>("info"));
        LoggerTable.setItems(LogData);
        addNewItem(new Date(), "Program start running.");
    }


    void addNewItem(Date date, String info){
        LogData.add(new LogItem(date, info));
    }
}
