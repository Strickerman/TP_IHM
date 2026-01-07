package ensisa.commands;

import ensisa.MainController;
import javafx.scene.image.Image;

public class OpenImageCommand implements Command {
    private final MainController controller;
    private final Image oldImage, newImage;

    public OpenImageCommand(MainController controller, Image oldImage, Image newImage) {
        this.controller = controller;
        this.oldImage = oldImage;
        this.newImage = newImage;
    }

    @Override public void doIt() { controller.setOriginalImage(newImage); }
    @Override public void undoIt() { controller.setOriginalImage(oldImage); }
}