import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

/**
 * Diese Klasse verwaltet ein Bild im RGB-Format. Sie ermoeglicht das
 * Manipulieren und Skalieren des Bildes. Fuer die Bearbeitung dieser
 * Programmieraufgabe ist ein Verstaendnis dieser Klasse nicht erforderlich
 *
 * @author braeckle
 */
public class Picture {

    /**
     * die verschiedenen Interpolations-Modi
     */
    final static int NEAREST = 0; /* Auswahl des nächsten Nachbarn */
    final static int LINEAR = 1; /* lineare Interpolation */
    final static int POLY = 2; /* Interpolation mit Polynominterpolation */
    final static int CUBIC = 3; /* Interpolation mit kubischen Splines */

    /**
     * die verschiedenen Grundfarben des Farbraums RGB
     */
    final static int RED = 0;
    final static int GREEN = 1;
    final static int BLUE = 2;

    /**
     * unsere Membervariablen für das Bild
     */
    private BufferedImage img;

    /**
     * Die Breite und Hoehe des Bildes
     */
    private int width, height;

    /**
     * Konstruktor Lädt ein Bild mit dem Pfad filename
     */
    public Picture(String filename) throws IOException {
        File f = new File(filename);
        img = ImageIO.read(f);
        if (!isNull()) {
            width = img.getWidth();
            height = img.getHeight();
        }
    }

    /**
     * Konstruktor Initialisiert ein Bild mit Breite width und Hoehe height
     */
    public Picture(int width, int height) {
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.width = width;
        this.height = height;
    }

    /**
     * Erzeugt eine Kopie des Bildes
     */
    public Picture(BufferedImage img) {
        if (img != null) {
            ColorModel cm = img.getColorModel();
            boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
            WritableRaster raster = img.copyData(null);
            this.img = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
            width = img.getWidth();
            height = img.getHeight();
        }

    }

    /**
     * Gibt die Breite des Bildes zurueck
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gibt die Hoehe des Bildes zurueck
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gibt an, ob aktuell kein Bild verwaltet wird
     */
    public boolean isNull() {
        return (img == null);
    }

    /**
     * Gibt das Bild zurueck
     */
    public BufferedImage getImage() {
        return img;
    }

    /**
     * Speichert das Bild im Pfad filename im Format formatName
     */
    public void save(String filename, String formatName) throws IOException {
        File f = new File(filename);
        ImageIO.write(img, formatName, f);
    }

    /**
     * Liefert eine String-Ausgabe der einzelnen Farbkanaele des Bildes
     */
    public String toString() {
        String[] colors = {"Rot", "Gruen", "Blau"};
        String result = "";
        for (int c = 0; c < 3; c++) {
            result += "Die " + colors[c] + "-Werte des Bildes: \n";
            for (int x = 1; x <= width; x++) {
                for (int y = 1; y <= height; y++) {
                    result += Math.round(getPixel(x, y, c) * 1000) / 1000.0
                            + "\t";
                }
                result += "\n";
            }
            result += "\n";
        }
        return result;
    }

    /**
     * Gibt den Farbwert der Farbe color an der Stelle (x,y) im Bild zurueck.
     * Indizierung erfolgt in beiden Dimensionen von 1 bis width bzw. height.
     * color entspricht: 0 = rot, 1 = gruen, 2 = blau Der resultierende Farbwert
     * ist aus dem Intervall [0,1]
     */
    public double getPixel(int x, int y, int color) {
        x = x - 1;
        y = y - 1;

        int rgb = img.getRGB(x, y);
        Color col = new Color(rgb);
        double c;

        switch (color) {
            case 0:
                c = col.getRed();
                break;
            case 1:
                c = col.getGreen();
                break;
            case 2:
                c = col.getBlue();
                break;
            default:
                c = 0;
        }

        return c / 255.;
    }

    /**
     * Setzt an der Stelle (x,y) im Bild die Farbe bestehend aus dem RGB-Wert
     * (r,g,b). Indizierung erfolgt in beiden Dimensionen von 1 bis width bzw.
     * height. Ist ein Farbwert ausserhalb der Grenzen von [0,1], wird diese
     * Farbe an den entsprechenden Randwert angepasst.
     */
    public void setPixel(int x, int y, double r, double g, double b) {
        if (r < 0)
            r = 0;
        if (r > 1)
            r = 1;
        if (g < 0)
            g = 0;
        if (g > 1)
            g = 1;
        if (b < 0)
            b = 0;
        if (b > 1)
            b = 1;
        Color col = new Color((int) Math.round(r * 255.),
                (int) Math.round(g * 255.), (int) Math.round(b * 255.));
        img.setRGB(x - 1, y - 1, col.getRGB());
    }

