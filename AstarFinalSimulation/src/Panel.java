import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import Framework.GamePanel;

public class Panel extends GamePanel
{
	protected static int frameW = 800; 
	protected static int mapWidth=16;
	protected static int nodeS =(int)((frameW/mapWidth)*.9); 
	protected  static int mapHeight=16;
	protected boolean shift = false, ctrl = false; 
	protected Node start=null, end =null; 
	

	protected Node[] nodes  = new Node[mapHeight*mapWidth]; // grid for the nodes 
	Panel()
	{
		this.setBackground(Color.black);
		
		for(int x =0; x<mapWidth; x++)
		{
			for(int y =0; y<mapHeight; y++)
			{
				nodes[y*mapWidth+x] = new Node(this);
				nodes[y*mapWidth+x].x = x; 
				nodes[y*mapWidth+x].y= y; 
				nodes[y*mapWidth+x].obstacle = false; 
				nodes[y*mapWidth+x].parent = null; 
				nodes[y*mapWidth+x].visited = false; 
			}
		}
		// inits a start and end (can change the location) 
		
		start = nodes[0]; 
		
		end =  nodes[(mapHeight)*mapWidth-1]; 
		
		for(int x =0; x<mapWidth; x++)
		{
			for(int y =0; y<mapHeight; y++)
			{
				if(y>0)
				{
					nodes[y*mapWidth+x].neighbors.add(nodes[(y-1)*mapWidth+x]);  // the node above this one 
				}
				if(y<mapHeight-1)
				{
					nodes[y*mapWidth+x].neighbors.add(nodes[(y+1)*mapWidth+x]);  // the node below this one 
				}
				if(x>0)
				{
					nodes[y*mapWidth+x].neighbors.add(nodes[(y)*mapWidth+(x-1)]);  // the node to the left of this one 
				}
				if(x<mapWidth-1)
				{
					nodes[y*mapWidth+x].neighbors.add(nodes[(y)*mapWidth+(x+1)]);  // the node node to the right of  this one 
				}
				
				
				
				
			}
		}
		
		this.UPS = 60.0; //framerate maxes at 60 for me
		
		
		
		this.start(); // starts the  gameThread}
	}
	
	
	public void solveAStar()
	{
		// this reset the nodes before the algorithm starts;  
		for(int x =0; x<mapWidth; x++)
		{
			for(int y =0; y<mapHeight; y++)
			{
				nodes[y*mapWidth+x].fGlobalGoal = Double.POSITIVE_INFINITY;  
				nodes[y*mapWidth+x].fLocalGoal = Double.POSITIVE_INFINITY;      
				nodes[y*mapWidth+x].visited = false; 
				nodes[y*mapWidth+x].parent = null; 
				
			}
		}
		
		Node current = start ; 
		start.fLocalGoal = 0.0; 
		start.fGlobalGoal = heuristic(start, end) ;  // this is the estimated distance from the start to the end 
		
		// make a list of nodes that are still open to be tested  (openset )
		ArrayList<Node> notTested = new ArrayList<Node>(); 
		notTested.add(start); 
		
		//alogrithm 
		while(!notTested.isEmpty()&& current != end)   // the current != end is to saving searching time; it goes with the first path that it finds instead of it being the absolute most optimal path.
																			//it usually is the most optimal path though so but it you want to search everything (Dikjstra's Algorithm)  remove current != end		
		{
			//sort untested nodes by global goal,  so lowest first 
			
			Collections.sort(notTested, new Comparator<Node>() {

				@Override
				public int compare(Node o1, Node o2)
				{
					Integer o1f = (int) o1.fGlobalGoal; 
					Integer o2f = (int) o2.fGlobalGoal; 
					return o1f.compareTo(o2f);
				}
			}); 
			// now the list has the best choice at the front but if we already  visited it we have to remove it from the list 
			while(!notTested.isEmpty()&&notTested.get(0).visited)
			{
				notTested.remove(0); // remove front 
			}
			
			// abort if there are no nodes left 
			if(notTested.isEmpty())
			{
				break; 
			}
			
			current = notTested.get(0); 
			current.visited = true;  // sets to visited to make sure we only search each node once 
			
			//now check each of the node's neighbors 
			for(Node neighbor:current.neighbors)
			{
				// if it is not an obstacle and is hasn't been visited 
				if(!neighbor.visited&&!neighbor.obstacle)
				{
					notTested.add(neighbor); 
				}
				
				// Calculate the neighbors lowest parent distance 
				double fPossiblyLowerGoal = current.fLocalGoal + distance(current,neighbor); 
				
				if(fPossiblyLowerGoal<neighbor.fLocalGoal)
				{
					// This means that this was the actual lower goal and its going on the right path; the current then becomes its neighbor
					neighbor.parent = current; 
					neighbor.fLocalGoal = fPossiblyLowerGoal; 
					
					//you update the neighbor's score right here // At some point the algo might abandon this path then search for the best path 
					neighbor.fGlobalGoal =  neighbor.fLocalGoal + heuristic(neighbor, end); 
				}
			}
			
			
			
		}
		
		
		
	}
	public int distance(Node a, Node b)// if any rounding errors make sure this is a double 
	{
		double x = Math.pow(b.x-a.x, 2);
		double y = Math.pow(b.y-a.y, 2); 
		double distance = Math.sqrt(x+y); 
		return (int)distance; 
	}
	
