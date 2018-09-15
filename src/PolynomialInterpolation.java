
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class PolynomialInterpolation extends JFrame {

    private final JComboBox algorithmComboBox = new JComboBox(new String[]{"Aitkena", "Newtona", "Hermite"});
    private final JComboBox degreeComboBox = new JComboBox(new String[]{"1", "2", "3", "4"});
    private final JButton resetButton = new JButton("Reset");
    private boolean dragging = false; 
    private Point.Double chosen = null; 

    private final JFrame parent = this;
    private final List<Point.Double> points = new ArrayList<>();
    private JPanel buttonPanel = null;
    private final InteractivePanel interactivePanel = new InteractivePanel();
    
    private final Map<Point.Double, Double> tangent = new HashMap<>();

    //Klasa rysująca wielomian
    private class InteractivePanel extends JPanel implements MouseListener, MouseMotionListener {

        public InteractivePanel() {
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        //Funkcja rysująca
        @Override
        public void paintComponent(Graphics g) {
            
            Graphics2D g2 = (Graphics2D) g;
            
            
            g.setColor(Color.white);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(new Color(240, 240, 240));
            g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
            g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);

            double tickWidth = getWidth() / 8;
            double tickHeight = getHeight() / 8;

            g.setColor(Color.darkGray);

            for (int i = 1; i < 8; i++) {
                g.drawLine((int) (i * tickWidth), getHeight() - 10, (int) (i * tickWidth), getHeight());
                g.drawString(String.format("%d", -40 + i * 10), (int) (i * tickWidth) - 5, getHeight() - 15);
            }

            for (int i = 1; i < 8; i++) {
                g.drawLine(0, (int) (i * tickHeight), 10, (int) (i * tickHeight));
                g.drawString(String.format("%d", -(-40 + i * 10)), 12, (int) (i * tickHeight) + 5);
            }

            points.sort((Point2D.Double p1, Point2D.Double p2) -> Double.compare(p1.x, p2.x));

            if (points.size() < 5) {

               //Rysowanie punktu
                g2.setStroke(new BasicStroke(2));
                int radius = 16;
                g.setColor(Color.blue);
                points.forEach(p -> {

                    double x = scale(p.x, 0, getWidth(), -40, 40);
                    double y = getHeight() - scale(p.y, 0, getHeight(), -40, 40);
                    g.drawOval((int) (x - radius / 2), (int) (y - radius / 2), radius, radius);
                });
                g2.setStroke(new BasicStroke(1));
                return;
            }

            List<CopyOnWriteArrayList<Point.Double>> pointGroups = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                int start = (int) (0.25 * i * points.size());
                int end = (int) (0.25 * (i + 1) * points.size());
                if (i < 3) {
                    end++;
                }
                pointGroups.add(new CopyOnWriteArrayList<>(points.subList(start, end)));
            }
            
            if (!(algorithmComboBox.getSelectedIndex() == 2 && degreeComboBox.getSelectedIndex() == 3)) {
                for (int i = 0; i < 4; i++) {
                    while (pointGroups.get(i).size() > degreeComboBox.getSelectedIndex() + 2) {
                        int index = pointGroups.get(i).size() - 2;
                        Point.Double p = pointGroups.get(i).get(index);
                        pointGroups.get(i).remove(p);
                        points.remove(p);

                    }
                }
            }

            
            g2.setStroke(new BasicStroke(2));
            int radius = 16;
            g.setColor(Color.blue);
            points.forEach(p -> {

                double x = scale(p.x, 0, getWidth(), -40, 40);
                double y = getHeight() - scale(p.y, 0, getHeight(), -40, 40);
                g.drawOval((int) (x - radius / 2), (int) (y - radius / 2), radius, radius);
            });
                            
            g2.setStroke(new BasicStroke(1));

            int index = 0;
            for (List<Point.Double> pointGroup : pointGroups) {

                g.setColor(new Color(64, 192, 64));

                Point.Double firstPoint = pointGroup.get(0);
                Point.Double lastPoint = pointGroup.get(pointGroup.size() - 1);
                if (index > 0) {
                    double x = scale(firstPoint.x, 0, getWidth(), -40, 40);
                    double y = getHeight() - scale(firstPoint.y, 0, getHeight(), -40, 40);
                    g.drawLine((int) x, 0, (int) x, getHeight());
                }

                double[] X = new double[pointGroup.size()];
                double[] Y = new double[pointGroup.size()];

                for (int i = 0; i < pointGroup.size(); i++) {
                    X[i] = pointGroup.get(i).x;
                    Y[i] = pointGroup.get(i).y;
                }
                
                double[] tangentValues = null;

                //Wybór odpowiedniego algorytmu
                Algorithm algorithm;
                switch (algorithmComboBox.getSelectedIndex()) {
                    case 0:
                        algorithm = new Aitken(X, Y);
                        break;
                    case 1:
                        algorithm = new Newton(X, Y);
                        break;
                    default:

                        int degree = degreeComboBox.getSelectedIndex() + 1;

                        if (degree != 3) {
                            algorithm = new Hermite(X, Y);
                        } else {
                            double[] m = new double[X.length];

                            for (int i = 0; i < m.length; i++) {
                                m[i] = tangent.get(pointGroup.get(i));
                            }

                            algorithm = HermiteSpline.create(X, Y, m);
                            tangentValues = ((HermiteSpline) algorithm).getTangent();
                        }
                        break;
                }

                g.setColor(Color.black);

                Point prev = null;

                double start = firstPoint.x;
                double end = lastPoint.x;

                if (index == 0) {
                    start = -40;
                }
                if (index == 3) {
                    end = 40;
                }

                for (double x = start; x <= end; x += 0.1) {

                    int degree = Math.min(pointGroup.size(), degreeComboBox.getSelectedIndex() + 2);
                    double r = algorithm.valueAt(x, degree);
                    double px = scale(x, 0, getWidth(), -40, 40);
                    double py = getHeight() - scale(r, 0, getHeight(), -40, 40);
                    if (prev != null) {
                        g.drawLine(prev.x, prev.y, (int) px, (int) py);
                    }
                    prev = new Point((int) px, (int) py);
                }
                
                g2.setStroke(new BasicStroke(2));
                
                if (tangentValues != null) {
                    
                    for (int i = 0; i < tangentValues.length; i++) {
                        
                        double px = scale(pointGroup.get(i).x, 0, getWidth(), -40, 40);
                        double py = getHeight() - scale(pointGroup.get(i).y, 0, getHeight(), -40, 40);
                        
                        double angle = tangentValues[i] + Math.PI / 2;
                        double startX = px - 40 * Math.sin(angle);
                        double startY = py - 40 * Math.cos(angle);
                        double endX   = px + 40 * Math.sin(angle);
                        double endY   = py + 40 * Math.cos(angle);
                         
                        g.drawLine((int)startX, (int)startY, (int)endX, (int)endY);
                        g.fillRect((int)endX - 3, (int)endY - 3, 6, 6);
                    }
                }
                
                g2.setStroke(new BasicStroke(1));

                index++;
            }
        }

        @Override
        public void mouseClicked(MouseEvent me) {
        }

       //Obsługa naciśnięcia myszy
        @Override
        public void mousePressed(MouseEvent me) {

            double x = scale(me.getX(), -40, 40, 0, getWidth());
            double y = scale(getHeight() - me.getY(), -40, 40, 0, getHeight());

            chosen = null;

            for (Point.Double p : points) {
                if (Math.sqrt((x - p.x) * (x - p.x) + (y - p.y) * (y - p.y)) < 0.8) {
                    chosen = p;
                    break;
                }
            }

            if (!dragging && chosen == null) {

                Point.Double p = new Point.Double(x, y);
                if (points.size() < 17) {
                    points.add(p);
                    tangent.put(p, Math.random() * 2.0 * Math.PI);             
                    parent.repaint();
                }
                dragging = false;
            }
        }

        //Dodawanie punktu po puszczeniu przycisku
        @Override
        public void mouseReleased(MouseEvent me) {

            double x = scale(me.getX(), -40, 40, 0, getWidth());
            double y = scale(getHeight() - me.getY(), -40, 40, 0, getHeight());

            if (!dragging && SwingUtilities.isLeftMouseButton(me) && chosen != null) {
                Point.Double p = new Point.Double(x, y);
                points.remove(chosen);
                points.add(0, p);
                
                tangent.remove(chosen);
                tangent.put(p, Math.random() * 2.0 * Math.PI);
                
                parent.repaint();
            }

            dragging = false;
        }

        @Override
        public void mouseEntered(MouseEvent me) {
        }

        @Override
        public void mouseExited(MouseEvent me) {
        }

        //Obsługa przesuwania myszy
        @Override
        public void mouseDragged(MouseEvent me) {
            if (!dragging && SwingUtilities.isLeftMouseButton(me)) {
                dragging = true;
            }

            double x = scale(me.getX(), -40, 40, 0, getWidth());
            double y = scale(getHeight() - me.getY(), -40, 40, 0, getHeight());

            if (SwingUtilities.isRightMouseButton(me) && chosen != null) {
                         
                double newTangentValue = tangent.get(chosen) + (y > chosen.y ? 0.02 : -0.02);
                
                if (newTangentValue > 2 * Math.PI) newTangentValue -= 2 * Math.PI;
                if (newTangentValue < 0) newTangentValue += 2 * Math.PI;
                
                tangent.put(chosen, newTangentValue);            
                parent.repaint();
            }          
            else if (dragging && chosen != null) {
               
                Point.Double p = new Point.Double(x, y);
                points.remove(chosen);
                chosen = p;
                points.add(0, p);
                tangent.remove(chosen);
                tangent.put(p, Math.random() * 2.0 * Math.PI);
                parent.repaint();
            }

            dragging = false;
        }

        @Override
        public void mouseMoved(MouseEvent me) {
        }
    }

    public PolynomialInterpolation() {

        setTitle("Wielomiany interpolacyjne");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        degreeComboBox.setSelectedIndex(3);

        buttonPanel = new JPanel(new GridLayout(1, 5));

        add(buttonPanel, BorderLayout.NORTH);
        add(interactivePanel, BorderLayout.CENTER);

        algorithmComboBox.addActionListener(e -> {
            repaint();
        });

        resetButton.addActionListener(e -> {
            points.clear();
            repaint();
        });

        degreeComboBox.addActionListener(e -> {
            repaint();
        });

        buttonPanel.add(new JLabel("Algorytm interpolacji"));
        buttonPanel.add(algorithmComboBox);
        buttonPanel.add(new JLabel("Stopień wielomianu"));
        buttonPanel.add(degreeComboBox);
        buttonPanel.add(resetButton);

        setSize(800, 620);
        repaint();
    }

    //Skalowanie wykresu do wymiaru okna
    private double scale(double x, double minAllowed, double maxAllowed, double min, double max) {
        return (maxAllowed - minAllowed) * (x - min) / (max - min) + minAllowed;
    }

    public static void main(String[] args) {
        new PolynomialInterpolation();
    }
}
