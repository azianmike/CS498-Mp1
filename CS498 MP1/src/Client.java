import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;

import java.awt.BorderLayout;

import javax.swing.SwingConstants;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;


public class Client {

	private static JFrame frmChatClient;
	private static JTextArea textArea;
	private static JEditorPane editorPane_1;
	private static PrintWriter out;
	private static JLabel charCount;
	private static boolean hasQuit;
	private static BufferedReader is;
	private static Socket hostSocket;
	private static boolean firstPass = false;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					@SuppressWarnings("unused")
					Client window = new Client();
					Client.frmChatClient.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public Client() throws IOException {
		hostSocket = new Socket();
		if(!firstPass)
		{
			initialize();
			firstPass = true;
		}
		else
		{
			//message to reconnect to server
			textArea.append("\nServer went down... Reconnecting");
		}
		connectToServer();		
		DataOutputStream os = new DataOutputStream(hostSocket.getOutputStream());
		out = new PrintWriter(os);
		
		startReader();
	}

	/**
	 * Starts a seperate thread for the reader to read chats from server
	 */
	public void startReader() {
		Reader currReader = new Reader(is,textArea, this, charCount); 
		currReader.start();
	}
	
	/**
	 * Connects to the load balancer
	 */
	public void connectToServer()
	{
		hasQuit = false;
		if(hostSocket.isClosed())
		{
			hostSocket = new Socket();
		}
		try 
		{
			System.out.println("connecting...");
			String output;
			editorPane_1.setEditable(false);
			editorPane_1.setText("");
			do
			{
				hostSocket.connect(new InetSocketAddress("Team03LoadBalancer-63998421.us-east-1.elb.amazonaws.com",8732));
				DataInputStream in = null;
				in = new DataInputStream(hostSocket.getInputStream());
				is = new BufferedReader(new InputStreamReader(in));
				output = is.readLine();
			}while(output==null);
			textArea.append("\n"+output);
			editorPane_1.setEditable(true);

		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}		
	}
	

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmChatClient = new JFrame();
		frmChatClient.setTitle("Chat Client");
		frmChatClient.setSize(500, 500);
		frmChatClient.setResizable(false);
		frmChatClient.setBounds(100, 100, 450, 300);
		frmChatClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Set JFrame in the center of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frmChatClient.setLocation(dim.width/2-frmChatClient.getSize().width/2, dim.height/2-frmChatClient.getSize().height/2);
		
		JButton btnSend = new JButton("Send");
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				sendText();
			}
		});
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		JLabel lblCharacters = new JLabel("#:");
		
		charCount = new JLabel("0");
		GroupLayout groupLayout = new GroupLayout(frmChatClient.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lblCharacters)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(charCount))
								.addComponent(btnSend, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 192, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE)
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblCharacters)
								.addComponent(charCount))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnSend, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
					.addContainerGap())
		);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		DefaultCaret scroll = (DefaultCaret) textArea.getCaret();
		scroll.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollPane_1.setViewportView(textArea);
		
		editorPane_1 = new JEditorPane();
		editorPane_1.addKeyListener(new KeyListener(){

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					e.consume();
					sendText();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		scrollPane.setViewportView(editorPane_1);
		frmChatClient.getContentPane().setLayout(groupLayout);
	}
	
	/**
	 * Grabs text from GUI to send to server
	 */
	private void sendText()
	{
		String toSend = editorPane_1.getText();
		out.println(toSend);
		out.flush();
		editorPane_1.setText("");
		
		if(hasQuit == false && !toSend.equalsIgnoreCase("quit"))
		{
			// Add chars to the counter
			int count = Integer.parseInt(charCount.getText());
			count += toSend.length();
			charCount.setText(count + "");
		}
		else
		{
			hasQuit = true;
			editorPane_1.setEditable(false);
		}
	}
}

/**
 * Seperate class to read from server
 * @author Michael
 *
 */
class Reader extends Thread
{
	public BufferedReader is;
	public JTextArea commentArea;
	public boolean hasQuit;
	public Client currConn;
	private JLabel charCount;
	
    public Reader(BufferedReader is, JTextArea commentArea, Client currConn, JLabel charCount) {
		// TODO Auto-generated constructor stub
    	this.is = is;
    	this.commentArea = commentArea;
    	hasQuit = false;
    	this.currConn = currConn;
    	this.charCount = charCount;    	
   }

    /**
     * Runs seperate thread and continously reads
     */
   public void run ()
   {
	   while(true)
		{
			String output = null;
			try {
				output = is.readLine();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Checks to see if user has quit and if not connected to server anymore
			if(output == null && hasQuit)
			{
				break;
			}
			else
			{	
				//checks to see if server is down, reconnects if down
				if(output == null && !hasQuit)
				{
					
					try {
						currConn = new Client();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					break;
				}
				else
				{
					//check for exit message
					if(output.contains("\\r\\n\\r\\n"))
					{
						String outputString = output.substring(8,output.length()-4);
						int charNum = Integer.parseInt(charCount.getText());
						String send = String.format(outputString+"%.2f", (.1)*charNum);
						commentArea.append("\n" + send);
						
						hasQuit=true;
					}
					else
					{
						commentArea.append("\n" + output);
					}
				}
			}
			
		}
   }
}