import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Cursor;

public class Renderer extends Thread
{
	private Window CurrentWindow;
	private RenderObject First, Last, Focused;
	private int MouseX, MouseY;
	
	public Renderer(Window CurrentWindow)
	{
		this.CurrentWindow = CurrentWindow;
	}
	
	public void addRenderObject(RenderObject Input)
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
		Input.CurrentRenderer = this;
	}
	
	public void removeRenderObject(RenderObject Output)
	{
		if(Output != null)
		{
			if(First == Last && First == Output)
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
				Output.After.Before = Output.Before;
				Output.Before.After = Output.After;
			}
			Output.Before = null;
			Output.After = null;
		}
	}
	
	public void run()
	{
		Image BackBuffer = CurrentWindow.createImage(CurrentWindow.SizeX, CurrentWindow.SizeY);
		Graphics CurGraphics = BackBuffer.getGraphics();
		
		while(true)
		{
			CurGraphics.setColor(Color.white);
			CurGraphics.fillRect(0, 0, CurrentWindow.SizeX, CurrentWindow.SizeY);
			
			RenderObject CurObject = First;
			while(CurObject != null)
			{
				CurGraphics.setColor(CurObject.DrawingColor);
				CurObject.paint(CurGraphics);
				CurObject = CurObject.After;
			}
			
			CurrentWindow.getGraphics().drawImage(BackBuffer, 0, 0, null);
		}
	}
	
	public RenderObject getClickedRenderObject()
	{
		return Focused;
	}
	
	public void setEnabled(RenderObject Obj, boolean Enabled)
	{
		if(Obj == Focused && Enabled == false)
		{
			Focused.setFocused(false);
			Focused.setDirectEnabled(false);
			CurrentWindow.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			Focused = null;
		}
		else
		{
			Obj.setDirectEnabled(Enabled);
		}
	}
	
	public void mouseMoved()
	{
		mouseMoved(MouseX, MouseY);
	}
	
	public synchronized void mouseMoved(int MouseX, int MouseY)
	{		
		this.MouseX = MouseX;
		this.MouseY = MouseY;
		
		if(Focused == null)
		{
			RenderObject CurObject = Last;
			while(CurObject != null)
			{
				if(CurObject.isMouseOver(MouseX, MouseY) && !CurObject.isFocused() && CurObject.setFocused(true))
				{
					Focused = CurObject;
					CurrentWindow.setCursor(new Cursor(Cursor.HAND_CURSOR));
					break;
				}
				CurObject = CurObject.Before;
			}
		}
		else
		{
			if(!Focused.isMouseOver(MouseX, MouseY) && Focused.setFocused(false))
			{
				CurrentWindow.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				Focused = null;
			}
		}
	}
}