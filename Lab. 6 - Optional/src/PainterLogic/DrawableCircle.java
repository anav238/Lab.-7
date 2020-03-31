package PainterLogic;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Circle;

public class DrawableCircle extends Circle implements Drawable {
    public DrawableCircle(double centerX, double centerY, int radius) {
        super(centerX, centerY, radius);
    }

    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setFill(this.getFill());
        double upperLeftX = this.getCenterX() - this.getRadius() / 2;
        double upperLeftY = this.getCenterY() - this.getRadius() / 2;
        graphicsContext.fillOval(upperLeftX, upperLeftY, this.getRadius(), this.getRadius());
        graphicsContext.setStroke(this.getStroke());
        graphicsContext.strokeOval(upperLeftX, upperLeftY, this.getRadius(), this.getRadius());
    }
}
