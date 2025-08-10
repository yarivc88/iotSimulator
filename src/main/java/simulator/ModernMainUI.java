package simulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ModernMainUI extends JFrame {
    private static final long serialVersionUID = 1L;
    
    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color PANEL_COLOR = Color.WHITE;
    
    // Components
    private JTextField imeiField;
    private JTextField serverIpField;
    private JTextField serverPortField;
    private JTextField speedField;
    private JTextField latitudeField;
    private JTextField longitudeField;
    private JTextField altitudeField;
    private JTextField angleField;
    private JTextField dateTimeField;
    private JTextArea paramsArea;
    private JTextArea logArea;
    private JTextField intervalField;
    private JTextField hexDataField;
    private JComboBox<String> tripsCombo;
    private JCheckBox autoDateTimeCheck;
    private JCheckBox hexModeCheck;
    private JButton sendButton;
    private JButton autoSendButton;
    private JButton tripButton;
    private JButton updateTimeButton;
    private JButton clearLogButton;
    
    // Status variables
    private boolean isAutoSending = false;
    private boolean isTripRunning = false;
    private Thread autoSendThread;
    private Thread tripThread;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ModernMainUI().setVisible(true);
        });
    }
    
    public ModernMainUI() {
        initializeUI();
        setupEventHandlers();
    }
    
    private void initializeUI() {
        setTitle("GPS Simulator - Modern Interface");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Set background color
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Create main layout
        setLayout(new BorderLayout());
        
        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Add tabs
        tabbedPane.addTab("Device Configuration", createDeviceConfigPanel());
        tabbedPane.addTab("Trip Simulation", createTripSimulationPanel());
        tabbedPane.addTab("Advanced", createAdvancedPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        add(createLogPanel(), BorderLayout.SOUTH);
        add(createStatusPanel(), BorderLayout.NORTH);
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("GPS Device Simulator");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        
        panel.add(titleLabel);
        
        return panel;
    }
    
    private JPanel createDeviceConfigPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Device settings panel
        JPanel devicePanel = createStyledPanel("Device Settings");
        devicePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // IMEI
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        devicePanel.add(new JLabel("IMEI:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        imeiField = createStyledTextField("24343534");
        devicePanel.add(imeiField, gbc);
        
        // Server IP
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        devicePanel.add(new JLabel("Server IP:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        serverIpField = createStyledTextField("127.0.0.1");
        devicePanel.add(serverIpField, gbc);
        
        // Server Port
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        devicePanel.add(new JLabel("Server Port:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        serverPortField = createStyledTextField("11706");
        devicePanel.add(serverPortField, gbc);
        
        // Location panel
        JPanel locationPanel = createStyledPanel("Location Data");
        locationPanel.setLayout(new GridBagLayout());
        
        // Speed
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        locationPanel.add(new JLabel("Speed (km/h):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        speedField = createStyledTextField("60");
        locationPanel.add(speedField, gbc);
        
        // Latitude
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        locationPanel.add(new JLabel("Latitude:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        latitudeField = createStyledTextField("14.654918");
        locationPanel.add(latitudeField, gbc);
        
        // Longitude
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        locationPanel.add(new JLabel("Longitude:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        longitudeField = createStyledTextField("-90.48515");
        locationPanel.add(longitudeField, gbc);
        
        // Altitude
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        locationPanel.add(new JLabel("Altitude (m):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        altitudeField = createStyledTextField("30");
        locationPanel.add(altitudeField, gbc);
        
        // Angle
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        locationPanel.add(new JLabel("Angle (degrees):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        angleField = createStyledTextField("20");
        locationPanel.add(angleField, gbc);
        
        // DateTime
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        locationPanel.add(new JLabel("Date/Time:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        dateTimeField = createStyledTextField(getCurrentTimeDbFormatStr());
        locationPanel.add(dateTimeField, gbc);
        
        // Update time button
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        updateTimeButton = createStyledButton("Update", PRIMARY_COLOR);
        locationPanel.add(updateTimeButton, gbc);
        
        // Auto DateTime checkbox
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 3;
        autoDateTimeCheck = new JCheckBox("Use current time automatically");
        autoDateTimeCheck.setOpaque(false);
        locationPanel.add(autoDateTimeCheck, gbc);
        
        // Parameters panel
        JPanel paramsPanel = createStyledPanel("Parameters (JSON)");
        paramsPanel.setLayout(new BorderLayout());
        
        paramsArea = new JTextArea(4, 20);
        paramsArea.setText("{\n\"io1\":\"1\"\n}");
        paramsArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        paramsArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        JScrollPane paramsScroll = new JScrollPane(paramsArea);
        paramsPanel.add(paramsScroll, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = createStyledPanel("Send Controls");
        controlPanel.setLayout(new FlowLayout());
        
        sendButton = createStyledButton("Send Data", SUCCESS_COLOR);
        controlPanel.add(sendButton);
        
        autoSendButton = createStyledButton("Auto Send", WARNING_COLOR);
        controlPanel.add(autoSendButton);
        
        controlPanel.add(new JLabel("Interval (sec):"));
        intervalField = createStyledTextField("5");
        intervalField.setPreferredSize(new Dimension(60, 25));
        controlPanel.add(intervalField);
        
        // Layout main panel
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(devicePanel, BorderLayout.NORTH);
        leftPanel.add(locationPanel, BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(paramsPanel, BorderLayout.CENTER);
        rightPanel.add(controlPanel, BorderLayout.SOUTH);
        
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createTripSimulationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel tripPanel = createStyledPanel("Trip Simulation");
        tripPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Trip selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        tripPanel.add(new JLabel("Select Trip:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        tripsCombo = new JComboBox<>(new String[]{"Trip 1", "Trip 2"});
        tripsCombo.setPreferredSize(new Dimension(150, 25));
        tripPanel.add(tripsCombo, gbc);
        
        // Interval
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        tripPanel.add(new JLabel("Update Interval (sec):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField tripIntervalField = createStyledTextField("5");
        tripPanel.add(tripIntervalField, gbc);
        
        // Control buttons
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel tripControlPanel = new JPanel(new FlowLayout());
        tripControlPanel.setOpaque(false);
        
        tripButton = createStyledButton("Start Trip", SUCCESS_COLOR);
        tripControlPanel.add(tripButton);
        
        tripPanel.add(tripControlPanel, gbc);
        
        panel.add(tripPanel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createAdvancedPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel hexPanel = createStyledPanel("Raw Hex Data");
        hexPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Hex mode checkbox
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        hexModeCheck = new JCheckBox("Send raw hex data");
        hexModeCheck.setOpaque(false);
        hexPanel.add(hexModeCheck, gbc);
        
        // Hex data field
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        hexPanel.add(new JLabel("Hex Data:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        hexDataField = createStyledTextField("");
        hexPanel.add(hexDataField, gbc);
        
        panel.add(hexPanel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = createStyledPanel("Activity Log");
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(0, 200));
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        logArea.setBackground(new Color(248, 248, 248));
        logArea.setText("Ready to send data...\n");
        
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(logScroll, BorderLayout.CENTER);
        
        // Log control panel
        JPanel logControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logControlPanel.setOpaque(false);
        clearLogButton = createStyledButton("Clear Log", DANGER_COLOR);
        logControlPanel.add(clearLogButton);
        
        panel.add(logControlPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            PRIMARY_COLOR
        ));
        return panel;
    }
    
    private JTextField createStyledTextField(String defaultValue) {
        JTextField field = new JTextField(defaultValue);
        field.setPreferredSize(new Dimension(150, 25));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        return field;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setPreferredSize(new Dimension(100, 30));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        return button;
    }
    
    private void setupEventHandlers() {
        // Send button
        sendButton.addActionListener(e -> sendData(null));
        
        // Auto send button
        autoSendButton.addActionListener(e -> toggleAutoSend());
        
        // Trip button
        tripButton.addActionListener(e -> toggleTrip());
        
        // Update time button
        updateTimeButton.addActionListener(e -> 
            dateTimeField.setText(getCurrentTimeDbFormatStr()));
        
        // Clear log button
        clearLogButton.addActionListener(e -> logArea.setText(""));
        
        // Auto datetime checkbox
        autoDateTimeCheck.addActionListener(e -> 
            dateTimeField.setEditable(!autoDateTimeCheck.isSelected()));
    }
    
    private void sendData(TripData tripData) {
        logArea.append(String.format("[%s] Sending data to %s:%s\n", 
            getCurrentTimeDbFormatStr(), 
            serverIpField.getText(), 
            serverPortField.getText()));
        
        if (hexModeCheck.isSelected() && !hexDataField.getText().isEmpty()) {
            logArea.append("Mode: Raw Hex Data\n");
            logArea.append("Data: " + hexDataField.getText() + "\n");
            SimulatorSender.SendPackage(logArea, serverIpField.getText(), 
                Integer.valueOf(serverPortField.getText()), hexDataField.getText());
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
            
            logArea.append("Mode: JSON Location Data\n");
            logArea.append("Data: " + data.toString() + "\n");
            SimulatorSender.SendPackage(logArea, serverIpField.getText(), 
                Integer.valueOf(serverPortField.getText()), data);
        }
        
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    
    private void toggleAutoSend() {
        if (!isAutoSending) {
            isAutoSending = true;
            autoSendButton.setText("Stop Auto");
            autoSendButton.setBackground(DANGER_COLOR);
            sendButton.setEnabled(false);
            
            int interval = Integer.valueOf(intervalField.getText());
            autoSendThread = new Thread(() -> {
                while (isAutoSending) {
                    SwingUtilities.invokeLater(() -> sendData(null));
                    try {
                        TimeUnit.SECONDS.sleep(interval);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });
            autoSendThread.start();
        } else {
            stopAutoSend();
        }
    }
    
    private void stopAutoSend() {
        isAutoSending = false;
        autoSendButton.setText("Auto Send");
        autoSendButton.setBackground(WARNING_COLOR);
        sendButton.setEnabled(true);
        
        if (autoSendThread != null) {
            autoSendThread.interrupt();
        }
        
        logArea.append("[" + getCurrentTimeDbFormatStr() + "] Auto send stopped\n");
    }
    
    private void toggleTrip() {
        if (!isTripRunning) {
            try {
                isTripRunning = true;
                tripButton.setText("Stop Trip");
                tripButton.setBackground(DANGER_COLOR);
                startTrip();
            } catch (IOException e) {
                logArea.append("Error starting trip: " + e.getMessage() + "\n");
                isTripRunning = false;
                tripButton.setText("Start Trip");
                tripButton.setBackground(SUCCESS_COLOR);
            }
        } else {
            stopTrip();
        }
    }
    
    private void startTrip() throws IOException {
        List<TripData> tripData = loadTrip(tripsCombo.getSelectedIndex());
        int interval = Integer.valueOf(intervalField.getText());
        
        tripThread = new Thread(() -> {
            int count = 0;
            while (isTripRunning && !Thread.currentThread().isInterrupted()) {
                if (count >= tripData.size()) {
                    count = 0;
                }
                
                TripData data = tripData.get(count);
                SwingUtilities.invokeLater(() -> sendData(data));
                
                count++;
                try {
                    TimeUnit.SECONDS.sleep(interval);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        tripThread.start();
        
        logArea.append("[" + getCurrentTimeDbFormatStr() + "] Trip simulation started\n");
    }
    
    private void stopTrip() {
        isTripRunning = false;
        tripButton.setText("Start Trip");
        tripButton.setBackground(SUCCESS_COLOR);
        
        if (tripThread != null) {
            tripThread.interrupt();
        }
        
        logArea.append("[" + getCurrentTimeDbFormatStr() + "] Trip simulation stopped\n");
    }
    
    private List<TripData> loadTrip(int selectedIndex) throws IOException {
        List<TripData> tripData = new ArrayList<>();
        ClassLoader classLoader = ModernMainUI.class.getClassLoader();
        
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
}