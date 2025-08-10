package simulator;

import java.awt.EventQueue;

import javax.swing.*;
import java.awt.Dimension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;



import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;

import java.awt.Font;

public class MainUI {

	private JFrame frame;
	private JTextField imei;
	private JTextField serverIp;
	private JTextField serverPort;
	private JTextField speed;
	private JTextField latitude;
	private JTextField longitude;
	private JTextArea logArea;
	private JTextField altitude;
	private JLabel AltitudeLabel;
	private JTextField angle;
	private JLabel angelLabel;
	private JTextField dtTracker;
	private JLabel dtTrackerLabel;
	private JTextPane params;
	private JScrollPane scrolllog;
	private JButton updateTime;
	private JCheckBox autoDateTime;
	private JTextField autoInterval;
	private JButton sendButton;
	private JToggleButton autoSend;
	private JTextField hexText;
	private JCheckBox isWithHex;

	private JComboBox trips;
	private JToggleButton startTrip;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainUI window = new MainUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(true);
		frame.addComponentListener(new ComponentAdapter() {
	            public void componentResized(ComponentEvent e) {
	            	Dimension d = new Dimension (e.getComponent().getSize());
	            	d.setSize(d.getWidth() -40, d.getHeight() -380);
	           	scrolllog.setSize(d);
	          
	            }
	        });
		frame.getContentPane().setLayout(null);
		frame.setSize(827, 601);
		imei = new JTextField();
		imei.setText("24343534");
		imei.setBounds(126, 29, 132, 20);
		frame.getContentPane().add(imei);
		imei.setColumns(10);
		
		 logArea = new JTextArea();
		 logArea.setEditable(false);
		logArea.setText("log...");
		logArea.setBounds(0, 322, 706, 192);
		scrolllog = new JScrollPane(logArea);
		scrolllog.setSize(791, 231);
		scrolllog.setLocation(10, 320);
		scrolllog.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		frame.getContentPane().add(scrolllog);
		
