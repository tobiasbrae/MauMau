import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Card extends RenderObject
{
	public Card Before, After;
	private Image Front, Back;
	private int ID;
	private static int Steps = 100;
	private int Step = 100;
	private int TargetPosX, TargetPosY;
	private double CurPosX, CurPosY, StepX, StepY;
	
	public Card(int PosX, int PosY, int SizeX, int SizeY, int ID)
	{
		super(PosX, PosY, SizeX, SizeY);
		this.ID = ID;
		setVisible(false);
	
		ImageIcon Ico = new ImageIcon(Main.class.getResource("textures/" + ID + ".png"));
		Front = Ico.getImage();
		Ico = new ImageIcon(Main.class.getResource("textures/back.png"));
		Back = Ico.getImage();
	}
	
	public int getID()
	{
		return ID;
	}
	
	public void setPos(int TargetPosX, int TargetPosY)
	{
		this.TargetPosX = TargetPosX;
		this.TargetPosY = TargetPosY;
		CurPosX = (double) PosX;
		CurPosY = (double) PosY;
		StepX = (TargetPosX - CurPosX) / Steps;
		StepY = (TargetPosY - CurPosY) / Steps;
		Step = 0;
		if(CurrentRenderer != null)
		{
			CurrentRenderer.removeRenderObject(this);
			CurrentRenderer.addRenderObject(this);
		}
	}
	
	public void paint(Graphics g)
	{
		if((Step + 1) == Steps)
		{
			PosX = TargetPosX;
			PosY = TargetPosY;
			Step++;
			CurrentRenderer.mouseMoved();
		}
		else if(Step != Steps)
		{
			CurPosX += StepX;
			CurPosY += StepY;
			PosX = (int) CurPosX;
			PosY = (int) CurPosY;
			Step++;
			CurrentRenderer.mouseMoved();
		}
		
		if(isFocused())
		{
			g.drawRect(PosX - 2, PosY - 2, SizeX + 3, SizeY + 3);
		}
		
		if(isVisible())
		{
			g.drawImage(Front, PosX, PosY, null);
		}
		else
		{
			g.drawImage(Back, PosX, PosY, null);
		}
	}
}