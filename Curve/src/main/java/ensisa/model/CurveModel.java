package ensisa.model;

import java.util.ArrayList;
import java.util.List;

public class CurveModel {
    private final List<ControlPoint> controlPoints;

    public CurveModel() {
        controlPoints = new ArrayList<>();
        controlPoints.add(new ControlPoint(0, 0));
        controlPoints.add(new ControlPoint(64, 50));
        controlPoints.add(new ControlPoint(128, 200));
        controlPoints.add(new ControlPoint(192, 50));
        controlPoints.add(new ControlPoint(255, 255));
    }

    public List<ControlPoint> getControlPoints() {
        return controlPoints;
    }

    public void linearize() {
        for (ControlPoint p : controlPoints) {
            p.setY(p.getX());
        }
    }
}