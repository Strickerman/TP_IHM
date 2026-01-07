package ensisa.commands;

import ensisa.model.ControlPoint;

public class MovePointCommand implements Command {
    private final ControlPoint point;
    private final double oldY, newY;
    private final Runnable updateUI;

    public MovePointCommand(ControlPoint point, double oldY, double newY, Runnable updateUI) {
        this.point = point;
        this.oldY = oldY;
        this.newY = newY;
        this.updateUI = updateUI;
    }

    @Override public void doIt() { point.setY(newY); updateUI.run(); }
    @Override public void undoIt() { point.setY(oldY); updateUI.run(); }
}