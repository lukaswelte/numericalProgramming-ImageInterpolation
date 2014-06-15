import java.util.Arrays;

/**
 * Die Klasse Newton-Polynom beschreibt die Newton-Interpolation. Die Klasse
 * bietet Methoden zur Erstellung und Auswertung eines Newton-Polynoms, welches
 * uebergebene Stuetzpunkte interpoliert.
 *
 * @author braeckle
 */
public class NewtonPolynom implements InterpolationMethod {

    /**
     * Stuetzstellen xi
     */
    double[] x;

    /**
     * Koeffizienten/Gewichte des Newton Polynoms p(x) = a0 + a1*(x-x0) +
     * a2*(x-x0)*(x-x1)+...
     */
    double[] a;

    /**
     * die Diagonalen des Dreiecksschemas. Diese dividierten Differenzen werden
     * fuer die Erweiterung der Stuetzstellen benoetigt.
     */
    double[] f;

    /**
     * leerer Konstruktore
     */
    public NewtonPolynom() {
    }

    ;

    /**
     * Konstruktor
     *
     * @param x Stuetzstellen
     * @param y Stuetzwerte
     */
    public NewtonPolynom(double[] x, double[] y) {
        this.init(x, y);
    }

    /**
     * {@inheritDoc} Zusaetzlich werden die Koeffizienten fuer das
     * Newton-Polynom berechnet.
     */
    @Override
    public void init(double a, double b, int n, double[] y) {
        x = new double[n + 1];
        double h = (b - a) / n;

        for (int i = 0; i < n + 1; i++) {
            x[i] = a + i * h;
        }
        computeCoefficients(y);
    }

    /**
     * Initialisierung der Newtoninterpolation mit beliebigen Stuetzstellen. Die
     * Faelle "x und y sind unterschiedlich lang" oder "eines der beiden Arrays
     * ist leer" werden nicht beachtet.
     *
     * @param x Stuetzstellen
     * @param y Stuetzwerte
     */
    public void init(double[] x, double[] y) {
        this.x = Arrays.copyOf(x, x.length);
        computeCoefficients(y);
    }

    /**
     * computeCoefficients belegt die Membervariablen a und f. Sie berechnet zu
     * uebergebenen Stuetzwerten y, mit Hilfe des Dreiecksschemas der
     * Newtoninterpolation, die Koeffizienten a_i des Newton-Polynoms. Die
     * Berechnung des Dreiecksschemas soll dabei lokal in nur einem Array der
     * Laenge n erfolgen (z.B. spaltenweise Berechnung). Am Ende steht die
     * Diagonale des Dreiecksschemas in der Membervariable f, also f[0],f[1],
     * ...,f[n] = [x0...x_n]f,[x1...x_n]f,...,[x_n]f. Diese koennen spaeter bei
     * der Erweiterung der Stuetzstellen verwendet werden.
     * <p/>
     * Es gilt immer: x und y sind gleich lang.
     */
    private void computeCoefficients(double[] y) {
        int yLength = y.length;
        a = new double[yLength];
        f = new double[yLength];

        System.arraycopy(y, 0, f, 0, yLength);

        for (int n = 1; n < yLength; n++) {
            a[n - 1] = f[0];
            for (int m = 0; m <= yLength - n - 1; m++) {
                f[m] = (f[m + 1] - f[m]) / (x[m + n] - x[m]);
            }
        }

        a[yLength - 1] = f[0];
    }

    /**
     * Gibt die Koeffizienten des Newton-Polynoms a zurueck
     */
    public double[] getCoefficients() {
        return a;
    }

    /**
     * Gibt die Dividierten Differenzen der Diagonalen des Dreiecksschemas f
     * zurueck
     */
    public double[] getDividedDifferences() {
        return f;
    }

    /**
     * addSamplintPoint fuegt einen weiteren Stuetzpunkt (x_new, y_new) zu x
     * hinzu. Daher werden die Membervariablen x, a und f vergoessert und
     * aktualisiert . Das gesamte Dreiecksschema muss dazu nicht neu aufgebaut
     * werden, da man den neuen Punkt unten anhaengen und das alte
     * Dreiecksschema erweitern kann. Fuer diese Erweiterungen ist nur die
     * Kenntnis der Stuetzstellen und der Diagonalen des Schemas, bzw. der
     * Koeffizienten noetig. Ist x_new schon als Stuetzstelle vorhanden, werden
     * die Stuetzstellen nicht erweitert.
     *
     * @param x_new neue Stuetzstelle
     * @param y_new neuer Stuetzwert
     */
    public void addSamplingPoint(double x_new, double y_new) {
        int xLength = x.length;
        int arrayLength = xLength + 1;
        double[] b = new double[arrayLength];
        double[] g = new double[arrayLength];
        double[] z = new double[arrayLength];
        for (int i = 0; i < xLength; i++) {
            b[i] = a[i];
            g[i] = f[i];
            z[i] = x[i];
        }
        z[z.length - 1] = x_new;
        x = z;
        a = b;
        f = g;
        f[f.length - 1] = y_new;

        for (int m = xLength - 2; m >= 0; m--) {
            f[m] = (f[m + 1] - f[m]) / (x[m + (xLength - 1 - m)] - x[m]);
        }

        a[xLength - 1] = f[0];
    }

    /**
     * {@inheritDoc} Das Newton-Polynom soll effizient mit einer Vorgehensweise
     * aehnlich dem Horner-Schema ausgewertet werden. Es wird davon ausgegangen,
     * dass die Stuetzstellen nicht leer sind.
     */
    @Override
    public double evaluate(double z) {
        int xLength = x.length;
        double res = a[xLength - 1];
        for (int i = xLength - 2; i >= 0; i--) {
            res *= (z - x[i]) + a[i];
        }
        return res;
    }
}
