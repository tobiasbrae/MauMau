import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class UserInput implements KeyListener, MouseListener, MouseMotionListener
{
	private Window CurrentWindow;
	
	public UserInput(Window CurrentWindow)
	{
		this.CurrentWindow = CurrentWindow;
	}
	
	public void keyPressed(KeyEvent e)
	{
		CurrentWindow.keyPressed(e);
	}
	
	public void keyReleased(KeyEvent e)
	{
	
	}
	
	public void keyTyped(KeyEvent e)
	{
		CurrentWindow.keyTyped(e);
	}
	
	public void mouseClicked(MouseEvent e)
	{
		CurrentWindow.mouseClicked(e);
	}
	
	public void mouseEntered(MouseEvent e)
	{
	
	}
	
	public void mouseExited(MouseEvent e)
	{
	
	}
	
	public void mousePressed(MouseEvent e)
	{
	
	}
	
	public void mouseReleased(MouseEvent e)
	{
	
	}
	
	public void mouseDragged(MouseEvent e)
	{
	
	}
	
	public void mouseMoved(MouseEvent e)
	{
		CurrentWindow.CurrentRenderer.mouseMoved(e.getX(), e.getY());
	}
}