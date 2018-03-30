import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;
import org.apache.batik.dom.GenericDOMImplementation;
import org.w3c.dom.Document;
import org.apache.batik.anim.dom.SVG12DOMImplementation;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.apache.batik.swing.JSVGCanvas;
public class Canvas extends JComponent {
//    private int columnCount = 5;
//        private int rowCount = 5;
//        private List<Rectangle> cells;
//        private Point selectedCell;
//        private List<Point> fillCells;

    
    
     EntityManagerFactory emf = Persistence.createEntityManagerFactory("SwingPaint2PU");
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    
    
    
    
    
    
    
	private int X1, Y1, X2, Y2;
        int x, y, width,height;
	private Graphics2D g;
	private Image img, background, undoTemp, redoTemp;
	ArrayList<Shape> shapes = new ArrayList<Shape>();
	private final SizedStack<Image> undoStack = new SizedStack<>(12);
	private final SizedStack<Image> redoStack = new SizedStack<>(12);
	private Rectangle shape;
	private Point startPoint;
	private MouseMotionListener motion;
	private MouseListener listener;
        String svg;
        Coordinaat cd ;
        List<Coordinaat> coordinaten;
         List<String> lst ;
       //List<String> svg = new ArrayList<>();
	public void save(File file) {
		try {
			ImageIO.write((RenderedImage) img, "PNG", file);
//                        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
//                        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
//                        Document doc = impl.createDocument(svgNS, "svg", null);
//                        Element svgRoot = doc.getDocumentElement();
//                        svgRoot.setAttributeNS(null, "width", "400");
//                        svgRoot.setAttributeNS(null, "height", "450");
		} catch (IOException ex) {
		}
	}

	public void load(File file) {
		try {
			img = ImageIO.read(file);
			g = (Graphics2D) img.getGraphics();
		}

		catch (IOException ex) {
		}
	}

	protected void paintComponent(Graphics g1) {
            
            
            
		if (img == null) {
			img = createImage(getSize().width, getSize().height);
			g = (Graphics2D) img.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			clear();
			
		}
                   
                
            int xZ = 1;
            int yZ = 1;
            int size = 30;

            for (int i = 0; i < 100; i++) {
                for (int j = 0; j < 100; j++) {
                    g.drawRect(xZ, yZ, size, size);
                    yZ += size;
                }
                xZ += size;
                yZ = 1;
            }        
            
            
                
                
                
                
                
                
		g1.drawImage(img, 0, 0, null);
		
             
                
		if (shape != null) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.draw(shape);
                        
           
		}
	}

	public Canvas() {
		setBackground(Color.WHITE);
             cd = new Coordinaat();
            lst = new ArrayList();
		defaultListener();
                getData();
	}
    
        

	public void defaultListener() {
		setDoubleBuffered(false);
		listener = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				saveToStack(img);
				X2 = e.getX();
				Y2 = e.getY();
			}
		};

		motion = new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				X1 = e.getX();
				Y1 = e.getY();

				if (g != null) {
					g.drawLine(X2, Y2, X1, Y1);
					repaint();
					X2 = X1;
					Y2 = Y1;
				}
			}
		};
		addMouseListener(listener);
		addMouseMotionListener(motion);
	}

	public void addRectangle(Rectangle rectangle, Color color) {

		Graphics2D g2d = (Graphics2D) img.getGraphics();
		g2d.setColor(color);
		g2d.draw(rectangle);
                
                
                
                //g2d.fillRect(X1, Y1,getSize().width, getSize().height);
		repaint();
	}

	public void red() {
		g.setPaint(Color.red);
	}

	public void black() {
		g.setPaint(Color.black);
	}

	public void magenta() {
		g.setPaint(Color.magenta);
	}

	public void green() {
		g.setPaint(Color.green);
	}

	public void blue() {
		g.setPaint(Color.blue);
	}

	public void gray() {
		g.setPaint(Color.GRAY);
	}

	public void orange() {
		g.setPaint(Color.ORANGE);
	}

	public void yellow() {
		g.setPaint(Color.YELLOW);
	}

	public void pink() {
		g.setPaint(Color.PINK);
               
	}

	public void cyan() {
		g.setPaint(Color.CYAN);
	}

	public void lightGray() {
		g.setPaint(Color.lightGray);
	}

	public void picker(Color color) {
		g.setPaint(color);
	}

	public void clear() {
		if (background != null) {
			setImage(copyImage(background));
		} else {
			g.setPaint(Color.white);
			g.fillRect(0, 0, getSize().width, getSize().height);
			g.setPaint(Color.black);
		}
		repaint();
	}

	public void undo() {
		if (undoStack.size() > 0) {
			undoTemp = undoStack.pop();
			redoStack.push(img);
			setImage(undoTemp);
		}
	}

	public void redo() {
		if (redoStack.size() > 0) {
			redoTemp = redoStack.pop();
			undoStack.push(img);
			setImage(redoTemp);
		}
	}

	public void pencil() {
		removeMouseListener(listener);
		removeMouseMotionListener(motion);
		//defaultListener();
		
	}

	public void rect() {
		removeMouseListener(listener);
		removeMouseMotionListener(motion);
		MyMouseListener ml = new MyMouseListener();
		addMouseListener(ml);
		addMouseMotionListener(ml);
                
	}

	private void setImage(Image img) {
		g = (Graphics2D) img.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setPaint(Color.black);
		this.img = img;
		repaint();
	}

	public void setBackground(Image img) {
		background = copyImage(img);
		setImage(copyImage(img));
	}

	private BufferedImage copyImage(Image img) {
		BufferedImage copyOfImage = new BufferedImage(getSize().width,
				getSize().height, BufferedImage.TYPE_INT_RGB);
		Graphics g = copyOfImage.createGraphics();
		g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
		return copyOfImage;
	}

	private void saveToStack(Image img) {
		undoStack.push(copyImage(img));
	}

	public void setThickness(int thick) {
		g.setStroke(new BasicStroke(thick));
	}
        
        
