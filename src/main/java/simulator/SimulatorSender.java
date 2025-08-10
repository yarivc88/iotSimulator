package simulator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JTextArea;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class SimulatorSender {

	
	public static void SendPackage(JTextArea logArea, String ip, int port, simulator.DeviceWebLocationData location) {
		
		try {
		Socket clientSocket = new Socket(ip, port);
			clientSocket.setSoTimeout(10000);
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());
		

		ObjectMapper mapper = new ObjectMapper();
		String str =mapper.writeValueAsString(location);
		outToServer.write(str.getBytes());
		logArea.append("\n");
		logArea.append("respondes from server: " + inFromServer.readByte() + "\n");
		closeConnections(logArea, clientSocket,outToServer,inFromServer);
	
		
	}catch (Exception e) {
		logArea.append("\n");
		logArea.append("fails to  send message " +e);
		logArea.append("\n");
		e.printStackTrace();
	}
	
}
	
	
	public static void SendPackage(JTextArea logArea, String ip, Integer port, String hex) {
		try {
			Socket clientSocket = new Socket(ip, port);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());
			
			
			
			ObjectMapper mapper = new ObjectMapper();
			String str =mapper.writeValueAsString(binary(hex));
		//	outToServer.writeBytes(binary(hex));
			logArea.append("\n");
	//		logArea.append("respondes from server: " + inFromServer.readByte() + "\n");
			closeConnections(logArea, clientSocket,outToServer,inFromServer);
		
			
		}catch (Exception e) {
			logArea.append("\n");
			logArea.append("fails to  send message " +e);
			logArea.append("\n");
			e.printStackTrace();
		}
		
	}

	private static void closeConnections(JTextArea logArea,Socket clientSocket, DataOutputStream outToServer,
			DataInputStream inFromServer) {
			try {
				clientSocket.close();
				outToServer.close();
				inFromServer.close();
			} catch (IOException e) {
				logArea.append("\n");
				logArea.append("fails to close socket connection");
				logArea.append("\n");
				e.printStackTrace();
			}
		
		
	}

	protected static ByteBuf binary(String... data) {
		return Unpooled.wrappedBuffer(DataConverter.parseHex(concatenateStrings(data)));
	}

	private static String concatenateStrings(String... strings) {
		StringBuilder builder = new StringBuilder();
		for (String s : strings) {
			builder.append(s);
		}
		return builder.toString();
	}
}
