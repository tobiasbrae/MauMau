public class ConnectionList
{
	public Connection First, Last;
	private Server CurrentServer;
	
	public ConnectionList(Server CurrentServer)
	{
		this.CurrentServer = CurrentServer;
	}
	
	public void addConnection(Connection Con)
	{
		if(First == null)
		{
			First = Con;
			Last = Con;
		}
		else
		{
			Last.After = Con;
			Con.Before = Last;
			Last = Con;
		}
	}
	
	public void removeConnection(Connection Con)
	{
		if(Con != null)
		{			
			if(First == Last && First == Con)
			{
				First = null;
				Last = null;
				CurrentServer.display("Nun sind keine Clients mehr verbunden.");
			}
			else if(First == Con)
			{
				First.After.Before = null;
				First = First.After;
			}
			else if(Last == Con)
			{
				Last.Before.After = null;
				Last = Last.Before;
			}
			else
			{
				Con.Before.After = Con.After;
				Con.After.Before = Con.Before;
			}
		}
	}
	
	public Connection getConnection(int ID)
	{
		Connection CurCon = First;
		while(CurCon != null)
		{
			if(CurCon.getID() == ID)
			{
				return CurCon;
			}
			CurCon = CurCon.After;
		}
		return CurCon;
	}
	
	public Connection getConnection(String Username)
	{
		Connection CurCon = First;
		while(CurCon != null)
		{
			if(CurCon.getUsername().equals(Username))
			{
				return CurCon;
			}
			CurCon = CurCon.After;
		}
		return CurCon;
	}
	
	public int countConnections()
	{
		if(First == null)
		{
			return 0;
		}
		else if(First == Last)
		{
			return 1;
		}
		else
		{
			int Counter = 0;
			Connection CurCon = First;
			while(CurCon != null)
			{
				Counter++;
				CurCon = CurCon.After;
			}
			return Counter;
		}
	}
	
	public boolean broadcast(String Message)
	{
		if(First == null)
		{
			return false;
		}
		else if(First == Last)
		{
			First.sendMessage(Message);
		}
		else
		{
			Connection CurCon = First;
			while(CurCon != null)
			{
				CurCon.sendMessage(Message);
				CurCon = CurCon.After;
			}
		}
		return true;
	}
	
	public void broadcastEx(Connection Con, String Message)
	{
		if(First != null)
		{
			if(First == Last && First != Con)
			{
				First.sendMessage(Message);
			}	
			else
			{
				Connection CurCon = First;
				while(CurCon != null)
				{
					if(CurCon != Con)
					{
						CurCon.sendMessage(Message);
					}
					CurCon = CurCon.After;
				}
			}
		}
	}
	
	public void printClients()
	{
		CurrentServer.display("Verbundene Clients:");
		if(First == null)
		{
			CurrentServer.display("Keine Clients verbunden.");
		}
		else if(First == Last)
		{
			CurrentServer.display(First.getID() + " - " + First.getUsername());
		}
		else
		{
			Connection CurCon = First;
			while(CurCon != null)
			{
				CurrentServer.display(CurCon.getID() + " - " + CurCon.getUsername());
				CurCon = CurCon.After;
			}
		}
	}
}