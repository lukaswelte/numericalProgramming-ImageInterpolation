import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;


abstract class Function {
    public String name;

    public Function(String name) {
        this.name = name;
    }

    public abstract double evaluate(double x);
}


/**
 * ****************************************************************************************************************
 *
 * @author braeckle
 *         <p/>
 *         *****************************************************************************************************************
 */

class PlotComponent extends JComponent {
    private static final long serialVersionUID = 1L;
    /* Top, bottom, left and right margin of the coordinate system in the plot. */
    final int PADDING = 20;
    final int numOfPoints = 1000;
    double[] xData = new double[numOfPoints];
    double[] fData = new double[numOfPoints];
    double[] pData = new double[numOfPoints];
    Function f;
    InterpolationMethod method;
    /* Sampling Points */
    double a;
    double b;
    int n; /* Anzahl an Intervallen */
    double minX = Double.MAX_VALUE;
    double minY = Double.MAX_VALUE;
    double maxX = Double.MIN_VALUE;
    double maxY = Double.MIN_VALUE;

    public PlotComponent() {
        super();
        setFunction(0);
        a = -5;
        b = 5;
        n = 4;
        setInterpolationMethod(2);
    }

    public void setFunction(int f_index) {
        switch (f_index) {
            case 0:
                f = new Function("sin(x)") {

                    @Override
                    public double evaluate(double x) {
                        return Math.sin(x);
                    }
                };
                break;
            case 1:
                f = new Function("exp(x)") {

                    @Override
                    public double evaluate(double x) {
                        return Math.exp(x);
                    }
                };
                break;
            case 2:
                f = new Function("1/(1+x^2)") {

                    @Override
                    public double evaluate(double x) {
                        return 1.0 / (1 + x * x);
                    }
                };
                break;
            default:
                f = new Function("0") {

                    @Override
                    public double evaluate(double x) {
                        return 0;
                    }
                };
                break;
        }
        repaint();
    }

    public void setInterpolationMethod(int methodindex) {
        switch (methodindex) {
            case 0:
                method = new NearestNeighbour();
                break;
            case 1:
                method = new LinearInterpolation();
                break;
            case 2:
                method = new NewtonPolynom();
                break;
            case 3:
                method = new CubicSpline();
                break;
            default:
                break;
        }
        repaint();
    }

    public void setSamplingPoints(int a, int b, int n) {
        this.a = a;
        this.b = b;
        this.n = n;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);


        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int height = getHeight();
        int width = getWidth();
        double x, y;

        double[] y_i = new double[n + 1];
        for (int i = 0; i < n + 1; i++) {
            y_i[i] = f.evaluate(a + i * 1.0 * (b - a) / n);
        }
        this.method.init(a, b, n, y_i);

        minX = a;
        maxX = b;
        for (int i = 0; i < numOfPoints; i++) {
            xData[i] = minX + i * (maxX - minX) / (numOfPoints - 1);
        }

        for (int i = 0; i < numOfPoints; i++) {
            fData[i] = f.evaluate(xData[i]);
            pData[i] = method.evaluate(xData[i]);
        }

        minY = Double.MAX_VALUE;
        maxY = Double.MIN_VALUE;

        for (int i = 0; i < numOfPoints; i++) {
            if (fData[i] < minY) {
                minY = fData[i];
            }
            if (fData[i] > maxY) {
                maxY = fData[i];
            }
        }

		/* draw y-axis */
        graphics.draw(new Line2D.Double(scaleX(0, width), height - PADDING,
                scaleX(0, width), PADDING));

        for (int i = -4; i < 5; i++) {
            if (i == 0)
                continue;
            double y_ = i * (maxY - minY) / 4.0;
            graphics.draw(new Line2D.Double(scaleX(0, width) - 5, scaleY(y_,
                    height), scaleX(0, width) + 5, scaleY(y_, height)));
            graphics.drawString("" + (((int) (y_ * 1000 + 0.5)) / 1000.0),
                    (int) scaleX(0, width) + 2, (int) scaleY(y_, height) - 2);
        }

		/* draw x-axis */
        graphics.draw(new Line2D.Double(PADDING, scaleY(0, height), width
                - PADDING, scaleY(0, height)));

        for (int i = -4; i < 5; i++) {
            if (i == 0)
                continue;
            double x_ = i * (b - a) / 4.0;
            graphics.draw(new Line2D.Double(scaleX(x_, width),
                    scaleY(0, height) - 5, scaleX(x_, width),
                    scaleY(0, height) + 5));
            graphics.drawString("" + (((int) (x_ * 1000 + 0.5)) / 1000.0),
                    (int) scaleX(x_, width) + 2, (int) scaleY(0, height) + 12);
        }

