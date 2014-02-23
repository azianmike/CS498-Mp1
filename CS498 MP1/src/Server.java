import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
public class Server {
	public static void main(String args[]) throws IOException
	{
		ArrayList<PrintWriter> outputs = new ArrayList<PrintWriter>();
		ServerSocket server = null;
		int id = 0;
		int port = 8732;
		try
		{
			server = new ServerSocket(port,10);
			System.out.println("Setup Server on port: " + port);
			while(true)
			{
				Socket client = server.accept();
				new Handler(client, outputs, id).start();
				id++; 
			}
		}
		catch(Exception errno)
		{
			System.out.println("Cannot setup Server on Port: " + port);
		}
		server.close();
	}
}
