import java.awt.Graphics;
import java.awt.Color;

public class RenderObject
{
	public Renderer CurrentRenderer;
	public RenderObject Before, After;
	protected int PosX, PosY, SizeX, SizeY;
	protected Color DrawingColor;
	private boolean CanBeFocused;
	private boolean Enabled, Visible = true, Focused;
	
	public RenderObject(int PosX, int PosY, int SizeX, int SizeY)
	{
		this.PosX = PosX;
		this.PosY = PosY;
		this.SizeX = SizeX;
		this.SizeY = SizeY;
		this.DrawingColor = Color.black;
	}
	
	public RenderObject(int PosX, int PosY, int SizeX, int SizeY, Color DrawingColor)
	{
		this.PosX = PosX;
		this.PosY = PosY;
		this.SizeX = SizeX;
		this.SizeY = SizeY;
		this.DrawingColor = DrawingColor;
	}
	
	public void setColor(Color DrawingColor)
	{
		this.DrawingColor = DrawingColor;
	}
	
	public void paint(Graphics g)
	{
	
	}
	
	public void setPos(int PosX, int PosY)
	{
		this.PosX = PosX;
		this.PosY = PosY;
		if(CurrentRenderer != null)
		{
			CurrentRenderer.removeRenderObject(this);
			CurrentRenderer.addRenderObject(this);
			CurrentRenderer.mouseMoved();
		}
	}
	
	public void clicked()
	{
	
	}
	
	public boolean isMouseOver(int MouseX, int MouseY)
	{
		if(PosX <= MouseX && PosX + SizeX >= MouseX && PosY <= MouseY && PosY + SizeY >= MouseY)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void setEnabled(boolean Enabled)
	{
		if(CurrentRenderer == null)
		{
			this.Enabled = Enabled;
		}
		else
		{
			CurrentRenderer.setEnabled(this, Enabled);
		}
	}
	
	public void setDirectEnabled(boolean Enabled)
	{
		this.Enabled = Enabled;
	}
	
	public boolean isEnabled()
	{
		return Enabled;
	}
	
	public void setVisible(boolean Visible)
	{
		this.Visible = Visible;
	}
	
	public boolean isVisible()
	{
		return Visible;
	}
	
	public void setDirectFocusable(boolean CanBeFocused)
	{
		this.CanBeFocused = CanBeFocused;
	}
	
	public boolean setFocused(boolean Focused)
	{
		if(Enabled)
		{
			this.Focused = Focused;
			return true;
		}
		return false;
	}
	
	public boolean isFocused()
	{
		return Focused;
	}
	
	public int getWidth()
	{
		return SizeX;
	}
}