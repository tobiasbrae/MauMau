import javax.swing.JFrame;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Window extends JFrame
{
	public static int SizeX = 1000;
	public static int SizeY = 700;
	
	public static int MPosX = 742;
	public static int MPosY = 30;
	public static int MSizeX = 250;
	public static int MSizeY = 645;
	public static int TextSize = 13;
	public static int ArSizeX = 10;
	public static int ArSizeY = 15;
	
	public static int CardSizeX = 50;
	public static int CardSizeY = 100;
	
	public static int HandPosX = 50;
	public static int TopPosX = 100;
	public static int StaplePosX = 300;
	
	public static int Hand1PosY = 50;
	public static int StaplePosY = 300;
	public static int Hand2PosY = 550;
	
	public static int HandSizeX = 100;
	
	private Main CurrentMain;
	private boolean Finished;
	
	public Renderer CurrentRenderer;
	private UserInput UInput;
	public MessageList MList;
	public TextField TField;
	
	public CardList Cards[];
	
	public Window(Main CurrentMain)
	{		
		super("MauMau");
		setSize(SizeX, SizeY);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.CurrentMain = CurrentMain;
		
		CurrentRenderer = new Renderer(this);
	}
	
	public void start()
	{
		setVisible(true);
		CurrentRenderer.start();

		UInput = new UserInput(this);
		addKeyListener(UInput);
		addMouseListener(UInput);
		addMouseMotionListener(UInput);
		
		Label Test = new Label(100, 100, 15, "Dies ist ein Test-Text.");
		//CurrentRenderer.addRenderObject(Test);
		
		MList = new MessageList(getGraphics(), MPosX, MPosY, MSizeX, MSizeY, TextSize);
		CurrentRenderer.addRenderObject(MList);
		
		TField = new TextField(CurrentMain, MPosX, MPosY + MSizeY, MSizeX, TextSize);
		CurrentRenderer.addRenderObject(TField);
		
		Cards = new CardList[4];
		Cards[0] = new CardList(StaplePosX, StaplePosY, CardSizeX, CardList.STAPLE);
		Cards[1] = new CardList(TopPosX, StaplePosY, CardSizeX, CardList.STAPLE);
		Cards[2] = new CardList(HandPosX, Hand1PosY, HandSizeX, CardList.LIST);
		Cards[3] = new CardList(HandPosX, Hand2PosY, HandSizeX, CardList.LIST);
		
		for(int i = 1; i < 53; i++)
		{
			Card Input = new Card(StaplePosX, StaplePosY, CardSizeX, CardSizeY, i);
			Cards[0].addCard(Input);
			CurrentRenderer.addRenderObject(Input);
		}
		
		MList.addMessage("Willkommen bei MauMau!");
		MList.addMessage("Gib \"/hilfe\" ein, falls du Hilfe brauchst.");
		
		Arrow UpArrow = new Arrow(MPosX - ArSizeX - 2, MPosY, ArSizeX, ArSizeY, Arrow.UP, MList);
		CurrentRenderer.addRenderObject(UpArrow);
		Arrow DownArrow = new Arrow(MPosX - ArSizeX - 2, MPosY + MSizeY - ArSizeY, ArSizeX, ArSizeY, Arrow.DOWN, MList);
		CurrentRenderer.addRenderObject(DownArrow);
		Finished = true;
	}
	
	public void hasFinished()
	{
		return Finished;
	}
	
	public void keyPressed(KeyEvent e)
	{
		TField.keyPressed(e);
		MList.keyPressed(e);
	}
	
	public void keyTyped(KeyEvent e)
	{
		TField.keyTyped(e);
	}
	
	public void mouseClicked(MouseEvent e)
	{
		RenderObject Clicked = CurrentRenderer.getClickedRenderObject();
		if(Clicked != null)
		{
			if(Clicked instanceof Card)
			{
				Card ClickedCard = Cards[0].getClickedCard();
				if(ClickedCard != null)
				{
					CurrentMain.getCard();
				}
				else
				{
					ClickedCard = Cards[3].getClickedCard();
					if(ClickedCard != null)
					{
						CurrentMain.setCard(ClickedCard.getID());
					}
				}
			}
			else
			{
				Clicked.clicked();
			}
		}
	}
	
	public void moveCard(int ID, int Target, boolean Visible)
	{
		for(int i = 0; i < 4; i++)
		{
			if(Target != i && Cards[i].hasCard(ID))
			{
				Card Cache = Cards[i].removeCard(ID);
				Cache.setVisible(Visible);
				Cache.setEnabled(false);
				Cards[Target].addCard(Cache);
			}
		}
	}
	
	public void setEnabled(boolean Enabled)
	{
		Cards[0].setAllEnabled(Enabled);
		Cards[3].setAllEnabled(Enabled);
	}
	
	public void resetGame()
	{
		Cards[1].moveToList(Cards[0]);
		Cards[2].moveToList(Cards[0]);
		Cards[3].moveToList(Cards[0]);
		Cards[0].setAllEnabled(false);
		Cards[0].setAllVisible(false);
	}
	
	public void reset()
	{
		resetGame();
		setTitle("MauMau");
	}
}