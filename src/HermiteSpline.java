

public class HermiteSpline extends Algorithm {

    private double[] tangent;

    public HermiteSpline(double[] X, double[] Y) {      
        super(X, Y);
        System.out.println("Uzywany jest algorytm Hermite Spline");
    }

    static HermiteSpline create(double[] X, double[] Y, double[] tangent) {

        HermiteSpline h = new HermiteSpline(X, Y);
        h.setTangent(tangent);

        return h;
    }

    public void setTangent(double[] tangent) {
        this.tangent = tangent;
    }

    public double[] getTangent() {
        return tangent;
    }

    @Override
    public double valueAt(double x, int degree) {
        final int n = X.length;
        if (Double.isNaN(x)) {
            return x;
        }
        if (x <= X[0]) {
            return Y[0];
        }
        if (x >= X[n - 1]) {
            return Y[n - 1];
        }
        int i = 0;
        while (x >= X[i + 1]) {
            i += 1;
            if (x == X[i]) {
                return Y[i];
            }
        }

        double h = X[i + 1] - X[i];
        double t = (x - X[i]) / h;
        return (Y[i] * (1 + 2 * t) + h * tangent[i] * t) * (1 - t) * (1 - t)
                + (Y[i + 1] * (3 - 2 * t) + h * tangent[i + 1] * (t - 1)) * t * t;
    }
}
