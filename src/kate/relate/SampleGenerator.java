package kate.relate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class SampleGenerator {

    private final Random random = new Random(1);

    private double poly(double t, double[] koefs) {
        double p = 1;
        double y = 0;
        for (double k: koefs) {
            y += k * p;
            p *= t;
        }
        return y;
    }

    private double exp(double t, double[] koefs) {
        return koefs[0] * Math.exp(koefs[1] * t);
    }

    private void polySampleToFile(Config.GenerateConfig config) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(config.fileName))) {
            writer.write(config.size + "\n");
            double t = 0;
            for (int i = 0; i < config.size; i++) {
                double X = poly(t, config.koefs) + random.nextGaussian() * config.sigma;
                t += config.step;
                writer.write(t + " " + X + "\n");
            }
        } catch (IOException exception) {
            System.out.println("ERROR: " + exception.getLocalizedMessage());
        }
    }

    private void expSampleToFile(Config.GenerateConfig config) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(config.fileName))) {
            writer.write(config.size + "\n");
            double t = 0;
            for (int i = 0; i < config.size; i++) {
                double X = exp(t, config.koefs) + random.nextGaussian() * config.sigma;
                t += config.step;
                writer.write(t + " " + X + "\n");
            }
        } catch (IOException exception) {
            System.out.println("ERROR: " + exception.getLocalizedMessage());
        }
    }

    public void generate(Config.GenerateConfig config) {
        switch (config.type) {
            case POLY -> polySampleToFile(config);
            case EXP -> expSampleToFile(config);
        }
    }

}
