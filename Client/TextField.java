import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;

public class TextField extends RenderObject
{
	private Main CurrentMain;
	private Graphics CurrentGraphics;
	private int TextSize;
	private KeyChar First, Higher, Cursor, Lower, Last;
	
	public TextField(Main CurrentMain, int PosX, int PosY, int SizeX, int TextSize)
	{
		super(PosX, PosY, SizeX, TextSize + 5);
		this.CurrentMain = CurrentMain;
		this.CurrentGraphics = CurrentMain.CurrentWindow.getGraphics();
		this.TextSize = TextSize;
		
		Cursor = new KeyChar('a');
		Cursor.setCursor(true);
		addKeyChar(Cursor);
	}
	
	public synchronized void keyTyped(KeyEvent e)
	{
		if(e.getKeyChar() != 8)
		{
			addKeyChar(new KeyChar(e.getKeyChar()));
		}
	}
	
	public synchronized void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
		{
			removeCharBefore();
		}
		if(e.getKeyCode() == KeyEvent.VK_DELETE)
		{
			removeCharAfter();
		}
		else if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			CurrentMain.readMessage(getOutputString());
			reset();
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT)
		{
			moveCursorLeft();
		}
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			moveCursorRight();
		}
		else if(e.getKeyCode() == KeyEvent.VK_V)
		{
			if(e.isControlDown())
			{
				try
				{
					Clipboard SysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
					Transferable Transfer = SysClip.getContents(null);
					String Input = (String) Transfer.getTransferData(DataFlavor.stringFlavor);
					if(!Input.equals(""))
					{
						addString(Input);
					}
				}
				catch(Exception e2)
				{
					// Nothing to do here
				}
			}
		}
	}
	
	public void reset()
	{
		First = null;
		Higher = null;
		Lower = null;
		Last = null;
		addKeyChar(Cursor);
	}
	
	private void addString(String Input)
	{
		for(int i = 0; i < Input.length(); i++)
		{
			addKeyChar(new KeyChar(Input.charAt(i)));
		}
	}
	
	private void addKeyChar(KeyChar Input)
	{
		if(First == null)
		{
			First = Input;
			Higher = Input;
			Lower = Input;
			Last = Input;
		}
		else if(First == Cursor)
		{
			First.Before = Input;
			Input.After = First;
			First = Input;
		}
		else
		{
			Cursor.Before.After = Input;
			Input.Before = Cursor.Before;
			Input.After = Cursor;
			Cursor.Before = Input;
		}
		if(Cursor == Higher)
		{
			Higher = Input;
		}
		if(isToBig())
		{
			if(Cursor.After == Lower || Cursor == Lower)
			{
				Higher = Higher.After;
			}
			else
			{
				Lower = Lower.Before;
			}
		}
	}
	
	private void removeCharBefore()
	{
		if(Cursor != First)
		{
			if(isToBig())
			{
				if(Cursor.Before == Higher)
				{
					if(Cursor.Before.Before != null)
					{
						Higher = Cursor.Before.Before;
					}
					else
					{
						Higher = Cursor;
					}
				}
				else if(Lower == Last)
				{
					Higher = Higher.Before;
				}
				else
				{
					Lower = Lower.After;
				}
			}
			if(Cursor.Before == Higher)
			{
				Higher = Cursor;
			}
			if(Cursor.Before == First)
			{
				Cursor.Before = null;
				First = Cursor;
			}
			else
			{
				Cursor.Before.Before.After = Cursor;
				Cursor.Before = Cursor.Before.Before;
			}
		}
	}
	
	private void removeCharAfter()
	{
		if(Cursor != Last)
		{
			if(isToBig())
			{
				if(Cursor.After == Lower)
				{
					Lower = Cursor;
				}
				else if(Cursor.After.After != null)
				{
				
				}
			}
			if(Cursor.After == Last)
			{
				Cursor.After = null;
				Last = Cursor;
			}
			else
			{
				Cursor.After = Cursor.After.After;
				Cursor.After.Before = Cursor;
			}
		}
	}
	
	private synchronized void moveCursorLeft()
	{
		if(Cursor != First)
		{
			if(isToBig())
			{
				if(Cursor.Before == Higher)
				{
					if(Higher.Before != null)
					{
						Higher = Higher.Before;
						Lower = Lower.Before;
					}
					else
					{
						Higher = Cursor;
					}
				}
				else
				{
					if(Lower == Cursor)
					{
						Lower = Cursor.Before;
					}
				}
			}
			
			KeyChar One, Two, Three, Four;
			One = Cursor.Before.Before;
			Two = Cursor;
			Three = Cursor.Before;
			Four = Cursor.After;
			
			if(One == null)
			{
				First = Two;
				Two.Before = null;
			}
			else
			{
				Two.Before = One;
				One.After = Two;
			}
			Two.After = Three;
			Three.Before = Two;
			if(Four == null)
			{
				Three.After = null;
				Last = Three;
			}
			else
			{
				Three.After = Four;
				Four.Before = Three;
			}
		}
	}
	
	private synchronized void moveCursorRight()
	{
		if(Cursor != Last)
		{
			if(isToBig())
			{
				if(Cursor.After == Lower)
				{
					if(Cursor.After.After == null)
					{
						Lower = Cursor;
					}
					else
					{
						Higher = Higher.After;
						Lower = Lower.After;
					}
				}
				else
				{
					if(Cursor == Higher)
					{
						Higher = Cursor.After;
					}
				}
			}
			
			KeyChar One, Two, Three, Four;
			One = Cursor.Before;
			Two = Cursor.After;
			Three = Cursor;
			Four = Cursor.After.After;
			
			if(One == null)
			{
				First = Two;
				Two.Before = null;
			}
			else
			{
				One.After = Two;
				Two.Before = One;
			}
			Three.Before = Two;
			Two.After = Three;
			if(Four == null)
			{
				Last = Three;
				Last.After = null;
			}
			else
			{
				Three.After = Four;
				Four.Before = Three;
			}
		}
	}
	
	private String getString()
	{
		String Output = "";
		if(First == Last)
		{
			Output = "" + First.getChar();
		}
		else
		{
			KeyChar CurChar = Higher;
			while(CurChar != null)
			{
				Output = Output + CurChar.getChar();
				if(CurChar == Lower)
				{
					CurChar = null;
					//CurChar = CurChar.After;
				}
				else
				{
					CurChar = CurChar.After;
				}
			}
		}
		return Output;
	}
	
	private String getFullString()
	{
		String Output = "";
		if(First != Last)
		{
			KeyChar CurChar = First;
			while(CurChar != null)
			{
				Output = Output + CurChar.getChar();
				CurChar = CurChar.After;
			}
		}
		return Output;
	}
	
	private String getOutputString()
	{
		String Output = "";
		if(First != Last)
		{
			KeyChar CurChar = First;
			while(CurChar != null)
			{
				if(!CurChar.isCursor())
				{
					Output = Output + CurChar.getChar();
				}
				CurChar = CurChar.After;
			}
		}
		return Output;
	}
	
	private int getWidth(String Input)
	{
		CurrentGraphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, TextSize));
		FontMetrics FM = CurrentGraphics.getFontMetrics();
		return FM.stringWidth(Input);
	}
	
	private boolean isToBig()
	{
		if(getWidth(getFullString()) > (SizeX - 4))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void paint(Graphics g)
	{
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, TextSize));
		g.setColor(DrawingColor);
		g.drawRect(PosX, PosY, SizeX, SizeY);
		g.drawString(getString(), PosX + 2, PosY + TextSize);
	}
	
	private class KeyChar
	{
		public KeyChar Before, After;
		private char Value;
		private boolean Cursor, Show;
		private long LastTime;
		private int Wait = 500;
		
		public KeyChar(char Value)
		{
			this.Value = Value;
		}
		
		public boolean isCursor()
		{
			return Cursor;
		}
		
		public void setCursor(boolean Cursor)
		{
			this.Cursor = Cursor;
			if(Cursor)
			{
				LastTime = System.currentTimeMillis();
			}
		}
		
		public char getChar()
		{
			if(Cursor)
			{
				if((System.currentTimeMillis() - LastTime) >= Wait)
				{
					LastTime = System.currentTimeMillis();
					Show = !Show;
				}
				if(Show)
				{
					return 124;
				}				
				else
				{
					return 32;
				}
			}
			else
			{
				return Value;
			}
		}
	}
}