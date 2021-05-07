package kate.relate.optimization;

import org.apache.commons.math3.distribution.CauchyDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

public class CauchyAnnealing {

    private final UniformRealDistribution uniform = new UniformRealDistribution(0, 1);

    private double[] generateY(double[] m, double s, Compact S) {
        double[] y =  new double[S.getDim()];
        for (int i = 0; i < S.getDim(); i++) {
            CauchyDistribution cauchy = new CauchyDistribution(m[i], s);
            y[i]  = cauchy.sample();
            while (y[i]  < S.start[i] || y[i]  > S.end[i]) {
                y[i]  = cauchy.sample();
            }
        }
        return y;
    }

    double T(int i, double T0, int dim) {
        return T0 / Math.pow(i, 1. / dim);
    }

    double H(double df, double T) {
        return Math.exp(-df/T);
    }

    public double[] optimize(Target f, Compact S, double T0, double TN) {
        int dim = S.getDim(), iter = 0;
        double T = T0;
        double[] x0 = generateY(new double[S.getDim()], T0, S);
        double[] x = x0;
        double E = f.eval(x0);
        while (T > TN) {
            T = T(++iter, T0, dim);
            double[] y = generateY(x, T, S);
            double fy = f.eval(y);
            double t = H(E - fy, T);
            double u = uniform.sample();
            if (t < u) {
                x = y;
                double fx = f.eval(x);
                E = Math.min(fx, E);
            }
        }
        return x;
    }

}
