package kate.relate;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import java.util.Arrays;
import java.util.List;

public class PolyRegression {

    private RealMatrix A;
    private RealVector b;
    private double[] betta = new double[4];
    private double sigma;

    private double poly(double t) {
        double p = 1;
        double y = 0;
        for (double b: betta) {
            y += b * p;
            p *= t;
        }
        return y;
    }

    public PolyRegression(List<Point> points, int k, int l) {
        betta = new double[l+1];
        RealVector X = MatrixUtils.createRealVector(points.stream().mapToDouble(p->p.X).toArray());
        double[][] z = new double[k+1][points.size()];
        for (int i = 0; i < points.size(); i++) {
            double t = points.get(i).t;
            double tk = 1;
            for (int j = 0; j < k+1; j++) {
                z[j][i] = tk;
                tk *= t;
            }
        }
        RealMatrix Z = MatrixUtils.createRealMatrix(z);
        this.A = Z.multiply(Z.transpose());
        this.b = Z.transpose().preMultiply(X);
        SingularValueDecomposition svd = new SingularValueDecomposition(A);
        RealVector betta = svd.getSolver().solve(b);
        System.arraycopy(betta.toArray(), 0, this.betta, 0, k+1);
        double mse = 0;
        for (int i = 0; i < points.size(); i++) {
            double X_i =  points.get(i).X;
            double t_i =  points.get(i).t;
            double deviation =  X_i -predict(t_i);
            mse += deviation * deviation;
        }
        this.sigma = Math.sqrt(mse / points.size());
    }

    public RealMatrix getA() {
        return A;
    }

    public RealVector getB() {
        return b;
    }

    public PolyRegression(List<Point> points, int k) {
        this(points, k, 4);
    }

    public double predict(double t) {
        return poly(t);
    }

    public double[] getBetta() {
        return betta;
    }

    public double getSigma() {
        return sigma;
    }

    @Override
    public String toString() {
        return "PolyRegression{" +
                "betta=" + Arrays.toString(betta) +
                ", sigma=" + sigma +
                '}';
    }
}
