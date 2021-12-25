import java.util.Scanner;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;

public class Main
{
	public Window CurrentWindow;
	private int Port = 5000;
	private ServerListener SVListener;
	
	public static void main(String args[])
	{
		Main main = new Main();
	}
	
	public Main()
	{
		CurrentWindow = new Window(this);
		CurrentWindow.start();
	}
	
	public void readMessage(String Input)
	{
		Scanner CurrentLine = new Scanner(Input);
		
		if(CurrentLine.hasNext())
		{
			String Command = CurrentLine.next();
			if(Command.equals("/hilfe"))
			{
				addMessage("\"/hilfe\" - listet alle Befehle");
				addMessage("\"/connect <IP> <Name>\" - Verbindet zu einem Server");
				addMessage("\"/pn <ID/Name> <Nachricht>\" - Sendet eine private Nachricht");
				addMessage("\"/setport <Port>\" - Stellt den Port ein");
				addMessage("\"/logout\" - Verbindung trennen");
				addMessage("\"/exit\" - Client beenden");
			}
			else if(Command.equals("/connect"))
			{
				if(CurrentLine.hasNext())
				{
					String Address = CurrentLine.next();
					if(CurrentLine.hasNext())
					{
						String Username = CurrentLine.next();
						connect(Address, Username);
					}
					else
					{
						addMessage("Du musst einen Namen angeben.");
					}
				}
				else
				{
					addMessage("Du musst eine IP angeben.");
				}
			}
			else if(Command.equals("/pn"))
			{
				if(SVListener != null)
				{
					String Message = "PRIVATE ";
					if(CurrentLine.hasNext())
					{
						Message = Message + CurrentLine.next();
						if(CurrentLine.hasNext())
						{
							Message = Message + " " + CurrentLine.next();
							if(CurrentLine.hasNext())
							{
								Message = Message + CurrentLine.nextLine();
							}
							SVListener.sendMessage(Message);
						}
						else
						{
							addMessage("Fehler. Korrekte Nutzung: \"/pn <ID/Name> <Nachricht>\"");
						}
					}
					else
					{
						addMessage("Fehler. Korrekte Nutzung: \"/pn <ID/Name> <Nachricht>\"");
					}
				}
				else
				{
					addMessage("Du bist mit keinem Server verbunde.");
				}
			}
			else if(Command.equals("/setport"))
			{
				if(SVListener == null)
				{
					if(CurrentLine.hasNextInt())
					{
						Port = CurrentLine.nextInt();
						addMessage("Der Port wurde erfolgreich auf " + Port + " eingestellt.");
					}
					else
					{
						addMessage("Fehler. Du musst einen Port angeben.");
					}
				}
				else
				{
					addMessage("Fehler. Du bist zur Zeit verbunden.");
				}
			}
			else if(Command.equals("/logout"))
			{
				if(SVListener != null)
				{
					SVListener.sendMessage("LOGOUT");
					disconnect();
					addMessage("Du hast dich ausgeloggt.");
				}
				else
				{
					addMessage("Fehler. Du bist nicht verbunden.");
				}
			}
			else if(Command.equals("/exit"))
			{
				CurrentWindow.MList.addMessage("Noch nicht implementiert.");
			}
			else if(SVListener != null)
			{
				String Message = Command;
				if(CurrentLine.hasNext())
				{
					Message = Message + CurrentLine.nextLine();
				}
				SVListener.sendMessage("MESSAGE " + Message);
			}
			else
			{
				CurrentWindow.MList.addMessage("Unbekannter Befehl.");
			}
		}
	}
	
	public void connect(String Address, String Username)
	{
		while(CurrentWindow.hasFinished())
		{
			// Nothing to do here...
		}
		if(SVListener == null)
		{
			SVListener = new ServerListener(this);
			if(SVListener.connect(Address, Port, Username))
			{
				CurrentWindow.setTitle(Username + " - MauMau");
			}
			else
			{
				SVListener = null;
			}
		}
		else
		{
			addMessage("Fehler. Du bist bereits verbunden.");
		}
	}
	
	public void disconnect()
	{
		SVListener.disconnect();
		SVListener = null;
		CurrentWindow.reset();
	}
	
	public void getCard()
	{
		if(SVListener != null)
		{
			SVListener.sendMessage("GETCARD");
		}
	}
	
	public void setCard(int ID)
	{
		if(SVListener != null)
		{
			SVListener.sendMessage("SETCARD " + ID);
		}
	}
	
	public void close()
	{
		SVListener.sendMessage("LOGOUT");
		disconnect();
		System.exit(0);
	}
	
	public void addMessage(String Msg)
	{
		CurrentWindow.MList.addMessage(Msg);
	}
	
	public void addMessage(Color MessageColor, String Msg)
	{
		CurrentWindow.MList.addMessage(MessageColor, Msg);
	}
	
	public void displayException(Exception e)
	{
		displayException(e, true);
	}
	
	public void displayException(Exception e, boolean logFile)
	{
		addMessage("Fehler. Details in \"errorlog.txt\"");
		
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