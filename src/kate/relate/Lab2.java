package kate.relate;

import kate.relate.optimization.CauchyAnnealing;
import kate.relate.optimization.Compact;
import kate.relate.optimization.Target;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.exp;
import static java.lang.Math.sqrt;

public class Lab2 {

    public static void main(String[] args) throws IOException {
        String confFileName = args[0];
        Config config = new Config(confFileName);
        if (config.shouldGenerate()) {
            SampleGenerator generator = new SampleGenerator();
            generator.generate(config.generateConfig);
        }
        BufferedWriter resultWriter = new BufferedWriter(new FileWriter(config.output));
        RealizationReader reader = new RealizationReader(config.input, config.chunk);
        int intervals = reader.size / config.chunk;
        for (int i = 0; i < intervals; i++) {
            List<Point> chunk = reader.readChunk();
            resultWriter.write("chunk = {\n");
            researchLinearRegression(chunk, resultWriter);
            researchNonlinearRegression(chunk, resultWriter);
            resultWriter.write("}\n");
        }
        resultWriter.close();
    }

    private static void researchLinearRegression(List<Point> chunk, BufferedWriter resultWriter) throws IOException {
        resultWriter.write("\tLinearRegression = {\n");
        int k = 4;
        PolyRegression regression = new PolyRegression(chunk, k, k);
        TikhonovRegularization regularization = new TikhonovRegularization(regression.getA(), regression.getB());
        double[] alphas = new double[]{
                0, 0.01, 0.1, 0.2, 0.4, 1
        };
        for (double alpha: alphas) {
            Pair<double[], Double> result = regularization.solveWith(alpha);
            double[] gamma = result.getFirst();
            RealVector b = MatrixUtils.createRealVector(new double[]{
                    gamma[1] / gamma[0],
                    -Math.pow(2 * gamma[2] / gamma[0], 1./2),
                    -Math.pow(-6 * gamma[3] / gamma[0], 1./3),
                    -Math.pow(24 * gamma[4] / gamma[0], 1./4),
            });
            double discrepancy = result.getSecond();
            resultWriter.write(String.format(
                    """
                            \t\t{
                            \t\t\t alpha=%f discrepancy=%f
                            \t\t\t betta_0=%f betta_1=%s
                            \t\t}
                    """,
                    alpha, discrepancy, gamma[0], Arrays.toString(b.toArray())
            ));
        }
        resultWriter.write("\t}\n");
    }

    private static void researchNonlinearRegression(List<Point> chunk, BufferedWriter resultWriter) throws IOException {
        Target target = betta -> {
            double mse = 0;
            for (Point p: chunk) {
                double d = p.X - betta[0] * exp(betta[1] * p.t);
                mse += d;
            }
            return sqrt(mse);
        };
        Compact S = new Compact(
          new double[]{0, -10},
          new double[]{10, 0}
        );
        CauchyAnnealing annealing = new CauchyAnnealing();
        double[] betta = annealing.optimize(target, S, 10, 0.02);
        resultWriter.write(String.format(
                """
                \tNonlinearRegression = {
                \t\tbetta_0=%f betta_1=%f
                \t}
                """, betta[0], betta[1]
        ));
    }

}
