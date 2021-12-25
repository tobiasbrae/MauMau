import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Color;

public class Arrow extends RenderObject
{
	public static int UP = 1;
	public static int DOWN = 2;
	private int Direction;
	private MessageList MList;
	private boolean Visible;
	private Polygon Poly;
	
	public Arrow(int PosX, int PosY, int SizeX, int SizeY, int Direction, MessageList MList)
	{
		super(PosX, PosY, SizeX, SizeY);
		this.Direction = Direction;
		this.MList = MList;
		MList.addArrow(this, Direction);
		
		Poly = new Polygon();
		if(Direction == UP)
		{
			Poly.addPoint(PosX, PosY + SizeY);
			Poly.addPoint(PosX + (SizeX / 2), PosY);
			Poly.addPoint(PosX + SizeX, PosY + SizeY);
			Poly.addPoint(PosX, PosY + SizeY);
		}
		else if(Direction == DOWN)
		{
			Poly.addPoint(PosX, PosY);
			Poly.addPoint(PosX + (SizeX / 2), PosY + SizeY);
			Poly.addPoint(PosX + SizeX, PosY);
			Poly.addPoint(PosX, PosY);
		}
	}
	
	public void clicked()
	{
		if(Direction == UP)
		{
			MList.scrollUp();
		}
		else if(Direction == DOWN)
		{
			MList.scrollDown();
		}
	}
	
	public void paint(Graphics g)
	{
		if(isEnabled())
		{
			g.setColor(DrawingColor);
		}
		else
		{
			g.setColor(Color.gray);
		}
		g.fillPolygon(Poly);
	}
}