	public int heuristic(Node a, Node b)
	{
		
		return distance(a, b); 
	}

	@Override
	public void update()
	{
		 
		
	}

	@Override
	public void paint(Graphics g)
	{
		super.paintComponent(g);// makes sure the background stays the same 
		
		
		//draw the nodes
		for(int x =0; x<mapWidth; x++)
		{
			for(int y =0; y<mapHeight; y++)
			{
				nodes[y*mapWidth+x].draw(g);; 
			}
		}
		
		// bold line 
		Graphics2D gd = (Graphics2D) g.create(); 
		gd.setColor(new Color(0,0,50));
		//
		gd.setStroke(new BasicStroke(4, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
		for(int x =0; x<mapWidth; x++) // draw the connections 
		{
			for(int y =0; y<mapHeight; y++)
			{
				for(Node n:nodes[y*mapWidth+x].neighbors)
				{
					gd.drawLine(x*nodeS+ nodeS/2, y*nodeS+ nodeS/2, n.x*nodeS+ nodeS/2, n.y*nodeS+ nodeS/2);
				}
			}
		}
		// draws path
		if(end!=null)
		{
			Node p = end; 
			while(p.parent!=null)
			{
				gd.setColor(new Color(255,255,0));
				gd.drawLine(p.x*nodeS+ nodeS/2, p.y*nodeS+ nodeS/2, p.parent.x*nodeS+ nodeS/2, p.parent.y*nodeS+ nodeS/2);
				//set next node to this node parent 
				p = p.parent; 
			}
		}
		
		g.dispose(); 
		gd.dispose(); 
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode()==KeyEvent.VK_SHIFT)
		{
			shift = true; 
		}
		if(e.getKeyCode()==KeyEvent.VK_CONTROL)
		{
			ctrl = true; 
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if(e.getKeyCode()==KeyEvent.VK_SHIFT)
		{
			shift = false; 
		}
		if(e.getKeyCode()==KeyEvent.VK_CONTROL)
		{
			ctrl = false; 
			
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		
		
		
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		
		int selectedx= e.getX()/nodeS; 
		int selectedy= e.getY()/nodeS; 
		
		if(selectedx>=0&&selectedx<mapWidth)
		{
			if(selectedy>=0&&selectedy<mapHeight)
			{
				if(shift)
				{
					start = nodes[selectedy*mapWidth+selectedx]; 

				}else if(ctrl)
				{
					end= nodes[selectedy*mapWidth+selectedx]; 

				}else
				{
					nodes[selectedy*mapWidth+selectedx].obstacle= !nodes[selectedy*mapWidth+selectedx].obstacle; 
				}
				
				solveAStar(); 
			}
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

}
