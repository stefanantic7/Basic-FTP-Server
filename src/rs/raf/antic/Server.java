package rs.raf.antic;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {

	private ArrayList<File> files;
	private HashMap<String, String> users;
	private ServerSocket dataServerSocekt;
	
	public Server() throws IOException {
		loadFiles();
		loadUsers();
		
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(21);
		this.dataServerSocekt = new ServerSocket(20);
		
		System.out.println("Ports 20 and 21 are opened");
		
		while(true) {
			Socket controllSocket = serverSocket.accept();
			
			IFTP ftp = new FTP(controllSocket, dataServerSocekt, files);
			
			ServerThread serverThread = new ServerThread(ftp, controllSocket, this);
			Thread thread = new Thread(serverThread);
			thread.start();
		}
		
	}
	
	public void loadFiles() {
		this.files = new ArrayList<>();
		
		File folder = new File("./Files");
		
		for (File file : folder.listFiles()) {
			this.files.add(file);
		}
	}
	
	private void loadUsers() {
		this.users = new HashMap<>();
		this.users.put("admin", "admin");
	}
	
	public HashMap<String, String> getUsers() {
		return users;
	}
	
	public static void main(String[] args) {
		try {
			new Server();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
