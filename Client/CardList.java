public class CardList
{
	private Card First, Last;
	public static int STAPLE = 0;
	public static int LIST = 1;
	public static int Spacing = 10;
	private int PosX, PosY, SizeX, Sorting;
	
	public CardList(int PosX, int PosY, int SizeX, int Sorting)
	{
		this.PosX = PosX;
		this.PosY = PosY;
		this.SizeX = SizeX;
		this.Sorting = Sorting;
	}
	
	public void addCard(Card Input)
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
		sortCards();
	}
	
	private Card getCard(int ID)
	{
		Card CurCard = First;
		while(CurCard != null)
		{
			if(CurCard.getID() == ID)
			{
				break;
			}
			CurCard = CurCard.After;
		}
		return CurCard;
	}
	
	public Card getClickedCard()
	{
		Card CurCard = First;
		while(CurCard != null)
		{
			if(CurCard.isFocused())
			{
				break;
			}
			CurCard = CurCard.After;
		}
		return CurCard;
	}
	
	public boolean hasCard(int ID)
	{
		if(getCard(ID) != null)
		{
			return true;
		}
		else
		{
			return false;
		}
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
			sortCards();
			return Output;
		}
		return Output;
	}
	
	public void moveToList(CardList Target)
	{
		if(First != null)
		{
			if(First == Last)
			{
				First.Before = null;
				First.After = null;
				Target.addCard(First);
				First = null;
				Last = null;
			}
			else
			{
				Card Cache, CurCard = First;
				while(CurCard != null)
				{
					Cache = CurCard.After;
					CurCard.Before = null;
					CurCard.After = null;
					Target.addCard(CurCard);
					CurCard = Cache;
				}
				First = null;
				Last = null;
			}
		}
	}
	
	public void setAllEnabled(boolean Enabled)
	{
		Card CurCard = First;
		while(CurCard != null)
		{
			CurCard.setEnabled(Enabled);
			CurCard = CurCard.After;
		}
	}
	
	public void setAllVisible(boolean Visible)
	{
		Card CurCard = First;
		while(CurCard != null)
		{
			CurCard.setVisible(Visible);
			CurCard = CurCard.After;
		}
	}
	
	private void sortCards()
	{
		if(First != null && First == Last)
		{
			First.setPos(PosX, PosY);
		}
		else
		{
			if(Sorting == STAPLE)
			{
				Card CurCard = First;
				while(CurCard != null)
				{
					CurCard.setPos(PosX, PosY);
					CurCard = CurCard.After;
				}
			}
			else if(Sorting == LIST)
			{
				int NewPosX = PosX;
				Card CurCard = First;
				while(CurCard != null)
				{
					CurCard.setPos(NewPosX, PosY);
					NewPosX += CurCard.getWidth() + Spacing;
					CurCard = CurCard.After;
				}
			}
		}
	}
}