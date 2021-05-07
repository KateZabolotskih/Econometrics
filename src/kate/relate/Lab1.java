package kate.relate;

import org.apache.commons.math3.stat.inference.MannWhitneyUTest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Lab1 {

    static RealizationWriter[] writers = new RealizationWriter[] {
       new RealizationWriter("eps0.txt"),
       new RealizationWriter("eps1.txt"),
       new RealizationWriter("eps2.txt"),
       new RealizationWriter("eps3.txt"),
    };

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
            researchChank(reader.readChunk(), resultWriter);
        }
        for (RealizationWriter writer : writers) {
            writer.close();
        }
        resultWriter.close();
    }

    private static void researchChank(List<Point> points, BufferedWriter resultWriter) throws IOException {
        for (int k = 0; k < 4; k++) {
            PolyRegression regression = new PolyRegression(points, k);
            List<Point> epsPoints = points.stream()
                    .map(p -> new Point(p.t,p.X - regression.predict(p.t))).collect(Collectors.toList());
            writers[k].writeChunk(epsPoints);
            double[] eps = epsPoints.stream().mapToDouble(p->p.X).toArray();
            double[] left = new double[eps.length / 2];
            double[] right = new double[eps.length - eps.length / 2];
            System.arraycopy(eps, 0, left, 0, left.length);
            System.arraycopy(eps, left.length, right, 0, right.length);
            MannWhitneyUTest test = new MannWhitneyUTest();
            double pvalue = test.mannWhitneyUTest(left, right);
            resultWriter.write(
                    "k=" + k + " " + pvalue + " " + (pvalue > 0.05) + " " + regression + "\n"
            );
        }

    }



}
