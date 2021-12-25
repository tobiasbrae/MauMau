import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.Scanner;
import java.awt.Color;

public class ServerListener extends Thread
{
	private Main CurrentMain;
	private Socket CurrentSocket;
	private ObjectOutputStream sOutput;
	private ObjectInputStream sInput;
	private boolean KeepRunning;
	
	public ServerListener(Main CurrentMain)
	{
		this.CurrentMain = CurrentMain;
	}
	
	public boolean connect(String Address, int Port, String Username)
	{
		try
		{
			CurrentSocket = new Socket(Address, Port);
			sOutput = new ObjectOutputStream(CurrentSocket.getOutputStream());
			sInput = new ObjectInputStream(CurrentSocket.getInputStream());
			sendMessage(Username);
			String Response = (String) sInput.readObject();
			if(Response.equals("NAMEOK"))
			{
				KeepRunning = true;
				start();
				CurrentMain.addMessage("Server: Erfolgreich verbunden.");
				return true;
			}
			else if(Response.equals("NAMEEXISTS"))
			{
				CurrentMain.addMessage("Server: Name bereits vergeben.");
				return false;
			}
		}
		catch(Exception e)
		{
			CurrentMain.displayException(e);
			return false;
		}
		return false;
	}
	
	public void run()
	{
		while(KeepRunning)
		{
			try
			{
				String Input = (String) sInput.readObject();
				Scanner CurrentLine = new Scanner(Input);
				if(CurrentLine.hasNext())
				{
					CurrentMain.CurrentWindow.requestFocus();
					String Command = CurrentLine.next();
					if(Command.equals("SERVERCLOSED"))
					{
						CurrentMain.addMessage("Server: Der Server wird geschlossen.");
						sendMessage("LOGOUT");
						CurrentMain.disconnect();
					}
					else if(Command.equals("KICKED"))
					{
						CurrentMain.addMessage("Server: Du wurdest gekickt.");
						sendMessage("IAMKICKED");
						CurrentMain.disconnect();
					}
					else if(Command.equals("MESSAGE"))
					{
						Color MessageColor = new Color(CurrentLine.nextInt());
						String Message = CurrentLine.next();
						if(CurrentLine.hasNext())
						{
							Message = Message + CurrentLine.nextLine();
						}
						CurrentMain.addMessage(MessageColor, Message);
					}
					else if(Command.equals("MOVECARD"))
					{
						CurrentMain.CurrentWindow.moveCard(CurrentLine.nextInt(), CurrentLine.nextInt(), CurrentLine.nextBoolean());
					}
					else if(Command.equals("ENABLED"))
					{
						CurrentMain.CurrentWindow.setEnabled(CurrentLine.nextBoolean());
					}
					else if(Command.equals("RESETGAME"))
					{
						CurrentMain.CurrentWindow.resetGame();
					}
					else
					{
						System.out.println("Server: " + Command);
					}
				}
			}	
			catch(Exception e)
			{
				CurrentMain.displayException(e);
				CurrentMain.disconnect();
			}
		}
	}
	
	public void sendMessage(String Msg)
	{
		try
		{
			sOutput.writeObject(Msg);
		}
		catch(Exception e)
		{
			CurrentMain.displayException(e);
			CurrentMain.disconnect();
		}
	}
	
	public void disconnect()
	{
		KeepRunning = false;
	}
}