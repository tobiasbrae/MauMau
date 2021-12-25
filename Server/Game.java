public class Game extends Thread
{
	private Server CurrentServer;
	private Gamer CurrentGamer;
	private GamerList Gamers;
	private CardList Bank;
	private Card Top;
	private int Winner;
	private long LastTime;
	
	private int DutyCards;
	private boolean RunOver;
	private boolean Left, DirectionChanged;
	
	private boolean KeepRunning;
	
	public static long WaitForAction = 60000;
	
	public Game(Server CurrentServer)
	{
		this.CurrentServer = CurrentServer;
	}
	
	public void startGame()
	{
		CurrentServer.display("Starte Spiel...");
		
		CurrentServer.display_("Lade Spieler...");
		Gamers = new GamerList(CurrentServer.ConList);
		CurrentServer._display("fertig.");
		
		CurrentServer.display_("Lade Karten...");
		CardList Cache = new CardList();
		int Counter = 0;
		for(int i = 1; i < 5; i++)
		{
			for(int j = 1; j < 14; j++)
			{
				Cache.addCard(new Card(++Counter, i, j));
			}
		}
		CurrentServer._display("fertig.");
		
		CurrentServer.display_("Mische Stapel...");
		Bank = new CardList();
		while(Cache.hasCards())
		{
			double Random = Math.random() * 52.0 + 1.0;
			Card Output = Cache.removeCard((int) Random);
			if(Output != null)
			{
				Bank.addCard(Output);
			}
		}
		CurrentServer._display("fertig.");
		
		CurrentServer.display("Verteile Karten...");
		for(int i = 0; i < 6; i++)
		{
			Gamer CurGamer = Gamers.First;
			while(CurGamer != null)
			{
				Card Change = Bank.removeCard();
				CurGamer.Con.sendMessage("MOVECARD " + Change.getID() + " 3 true");
				CurrentServer.display(CurGamer.Con.getUsername() + " hat " + Change.getName() + "(" + Change.getID() + ") erhalten.");
				CurGamer.Inventory.addCard(Change);
				CurGamer = CurGamer.After;
			}
		}
		CurrentServer.display("Die Karten sind verteilt.");
		
		CurrentServer.display_("Decke Karte auf...");
		Top = Bank.removeCard();
		CurrentServer.ConList.broadcast("MOVECARD " + Top.getID() + " 1 true");
		CurrentServer._display(Top.getName() + ".");
		
		CurrentServer.display("Das Spiel beginnt!");
		CurrentServer.ConList.broadcast(Server.message(3, "Das Spiel beginnt!"));
		
		CurrentGamer = Gamers.Last;
		checkCardEffects(Top);
		setNextGamer();
		
		KeepRunning = true;
		start();
	}
	
	public void resetTimer()
	{
		LastTime = System.currentTimeMillis();
	}
	
	public void run()
	{
		setName("Server_Game");
		LastTime = System.currentTimeMillis();
		while(KeepRunning)
		{
			try
			{
				sleep(20);
			}
			catch(Exception e)
			{
				Server.displayException(e);
			}
			
			if((System.currentTimeMillis() - LastTime) >= WaitForAction)
			{
				System.out.println((System.currentTimeMillis() - LastTime) + "");
				CurrentServer.ConList.broadcastEx(CurrentGamer.Con, Server.message(3, CurrentGamer.Con.getUsername() + " war zu lange inaktiv und zieht zur Strafe eine Karte."));
				CurrentGamer.Con.sendMessage(Server.message(4, "Du warst zu lange inaktiv und musst nun eine Karte ziehen."));
				CurrentServer.display(CurrentGamer.Con.getUsername() + " war zu lange inaktiv und hat eine Strafkarte gezogen.");
				giveCard();
				setNextGamer();				
			}
		}
	}
	
	public void setCurrentGamer(Gamer NextGamer)
	{
		if(CurrentGamer != null)
		{
			Card CurCard = CurrentGamer.Inventory.First;
			while(CurCard != null)
			{
				CurrentServer.ConList.broadcastEx(CurrentGamer.Con, "MOVECARD " + CurCard.getID() + " 0 false");
				CurCard = CurCard.After;
			}
			CurrentGamer.Con.sendMessage("ENABLED false");
		}
		
		Card CurCard = NextGamer.Inventory.First;
		while(CurCard != null)
		{
			CurrentServer.ConList.broadcastEx(NextGamer.Con, "MOVECARD " + CurCard.getID() + " 2 false");
			CurCard = CurCard.After;
		}
		
		CurrentGamer = NextGamer;
		CurrentServer.display(CurrentGamer.Con.getUsername() + " ist dran.");
		CurrentServer.ConList.broadcastEx(CurrentGamer.Con, Server.message(3, CurrentGamer.Con.getUsername() + " ist dran."));
		CurrentGamer.Con.sendMessage(Server.message(4, "Du bist dran."));
		if(DutyCards > 0)
		{
			CurrentServer.display(CurrentGamer.Con.getUsername() + " muss " + DutyCards + " Karten ziehen...");
			CurrentServer.ConList.broadcastEx(CurrentGamer.Con, Server.message(3, CurrentGamer.Con.getUsername() + " muss " + DutyCards + " Karten ziehen..."));
			CurrentGamer.Con.sendMessage(Server.message(4, "Du musst " + DutyCards + " ziehen..."));
		}
		CurrentGamer.Con.sendMessage("ENABLED true");
		resetTimer();
	}
	
	private void checkCardEffects(Card CurCard)
	{
		if(CurCard.getValue() == 7)
		{
			DutyCards += 2;
		}
		else if(CurCard.getValue() == 8)
		{
			RunOver = true;
		}
		else if(CurCard.getValue() == 9)
		{
			Left = !Left;
			DirectionChanged = true;
			CurrentServer.display("9! Richtungswechsel.");
			CurrentServer.ConList.broadcast(Server.message(3, "9! Richtungswechsel."));
		}
		else if(CurCard.getValue() == 11)
		{
			//Wunschfarbe
		}
	}
	
	public void setCard(Connection Con, int ID)
	{
		if(Con == CurrentGamer.Con)
		{
			Card CurCard = CurrentGamer.Inventory.removeCard(ID);
			if(CurCard != null && CurCard.getValue() == 11 || Top.getSymbol() == CurCard.getSymbol() || Top.getValue() == CurCard.getValue())
			{				
				if(DutyCards > 0 && CurCard.getValue() != 7)
				{
					CurrentServer.display(Con.getUsername() + " zieht " + DutyCards + " Karten vom Stapel.");
					Con.sendMessage(Server.message(4, "Du ziehst " + DutyCards + " Karten vom Stapel."));
					CurrentServer.ConList.broadcastEx(Con, Server.message(3, Con.getUsername() + " zieht " + DutyCards + " vom Stapel..."));
					for(int i = 0; i < DutyCards; i++)
					{
						giveCard();
					}
					DutyCards = 0;
				}
				checkCardEffects(CurCard);
				
				Bank.shoveCard(Top);
				CurrentServer.ConList.broadcast("MOVECARD " + Top.getID() + " 0 false");
				Top = CurCard;
				CurrentServer.ConList.broadcast("MOVECARD " + Top.getID() + " 1 true");
				CurrentServer.display(CurrentGamer.Con.getUsername() + " hat " + Top.getName() + " gelegt.");
				
				if(CurrentGamer.Inventory.hasCards())
				{
					setNextGamer();
				}
				else
				{
					CurrentServer.display(CurrentGamer.Con.getUsername() + " ist Gewinner Nr." + ++Winner + ".");
					CurrentServer.ConList.broadcastEx(CurrentGamer.Con, Server.message(3, CurrentGamer.Con.getUsername() + " ist Gewinner Nr." + Winner + "."));
					CurrentGamer.Con.sendMessage(Server.message(4, "Du bist Gewinner Nr. " + Winner + "."));
					removeGamer(CurrentGamer);
				}
			}
		}
	}
	
	public void getCard(Connection Con)
	{
		if(Con == CurrentGamer.Con)
		{
			CurrentServer.display(Con.getUsername() + " hat " + Bank.Last.getName() + " vom Stapel gezogen.");
			giveCard();
			setNextGamer();
		}
	}
	
	public synchronized void giveCard()
	{
		Card Change = Bank.removeCard();
		CurrentGamer.Inventory.addCard(Change);
		CurrentGamer.Con.sendMessage("MOVECARD " + Change.getID() + " 3 true");
	}
	
	public void removeGamer(Connection Con)
	{
		CurrentServer.ConList.broadcastEx(Con, Server.message(0, Con.getUsername() + " wurde aus dem Spiel entfernt."));
		CurrentServer.display(Con.getUsername() + " wurde aus dem Spiel entfernt.");
		removeGamer(Gamers.getGamer(Con));
	}
	
	private void setNextGamer()
	{
		Gamer NextGamer = CurrentGamer;
		if(RunOver)
		{
			NextGamer = Gamers.getNextGamer(CurrentGamer, Left);
			CurrentServer.display("8! " + NextGamer.Con.getUsername() + " muss aussetzen.");
			CurrentServer.ConList.broadcastEx(NextGamer.Con, Server.message(3, "8! " + NextGamer.Con.getUsername() + " muss aussetzen."));
			NextGamer.Con.sendMessage(Server.message(4, "8! Du musst aussetzen."));
			RunOver = false;
		}
		else if(DirectionChanged)
		{
			if(Gamers.countGamers() == 2)
			{
				NextGamer = CurrentGamer;
			}
			DirectionChanged = false;
		}
		NextGamer = Gamers.getNextGamer(CurrentGamer, Left);
		setCurrentGamer(NextGamer);
	}
	
	public void removeGamer(Gamer Output)
	{
		if(Output != null)
		{
			if(CurrentGamer == Output)
			{
				DutyCards = 0;
				setNextGamer();
			}
			
			while(Output.Inventory.hasCards())
			{
				Card Change = Output.Inventory.removeCard();
				Bank.shoveCard(Change);
				CurrentServer.ConList.broadcast("MOVECARD " + Change.getID() + " 0 false");
			}
			
			Gamers.removeGamer(Output);
			
			if(Gamers.isLastGamer())
			{
				CurrentServer.ConList.broadcast(Server.message(3, "Das Spiel ist vorbei!"));
				CurrentServer.display("Das Spiel ist vorbei!");
				CurrentGamer.Con.sendMessage(Server.message(4, "Du bist ein totaler Loser!"));
				CurrentServer.ConList.broadcastEx(CurrentGamer.Con, Server.message(3, CurrentGamer.Con.getUsername() + " ist ein totaler Loser."));
				CurrentServer.display(CurrentGamer.Con.getUsername() + " ist ein totaler Loser.");
				
				CurrentServer.closeGame();
			}
		}
	}
}