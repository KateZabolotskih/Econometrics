package kate.relate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RealizationReader {

    private BufferedReader reader;
    private int chank;
    int size;

    public RealizationReader(String fileName, int chank) throws IOException {
        this.reader = new BufferedReader(new FileReader(fileName));
        this.chank = chank;
        this.size = Integer.parseInt(reader.readLine());
    }

    public List<Point> readChunk() throws IOException {
        List<Point> res = new ArrayList<>();
        for (int i = 0; i < chank; i++) {
            String[] tokens = reader.readLine().split(" ");
            res.add(new Point(
                    Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1])
            ));
        }
        return res;
    }
}
