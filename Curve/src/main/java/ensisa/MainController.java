package ensisa;

import ensisa.math.Lagrange;
import ensisa.model.ControlPoint;
import ensisa.model.CurveModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class MainController {

    @FXML private Canvas canvasRed;
    @FXML private Canvas canvasGreen;
    @FXML private Canvas canvasBlue;

    private CurveModel modelRed;
    private CurveModel modelGreen;
    private CurveModel modelBlue;

    private CurveModel selectedModel = null;
    private ControlPoint selectedPoint = null;

    private final double OFFSET_X = 20.0;
    private final double OFFSET_Y = 20.0;
    private final double GRAPH_HEIGHT = 255.0;

    @FXML
    public void initialize() {
        modelRed = new CurveModel();
        modelGreen = new CurveModel();
        modelBlue = new CurveModel();

        setupCanvasEvents(canvasRed, modelRed);
        setupCanvasEvents(canvasGreen, modelGreen);
        setupCanvasEvents(canvasBlue, modelBlue);

        drawAll();
    }

    private void draw(Canvas canvas, CurveModel model, Color color) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(Color.LIGHTGRAY);
        gc.strokeRect(OFFSET_X, OFFSET_Y, 255, 255);

        gc.setStroke(color);
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

    private void drawAll() {
        draw(canvasRed, modelRed, Color.RED);
        draw(canvasGreen, modelGreen, Color.GREEN);
        draw(canvasBlue, modelBlue, Color.BLUE);
    }

    private void setupCanvasEvents(Canvas canvas, CurveModel model) {
        canvas.setOnMousePressed(e -> {
            for (ControlPoint p : model.getControlPoints()) {
                double pX = OFFSET_X + p.getX();
                double pY = OFFSET_Y + (GRAPH_HEIGHT - p.getY());

                if (Math.abs(e.getX() - pX) < 10 && Math.abs(e.getY() - pY) < 10) {
                    selectedPoint = p;
                    selectedModel = model;
                    break;
                }
            }
        });

        canvas.setOnMouseDragged(e -> {
            if (selectedPoint != null) {
                double newY = GRAPH_HEIGHT - (e.getY() - OFFSET_Y);
                selectedPoint.setY(newY);
                drawAll();
            }
        });

        canvas.setOnMouseReleased(e -> {
            selectedPoint = null;
            selectedModel = null;
        });
    }

    @FXML
    private void onQuit() {
        Platform.exit();
    }

    @FXML
    private void onLinear() {
        modelRed.linearize();
        modelGreen.linearize();
        modelBlue.linearize();
        drawAll();
    }
}