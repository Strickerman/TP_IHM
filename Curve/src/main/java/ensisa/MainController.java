package ensisa;

import ensisa.math.Lagrange;
import ensisa.model.ControlPoint;
import ensisa.model.CurveModel;
import ensisa.commands.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import javafx.scene.paint.Color;
import java.io.File;
import java.util.List;

public class MainController {

    @FXML private Canvas canvasRed;
    @FXML private Canvas canvasGreen;
    @FXML private Canvas canvasBlue;
    @FXML private ImageView imageView;

    private CurveModel modelRed;
    private CurveModel modelGreen;
    private CurveModel modelBlue;

    private ControlPoint selectedPoint = null;
    private double initialY;

    private final double OFFSET_X = 20.0;
    private final double OFFSET_Y = 20.0;
    private final double GRAPH_HEIGHT = 255.0;

    private Image originalImage;
    private final UndoManager undoManager = new UndoManager();

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
            yMath = Math.min(255, Math.max(0, yMath));
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

    public void drawAll() {
        draw(canvasRed, modelRed, Color.RED);
        draw(canvasGreen, modelGreen, Color.GREEN);
        draw(canvasBlue, modelBlue, Color.BLUE);
        applyFilter();
    }

    private void setupCanvasEvents(Canvas canvas, CurveModel model) {
        canvas.setOnMousePressed(e -> {
            for (ControlPoint p : model.getControlPoints()) {
                double pX = OFFSET_X + p.getX();
                double pY = OFFSET_Y + (GRAPH_HEIGHT - p.getY());
                if (Math.abs(e.getX() - pX) < 10 && Math.abs(e.getY() - pY) < 10) {
                    selectedPoint = p;
                    initialY = p.getY();
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
            if (selectedPoint != null) {
                undoManager.execute(new MovePointCommand(selectedPoint, initialY, selectedPoint.getY(), this::drawAll));
                selectedPoint = null;
            }
        });
    }

    @FXML private void onUndo() { undoManager.undo(); }
    @FXML private void onRedo() { undoManager.redo(); }
    @FXML private void onQuit() { Platform.exit(); }

    @FXML
    private void onLinear() {
        undoManager.execute(new LinearizeCommand(List.of(modelRed, modelGreen, modelBlue), this::drawAll));
    }

    @FXML
    private void onOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images JPEG", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(imageView.getScene().getWindow());

        if (selectedFile != null) {
            Image newImg = new Image(selectedFile.toURI().toString());
            undoManager.execute(new OpenImageCommand(this, originalImage, newImg));
        }
    }

    public void setOriginalImage(Image img) {
        this.originalImage = img;
        applyFilter();
    }

    private void applyFilter() {
        if (originalImage == null) return;

        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();
        WritableImage dstImage = new WritableImage(width, height);
        PixelReader reader = originalImage.getPixelReader();
        PixelWriter writer = dstImage.getPixelWriter();

        int[] lutR = prepareLUT(modelRed);
        int[] lutG = prepareLUT(modelGreen);
        int[] lutB = prepareLUT(modelBlue);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int argb = reader.getArgb(x, y);
                int a = (argb >> 24) & 0xff;
                int r = (argb >> 16) & 0xff;
                int g = (argb >> 8) & 0xff;
                int b = argb & 0xff;

                int newR = lutR[r];
                int newG = lutG[g];
                int newB = lutB[b];

                int newArgb = (a << 24) | (newR << 16) | (newG << 8) | newB;
                writer.setArgb(x, y, newArgb);
            }
        }
        imageView.setImage(dstImage);
    }

    private int[] prepareLUT(CurveModel model) {
        int[] lut = new int[256];
        var points = model.getControlPoints();
        for (int i = 0; i < 256; i++) {
            double val = Lagrange.compute(i, points);
            lut[i] = (int) Math.min(255, Math.max(0, val));
        }
        return lut;
    }
}