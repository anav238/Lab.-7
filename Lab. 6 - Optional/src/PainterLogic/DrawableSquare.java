package PainterLogic;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;

public class DrawableSquare extends Rectangle implements Drawable {
    public DrawableSquare(double x, double y, double size) {
        super(x, y, size, size);
    }

    @Override
    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setFill(this.getFill());
        graphicsContext.fillRect(this.getX(), this.getY(), this.getWidth(), this.getWidth());
        graphicsContext.setStroke(this.getStroke());
        graphicsContext.strokeRect(this.getX(), this.getY(), this.getWidth(), this.getWidth());
    }
}
