package rs.raf.antic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread implements Runnable {

	private Socket controlSocket;
	private Server server;
	private IFTP ftp;
	public ServerThread(IFTP ftp, Socket controlSocket, Server server) {
		this.ftp = ftp;
		this.controlSocket = controlSocket;
		this.server = server;
	}
	
	public boolean login(String username, String password) {
		if(!password.equals(server.getUsers().get(username))) {
			return false;
		}
		return true;
	}
	@Override
	public void run() {
		try {
			BufferedReader controlSocketIn = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
			
			ftp.welcome();
			
			String username = controlSocketIn.readLine().substring(5);
			ftp.user();
			
			String password = controlSocketIn.readLine().substring(5);
			
			if(!ftp.pass(login(username, password))) {
				return;
			}
						
			String command = controlSocketIn.readLine();
			
			while(command != null && !command.equals("QUIT")) {
				
				System.out.println("User: "+username+", command: "+command);
				
				if(command.contains("SYST")) {
					ftp.syst();
				}
				else if(command.contains("FEAT")) {
					ftp.feat();
				}
				else if(command.contains("PWD")) {
					ftp.pwd();
				}
				else if(command.contains("TYPE")) {
					ftp.type();
				}
				else if(command.contains("PASV")) {
					ftp.pasv();
				}
				else if(command.contains("RETR")) {
					String fileName = command.substring(5);
					ftp.retr(fileName);
				}
				else if(command.contains("DELE")) {
					String fileName = command.substring(5);
					ftp.dele(fileName);
				}
				else if(command.contains("LIST")) {
					ftp.list();
				}
				else if(command.contains("STOR")) {
					String fileName = command.substring(5);
					ftp.stor(fileName);
				}
				else {
					ftp.notImplemented();
				}
				
				command = controlSocketIn.readLine();
			}

			ftp.quit();
			
			System.out.println("Close connection");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
