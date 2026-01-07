package ensisa.commands;

import ensisa.model.ControlPoint;
import ensisa.model.CurveModel;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class LinearizeCommand implements Command {
    private final List<CurveModel> models;
    private final Map<ControlPoint, Double> savedStates = new HashMap<>();
    private final Runnable updateUI;

    public LinearizeCommand(List<CurveModel> models, Runnable updateUI) {
        this.models = models;
        this.updateUI = updateUI;
        for (CurveModel m : models) {
            for (ControlPoint p : m.getControlPoints()) {
                savedStates.put(p, p.getY());
            }
        }
    }

    @Override
    public void doIt() {
        for (CurveModel m : models) m.linearize();
        updateUI.run();
    }

    @Override
    public void undoIt() {
        savedStates.forEach(ControlPoint::setY);
        updateUI.run();
    }
}