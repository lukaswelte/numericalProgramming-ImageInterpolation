import java.util.Arrays;

/**
 * Die Klasse CubicSpline bietet eine Implementierung der kubischen Splines. Sie
 * dient uns zur effizienten Interpolation von aequidistanten Stuetzpunkten.
 *
 * @author braeckle
 */
public class CubicSpline implements InterpolationMethod {

    /**
     * linke und rechte Intervallgrenze x[0] bzw. x[n]
     */
    double a, b;

    /**
     * Anzahl an Intervallen
     */
    int n;

    /**
     * Intervallbreite
     */
    double h;

    /**
     * Stuetzwerte an den aequidistanten Stuetzstellen
     */
    double[] y;

    /**
     * zu berechnende Ableitunge an den Stuetzstellen
     */
    double yprime[];

    /**
     * {@inheritDoc} Zusaetzlich werden die Ableitungen der stueckweisen
     * Polynome an den Stuetzstellen berechnet. Als Randbedingungen setzten wir
     * die Ableitungen an den Stellen x[0] und x[n] = 0.
     */
    @Override
    public void init(double a, double b, int n, double[] y) {
        this.a = a;
        this.b = b;
        this.n = n;
        h = (b - a) / (n);

        this.y = Arrays.copyOf(y, n + 1);

		/* Randbedingungen setzten */
        yprime = new double[n + 1];
        yprime[0] = 0;
        yprime[n] = 0;

		/* Ableitungen berechnen. Nur noetig, wenn n > 1 */
        if (n > 1) {
            computeDerivatives();
        }
    }

    /**
     * getDerivatives gibt die Ableitungen yprime zurueck
     */
    public double[] getDerivatives() {
        return yprime;
    }

    /**
     * Setzt die Ableitungen an den Raendern x[0] und x[n] neu auf yprime0 bzw.
     * yprimen. Anschliessend werden alle Ableitungen aktualisiert.
     */
    public void setBoundaryConditions(double yprime0, double yprimen) {
        yprime[0] = yprime0;
        yprime[n] = yprimen;
        if (n > 1) {
            computeDerivatives();
        }
    }

    /**
     * Berechnet die Ableitungen der stueckweisen kubischen Polynome an den
     * einzelnen Stuetzstellen. Dazu wird ein lineares System Ax=c mit einer
     * Tridiagonalen Matrix A und der rechten Seite c aufgebaut und geloest.
     * Anschliessend sind die berechneten Ableitungen y1' bis yn-1' in der
     * Membervariable yprime gespeichert.
     * <p/>
     * Zum Zeitpunkt des Aufrufs stehen die Randbedingungen in yprime[0] und yprime[n].
     * Speziell bei den "kleinen" Faellen mit Intervallzahlen n = 2
     * oder 3 muss auf die Struktur des Gleichungssystems geachtet werden. Der
     * Fall n = 1 wird hier nicht beachtet, da dann keine weiteren Ableitungen
     * berechnet werden muessen.
     */
    public void computeDerivatives() {
        int n = yprime.length - 1;
        int len = n + 1;
        double right[] = new double[len];
        double a[] = new double[len];
        double b[] = new double[len];

        for (int i = 2; i <= n - 2; i++) {
            right[i] = ((y[i + 1] - y[i - 1]) * 3) / h;
        }
        right[1] = ((y[2] - y[0] - ((h / 3) * yprime[0])) * 3) / h;
        right[n - 1] = ((y[n] - y[n - 2] - ((h / 3) * yprime[n])) * 3) / h;

        //trivial zur Berechnung von yprime[1]:
        a[0] = 0;
        b[0] = 1;

        a[1] = right[1];
        b[1] = -4;

        for (int i = 2; i <= n - 2; i++) {
            a[i] = right[i] - 4 * a[i - 1];
            b[i] = -1 - 4 * b[i - 1];
        }

        a[n - 1] = (right[n - 1] - a[n - 3]) / 4;
        b[n - 1] = -b[n - 3] / 4;

        // die letzten beiden Zeilen berechnen BEIDE y[n-1] in Abhängigkeit von y[1]
        // => y1 berechnen durch Gleichsetzen
        yprime[1] = (a[n - 1] - a[n - 2]) / (b[n - 2] - b[n - 1]);

        // wir haben ja schon alles in Abhängigkeit von y1

        for (int i = 2; i <= n - 1; i++) {
            yprime[i] = a[i - 1] + b[i - 1] * yprime[1];
        }
    }

    /**
     * {@inheritDoc} Liegt z ausserhalb der Stuetzgrenzen, werden die
     * aeussersten Werte y[0] bzw. y[n] zurueckgegeben. Liegt z zwischen den
     * Stuetzstellen x_i und x_i+1, wird z in das Intervall [0,1] transformiert
     * und das entsprechende kubische Hermite-Polynom ausgewertet.
     */
    @Override
    public double evaluate(double z) {
        if (z <= a) {
            return y[0];
        }
        if (z >= b) {
            return y[n];
        }

        int i = 0;
        while (a + h * (i + 1) < z) {
            i++;
        }

        z = (z - (a + i * h)) / (h);

        double zPower3 = Math.pow(z, 3);
        double zPower2 = Math.pow(z, 2);

        return (y[i] * ((1 - (3 * zPower2)) + (2 * zPower3))) +
                (y[i + 1] * ((3 * zPower2) - (2 * zPower3))) +
                (h * yprime[i] * ((z - (2 * zPower2)) + zPower3)) +
                (h * yprime[i + 1] * (-zPower2 + zPower3));
    }
}
