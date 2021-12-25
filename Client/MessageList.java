import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.Font;
import java.util.Scanner;
import java.awt.Color;
import java.awt.event.KeyEvent;

public class MessageList extends RenderObject
{
	private Graphics CurrentGraphics;
	private Label First, Higher, Lower, Last;
	private int TextSize;
	private boolean ScrollDown;
	private Arrow[] Arrows;
	
	public MessageList(Graphics CurrentGraphics, int PosX, int PosY, int SizeX, int SizeY, int TextSize)
	{
		super(PosX, PosY, SizeX, SizeY);
		this.CurrentGraphics = CurrentGraphics;
		this.TextSize = TextSize;
		Arrows = new Arrow[3];
	}
	
	public void addMessage(String Input)
	{
		addMessage(Color.black, Input);
	}
	
	public void addArrow(Arrow Input, int Direction)
	{
		Arrows[Direction] = Input;
	}
	
	private void checkArrows()
	{
		if(Arrows[Arrow.UP] != null)
		{
			if(Higher != First)
			{
				Arrows[Arrow.UP].setEnabled(true);
			}
			else
			{
				Arrows[Arrow.UP].setEnabled(false);
			}
		}
		if(Arrows[Arrow.DOWN] != null)
		{
			if(Lower != Last)
			{
				Arrows[Arrow.DOWN].setEnabled(true);
			}
			else
			{
				Arrows[Arrow.DOWN].setEnabled(false);
			}
		}
	}
	
	public void addMessage(Color MessageColor, String Input)
	{
		Scanner Line = new Scanner(Input);
		String Msg = "", Word = "";
		while(Line.hasNext() || !Word.equals(""))
		{
			if(Word.equals(""))
			{
				Word = Line.next();
			}
			if(getWidth(Word) > (SizeX - 8))
			{
				if(!Msg.equals(""))
				{
					Msg = Msg + " ";
				}
				int Counter = 0;
				while(getWidth(Msg) < (SizeX - 8))
				{
					Msg = Msg + Word.charAt(Counter);
					Counter++;
				}
				addMessage(new Label(0, 0, TextSize, Msg, MessageColor));
				Msg = " ";
				Word = Word.substring(Counter);
			}
			else if(getWidth(Msg + Word) > (SizeX - 8))
			{
				addMessage(new Label(0, 0, TextSize, Msg, MessageColor));
				Msg = " ";
			}
			else
			{
				if(Msg.equals(""))
				{
					Msg = Word;
				}
				else
				{
					Msg = Msg + " " + Word;
				}
				Word = "";
			}
		}
		addMessage(new Label(0, 0, TextSize, Msg, MessageColor));
	}
	
	private int getWidth(String Input)
	{
		CurrentGraphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, TextSize));
		FontMetrics FM = CurrentGraphics.getFontMetrics();
		return FM.stringWidth(Input);
	}
	
	private void addMessage(Label Input)
	{
		if(First == null)
		{
			First = Input;
			Higher = Input;
			Lower = Input;
			Last = Input;
		}
		else
		{
			Last.After = Input;
			Input.Before = Last;
			Last = Input;
		}
		
		ScrollDown = true;
	}
	
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_PAGE_UP)
		{
			scrollUp();
		}
		else if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
		{
			scrollDown();
		}
	}
	
	public void scrollDown()
	{
		if(Lower != Last)
		{
			Higher = Higher.After;
			Lower = Lower.After;
		}
		checkArrows();
	}
	
	public void scrollUp()
	{
		if(Higher != First)
		{
			Higher = Higher.Before;
			Lower = Lower.Before;
		}
		checkArrows();
	}
	
	public void paint(Graphics g)
	{
		g.setColor(Color.black);
		g.drawRect(PosX, PosY, SizeX, SizeY);
		
		if(First != null)
		{
			if(First == Last)
			{
				First.setPos(PosX + 2, PosY + TextSize);
				First.paint(g);
			}
			else
			{
				int NewPosY = PosY + TextSize;
				Label CurLabel = Higher;
				while(CurLabel != null)
				{
					CurLabel.setPos(PosX + 2, NewPosY);
					CurLabel.paint(g);
					if(CurLabel == Last)
					{
						Lower = CurLabel;
						break;
					}
					NewPosY += TextSize;
					if(NewPosY > (PosY + SizeY))
					{
						Lower = CurLabel;
						break;
					}
					CurLabel = CurLabel.After;
				}
			}
		}
		
		if(ScrollDown)
		{
			while(Lower != Last)
			{
				scrollDown();
			}
			ScrollDown = false;
		}
	}
}