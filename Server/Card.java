public class Card
{
	public Card Before, After;
	private int ID, Symbol, Value;
	
	public Card(int ID, int Symbol, int Value)
	{
		this.ID = ID;
		this.Symbol = Symbol;
		this.Value = Value;
	}
	
	public int getID()
	{
		return ID;
	}
	
	public int getSymbol()
	{
		return Symbol;
	}
	
	public int getValue()
	{
		return Value;
	}
	
	private String getSymbolName()
	{
		if(Symbol == 1)
		{
			return "Herz";
		}
		else if(Symbol == 2)
		{
			return "Karo";
		}
		else if(Symbol == 3)
		{
			return "Pik";
		}
		else if(Symbol == 4)
		{
			return "Kreuz";
		}
		else
		{
			return "";
		}
	}
	
	private String getValueName()
	{
		if(Value == 1)
		{
			return "Ass";
		}
		else if(Value < 11)
		{
			return Value + "";
		}
		else if(Value == 11)
		{
			return "Bube";
		}
		else if(Value == 12)
		{
			return "Dame";
		}
		else if(Value == 13)
		{
			return "Koenig";
		}
		else
		{
			return "";
		}
	}
	
	public String getName()
	{
		return getSymbolName() + " " + getValueName();
	}
}