package simulator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ModernGPSSimulator extends Application {
    
    // UI Components
    private TextField imeiField;
    private TextField serverIpField;
    private TextField serverPortField;
    private TextField speedField;
    private TextField latitudeField;
    private TextField longitudeField;
    private TextField altitudeField;
    private TextField angleField;
    private TextField dateTimeField;
    private TextArea paramsArea;
    private TextArea logArea;
    private TextField intervalField;
    private TextField hexDataField;
    private ComboBox<String> tripsCombo;
    private CheckBox autoDateTimeCheck;
    private CheckBox hexModeCheck;
    private Button sendButton;
    private Button autoSendButton;
    private Button tripButton;
    private Button updateTimeButton;
    private Button clearLogButton;
    private ProgressIndicator progressIndicator;
    private Label statusLabel;
    
    // State management
    private final BooleanProperty isAutoSending = new SimpleBooleanProperty(false);
    private final BooleanProperty isTripRunning = new SimpleBooleanProperty(false);
    private Task<Void> autoSendTask;
    private Task<Void> tripTask;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("GPS Device Simulator - Modern Interface");
        
        // Create main layout
        BorderPane root = new BorderPane();
        root.getStyleClass().add("main-container");
        
        // Top bar
        root.setTop(createTopBar());
        
        // Main content
        TabPane tabPane = createMainContent();
        root.setCenter(tabPane);
        
        // Bottom status bar
        root.setBottom(createStatusBar());
        
        // Create scene and apply CSS
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);
        primaryStage.show();
        
        // Initialize default values
        initializeDefaults();
        setupEventHandlers();
    }
    
    private VBox createTopBar() {
        VBox topBar = new VBox();
        topBar.getStyleClass().add("top-bar");
        
        // Header
        HBox header = new HBox();
        header.getStyleClass().add("header");
        header.setAlignment(Pos.CENTER_LEFT);
        
        Text title = new Text("GPS Device Simulator");
        title.getStyleClass().add("title");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        progressIndicator.setPrefSize(20, 20);
        
        header.getChildren().addAll(title, spacer, progressIndicator);
        
        topBar.getChildren().add(header);
        return topBar;
    }
    
    private TabPane createMainContent() {
        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("main-tabs");
        
        // Device Configuration Tab
        Tab deviceTab = new Tab("Device Configuration");
        deviceTab.setClosable(false);
        deviceTab.setContent(createDeviceConfigTab());
        
        // Trip Simulation Tab
        Tab tripTab = new Tab("Trip Simulation");
        tripTab.setClosable(false);
        tripTab.setContent(createTripSimulationTab());
        
        // Advanced Tab
        Tab advancedTab = new Tab("Advanced");
        advancedTab.setClosable(false);
        advancedTab.setContent(createAdvancedTab());
        
        // Activity Log Tab
        Tab logTab = new Tab("Activity Log");
        logTab.setClosable(false);
        logTab.setContent(createLogTab());
        
        tabPane.getTabs().addAll(deviceTab, tripTab, advancedTab, logTab);
        
        return tabPane;
    }
    
    private ScrollPane createDeviceConfigTab() {
        VBox mainContent = new VBox();
        mainContent.getStyleClass().add("tab-content");
        
        // Device Settings Card
        VBox deviceCard = createCard("Device Settings", createDeviceSettings());
        
        // Location Data Card
        VBox locationCard = createCard("Location Data", createLocationSettings());
        
        // Parameters Card
        VBox paramsCard = createCard("Parameters", createParametersSettings());
        
        // Control Panel Card
        VBox controlCard = createCard("Send Controls", createControlPanel());
        
        mainContent.getChildren().addAll(deviceCard, locationCard, paramsCard, controlCard);
        
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        
        return scrollPane;
    }
    
    private VBox createCard(String title, VBox content) {
        VBox card = new VBox();
        card.getStyleClass().add("card");
        
        // Card header
        Label cardTitle = new Label(title);
        cardTitle.getStyleClass().add("card-title");
        
        // Card content
        content.getStyleClass().add("card-content");
        
        card.getChildren().addAll(cardTitle, content);
        
        return card;
    }
    
    private VBox createDeviceSettings() {
        VBox content = new VBox();
        
        GridPane grid = new GridPane();
        grid.getStyleClass().add("form-grid");
        
        // IMEI
        grid.add(new Label("IMEI:"), 0, 0);
        imeiField = new TextField();
        imeiField.getStyleClass().add("text-field");
        grid.add(imeiField, 1, 0);
        
        // Server IP
        grid.add(new Label("Server IP:"), 0, 1);
        serverIpField = new TextField();
        serverIpField.getStyleClass().add("text-field");
        grid.add(serverIpField, 1, 1);
        
        // Server Port
        grid.add(new Label("Server Port:"), 0, 2);
        serverPortField = new TextField();
        serverPortField.getStyleClass().add("text-field");
        grid.add(serverPortField, 1, 2);
        
        content.getChildren().add(grid);
        return content;
    }
    
    private VBox createLocationSettings() {
        VBox content = new VBox();
        
        GridPane grid = new GridPane();
        grid.getStyleClass().add("form-grid");
        
        // Speed
        grid.add(new Label("Speed (km/h):"), 0, 0);
        speedField = new TextField();
        speedField.getStyleClass().add("text-field");
        grid.add(speedField, 1, 0);
        
        // Latitude
        grid.add(new Label("Latitude:"), 0, 1);
        latitudeField = new TextField();
        latitudeField.getStyleClass().add("text-field");
        grid.add(latitudeField, 1, 1);
        
        // Longitude
        grid.add(new Label("Longitude:"), 0, 2);
        longitudeField = new TextField();
        longitudeField.getStyleClass().add("text-field");
        grid.add(longitudeField, 1, 2);
        
        // Altitude
        grid.add(new Label("Altitude (m):"), 0, 3);
        altitudeField = new TextField();
        altitudeField.getStyleClass().add("text-field");
        grid.add(altitudeField, 1, 3);
        
        // Angle
        grid.add(new Label("Angle (degrees):"), 0, 4);
        angleField = new TextField();
        angleField.getStyleClass().add("text-field");
        grid.add(angleField, 1, 4);
        
        // DateTime
        grid.add(new Label("Date/Time:"), 0, 5);
        dateTimeField = new TextField();
        dateTimeField.getStyleClass().add("text-field");
        grid.add(dateTimeField, 1, 5);
        
        updateTimeButton = new Button("Update Time");
        updateTimeButton.getStyleClass().addAll("button", "secondary-button");
        grid.add(updateTimeButton, 2, 5);
        
        // Auto DateTime
        autoDateTimeCheck = new CheckBox("Use current time automatically");
        autoDateTimeCheck.getStyleClass().add("checkbox");
        grid.add(autoDateTimeCheck, 0, 6);
        GridPane.setColumnSpan(autoDateTimeCheck, 3);
        
        content.getChildren().add(grid);
        return content;
    }
    
    private VBox createParametersSettings() {
        VBox content = new VBox();
        
        Label label = new Label("JSON Parameters:");
        label.getStyleClass().add("field-label");
        
        paramsArea = new TextArea();
        paramsArea.getStyleClass().add("text-area");
        paramsArea.setPrefRowCount(4);
        
        content.getChildren().addAll(label, paramsArea);
        return content;
    }
    
    private VBox createControlPanel() {
        VBox content = new VBox();
        
        HBox buttonRow1 = new HBox();
        buttonRow1.getStyleClass().add("button-row");
        
        sendButton = new Button("Send Data");
        sendButton.getStyleClass().addAll("button", "primary-button");
        
        autoSendButton = new Button("Auto Send");
        autoSendButton.getStyleClass().addAll("button", "secondary-button");
        
        Label intervalLabel = new Label("Interval (sec):");
        intervalLabel.getStyleClass().add("field-label");
        
        intervalField = new TextField();
        intervalField.getStyleClass().add("text-field");
        intervalField.setPrefWidth(80);
        
        buttonRow1.getChildren().addAll(sendButton, autoSendButton, intervalLabel, intervalField);
        
        content.getChildren().add(buttonRow1);
        return content;
    }
    
    private ScrollPane createTripSimulationTab() {
        VBox mainContent = new VBox();
        mainContent.getStyleClass().add("tab-content");
        
        VBox tripCard = createCard("Trip Simulation", createTripSettings());
        
        mainContent.getChildren().add(tripCard);
        
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        
        return scrollPane;
    }
    
    private VBox createTripSettings() {
        VBox content = new VBox();
        
        GridPane grid = new GridPane();
        grid.getStyleClass().add("form-grid");
        
        // Trip selection
        grid.add(new Label("Select Trip:"), 0, 0);
        tripsCombo = new ComboBox<>();
        tripsCombo.getStyleClass().add("combo-box");
        tripsCombo.getItems().addAll("Trip 1", "Trip 2");
        grid.add(tripsCombo, 1, 0);
        
        // Trip controls
        HBox tripControls = new HBox();
        tripControls.getStyleClass().add("button-row");
        
        tripButton = new Button("Start Trip");
        tripButton.getStyleClass().addAll("button", "primary-button");
        
        tripControls.getChildren().add(tripButton);
        
        grid.add(tripControls, 0, 1);
        GridPane.setColumnSpan(tripControls, 2);
        
        content.getChildren().add(grid);
        return content;
    }
    
    private ScrollPane createAdvancedTab() {
        VBox mainContent = new VBox();
        mainContent.getStyleClass().add("tab-content");
        
        VBox hexCard = createCard("Raw Hex Data", createHexSettings());
        
        mainContent.getChildren().add(hexCard);
        
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        
        return scrollPane;
    }
    
    private VBox createHexSettings() {
        VBox content = new VBox();
        
        hexModeCheck = new CheckBox("Send raw hex data");
        hexModeCheck.getStyleClass().add("checkbox");
        
        Label hexLabel = new Label("Hex Data:");
        hexLabel.getStyleClass().add("field-label");
        
        hexDataField = new TextField();
        hexDataField.getStyleClass().add("text-field");
        
        content.getChildren().addAll(hexModeCheck, hexLabel, hexDataField);
        return content;
    }
    
    private ScrollPane createLogTab() {
        VBox mainContent = new VBox();
        mainContent.getStyleClass().add("tab-content");
        
        VBox logCard = createCard("Activity Log", createLogSettings());
        
        mainContent.getChildren().add(logCard);
        
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        
        return scrollPane;
    }
    
    private VBox createLogSettings() {
        VBox content = new VBox();
        
        HBox logControls = new HBox();
        logControls.getStyleClass().add("button-row");
        
        clearLogButton = new Button("Clear Log");
        clearLogButton.getStyleClass().addAll("button", "danger-button");
        
        logControls.getChildren().add(clearLogButton);
        
        logArea = new TextArea();
        logArea.getStyleClass().add("log-area");
        logArea.setEditable(false);
        logArea.setPrefRowCount(15);
        
        content.getChildren().addAll(logControls, logArea);
        return content;
    }
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox();
        statusBar.getStyleClass().add("status-bar");
        statusBar.setAlignment(Pos.CENTER_LEFT);
        
        statusLabel = new Label("Ready");
        statusLabel.getStyleClass().add("status-label");
        
        statusBar.getChildren().add(statusLabel);
        return statusBar;
    }
    
    private void initializeDefaults() {
        imeiField.setText("24343534");
        serverIpField.setText("127.0.0.1");
        serverPortField.setText("11706");
        speedField.setText("60");
        latitudeField.setText("14.654918");
        longitudeField.setText("-90.48515");
        altitudeField.setText("30");
        angleField.setText("20");
        dateTimeField.setText(getCurrentTimeDbFormatStr());
        paramsArea.setText("{\n\"io1\":\"1\"\n}");
        intervalField.setText("5");
        logArea.setText("Ready to send data...\n");
        tripsCombo.getSelectionModel().selectFirst();
    }
    
    private void setupEventHandlers() {
        // Send button
        sendButton.setOnAction(e -> sendData(null));
        
        // Auto send button
        autoSendButton.setOnAction(e -> toggleAutoSend());
        
        // Trip button
        tripButton.setOnAction(e -> toggleTrip());
        
        // Update time button
        updateTimeButton.setOnAction(e -> 
            dateTimeField.setText(getCurrentTimeDbFormatStr()));
        
        // Clear log button
        clearLogButton.setOnAction(e -> logArea.clear());
        
        // Auto datetime checkbox
        autoDateTimeCheck.setOnAction(e -> 
            dateTimeField.setDisable(autoDateTimeCheck.isSelected()));
        
        // State bindings
        autoSendButton.textProperty().bind(
            isAutoSending.asString().concat("Stop Auto Send").concat(
                isAutoSending.not().asString().concat("Start Auto Send")));
        
        tripButton.textProperty().bind(
            isTripRunning.asString().concat("Stop Trip").concat(
                isTripRunning.not().asString().concat("Start Trip")));
        
        progressIndicator.visibleProperty().bind(
            isAutoSending.or(isTripRunning));
    }
    
    private void sendData(TripData tripData) {
        Platform.runLater(() -> {
            logArea.appendText(String.format("[%s] Sending data to %s:%s\n", 
                getCurrentTimeDbFormatStr(), 
                serverIpField.getText(), 
                serverPortField.getText()));
            
            statusLabel.setText("Sending data...");
            
            if (hexModeCheck.isSelected() && !hexDataField.getText().isEmpty()) {
                logArea.appendText("Mode: Raw Hex Data\n");
                logArea.appendText("Data: " + hexDataField.getText() + "\n");
                SimulatorSender.SendPackage(new MockTextArea(logArea), 
                    serverIpField.getText(), 
                    Integer.valueOf(serverPortField.getText()), 
                    hexDataField.getText());
            } else {
                DeviceWebLocationData data = new DeviceWebLocationData();
                data.setImei(imeiField.getText());
                data.setSpeed(Double.valueOf(speedField.getText()));
                data.setLat(tripData == null ? Double.valueOf(latitudeField.getText()) : Double.valueOf(tripData.getLat()));
                data.setLng(tripData == null ? Double.valueOf(longitudeField.getText()) : Double.valueOf(tripData.getLng()));
                data.setAltitude(Double.valueOf(altitudeField.getText()));
                data.setAngle(Double.valueOf(angleField.getText()));
                data.setDtDevice(getDtTracker());
                data.setParams(paramsArea.getText());
                
                logArea.appendText("Mode: JSON Location Data\n");
                logArea.appendText("Data: " + data.toString() + "\n");
                SimulatorSender.SendPackage(new MockTextArea(logArea), 
                    serverIpField.getText(), 
                    Integer.valueOf(serverPortField.getText()), 
                    data);
            }
            
            statusLabel.setText("Ready");
        });
    }
    
    private void toggleAutoSend() {
        if (!isAutoSending.get()) {
            startAutoSend();
        } else {
            stopAutoSend();
        }
    }
    
    private void startAutoSend() {
        isAutoSending.set(true);
        statusLabel.setText("Auto sending active...");
        
        autoSendTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int interval = Integer.valueOf(intervalField.getText());
                
                while (!isCancelled()) {
                    Platform.runLater(() -> sendData(null));
                    
                    try {
                        Thread.sleep(interval * 1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                return null;
            }
        };
        
        new Thread(autoSendTask).start();
    }
    
    private void stopAutoSend() {
        isAutoSending.set(false);
        statusLabel.setText("Auto send stopped");
        
        if (autoSendTask != null) {
            autoSendTask.cancel();
        }
    }
    
    private void toggleTrip() {
        if (!isTripRunning.get()) {
            startTrip();
        } else {
            stopTrip();
        }
    }
    
    private void startTrip() {
        try {
            List<TripData> tripData = loadTrip(tripsCombo.getSelectionModel().getSelectedIndex());
            isTripRunning.set(true);
            statusLabel.setText("Trip simulation active...");
            
            tripTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    int interval = Integer.valueOf(intervalField.getText());
                    int count = 0;
                    
                    while (!isCancelled()) {
                        if (count >= tripData.size()) {
                            count = 0;
                        }
                        
                        TripData data = tripData.get(count);
                        Platform.runLater(() -> sendData(data));
                        
                        count++;
                        try {
                            Thread.sleep(interval * 1000);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                    return null;
                }
            };
            
            new Thread(tripTask).start();
        } catch (IOException e) {
            statusLabel.setText("Error starting trip: " + e.getMessage());
            isTripRunning.set(false);
        }
    }
    
    private void stopTrip() {
        isTripRunning.set(false);
        statusLabel.setText("Trip simulation stopped");
        
        if (tripTask != null) {
            tripTask.cancel();
        }
    }
    
    private List<TripData> loadTrip(int selectedIndex) throws IOException {
        List<TripData> tripData = new ArrayList<>();
        ClassLoader classLoader = ModernGPSSimulator.class.getClassLoader();
        
        Reader reader = new InputStreamReader(Objects.requireNonNull(
            classLoader.getResourceAsStream("trip1.json")));
        Gson gson = new Gson();
        
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
        JsonArray routes = jsonObject.getAsJsonArray("route");
        
        routes.forEach(e -> {
            JsonArray routePoint = e.getAsJsonArray();
            tripData.add(new TripData(routePoint.get(1).getAsString(), 
                routePoint.get(2).getAsString()));
        });
        
        return tripData;
    }
    
    private String getDtTracker() {
        if (autoDateTimeCheck.isSelected()) {
            return getCurrentTimeDbFormatStr();
        }
        
        String dtTrackerStr = dateTimeField.getText();
        if (dtTrackerStr.isEmpty()) {
            return getCurrentTimeDbFormatStr();
        }
        
        return dtTrackerStr;
    }
    
    public static String getCurrentTimeDbFormatStr() {
        DateTime result = new DateTime(DateTimeZone.UTC);
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        return dtf.print(result);
    }
    
    // Mock TextArea wrapper for compatibility with existing SimulatorSender
    private static class MockTextArea extends javax.swing.JTextArea {
        private final TextArea fxTextArea;
        
        public MockTextArea(TextArea fxTextArea) {
            this.fxTextArea = fxTextArea;
        }
        
        @Override
        public void append(String str) {
            Platform.runLater(() -> fxTextArea.appendText(str));
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}