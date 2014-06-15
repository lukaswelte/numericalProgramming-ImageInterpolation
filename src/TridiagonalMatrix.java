import java.util.Arrays;

/**
 * Klasse zur Verwaltung von Tridiagonalmatrizen
 *
 * @author braeckle
 */
public class TridiagonalMatrix {

    /**
     * Dimension der Matrix
     */
    int dim;

    /**
     * Arrays fuer die obere Nebendiagonale, die Diagonale und die untere
     * Nebendiagonale
     */
    double[] lower; /* Laenge dim-1 */
    double[] diag; /* Laenge dim */
    double[] upper; /* Laenge dim-1 */

    /**
     * Die Matrix hat z.B. mit dim=3 folgendes Aussehen:
     *  d[0]	u[0]	 0
     *  l[0]	d[1]	u[1]
     *   0 		l[1]	d[2]
     */

    /**
     * Konstruktoren für Tridiagonalmatrix
     */
    public TridiagonalMatrix(int dim) {
        this.dim = dim;
        lower = new double[dim - 1];
        diag = new double[dim];
        upper = new double[dim - 1];
    }

    /**
     * Belegt die untere Nebendiagonale mit lower,
     * die Diagonale mit diag und die obere Nebendiagonale mit upper
     */
    public TridiagonalMatrix(double[] lower, double[] diag, double[] upper) {
        dim = diag.length;
        this.lower = Arrays.copyOf(lower, dim - 1);
        this.diag = Arrays.copyOf(diag, dim);
        this.upper = Arrays.copyOf(upper, dim - 1);
    }

    /**
     * Erzeugt eine Kopie von tri
     */
    public TridiagonalMatrix(TridiagonalMatrix tri) {
        dim = tri.dim;
        this.lower = Arrays.copyOf(tri.lower, dim - 1);
        this.diag = Arrays.copyOf(tri.diag, dim);
        this.upper = Arrays.copyOf(tri.upper, dim - 1);
    }

    /**
     * Belegt das Matrixfeld (i,j) mit dem Wert a. Indizierung der Elemente von
     * 1 bis dim;
     */
    public void setElement(int i, int j, double a) {
        if (i == j + 1)
            lower[j - 1] = a;
        else if (i == j)
            diag[i - 1] = a;
        else if (i == j - 1)
            upper[i - 1] = a;
        else
            System.out.println("(i,j) liegt nicht auf der Tridiagonalen!");

    }

    /**
     * Gibt das Matrixelement an der Stelle (i,j) zurueck. Indizierung der
     * Elemente von 1 bis dim;
     */
    public double getElement(int i, int j) {
        if (i == j + 1)
            return lower[j - 1];
        if (i == j)
            return diag[i - 1];
        if (i == j - 1)
            return upper[i - 1];
        else {
            System.out.println("(i,j) liegt nicht auf der Tridiagonalen!");
            return 0;
        }
    }

    /**
     * Gibt die Dimension der Matrix zurueck
     */
    public int getDimension() {
        return dim;
    }

    /**
     * Erzeugt eine Stringdarstellung der Matrix zu Testzwecken
     */
    public String toString() {
        String str = "";
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (i == j + 1)
                    str += lower[j] + "\t";
                else if (i == j)
                    str += diag[i] + "\t";
                else if (i == j - 1)
                    str += upper[i] + "\t";
                else
                    str += "-\t";
            }
            str += "\n";
        }
        return str;
    }

    /**
     * Loest das System Ax=b mit der Tridiagonalmatrix A. Verwendet wird die
     * Gauss-Elimination unter Ausnutzung der Tridioganlstruktur ohne
     * Pivotisierung (Thomas-Algorithmus)
     */
    public double[] solveLinearSystem(double[] b) {

        double[] l = Arrays.copyOf(lower, dim - 1);
        double[] d = Arrays.copyOf(diag, dim);
        double[] u = Arrays.copyOf(upper, dim - 1);
        double[] br = Arrays.copyOf(b, dim);

		/* Loesung x */
        double[] x = new double[dim];

		/* untere Nebendiagonale eliminieren */
        for (int i = 0; i < dim - 1; i++) {
            double factor = l[i] / d[i];
            d[i + 1] -= factor * u[i];
            br[i + 1] -= factor * br[i];
        }

		/* Ruecksubstitution */
        x[dim - 1] = br[dim - 1] / d[dim - 1];
        for (int i = dim - 2; i >= 0; i--) {
            x[i] = (br[i] - u[i] * x[i + 1]) / d[i];
        }

        return x;
    }
}
