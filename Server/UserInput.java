import java.util.Scanner;

public class UserInput extends Thread
{
	private Server CurrentServer;
	private boolean KeepRunning;
	
	public UserInput(Server CurrentServer)
	{
		this.CurrentServer = CurrentServer;
	}
	
	public void run()
	{
		setName("Server_UserInput");
		KeepRunning = true;
		
		Scanner Input = new Scanner(System.in);
		
		while(KeepRunning)
		{
			Scanner CurrentLine = new Scanner(Input.nextLine());
			if(CurrentLine.hasNext())
			{
				String Command = CurrentLine.next();
				
				if(Command.equals("hilfe"))
				{
					CurrentServer.display("Folgende Befehle sind vorhanden:");
					CurrentServer.display("hilfe - Listet alle Befehle auf.");
					CurrentServer.display("setport <Port> - Stellt den Port ein(Standard: 5000)"); 
					CurrentServer.display("start - Der Server beginnt, Clients zu empfangen.");
					CurrentServer.display("connected - Zeigt alle verbundenen Clients an.");
					CurrentServer.display("stop - Alle Verbindungen werden unterbrochen und der Server gestoppt.");
					CurrentServer.display("broadcast <Nachricht> - Sendet eine Nachricht an alle Clients.");
					CurrentServer.display("send <ID/Nutzername> <Nachricht> - Sendet eine Nachricht an einen Client.");
					CurrentServer.display("kick <ID/Nutzername> - Wirft einen Client aus dem Server.");
					CurrentServer.display("startgame - Startet ein neues Spiel.");
					CurrentServer.display("closegame - Beendet das aktuelle Spiel.");
					CurrentServer.display("exit - Beendet den Server.");
				}
				else if(Command.equals("setport"))
				{
					if(CurrentLine.hasNextInt())
					{
						CurrentServer.setPort(CurrentLine.nextInt());
					}
					else
					{
						CurrentServer.display("Fehler. Korrekte Nutzung: \"setport <Port>\"");
					}
				}
				else if(Command.equals("start"))
				{
					CurrentServer.startListening();
				}
				else if(Command.equals("connected"))
				{
					CurrentServer.ConList.printClients();
				}
				else if(Command.equals("stop"))
				{
					CurrentServer.stopListening();
				}
				else if(Command.equals("broadcast"))
				{
					if(CurrentLine.hasNext())
					{
						String Message = "MESSAGE Server: ";
						Message = Message + CurrentLine.next();
						if(CurrentLine.hasNext())
						{
							Message = Message + CurrentLine.nextLine();
						}
						
						if(!CurrentServer.ConList.broadcast(Message))
						{
							CurrentServer.display("Fehler. Keine Clients verbunden.");
						}
					}
					else
					{	
						CurrentServer.display("Fehler. Korrekte Nutzung: \"broadcast <Nachricht>\"");
					}
				}
				else if(Command.equals("send"))
				{
					if(CurrentLine.hasNextInt())
					{
						int ID = CurrentLine.nextInt();
						if(CurrentLine.hasNext())
						{
							Connection Target = CurrentServer.ConList.getConnection(ID);
							if(Target != null)
							{
								String Msg = "MESSAGE Server: ";
								Msg = Msg + CurrentLine.next();
								if(CurrentLine.hasNext())
								{
									Msg = Msg + CurrentLine.nextLine();
								}
								Target.sendMessage(Msg);
							}
							else
							{
								CurrentServer.display("Fehler. Kein Nutzer mit ID " + ID + " verbunden.");
							}
						}
						else
						{
							CurrentServer.display("Fehler. Korrekte Nutzung: \"send <ID/Nutzername> <Nachricht>\"");
						}
					}
					else if(CurrentLine.hasNext())
					{
						String Username = CurrentLine.next();
						if(CurrentLine.hasNext())
						{
							Connection Target = CurrentServer.ConList.getConnection(Username);
							if(Target != null)
							{
								String Msg = "MESSAGE Server: ";
								Msg = Msg + CurrentLine.next();
								if(CurrentLine.hasNext())
								{
									Msg = Msg + CurrentLine.nextLine();
								}
								Target.sendMessage(Msg);
							}
							else
							{
								CurrentServer.display("Fehler. Kein Nutzer mit Namen " + Username + " verbunden.");
							}
						}
						else
						{
							CurrentServer.display("Fehler. Korrekte Nutzung: \"send <ID/Nutzername> <Nachricht>\"");
						}
					}
					else
					{
						CurrentServer.display("Fehler. Korrekte Nutzung: \"send <ID/Nutzername> <Nachricht>\"");
					}
				}
				else if(Command.equals("kick"))
				{
					if(CurrentLine.hasNextInt())
					{
						int ID = CurrentLine.nextInt();
						Connection Target = CurrentServer.ConList.getConnection(ID);
						if(Target != null)
						{
							Target.sendMessage("KICKED");
						}
						else
						{
							CurrentServer.display("Fehler. Kein Nutzer mit ID " + ID + " verbunden.");
						}
					}
					else if(CurrentLine.hasNext())
					{
						String Username = CurrentLine.next();
						Connection Target = CurrentServer.ConList.getConnection(Username);
						if(Target != null)
						{
							Target.sendMessage("KICKED");
						}
						else
						{
							CurrentServer.display("Fehler. Kein Nutzer mit Namen " + Username + " verbunden.");
						}
					}
					else
					{
						CurrentServer.display("Fehler. Korrekte Nutzung: \"kick <ID/Nutzername>\"");
					}
				}
				else if(Command.equals("startgame"))
				{
					CurrentServer.startNewGame();
				}
				else if(Command.equals("closegame"))
				{
					if(CurrentServer.CurrentGame != null)
					{
						CurrentServer.closeGame();
						CurrentServer.ConList.broadcast("MESSAGE Das Spiel wurde durch den Server beendet.");
						CurrentServer.display("Du hast das Spiel beendet.");
					}
					else
					{
						CurrentServer.display("Fehler. Es ist kein Spiel aktiv.");
					}
				}
				else if(Command.equals("exit"))
				{
					CurrentServer.close();
				}
				else
				{
					CurrentServer.display("Unbekannter Befehl.");
				}
			}
			else
			{
				CurrentServer.display("Unbekannter Befehl.");
			}
		}
	}
	
	public void close()
	{
		KeepRunning = false;
	}
}