		 sendButton = new JButton("send");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doSendButton(null);
				
			}
		});
		sendButton.setBounds(10, 288, 89, 23);
		frame.getContentPane().add(sendButton);
		
		JLabel lblNewLabel = new JLabel("imei");
		lblNewLabel.setBounds(10, 32, 46, 14);
		frame.getContentPane().add(lblNewLabel);
		
		serverIp = new JTextField();
		serverIp.setText("127.0.0.1");
		serverIp.setBounds(620, 29, 86, 20);
		frame.getContentPane().add(serverIp);
		serverIp.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("server ip:");
		lblNewLabel_1.setBounds(525, 32, 76, 14);
		frame.getContentPane().add(lblNewLabel_1);
		
		serverPort = new JTextField();
		serverPort.setText("11706");
		serverPort.setColumns(10);
		serverPort.setBounds(620, 60, 86, 20);
		frame.getContentPane().add(serverPort);
		
		JLabel lblNewLabel_1_1 = new JLabel("server port:");
		lblNewLabel_1_1.setBounds(525, 62, 85, 17);
		frame.getContentPane().add(lblNewLabel_1_1);
		
		speed = new JTextField();
		speed.setText("60");
		speed.setColumns(10);
		speed.setBounds(126, 60, 132, 20);
		frame.getContentPane().add(speed);
		
		JLabel lblNewLabel_2 = new JLabel("speed");
		lblNewLabel_2.setBounds(10, 63, 57, 14);
		frame.getContentPane().add(lblNewLabel_2);
		
		latitude = new JTextField();
		latitude.setText("14.654918");
		latitude.setBounds(126, 91, 132, 20);
		frame.getContentPane().add(latitude);
		latitude.setColumns(10);
		
		JLabel latitudeLabel = new JLabel("latitude");
		latitudeLabel.setBounds(10, 94, 53, 14);
		frame.getContentPane().add(latitudeLabel);
		
		longitude = new JTextField();
		longitude.setText("-90.48515");
		longitude.setBounds(126, 122, 132, 20);
		frame.getContentPane().add(longitude);
		longitude.setColumns(10);
		
		JLabel longitudeLabel = new JLabel("longitude");
		longitudeLabel.setBounds(10, 125, 57, 14);
		frame.getContentPane().add(longitudeLabel);
		
		altitude = new JTextField();
		altitude.setText("30");
		altitude.setColumns(10);
		altitude.setBounds(126, 153, 132, 20);
		frame.getContentPane().add(altitude);
		
		AltitudeLabel = new JLabel("altitude");
		AltitudeLabel.setBounds(10, 156, 57, 14);
		frame.getContentPane().add(AltitudeLabel);
		
		angle = new JTextField();
		angle.setText("20");
		angle.setColumns(10);
		angle.setBounds(126, 184, 132, 20);
		frame.getContentPane().add(angle);
		
		angelLabel = new JLabel("angel");
		angelLabel.setBounds(10, 187, 57, 14);
		frame.getContentPane().add(angelLabel);
		
		dtTracker = new JTextField();
		dtTracker.setBounds(126, 215, 132, 20);
		dtTracker.setText(getCurrentTimeDbFormatStr());
		frame.getContentPane().add(dtTracker);
		dtTracker.setColumns(10);
		
		dtTrackerLabel = new JLabel("tracker dateTime");
		dtTrackerLabel.setBounds(10, 218, 106, 14);
		frame.getContentPane().add(dtTrackerLabel);
		
		params = new JTextPane();
		params.setEditable(true);
		params.setText("{\r\n\"io1\":\"1\"\r\n}");
		params.setBounds(526, 122, 226, 189);
		frame.getContentPane().add(params);
		
		JLabel paramsLabel = new JLabel("params (json):");
		paramsLabel.setBounds(525, 94, 132, 14);
		frame.getContentPane().add(paramsLabel);
		
		updateTime = new JButton("update time");
		updateTime.setFont(new Font("Tahoma", Font.PLAIN, 10));
		updateTime.setHorizontalAlignment(SwingConstants.LEFT);
		updateTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dtTracker.setText(getCurrentTimeDbFormatStr());
			}
		});
		updateTime.setBounds(268, 214, 89, 23);
		frame.getContentPane().add(updateTime);
		
		 autoDateTime = new JCheckBox("send with current dateTime (utc)");
		
		autoDateTime.setBounds(6, 239, 252, 23);
		
		autoDateTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			      AbstractButton abstractButton = (AbstractButton) e.getSource();
			        boolean selected = abstractButton.getModel().isSelected();
			        dtTracker.setEditable(!selected);
			      
			};
		});
		frame.getContentPane().add(autoDateTime);
		
		 autoSend = new JToggleButton("auto send");
		autoSend.setBounds(109, 288, 99, 23);
		autoSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				  AbstractButton abstractButton = (AbstractButton) e.getSource();
				   boolean selected = abstractButton.getModel().isSelected();
				 if(selected) {
					 sendButton.setEnabled(false);
					 startAutoSend();
				 }else {
					 stopAutoSend();
					 sendButton.setEnabled(true);
				 }
			}

		
		});
			
		frame.getContentPane().add(autoSend);

		trips=new JComboBox<>(new String[] {"Trip 1", "Trip 2"});
		trips.setBounds(220, 285, 66, 25);
		frame.getContentPane().add(trips);

		startTrip = new JToggleButton("start trip");
		startTrip.setBounds(290, 285, 99, 25);
		startTrip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractButton abstractButton = (AbstractButton) e.getSource();
				boolean selected = abstractButton.getModel().isSelected();
				if(selected) {

					try {
						doStartTrip();
					} catch (IOException ex) {
						throw new RuntimeException(ex);
					}

				}else {
					stopStartTrip();

				}
			}

		});
		frame.getContentPane().add(startTrip);




		isWithHex = new JCheckBox("send hex");
		isWithHex.setBounds(6, 260, 86, 23);
		frame.getContentPane().add(isWithHex);

		hexText = new JTextField();
		hexText.setColumns(10);
		hexText.setBounds(94, 265, 200, 20);
		frame.getContentPane().add(hexText);

		JLabel lblNewLabel_3 = new JLabel("interval:");
		lblNewLabel_3.setBounds(300, 265, 46, 14);
		frame.getContentPane().add(lblNewLabel_3);

		autoInterval = new JTextField();
		autoInterval.setBounds(357, 265, 86, 20);
		frame.getContentPane().add(autoInterval);
		autoInterval.setColumns(10);

	
	}

	private void doStartTrip() throws IOException {
		List<TripData> tripData = loadTrip(trips.getSelectedIndex());
		final int[] count = {0};
		
		if(autoInterval.getText().isEmpty()) {
			autoInterval.setText("5");
		}
		int interval = Integer.valueOf(autoInterval.getText());
		new Thread(
				new Runnable() {
					public void run() {

						while(startTrip.isSelected()) {
							if(count[0] >=tripData.size()){
								count[0] =0;
							}
							TripData data=	tripData.get(count[0]);
							doSendButton(data);
							try {
								count[0]++;
								TimeUnit.SECONDS.sleep(interval);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}).start();
	}



	private List<TripData> loadTrip(int selectedIndex) throws IOException {
		List<TripData> tripData = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();
		StringBuilder jsonContent = new StringBuilder();
		ClassLoader classLoader = MainUI.class.getClassLoader();

		Reader reader = new InputStreamReader(Objects.requireNonNull(classLoader.getResourceAsStream("trip1.json")));
		Gson gson = new Gson();

		// Parse the JSON content as a JsonObject
		JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

		JsonArray routes= jsonObject.getAsJsonArray("route");
		routes.forEach(e->{
			JsonArray routPoint=	e.getAsJsonArray();
			tripData.add(new TripData(routPoint.get(1).getAsString(),routPoint.get(2).getAsString()))	;
		});

	return tripData;
	}


	private void stopStartTrip() {
		logArea.append("\n");
		logArea.append("trip  stopped");
	}

	protected void doSendButton(TripData tripData) {
		logArea.append("\n");
		logArea.append("send data to server ip:"+serverIp.getText() +" port:" +serverPort.getText() + " \n");

		if(isWithHex.isSelected()) {
			logArea.append("data: " + hexText.getText());
			logArea.setCaretPosition(logArea.getDocument().getLength());
			SimulatorSender.SendPackage(logArea,serverIp.getText(), Integer.valueOf(serverPort.getText()), hexText.getText());
			return;
		}
		
		DeviceWebLocationData data = new DeviceWebLocationData();
		data.setImei(imei.getText());
		data.setSpeed(Double.valueOf(speed.getText()));
		data.setLat(tripData==null? Double.valueOf(latitude.getText()):Double.valueOf(tripData.getLat()));
		data.setLng(tripData==null?Double.valueOf(longitude.getText()):Double.valueOf(tripData.getLng()));
		data.setAltitude(Integer.valueOf(altitude.getText()));
		data.setAngle(Integer.valueOf(angle.getText()));
		data.setDtDevice(getDtTracker());
		data.setParams(params.getText());
		logArea.append("data: " + data);
		logArea.setCaretPosition(logArea.getDocument().getLength());
		SimulatorSender.SendPackage(logArea,serverIp.getText(), Integer.valueOf(serverPort.getText()), data);
		
	}
	
	private void startAutoSend() {
	int interval = Integer.valueOf(autoInterval.getText());
	new Thread(
			  new Runnable() {

			      public void run() {
			
			    	  while(autoSend.isSelected()) {
			    		  doSendButton(null);
			    		  try {
							TimeUnit.SECONDS.sleep(interval);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    	  }
			      }
			}).start();
	}
	
	
	
	private void stopAutoSend() {
		logArea.append("\n");
		logArea.append("Auto send  stoped");
	
	}

	

	private String getDtTracker() {
		
		if(autoDateTime.isSelected()) {
			return getCurrentTimeDbFormatStr();
		}
		
		String dtTrackerStr = dtTracker.getText();
		if(dtTrackerStr.isEmpty()) {
			DateTime dateTime = getCurrentTimeUTC();
			DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		return 	dtf.print(dateTime);
		}
	
		return dtTrackerStr;
	}
	public static String getCurrentTimeDbFormatStr() {

		DateTime result = getCurrentTimeUTC();
		org.joda.time.format.DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		return dtf.print(result);

	}
	public static DateTime getCurrentTimeUTC() {

		return new DateTime(DateTimeZone.UTC);

	}
}
