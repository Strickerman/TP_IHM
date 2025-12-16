package ensisa;

import ensisa.math.Lagrange;
import ensisa.model.ControlPoint;
import ensisa.model.CurveModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class MainController {

    @FXML
    private Canvas canvas;

    private CurveModel model;
    private ControlPoint selectedPoint = null;

    private final double OFFSET_X = 20.0;
    private final double OFFSET_Y = 20.0;
    private final double GRAPH_HEIGHT = 255.0;

    @FXML
    public void initialize() {
        model = new CurveModel();

        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseReleased(e -> selectedPoint = null);

        draw();
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(Color.LIGHTGRAY);
        gc.strokeRect(OFFSET_X, OFFSET_Y, 255, 255);

        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2);
        gc.beginPath();

        var points = model.getControlPoints();

        for (int x = 0; x <= 255; x++) {
            double yMath = Lagrange.compute(x, points);

            if (yMath < 0) yMath = 0;
            if (yMath > 255) yMath = 255;

            double xScreen = OFFSET_X + x;
            double yScreen = OFFSET_Y + (GRAPH_HEIGHT - yMath);

            if (x == 0) gc.moveTo(xScreen, yScreen);
            else gc.lineTo(xScreen, yScreen);
        }
        gc.stroke();

        gc.setFill(Color.GRAY);
        for (ControlPoint p : points) {
            double pX = OFFSET_X + p.getX();
            double pY = OFFSET_Y + (GRAPH_HEIGHT - p.getY());
            gc.fillOval(pX - 4, pY - 4, 8, 8);
        }
    }

    private void handleMousePressed(MouseEvent e) {
        double mouseX = e.getX();
        double mouseY = e.getY();

        for (ControlPoint p : model.getControlPoints()) {
            double pX = OFFSET_X + p.getX();
            double pY = OFFSET_Y + (GRAPH_HEIGHT - p.getY());

            if (Math.abs(mouseX - pX) < 10 && Math.abs(mouseY - pY) < 10) {
                selectedPoint = p;
                break;
            }
        }
    }

    private void handleMouseDragged(MouseEvent e) {
        if (selectedPoint != null) {
            double newY = GRAPH_HEIGHT - (e.getY() - OFFSET_Y);
            selectedPoint.setY(newY);
            draw();
        }
    }

    @FXML
    private void onQuit() {
        Platform.exit();
    }

    @FXML
    private void onLinear() {
        model.linearize();
        draw();
    }
}