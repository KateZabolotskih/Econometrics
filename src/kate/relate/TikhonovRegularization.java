package kate.relate;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.commons.math3.util.Pair;

public class TikhonovRegularization {

    private final RealMatrix A;
    private final RealVector b;

    public TikhonovRegularization(RealMatrix A, RealVector b) {
        this.A = A;
        this.b = b;
    }

    public Pair<double[], Double> solveWith(double alpha) {
        int dim = A.getColumnDimension();
        RealMatrix M = A.transpose().multiply(A).add(MatrixUtils.createRealIdentityMatrix(dim).scalarMultiply(alpha));
        RealVector u = A.preMultiply(b);
        SingularValueDecomposition decomposition = new SingularValueDecomposition(M);
        RealVector y = decomposition.getSolver().solve(u);
        double discrepancy = A.transpose().preMultiply(y).subtract(b).getNorm();
        return new Pair<>(y.toArray(), discrepancy);
    }

}
