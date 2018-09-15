

public class Newton extends Algorithm {

     //Konstruktor przyjujący wektory współrzędnych
    public Newton(double[] X, double[] Y) {
        super(X, Y);
        System.out.println("Uzywany jest algorytm Newtona");
        int n = X.length;

        double[][] t = new double[n][n];

        for (int i = 0; i < n; i++) {
            t[i][0] = Y[i];
        }

        for (int i = 1; i < n; i++) {
            for (int j = i; j < n; j++) {
                
                double d = dividedDifference(t[j - 1][i - 1], t[j][i - 1], X[j - i], X[j]);
                t[j][i] = d;
            }          
        }
        
        a = new double[n];     
        
        
        for (int i = 0; i < n; i++) {
            a[i] = t[i][i];
        }
    }
    
    //Funkcja obliczająca wartość wielomianu w danym punkcie
    @Override
    public double valueAt(double x, int degree) {
        if (a == null) return 0;      
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
    
   
    private double dividedDifference(double fa, double fb, double xa, double xb) {
        return (fb - fa) / (xb - xa);
    }
}
