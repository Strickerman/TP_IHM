package ensisa.model;

public class ControlPoint {
    private final double x;
    private double y;

    public ControlPoint(double x, double y) {
        this.x = x;
        setY(y);
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public void setY(double y) {
        if (y < 0) this.y = 0;
        else if (y > 255) this.y = 255;
        else this.y = y;
    }
}