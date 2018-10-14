package rs.raf.antic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class FTP implements IFTP{
	
	private Socket controlSocket;
	
	private ServerSocket dataServerSocket;
	
	private PrintWriter controlSocketOut;
	
	private ArrayList<File> files;
	
	public FTP(Socket controlSocket, ServerSocket dataServerSocket, ArrayList<File> files) throws IOException {
		this.controlSocket = controlSocket;
		this.controlSocketOut =  new PrintWriter(new PrintWriter(controlSocket.getOutputStream()), true);
		this.dataServerSocket = dataServerSocket;
		this.files = files;
	}
	
	public boolean closeControlSocket() {
		try {
			this.controlSocket.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	@Override
	public void welcome() {
		controlSocketOut.println("332 Need account for login.");
	}
	
	@Override
	public void user() {
		controlSocketOut.println("331 User name okay, need password.");
	}
	
	@Override
	public boolean pass(boolean validPassword) {
		if(validPassword) {
			controlSocketOut.println("230 User logged in, proceed.");
			return true;
		}
		else {
			controlSocketOut.println("530 Invalid credentials");
			closeControlSocket();
			return false;
		}
	}
	
	@Override
	public void syst() {
		controlSocketOut.println("UNIX Type: L8");
	}

	@Override
	public void feat() {
		controlSocketOut.println("LIST\nRETR\nSTOR\nDELE\nQUIT\nPASV\nTYPE\nSYST\nFEAT\nPWD");
	}

	@Override
	public void pwd() {
		controlSocketOut.println("257 \"/Files\" ");
	}

	@Override
	public void type() {
		controlSocketOut.println("215 A");
	}

	@Override
	public void pasv() {
		controlSocketOut.println("227 Entering Passive Mode (127,0,0,1,0,20)");		
	}

	@Override
	public void retr(String fileName) throws IOException {
		Socket dataSocket = dataServerSocket.accept();
		
		PrintWriter dataSocketOut = new PrintWriter(new PrintWriter(dataSocket.getOutputStream()), true);

		for(File f : files) {
			if(f.getName().equals(fileName)) {
				FileReader fileReader = new FileReader("./Files/" + fileName);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				StringBuilder stringBuilder = new StringBuilder();
				String line;
				while((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line).append("\n");
				}
				stringBuilder.deleteCharAt(stringBuilder.length()-1);
				controlSocketOut.println("150 File status okay; about to open data connection.");

				dataSocketOut.println(stringBuilder.toString());
				
				bufferedReader.close();
				fileReader.close();
				
				controlSocketOut.println("250 Requested file action okay, completed");
				break;
			}
		}
		
		dataSocket.close();
	}

	@Override
	public void dele(String fileName) {
		for (File file : files) {
			if(file.getName().equals(fileName)) {
				files.remove(file);
				file.delete();
				controlSocketOut.println("250 Requested file action okay, completed");
				break;
			}
		}
	}

	@Override
	public void list() throws IOException {
		Socket dataSocket = dataServerSocket.accept();
		PrintWriter dataSocketOut = new PrintWriter(new PrintWriter(dataSocket.getOutputStream()), true);
		

		StringBuilder stringBuilder = new StringBuilder();
		for(File f : files) {
			stringBuilder.append(f.getName()).append("\n") ;
		}
		dataSocketOut.println(stringBuilder.toString());
		controlSocketOut.println("200 Completed");
		
		dataSocket.close();
	}

	@Override
	public void stor(String fileName) throws IOException {
		Socket dataSocket = dataServerSocket.accept();
		BufferedReader dataSocketIn = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));

		String line;

		String filePath = "./Files/"+fileName;
		PrintWriter printWriter = new PrintWriter(filePath, "UTF-8");
		StringBuilder stringBuilder = new StringBuilder();
		while((line = dataSocketIn.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append("\n");
		}
		stringBuilder.deleteCharAt(stringBuilder.length()-1);
		printWriter.append(stringBuilder.toString());
		printWriter.close();

		
		controlSocketOut.println("226 Closing data connection.");
		dataSocket.close();
		files.add(new File(filePath));
	}

	@Override
	public void notImplemented() {
		controlSocketOut.println("202 Not implemented");
	}
	
	@Override
	public void quit() {
		controlSocketOut.println("221ClosingControlConnection");
		this.closeControlSocket();
	}

}
