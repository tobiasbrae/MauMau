public class Gamer
{
	public Gamer Before, After;
	public Connection Con;
	public CardList Inventory;
	
	public Gamer(Connection Con)
	{
		this.Con = Con;
		Inventory = new CardList();
	}
}