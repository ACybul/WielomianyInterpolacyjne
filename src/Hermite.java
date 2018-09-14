
public class Hermite extends Algorithm {


    public Hermite(double[] X, double[] Y) {
        super(X, Y);
        System.out.println("Uzywany jest algorytm Hermite'a");
        int n = X.length;

     
        double[][] t = new double[n][n];

        for (int i = 0; i < n; i++) {
            t[i][0] = Y[i];
        }

 
        for (int i = 1; i < n; i++) {
           
            for (int j = i; j < n; j++) {

           
                if (Double.compare(X[j], X[j - 1]) != 0) {
                    double d = dividedDifference(t[j - 1][i - 1], t[j][i - 1], X[j - i], X[j]);
                    t[j][i] = d;
                
                } else {
                    double d = t[j][i - 1] / factorial(2);
                    t[j][i] = d;
                }
            }
        }

        a = new double[n];
        
       
        for (int i = 0; i < n; i++) {
            a[i] = t[i][i];
        }
    }

  
    @Override
    public double valueAt(double x, int degree) {
        if (a == null) {
            return 0;
        }
        double result = 0;
        for (int i = 0; i < degree; i++) {
            double p = a[i];
            for (int j = 0; j < i; j++) {
                p *= (x - X[j]);
            }
            result += p;

        }
        return result;
    }

  
    public static long factorial(int number) {
        long result = 1;

        for (int factor = 2; factor <= number; factor++) {
            result *= factor;
        }

        return result;
    }


    private double dividedDifference(double fa, double fb, double xa, double xb) {
        return (fb - fa) / (xb - xa);
    }
}
