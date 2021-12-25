public class GamerList
{
	public Gamer First, Last;
	
	public GamerList(ConnectionList ConList)
	{
		Connection CurCon = ConList.First;
		while(CurCon != null)
		{
			addGamer(new Gamer(CurCon));
			CurCon = CurCon.After;
		}
	}
	
	public void addGamer(Gamer Input)
	{
		if(Input != null)
		{
			if(First == null)
			{
				First = Input;
				Last = Input;
			}
			else
			{
				Last.After = Input;
				Input.Before = Last;
				Last = Input;
			}
		}
	}
	
	public Gamer getGamer(Connection Con)
	{
		Gamer CurGamer = First;
		while(CurGamer != null)
		{
			if(CurGamer.Con == Con)
			{
				return CurGamer;
			}
			CurGamer = CurGamer.After;
		}
		return CurGamer;
	}
	
	public boolean isLastGamer()
	{
		if(First != null && First == Last)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public int countGamers()
	{
		if(First = null)
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
			Gamer CurGamer = First;
			while(CurGamer != null)
			{
				Counter++;
				CurGamer = CurGamer.After;
			}
			return Counter;
		}
	}
	
	public void removeGamer(Gamer Output)
	{
		if(Output != null)
		{
			if(First == Output)
			{
				First.After.Before = null;
				First = First.After;
			}
			else if(Last == Output)
			{
				Last.Before.After = null;
				Last = Last.Before;
			}
			else
			{
				Output.Before.After = Output.After;
				Output.After.Before = Output.Before;
			}
		}
	}
	
	public Gamer getNextGamer(Gamer CurrentGamer, boolean Left)
	{
		if(CurrentGamer != null)
		{
			if(Left)
			{
				if(CurrentGamer == First)
				{
					return Last;
				}
				else
				{
					return CurrentGamer.Before;
				}
			}
			else
			{
				if(CurrentGamer == Last)
				{
					return First;
				}
				else
				{
					return CurrentGamer.After;
				}
			}
		}
		return null;
	}
}