		/* draw "exact" solution */
        graphics.setPaint(Color.GREEN);

        for (int i = 0; i < xData.length; i++) {
            x = scaleX(xData[i], width);
            y = scaleY(fData[i], height);
            graphics.fill(new Ellipse2D.Double(x - 1.0, y - 1.0, 2.0, 2.0));
        }

		/* draw interpolation values */
        graphics.setPaint(Color.red);

        for (int i = 0; i < xData.length; i++) {
            x = scaleX(xData[i], width);
            y = scaleY(pData[i], height);
            graphics.fill(new Ellipse2D.Double(x - 1.0, y - 1.0, 2.0, 2.0));
        }

        graphics.setPaint(Color.blue);

		/* draw Sampling Points */
        for (int i = 0; i < n + 1; i++) {
            x = scaleX(a + i * 1.0 * (b - a) / n, width);
            y = scaleY(f.evaluate(a + i * 1.0 * (b - a) / n), height);
            graphics.fill(new Ellipse2D.Double(x - 3.0, y - 3.0, 6.0, 6.0));
        }

    }

    private double scaleX(double x, double width) {
        double scale = (width - 2 * PADDING) / (maxX - minX);
        double offset = PADDING - scale * minX;
        return scale * x + offset;
    }

    private double scaleY(double y, double height) {
        double scale = (height - 2 * PADDING) / (minY - maxY);
        double offset = PADDING - scale * maxY;
        return scale * y + offset;
    }
}


/**
 * ****************************************************************************************************************
 *
 * @author braeckle
 *         <p/>
 *         Diese Klasse bietet eine GUI fuer die Darstellung einer Interpolation.
 *         gruen : die Funktion f
 *         rot: Interpolant
 *         blau Stützpunkte
 *         <p/>
 *         *****************************************************************************************************************
 */
public class InterpolationsPlotter extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    JMenuBar mbar = new JMenuBar();
    /**
     * Combobox fuer die Interpolationsmethoden
     */
    String[] modeStrings = {"Nearest", "Linear", "Poly", "Cubic"};
    JComboBox interpolationmodeBox = new JComboBox(modeStrings);
    /**
     * Combobox fuer die Funktionen
     */
    String[] funcStrings = {"sin(x)", "exp(x)", "1/(1+x^2)"};
    JComboBox functionsBox = new JComboBox(funcStrings);
    /**
     * Textfelder fuer die Eingabe des Stuetzstellen
     */
    JTextField aField = new JTextField(-5 + "", 4);
    JTextField bField = new JTextField(5 + "", 4);
    JTextField nField = new JTextField(4 + "", 4);
    JLabel aLabel = new JLabel("a = ");
    JLabel bLabel = new JLabel("b = ");
    JLabel nLabel = new JLabel("n = ");
    private PlotComponent plotComponent = new PlotComponent();

    public InterpolationsPlotter() {
        super("Funktionenplotter");

        interpolationmodeBox.setSelectedIndex(2);
        functionsBox.setSelectedIndex(0);

        mbar.add(interpolationmodeBox);
        mbar.add(functionsBox);
        mbar.add(aLabel);
        mbar.add(aField);
        mbar.add(bLabel);
        mbar.add(bField);
        mbar.add(nLabel);
        mbar.add(nField);

        interpolationmodeBox.addActionListener(this);
        functionsBox.addActionListener(this);
        aField.addActionListener(this);
        bField.addActionListener(this);
        nField.addActionListener(this);

        setJMenuBar(mbar);
        add(plotComponent);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocation(0, 0);
    }

    public static void main(String[] args) {
        new InterpolationsPlotter().setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComponent source = (JComponent) (e.getSource());

        if (source == (JComponent) interpolationmodeBox) {
            plotComponent.setInterpolationMethod(interpolationmodeBox.getSelectedIndex());
        } else if (source == (JComponent) functionsBox) {
            plotComponent.setFunction(functionsBox.getSelectedIndex());
        } else if (source == (JComponent) aField || source == (JComponent) bField || source == (JComponent) nField) {
            plotComponent.setSamplingPoints(Integer.parseInt(aField.getText()), Integer.parseInt(bField.getText()), Integer.parseInt(nField.getText()));
        }
    }
}