    /**
     * Skaliert das Bild und passt die Breite zu newWidth und die Hoehe zu
     * newHeight an. Das bei der Skalierung verwendete Interpolationsverfahren
     * wird mit dem Parameter interpolationMode uebergeben: 0 = NearestNeighbor,
     * 1 = LinearInterpolation, 2 = Polynominterpolation, 3 = Kubische
     * Spline-Interpolation
     */
    public void scale(int newWidth, int newHeight, int interpolationMode) {

		/* Tatsaechlicher Skalierungsfaktor in x- und y-Richtung */
        double scale_x = (double) newWidth / width;
        double scale_y = (double) newHeight / height;

		/* Ermittlung des Interpolationsverfahren */
        InterpolationMethod interpolation;

        switch (interpolationMode) {
            case NEAREST:
                interpolation = new NearestNeighbour();
                break;
            case LINEAR:
                interpolation = new LinearInterpolation();
                break;
            case POLY:
                interpolation = new NewtonPolynom();
                break;
            case CUBIC:
                interpolation = new CubicSpline();
                break;
            default:
                return;
        }

        /*******************************************************/
        /**
         * Durch Festhalten der y-Werte wird fuer jede Zeile eine
         * Interpolationsfunktion aufgebaut und an den x-Werten der Pixel des
         * neuen Bildes ausgewertet. Das ganze muss fuer alle Grundfarben
         * unabhaengig voneinander geschehen.
         */

		/*
         * temporaeres Array fuer Zwischenspeicherung von Auswertungen nach dem
		 * Festhalten der y-Achse. Enthaelt die Farbwerte fuer alle drei
		 * Grundfarben an den Stellen mit den x-Koordinaten der neuen Pixel und
		 * den y-Koordinaten der Pixel im alten Bild.
		 */
        double tempImageArray[][][] = new double[newWidth][height][3];

		/* Aequidistante Intervall der Stuetzstellen relativ zum neuen Bild */
        double a = (1 - 0.5) * scale_x + 0.5;
        double b = (width - 0.5) * scale_x + 0.5;
        int n = width - 1;

		/* Fuer jede Zeile */
        for (int y = 1; y <= height; y++) {
			/* Fuer jeden Farbwert des RGB */
            for (int c = 0; c < 3; c++) {
				/* Stuetzwerte setzten */
                double[] dataPoints = new double[width];
                for (int x = 1; x <= width; x++) {
                    dataPoints[x - 1] = getPixel(x, y, c);
                }
				/* Interpolationsmethode initialisieren */
                interpolation.init(a, b, n, dataPoints);
				/* Interpolation auswerten an neuen Stellen */
                for (int x = 1; x <= newWidth; x++) {
                    tempImageArray[x - 1][y - 1][c] = interpolation.evaluate(x);
                }
            }
        }


        /*******************************************************/
        /**
         * In tempImageArray sind alle noetigen Werte in den Zeilen des alten
         * Bildes bekannt. Darauf basierend wird jetzt in jeder Spalte eine
         * Interpolationsfunktion aufgebaut und an den Pixelstellen des neuen
         * Bildes ausgewertet. Das ganze muss fuer alle Grundfarben unabhaengig
         * voneinander geschehen.
         */

		/*
		 * Das Array resultImageArray enthaelt alle Auswertungen fuer alle drei
		 * Grundfarben an den Pixelstellen des neuen Bildes
		 */
        double resultImageArray[][][] = new double[newWidth][newHeight][3];

		/* Aequidistante Intervall der Stuetzstellen im neuen Bild */
        a = (1 - 0.5) * scale_y + 0.5;
        b = (height - 0.5) * scale_y + 0.5;
        n = height - 1;

		/* Fuer jede Spalte */
        for (int x = 1; x <= newWidth; x++) {
			/* Fuer jeden Farbwert des RGB */
            for (int c = 0; c < 3; c++) {
				/* Stuetzwerte setzten */
                double[] dataPoints = new double[height];
                for (int y = 1; y <= height; y++) {
                    dataPoints[y - 1] = tempImageArray[x - 1][y - 1][c];
                }
				/* Interpolation initialisieren */
                interpolation.init(a, b, n, dataPoints);
				/* Interpolation auswerten an neuen Stellen */
                for (int y = 1; y <= newHeight; y++) {
                    resultImageArray[x - 1][y - 1][c] = interpolation
                            .evaluate(y);
                }
            }
        }

        /*******************************************************/
        /**
         * Jetzt wird mit allen Auswertungen aus resultImageArray das neue Bild
         * gesetzt
         */
        this.img = new BufferedImage(newWidth, newHeight,
                BufferedImage.TYPE_INT_ARGB);
        this.img.createGraphics();
        width = newWidth;
        height = newHeight;

        for (int x = 1; x <= newWidth; x++) {
            for (int y = 1; y <= newHeight; y++) {
                double red = resultImageArray[x - 1][y - 1][0];
                double green = resultImageArray[x - 1][y - 1][1];
                double blue = resultImageArray[x - 1][y - 1][2];

                this.setPixel(x, y, red, green, blue);
            }
        }

    }
}
