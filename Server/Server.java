import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread
{
	private UserInput UInput;
	private int Port = 5000, UniqueID;
	private boolean Sleep, Close;
	private ServerSocket SSocket;
	public ConnectionList ConList;
	public Game CurrentGame;
	
	public static void main(String args[])
	{
		Server Serve = new Server();
	}
	
	public static String message(int Color, String Message)
	{
		int[] Colors = new int[6];
		Colors[0] = -12566464; // Server Message
		Colors[1] = -16777216; // Normal Message
		Colors[2] = -65281; // Private Message
		Colors[3] = 441600; // Game Message
		Colors[4] = 28671; // Personal Game Message
		Colors[5] = 28671; // Console Message
		
		return "MESSAGE " + Colors[Color] + " " + Message; 
	}
	
	public Server()
	{
		display("Willkommen im MauMau-Server!");
		display("Gib \"hilfe\" ein, um alle Befehle aufzulisten.");
		display("Mit \"start\" startest du den Server.");
		
		ConList = new ConnectionList(this);
		
		UInput = new UserInput(this);
		UInput.start();
		
		Sleep = true;
		start();
	}
	
	public void setPort(int Port)
	{
		if(SSocket == null)
		{
			this.Port = Port;
		}
		else
		{
			display("Fehler. Der Server ist bereits aktiv.");
		}
	}
	
	public void startListening()
	{
		if(SSocket == null)
		{
			try
			{
				UniqueID = 0;
			
				SSocket = new ServerSocket(Port);
				Sleep = false;
			}
			catch(Exception e)
			{
				displayException(e);
			}
		}
		else
		{
			display("Fehler. Der Server ist bereits aktiv.");
		}
	}
	
	public void stopListening()
	{
		if(SSocket != null)
		{
			try
			{
				display("Deaktiviere Server...");
				Sleep = true;
				ConList.broadcast("SERVERCLOSED");
				while(ConList.countConnections() > 1) { }
				Socket Kill = new Socket("localhost", 5000);
				SSocket.close();
				SSocket = null;
				display("Der Server ist nun deaktiviert.");
			}
			catch(Exception e)
			{
				displayException(e);
			}
		}
		else
		{
			display("Fehler. Der Server ist bereits inaktiv.");
		}
	}
	
	public void close()
	{
		if(SSocket != null)
		{
			Close = true;
			stopListening();
		}
		UInput.close();
		display("Beenden...");
		System.exit(0);
	}
	
	public void run()
	{
		setName("Server_Listen");
		try
		{
			while(true)
			{
				while(Sleep) { sleep(100); }
				display("Warte auf Clients auf Port " + Port + "...");
				
				Connection Con = new Connection(this, ++UniqueID);
				
				Socket NewSocket = SSocket.accept();
				
				if(Close)
				{
					interrupt();
				}
				else if(Sleep)
				{
					continue;
				}
				
				if(Con.createConnection(NewSocket))
				{
					display_(Con.getUsername() + " versucht zu verbinden...");
				
					if(ConList.getConnection(Con.getUsername()) != null)
					{
						_display("der Name ist bereits vergeben.");
						Con.sendMessage("NAMEEXISTS");
					}
					else
					{
						ConList.broadcast(Server.message(0, Con.getUsername() + " hat sich verbunden."));
						_display("erfolgreich.");
						Con.sendMessage("NAMEOK");
						Con.start();
						ConList.addConnection(Con);
					}
				}
			}
		}
		catch(Exception e)
		{
			displayException(e);
			stopListening();
		}
	}
	
	public synchronized void requestReadMessage(Connection Con, String Message)
	{
		Con.readMessage(Message);
	}
	
	public void removeConnection(Connection Con)
	{
		Con.close();
		if(CurrentGame != null)
		{
			CurrentGame.removeGamer(Con);
		}
		display(Con.getUsername() + " wurde entfernt.");
		ConList.removeConnection(Con);
		ConList.broadcast(Server.message(0, Con.getUsername() + " wurde entfernt."));
	}
	
	public void disconnectConnection(Connection Con, String Message)
	{
		Con.close();
		if(CurrentGame != null)
		{
			CurrentGame.removeGamer(Con);
		}
		display(Message);
		ConList.removeConnection(Con);
		ConList.broadcast(Server.message(0, Message));
	}
	
	public void startNewGame()
	{
		if(CurrentGame == null)
		{
			if(ConList.countConnections() > 1)
			{
				CurrentGame = new Game(this);
				CurrentGame.startGame();
			}
			else
			{
				display("Fehler. Es werden mindestens 2 Mitspieler gebraucht.");
			}
		}
		else
		{
			display("Fehler. Es ist bereits ein Spiel aktiv.");
		}
	}
	
	public void closeGame()
	{
		if(CurrentGame != null)
		{
			CurrentGame = null;
			ConList.broadcast("RESETGAME");
		}
	}
	
	public void display_(String Message)
	{
		System.out.print(Message);
	}
	
	public void _display(String Message)
	{
		System.out.println(Message);
	}
	
	public void display(String Message)
	{
		System.out.println(Message);
	}
	
	public static void displayException(Exception e)
	{
		displayException(e, true);
	}
	
	public void displayException(Exception e, boolean logFile)
	{		
		StackTraceElement[] Log = e.getStackTrace();
		StackTraceElement[] Console = Log;
		
		if(logFile)
		{
			try
			{			
				File ErrorFile = new File("errorlog.txt");
				FileOutputStream ErrorStream;
				String Line;
				
				if(ErrorFile.exists())
				{
					ErrorStream = new FileOutputStream(ErrorFile, true);
					Line = String.format("%n");
					ErrorStream.write(Line.getBytes());
				}
				else
				{
					ErrorFile.createNewFile();
					ErrorStream = new FileOutputStream(ErrorFile);
				}
				if(ErrorStream != null)
				{
					Line = "Exception: " + e.toString() + String.format("%n");
					ErrorStream.write(Line.getBytes());
					for(int i = 0; i < Log.length; i++)
					{
						Line = "\t at " + Log[i].getFileName() + " LINE " + Log[i].getLineNumber() + " METHOD " + Log[i].getMethodName() + String.format("%n");
						ErrorStream.write(Line.getBytes());
					}
				}
			}
			catch(Exception e2)
			{
				displayException(e2, false);
			}
		}
			
		System.out.println("Exception: " + e.toString());
		for(int i = 0; i < Log.length; i++)
		{
			System.out.println("\t at " + Log[i].getFileName() + " LINE " + Log[i].getLineNumber() + " METHOD " + Log[i].getMethodName());
		}
	}
}