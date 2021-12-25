public class CardList
{
	public Card First, Last;
	
	public CardList()
	{
	
	}
	
	public void addCard(Card Input)
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
	
	public void shoveCard(Card Input)
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
				First.Before = Input;
				Input.After = First;
				First = Input;
			}
		}
	}
	
	public Card removeCard()
	{
		Card Output = Last;
		if(Output != null)
		{
			if(First == Last)
			{
				First = null;
				Last = null;
			}
			else
			{
				Last.Before.After = null;
				Last = Last.Before;
			}
			Output.Before = null;
			Output.After = null;
		}
		return Output;
	}
	
	public Card removeCard(int ID)
	{
		Card Output = getCard(ID);
		if(Output != null)
		{
			if(First == Last)
			{
				First = null;
				Last = null;
			}
			else if(First == Output)
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
			Output.Before = null;
			Output.After = null;
		}
		return Output;
	}
	
	private Card getCard(int ID)
	{
		Card CurCard = First;
		while(CurCard != null)
		{
			if(CurCard.getID() == ID)
			{
				return CurCard;
			}
			CurCard = CurCard.After;
		}
		return CurCard;
	}
	
	public boolean hasCards()
	{
		if(First != null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}