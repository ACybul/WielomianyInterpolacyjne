

public abstract class Algorithm {
   
    protected final double[] X;
    protected final double[] Y;
    protected double[] a = null;

    public Algorithm(double[] X, double[] Y) {
        this.X = X;
        this.Y = Y;
        this.a = null;
    }
    
    public abstract double valueAt(double x, int degree);
}
