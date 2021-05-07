package kate.relate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

class Config {

    GenerateConfig generateConfig;
    String input;
    int chunk;
    String output;

    Config(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.replaceAll(" ", "").charAt(0) == '#') {
                    continue;
                }
                String[] tokens = line.split(":");
                String key = tokens[0].replaceAll(" ", "");
                String value = tokens.length > 1 ? tokens[1].replaceAll(" ", "") : null;
                ConfType type = ConfType.fromString(key);
                switch (type) {
                    case GENERATE -> {
                        generateConfig = new GenerateConfig(reader);
                        input = generateConfig.fileName;
                    }
                    case CHUNK -> {
                        chunk = Integer.decode(value);
                    }
                    case OUTPUT -> {
                        output = value;
                    }
                    case FILE -> {
                        input = value;
                    }
                }
            }
        } catch (IOException exception) {
            System.out.println("ERROR: " + exception.getLocalizedMessage());
        }
    }

    boolean shouldGenerate() {
        return generateConfig != null;
    }

    enum ConfType {
        EXPERIMENT("experiment"),
        INPUT("input"),
        GENERATE("generate"),
        CHUNK("chunk"),
        OUTPUT("output"),
        FILE("file");

        String confType;

        ConfType(String confType) {
            this.confType = confType;
        }

        public static ConfType fromString(String sampleType) {
            for (ConfType type: ConfType.values()) {
                if (type.confType.equals(sampleType)) {
                    return type;
                }
            }
            return null;
        }
    }

    static class GenerateConfig {
        double[] koefs;
        SampleType type;
        int size;
        double step;
        String fileName;
        double sigma;

        GenerateConfig(BufferedReader reader) throws IOException {
            {
                for (int i = 0; i < GenerateCongifType.values().length; i++) {
                    String line = reader.readLine();
                    if (line.replaceAll(" ", "").charAt(0) == '#') {
                        i--;
                        continue;
                    }
                    String[] tokens = line.split(": ");
                    String key = tokens[0].replaceAll(" ", "");
                    String value = tokens.length > 1 ? tokens[1] : null;
                    GenerateCongifType type = GenerateCongifType.fromString(key);
                    switch (type) {
                        case TYPE -> this.type = SampleType.fromString(value);
                        case SIZE -> this.size = Integer.parseInt(value);
                        case STEP -> this.step = Double.valueOf(value);
                        case FILENAME -> this.fileName = value;
                        case SIGMA -> this.sigma = Double.valueOf(value);
                        case KOEFS -> this.koefs = Arrays.stream(value.split(" "))
                                .mapToDouble(Double::valueOf).toArray();

                    }
                }
            }
        }
        enum GenerateCongifType {
            TYPE("type"),
            KOEFS("koefs"),
            SIZE("size"),
            STEP("step"),
            SIGMA("sigma"),
            FILENAME("fileName");

            String confType;

            GenerateCongifType(String confType) {
                this.confType = confType;
            }

            public static GenerateCongifType fromString(String sampleType) {
                for (GenerateCongifType type: GenerateCongifType.values()) {
                    if (type.confType.equals(sampleType)) {
                        return type;
                    }
                }
                return null;
            }
        }
        enum SampleType {
            POLY("poly"), EXP("exp");

            String confType;

            SampleType(String confType) {
                this.confType = confType;
            }

            public static SampleType fromString(String sampleType) {
                for (SampleType type: SampleType.values()) {
                    if (type.confType.equals(sampleType)) {
                        return type;
                    }
                }
                return null;
            }
        }
    }

}