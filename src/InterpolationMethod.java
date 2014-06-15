/**
 * Dies ist das Interface fuer alle Interpolationsverfahren
 *
 * @author braeckle
 */
public interface InterpolationMethod {

    /**
     * Initialisierung des Interpolationsverfahrens mit aequidistanten Stuetzstellen.
     * Es gilt immer und wird von init() vorausgesetzt:
     * a < b
     * n > 0
     * Laenge von y = n+1
     *
     * @param a Kleinste Stuetzstelle
     * @param b Groesste Stuetzstelle
     * @param n Anzahl an aequidistanten Intervallen zwischen a und b. Damit hat das Interpolationsverfahren n+1 Stuetzstellen
     * @param y n+1 Stuetzwerte.
     */
    public void init(double a, double b, int n, double[] y);

    /**
     * Wertet das Interpolationsverfahren an einer Stelle z aus
     */
    public double evaluate(double z);
}
