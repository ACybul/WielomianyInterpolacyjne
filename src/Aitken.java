public class Aitken extends Algorithm {
    
    
    public Aitken(double[] X, double[] Y) {
        super(X, Y);
        System.out.println("Uzywany jest algorytm Aitkena");
    }

 
    @Override
    public double valueAt(double x, int degree) {   
        double[] r = (double[]) Y.clone();
        int d = 0;
        for (int i = 0; i < r.length; ++i) {
            for (int k = i - 1; k >= 0; --k) {
                r[k] = r[k + 1] + (r[k + 1] - r[k]) * (x - X[i]) / (X[i] - X[k]);
                
            }
            if (d++ == degree - 1) {
                break;
            }
        }
        return r[0];
    }
}
