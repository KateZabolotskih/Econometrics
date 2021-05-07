package kate.relate.optimization;

public class Compact {

    double[] start;
    double[] end;

    public Compact(double[] start, double[] end) {
        this.start = start;
        this.end = end;
    }

    int getDim() {
        return start.length;
    }

    boolean contains(double[] x) {
        for (int i = 0; i < getDim(); i++) {
            if (x[i] < start[i] || x[i] > end[i]) {
                return false;
            }
        }
        return true;
    }

}
