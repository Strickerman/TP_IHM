package ensisa.math;

import ensisa.model.ControlPoint;
import java.util.List;

public class Lagrange {
    public static double compute(double x, List<ControlPoint> points) {
        double result = 0.0;
        int n = points.size();

        for (int i = 0; i < n; i++) {
            ControlPoint pi = points.get(i);

            double li = 1.0;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    ControlPoint pj = points.get(j);
                    li *= (x - pj.getX()) / (pi.getX() - pj.getX());
                }
            }

            result += pi.getY() * li;
        }

        return result;
    }
}