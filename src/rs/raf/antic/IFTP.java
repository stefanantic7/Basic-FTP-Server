package rs.raf.antic;

import java.io.IOException;

public interface IFTP {
	
	public void welcome();
	
	public void user();
	
	public boolean pass(boolean validPassword);

	public void syst();
	
	public void feat();

	public void pwd();
	
	public void type();
	
	public void pasv();
	
	public void retr(String fileName) throws IOException;
	
	public void dele(String fileName);
	
	public void list() throws IOException;
	
	public void stor(String fileName) throws IOException;
	
	public void notImplemented();
	
	public void quit();
}
