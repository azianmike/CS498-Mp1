import java.net.*;
import java.util.ArrayList;
import java.io.*;
public class Handler extends Thread {
	private int id;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
    private int numChars;
    private ArrayList<PrintWriter> outputs;
    
    public Handler(Socket socket, ArrayList<PrintWriter> outputs, int id)
    {
    	this.socket = socket;
    	this.outputs = outputs;
    	this.id = id;
    }
    
    public void run()
    {
    	try
    	{
    		out = new PrintWriter(socket.getOutputStream(), true);
    		outputs.add(out);
	    	System.out.println(socket.getRemoteSocketAddress().toString() + " Number of outputs: " + outputs.size());
	    	in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    	
	    	// Send welcome message
	    	out.println("Welcome to Server " + InetAddress.getLocalHost());
	    	
	    	while(true)
	    	{
	    		String input = in.readLine();
	    		if(input.equalsIgnoreCase("quit"))
	    		{
	    			System.out.println("QUIT");
	    			quit();
	    			break;
	    		}
	    		else
	    		{
	    			numChars += input.length();
	    			String send = id + ": " + input;
	    			System.out.println("Mass Message - " + send);
	    			for(int i=0;i<outputs.size();i++)
	    			{
	    				outputs.get(i).println(send);
	    			}
	    		}
	    	}
    	}
    	catch(Exception errno)
    	{
    		
    	}
    }
    
    private void quit()
    {
    	String send = String.format("\\r\\n\\r\\nYou quit, you are being charged $%.2f", (.1)*numChars);
    	out.println(send);
    	try 
    	{
			socket.close();
		} 
    	catch (IOException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	// Remove self from outputs
    	outputs.remove(out);
    }
}
