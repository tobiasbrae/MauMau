import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;

public class Label extends RenderObject
{
	public Label Before, After;
	private String Text;
	
	public Label(int PosX, int PosY, int Size, String Text)
	{
		super(PosX, PosY, Size, 0);
		this.Text = Text;
	}
	
	public Label(int PosX, int PosY, int Size, String Text, Color Col)
	{
		super(PosX, PosY, Size, 0, Col);
		this.Text = Text;
	}
	
	public void paint(Graphics g)
	{
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, SizeX));
		g.setColor(DrawingColor);
		g.drawString(Text, PosX, PosY);
	}
}