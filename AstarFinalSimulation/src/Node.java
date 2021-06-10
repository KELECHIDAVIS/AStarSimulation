import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Node 
{
	
	protected boolean obstacle= false, visited=false; // is the node and obstacle? ;  has it been visited already 
	protected int nodeSize = Panel.nodeS,  nodeBorder = 20; 
	protected double fGlobalGoal, fLocalGoal;//  Global is the distance to the goal +distance from parent (f) ; local is distance to goal if you took the alternative (g?)
	protected ArrayList<Node> neighbors = new ArrayList<Node>() ; // neighbors
	protected Node parent; 
	protected int x,y; 
	
	
	private Panel panel; 
	Node(Panel panel)
	{
		this.panel = panel; 
	}

	
	
	public void draw(Graphics g)
	{
		g.setColor(new Color(0,0,255,100));
		if(obstacle)
		{
			g.setColor(new Color(125,125,125,200));
		}
		
		if(this.visited)
		{
			g.setColor(new Color(0,0,200));
		}
		if(this == panel.start)
		{
			g.setColor(new Color(0,255,0,200)); 
		}
		if(this == panel.end)
		{
			g.setColor(new Color(255,0,0,200)); 
		}
		g.fillOval(x*nodeSize, y*nodeSize, nodeSize, nodeSize);
	}
	
	
}
