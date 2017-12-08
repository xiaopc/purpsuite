package purpsuite.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import purpsuite.model.InProxy;
import purpsuite.model.OutConnection;
import purpsuite.model.ThrowToLogger;

public class ProxyController extends ThrowToLogger{
    public class HistoryItem{
        private final SimpleObjectProperty<Date> itemDate;
        private final SimpleStringProperty itemHost;
        private final SimpleStringProperty itemMethod;
        private final SimpleStringProperty itemURL;
        private final SimpleStringProperty itemStatus;
        HistoryItem(Date itemDate, String itemHost, String itemMethod, String itemURL, String itemStatus) {
            this.itemDate = new SimpleObjectProperty<Date>(itemDate);
            this.itemHost = new SimpleStringProperty(itemHost);
            this.itemMethod = new SimpleStringProperty(itemMethod);
            this.itemURL = new SimpleStringProperty(itemURL);
            this.itemStatus = new SimpleStringProperty(itemStatus);
        }

        public String getitemDate() {
            return itemDate.get().toString();
        }

        public SimpleObjectProperty<Date> itemDateProperty() {
            return itemDate;
        }

        public String getitemHost() {
            return itemHost.get();
        }

        public SimpleStringProperty itemHostProperty() {
            return itemHost;
        }

        public String getitemMethod() {
            return itemMethod.get();
        }

        public SimpleStringProperty itemMethodProperty() {
            return itemMethod;
        }

        public String getitemURL() {
            return itemURL.get();
        }

        public SimpleStringProperty itemURLProperty() {
            return itemURL;
        }

        public String getitemStatus() {
            return itemStatus.get();
        }

        public void setitemStatus(String s) {itemStatus.set(s);}

        public SimpleStringProperty itemStatusProperty() {
            return itemStatus;
        }
    }

    @FXML
    private Button AllowBtn;
    @FXML
    private Button RejectBtn;
    @FXML
    private Button FilterBtn;
    @FXML
    private Text HostText;
    @FXML
    private TextArea FilterTextArea;

    @FXML
    private TableView<HistoryItem> HistoryTable;
    @FXML
    private TableColumn<HistoryItem,String> HistoryTableColHost;
    @FXML
    private TableColumn<HistoryItem,String> HistoryTableColMethod;
    @FXML
    private TableColumn<HistoryItem,String> HistoryTableColURL;
    @FXML
    private TableColumn<HistoryItem,String> HistoryTableColStatus;
    @FXML
    private TableColumn<HistoryItem,String> HistoryTableColTime;

    @FXML
    private TextArea HistoryReqTextArea;
    @FXML
    private TextArea HistoryRecTextArea;

    @FXML
    private Button SettingApplyPortBtn;
    @FXML
    private TextField SettingPortField;

    // TODO
    //@FXML
    //private Button SettingWinProxyBtn;
    @FXML
    private Text SettingStatusText;

    private ObservableList<HistoryItem> HistoryData = FXCollections.observableArrayList();
    private Vector<String> HistoryReq = new Vector<>();
    private Vector<String> HistoryRes = new Vector<>();

    private boolean isFiltering = false;
    private boolean isProcessingFilter = false;
    private InProxy inProxy = new InProxy(this);
    private Thread inProxyThread;
    private int ItemCounter = 0;
    private InProxy.InPackageInfo NowPack;

    @FXML
    public void initialize(){
        HistoryTableColHost.setCellValueFactory(new PropertyValueFactory<HistoryItem, String>("itemHost"));
        HistoryTableColMethod.setCellValueFactory(new PropertyValueFactory<HistoryItem, String>("itemMethod"));
        HistoryTableColURL.setCellValueFactory(new PropertyValueFactory<HistoryItem, String>("itemURL"));
        HistoryTableColStatus.setCellValueFactory(new PropertyValueFactory<HistoryItem, String>("itemStatus"));
        HistoryTableColTime.setCellValueFactory(new PropertyValueFactory<HistoryItem, String>("itemDate"));
        HistoryTable.setItems(HistoryData);
        OutConnection.setUIController(this);
        HistoryTable.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            HistoryReqTextArea.setText(HistoryReq.get(newValue.intValue()));
            HistoryRecTextArea.setText(HistoryRes.get(newValue.intValue()));
        });
    }

    public void exit() { inProxy.close(); }

    @FXML
    private void handleAllowBtn(ActionEvent event){
        //HostText.setText("");
        HistoryReq.add(ItemCounter,FilterTextArea.getText());
        addHistoryLog(NowPack.getItemHostName(),NowPack.getMethod(),NowPack.getUri(),"wait");
        OutConnection out = new OutConnection(ItemCounter, NowPack.getItemHostName(), NowPack.getPort(),
                FilterTextArea.getText());
        Thread ot = new Thread(out);
        ot.start();
        UIBoxSet(false);
        ItemCounter++;
    }

    @FXML
    private void handleRejectBtn(ActionEvent event){
        inProxy.clearTopCon();
        UIBoxSet(false);
    }

    @FXML
    private void addHistoryLog(String ItemHost, String ItemMethod, String ItemURL, String ItemStatus){
        HistoryData.add(new HistoryItem(new Date(), ItemHost, ItemMethod, ItemURL, ItemStatus));
    }

    @FXML
    private void handleFilterBtn(ActionEvent event){
        try {
            isFiltering = !isFiltering;
            FilterBtn.setText(isFiltering?"拦截开":"拦截关");
            MainPane.getInstance().AddLog(new Date(), "Change Filter Switch: " + isFiltering);
        } catch (Exception e){
            Throw(e);
        }
    }

    @FXML
    public void HandleNotify(){
        NowPack = inProxy.getPacketInfo();
        if (isFiltering) {
            UIBoxSet(true);
            //HostText.setText("目标地址: "+NowPack.getItemHostName()+":"+NowPack.getPort());
        } else {
            HistoryReq.add(ItemCounter,NowPack.getItemContent());
            addHistoryLog(NowPack.getItemHostName(),NowPack.getMethod(),NowPack.getUri(),"wait");
            OutConnection out = new OutConnection(ItemCounter, NowPack.getItemHostName(), NowPack.getPort(),
                    NowPack.getItemContent());
            Thread ot = new Thread(out);
            ot.start();
            ItemCounter++;
        }
    }

    @FXML
    private void UIBoxSet(boolean is){
        isProcessingFilter = is;
        AllowBtn.setDisable(!is);
        RejectBtn.setDisable(!is);
        FilterTextArea.setDisable(!is);
        FilterTextArea.setText(is?NowPack.getItemContent():"");
        HostText.setText(is?"目标地址: "+NowPack.getItemHostName()+":"+NowPack.getPort():"");
    }

    public void HandleBack(int id, String backData) throws Exception{
        HistoryRes.add(id,backData);
        inProxy.returnConnection(backData);
        HistoryData.get(id).setitemStatus("true");
    }

    @FXML
    private void handleApplyPort(ActionEvent event) throws IOException{
        try {
            inProxy.initProxy(Integer.parseInt(SettingPortField.getText()));
            inProxyThread = new Thread(inProxy);
            inProxyThread.start();
            SettingStatusText.setText(inProxy.getIsSet() ? "当前状态: 正在运行" : "当前状态: 未启动");
        } catch (Exception e){
            Throw(e);
        }
    }

}
