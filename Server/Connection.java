import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class Connection extends Thread
{
	public Connection Before, After;
	private Server CurrentServer;
	private int ID;
	private boolean KeepListening;
	private String Username;
	private Socket CurrentSocket;
	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;
	
	public Connection(Server CurrentServer, int ID)
	{
		this.CurrentServer = CurrentServer;
		this.ID = ID;
	}
	
	public boolean createConnection(Socket CurrentSocket)
	{
		try
		{
			this.CurrentSocket = CurrentSocket;
			sInput = new ObjectInputStream(CurrentSocket.getInputStream());
			sOutput = new ObjectOutputStream(CurrentSocket.getOutputStream());
			
			Username = (String) sInput.readObject();
			return true;
		}
		catch(Exception e)
		{
			CurrentServer.displayException(e);
			return false;
		}
	}
	
	public void run()
	{
		setName("Server_Connection_" + ID + "_" + Username);
		KeepListening = true;
		
		while(KeepListening)
		{
			try
			{
				CurrentServer.requestReadMessage(this, (String) sInput.readObject());
			}
			catch(Exception e)
			{
				CurrentServer.displayException(e);
				CurrentServer.removeConnection(this);
			}
		}
	}
	
	public void readMessage(String Message)
	{
		Scanner CurrentLine = new Scanner(Message);
		
		if(CurrentLine.hasNext())
		{
			String Command = CurrentLine.next();
			if(Command.equals("LOGOUT"))
			{
				CurrentServer.disconnectConnection(this, Username + " hat sich ausgeloggt.");
			}
			else if(Command.equals("IAMKICKED"))
			{
				CurrentServer.disconnectConnection(this, Username + " wurde gekickt.");
			}
			else if(Command.equals("MESSAGE"))
			{
				String Msg = CurrentLine.next();
				if(CurrentLine.hasNext())
				{
					Msg = Msg + CurrentLine.nextLine();
				}
				CurrentServer.ConList.broadcast(Server.message(1, Username + ": " + Msg));
				CurrentServer.display(Username + ": " + Msg);
			}
			else if(Command.equals("PRIVATE"))
			{
				Connection Receiver;
				if(CurrentLine.hasNextInt())
				{
					int ID = CurrentLine.nextInt();
					Receiver = CurrentServer.ConList.getConnection(ID);
					if(Receiver == null)
					{
						sendMessage(Server.message(0, "Server: Kein Nutzer mit ID " + ID + " verbunden."));
						return;
					}
				}
				else
				{
					String Username = CurrentLine.next();
					Receiver = CurrentServer.ConList.getConnection(Username);
					if(Receiver == null)
					{
						sendMessage(Server.message(0, "Server: Kein Nutzer mit Namen " + Username + " verbunden."));
						return;
					}
				}
				String Msg = CurrentLine.nextLine();
				sendMessage(Server.message(2, "An " + Receiver.getUsername() + ":" + Msg));
				Receiver.sendMessage(Server.message(2, "Von " + Username + ":" + Msg));
			}
			else if(Command.equals("GETCARD"))
			{
				if(CurrentServer.CurrentGame != null)
				{
					CurrentServer.CurrentGame.getCard(this);
				}
			}
			else if(Command.equals("SETCARD"))
			{
				if(CurrentServer.CurrentGame != null)
				{
					CurrentServer.CurrentGame.setCard(this, CurrentLine.nextInt());
				}
			}
			else
			{
				CurrentServer.display(ID + ": " + Command);
			}
		}
	}
	
	public void sendMessage(String Message)
	{
		try
		{
			sOutput.writeObject(Message);
		}
		catch(Exception e)
		{
			CurrentServer.displayException(e);
			CurrentServer.removeConnection(this);
		}
	}
	
	public void close()
	{
		KeepListening = false;
	}
	
	public int getID()
	{
		return ID;
	}
	
	public String getUsername()
	{
		return Username;
	}
}