//         public void setStartPoint(int x, int y) {
//            this.x = x;
//            this.y = y;
//        }
//
//        public void setEndPoint(int x, int y) {
//            height = (x);
//            width = (y);
//        }
        
        

	class MyMouseListener extends MouseInputAdapter
	{
		//private Point startPoint;

		public void mousePressed(MouseEvent e)
		{
			startPoint = e.getPoint();
			shape = new Rectangle();
                        
		}

		public void mouseDragged(MouseEvent e)
		{
                    
                    
                   x =  Math.min(startPoint.x, e.getX());
			 
                   y =       Math.min(startPoint.y, e.getY());
			 
                   width =       Math.abs(startPoint.x - e.getX());
			 
                   height =      Math.abs(startPoint.y - e.getY());
			

                    shape.setBounds(x, y, width, height);
                        
                 
                    Graphics2D g2d = (Graphics2D) img.getGraphics();
                  //  svg = 
                  
                    
                    
                    g2d.fillRect( x,  y, width, height);
                    g2d.setColor(Color.BLUE);
			repaint();
                     
                       
                        
                        
		}

		public void mouseReleased(MouseEvent e)
		{
			if (shape.width != 0 || shape.height != 0)
			{
				addRectangle(shape, e.getComponent().getForeground());
                              //  coordinaten = new ArrayList<>();
                               // coordinaten.add(cd);
                               
			}

			shape = null;
                        repaint();
                         opslaan();  
		}
	}
public void opslaan(){
    if (!em.isOpen()) {
            em = emf.createEntityManager();
        }
    cd.setX1( x);
    
    cd.setY1 ( y );
    
    cd.setWidth1( width);
    
    cd.setHeight1( height);
      em.getTransaction().begin();
                        
                        try {
                // em.detach(stage);
                em.merge(cd);
                em.getTransaction().commit();
                
               
            } catch (Exception ex) {
                em.getTransaction().rollback();
            } finally {
                em.close();
            } 
}
   public void getData(){
       if (!em.isOpen()) {
            em = emf.createEntityManager();
        }
     
       coordinaten = new ArrayList<>();
      List<Coordinaat> coordinaten =  (List<Coordinaat>) em.createQuery("SELECT t FROM Coordinaat t ").getResultList();
     
      
         for (Coordinaat cd:coordinaten) {
            
             svg =   "<svg width='1500'"+" height='1500' >"+
                     "< rect width='"+cd.getWidth1()+"'"+" height='"+ cd.getHeight1() + " x='"+cd.getX1()+"'" + "y='"+ cd.getY1()+"'" + "/>"+
                     "</svg>";
             
            
             lst.add(svg);
         }
       
      
   }

//       public int getX1() {
//        return x;
//    }
//
//    public int getY1() {
//        return y;
//    }
//
//    public int getWidth1() {
//        return width;
//    }
//
//    public int getHeight1() {
//        return height;
//    }

//    public List<String> getSvg() {
//        return svg;
//    }

//    public String getSvg() {
//        return svg;
//    }

    public List<String> getLst() {
        return lst;
    }

}

	
