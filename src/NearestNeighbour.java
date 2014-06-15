/**
 * NearestNeighbour ist eine sehr einfache Interpolationsidee. Bei der
 * Auswertung an einer Stelle z wird der Funktionswert der am naechsten
 * liegenden Stuetzstelle gewaehlt.
 *
 * @author braeckle
 */
public class NearestNeighbour implements InterpolationMethod {

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

    @Override
    public void init(double a, double b, int n, double[] y) {
        this.a = a;
        this.b = b;
        this.n = n;
        h = (b - a) / n;
        this.y = y;
    }

    /**
     * {@inheritDoc} Die Funktion evaluate gibt fuer ein gegebenes z den
     * Stuetzwert derjenigen Stuetzstelle zurueck, die dem z am naechsten liegt.
     * Liegt z exakt in der Mitte zwischen zwei Stuetzstellen, wird der
     * Stuetzwert der groesseren Stuetzstelle zurueckgegeben.
     */
    @Override
    public double evaluate(double z) {

		/* Intervall finden, in dem z liegt */
        int interv = (int) ((z - a) / h);

		/* Intervall [a+interv*h, a+(interv+1)*h] gefunden */

		/* Sonderfaelle, wenn z ausserhalb des Eingangsintervalls liegt */
        if (interv < 0)
            return y[0];
        if (interv >= n)
            return y[y.length - 1];

		/*
         * Vergleich zwischen linker und rechter Grenze des gefundenen
		 * Intervalls
		 */
        if (Math.abs(z - a - interv * h) < Math.abs(z - a - (interv + 1) * h))
            return y[interv];
        else
            return y[interv + 1];
    }

}
