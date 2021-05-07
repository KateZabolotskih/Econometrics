package kate.relate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class RealizationWriter {
    private BufferedWriter writer;

    public RealizationWriter(String fileName) {
        try {
            writer = new BufferedWriter(new FileWriter(fileName));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void writeChunk(List<Point> points) throws IOException {
        for (Point p : points) {
            writer.write(p.t + " " + p.X + "\n");
        }
    }

    public void close() throws IOException {
        writer.close();
    }
}
