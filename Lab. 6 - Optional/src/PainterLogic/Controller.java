package PainterLogic;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    //Controalele folosite in logica controller-ului, publice pentru a nu adnota fiecare in parte cu @FXML
    public Label shapeSizeLabel;
    public TextField shapeSizeTextField;
    public ColorPicker colorSelector;
    public ColorPicker strokeColorSelector;
    public ComboBox shapeComboBox;
    public CheckBox eraserCheckBox;
    public Canvas canvas;

    GraphicsContext graphicsContext;
    List<Shape> shapes;
    String currentShape;
    Stage stage;

    @FXML
    public void initialize() {
        //Cream lista de forme necesara desenarii in retained mode.
        shapes = new ArrayList<>();
        //Selectam, by default, prima optiune din combobox-ul cu forme la deschiderea aplicatiei.
        shapeComboBox.getSelectionModel().selectFirst();
        //Obtinem graphicsContext-ul canvas-ului, folosit pentru desenare.
        graphicsContext = canvas.getGraphicsContext2D();
    }

    public void setStage(Stage stage) {
        //Setam stage-ul, necesar pentru a deschide fereastra de File Chooser la Save/Load.
        this.stage = stage;
    }

    public void onCanvasClick(MouseEvent event) {
        //La click pe canvas, exista 2 actiuni posibile in functie de modul setat. Eraser mode este cand checkbox-ul
        //corespunzator din fereastra este setat, ceea ce duce la stergerea formelor aflate in zona in care se da click.
        //Daca eraser mode nu este setat, se vor desena forme.
        if (eraserCheckBox.isSelected())
            eraseShape(event.getX(), event.getY());
        else
            drawShape(event.getX(), event.getY());
    }

    public void eraseShape(double x, double y) {
        List<Shape> toRemove = new ArrayList<>();
        for (Shape shape:shapes) {
            //Daca forma curenta contine in bounds punctul (x,y), atunci il vom sterge din lista de forme.
            //Folosim o lista in loc sa stergem direct in for pentru a preveni invalidarea iteratorului.
            Bounds bounds = shape.getBoundsInLocal();
            if (bounds.contains(x,y))
                toRemove.add(shape);
        }
        shapes.removeAll(toRemove);
        //Dupa ce am sters formele pe care a apasat utilizatorul, redesenam canvas-ul fara acestea.
        redrawCanvas();
    }

    private void redrawCanvas() {
        //La redesenare, stergem intai tot din canvas, dupa care redesenam toate formele ramase in lista.
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (Shape shape:shapes) {
            //Am convertit obiectul de tip shape intr-un obiect de tip Drawable pentru a putea apela functia Draw,
            //care nu este definita in Shape-urile din JavaFX, dar care este definita in clasele mele DrawableCircle
            //si DrawableSquare, care extind clasele Circle, respectiv Square si implementeaza interfata Drawable.
            Drawable toDraw = (Drawable)shape;
            toDraw.draw(graphicsContext);
        }
    }

    public void drawShape(double x, double y) {
        int shapeSize;
        try {
            //Preluam valorile necesare din configPanel. Instructiunile sunt intr-un bloc try/catch pentru
            //a prinde exceptiile generate de un input gresit (text in loc de numere).
            shapeSize = Integer.parseInt(shapeSizeTextField.getText());
            String shapeString = shapeComboBox.getValue().toString();

            if (shapeString.equals("Circle")) {
                //Cream un obiect nou de tip DrawableCircle, ce va avea ca centru locul unde am dat click
                //si va avea ca proprietati cele setate in ConfigPanel.
                DrawableCircle circle = new DrawableCircle(x, y, shapeSize);
                circle.setFill(colorSelector.getValue());
                circle.setStroke(strokeColorSelector.getValue());
                circle.draw(graphicsContext);
                //Adaugam obiectul nou creat in lista noastra de forme.
                shapes.add(circle);
            } else if (shapeString.equals("Square")) {
                //Cream un obiect nou de tip DrawableSquare. Acesta va avea ca centru locul unde am dat click
                //dar in constructor a trebuit sa ii pasez coordonatele coltului din stanga sus, pe care le-am calculat.
                double upperLeftX = x - shapeSize / 2;
                double upperLeftY = y - shapeSize / 2;
                DrawableSquare square = new DrawableSquare(upperLeftX, upperLeftY, shapeSize);
                square.setFill(colorSelector.getValue());
                square.setStroke(strokeColorSelector.getValue());
                square.draw(graphicsContext);
                shapes.add(square);
            }

        } catch (NumberFormatException e) {
            //Daca utilizatorul a introdus date in format gresit in input, acesta este avertizat.
            new Alert(Alert.AlertType.ERROR, "Please enter a number in the size field!").showAndWait();
        }
    }

    public void changeShape() {
        //Aceasta functie este apelata cand se schimba valoarea din comboBox, si are rolul de a reflecta
        //aceasta schimbare in ConfigPanel, prin schimbarea textului din label-ul legat de dimensiune.
        currentShape = shapeComboBox.getValue().toString();
        if (currentShape.equals("Circle"))
            shapeSizeLabel.setText("Radius: ");
        else if (currentShape.equals("Square"))
            shapeSizeLabel.setText("Size: ");
    }

    public void saveCanvas() {
        //La click pe butonul Save, se apeleaza aceasta functie.
        //Aici am creat un FileChooser nou, care are rolul de a afisa fereastra din windows pentru salvarea unui
        //fisier, unde acesta poate alege locatia de salvare si numele fisierului.
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));
        File file = fileChooser.showSaveDialog(stage);

        //Daca utilizatorul a ales un nume valid de fisier si nu a dat Cancel, incercam sa salvam fisierul.
        if (file != null) {
            try {
                //Cream o imagine noua de dimensiunea canvas-ului, in care adaugam continutul acestuia cu
                //ajutorul functiei snapshot.
                WritableImage writableImage = new WritableImage((int)canvas.getWidth(), (int)canvas.getHeight());
                canvas.snapshot(null, writableImage);
                //Pentru a putea salva imaginea, o rasterizam, dupa care ne folosim de functia write din
                //ImageIO pentru a o salva cu formatul png, la locatia si cu denumirea indicata de file.
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", file);
                //Daca salvarea se face cu succes, informam utilizatorul printr-o fereastra de informare.
                new Alert(Alert.AlertType.INFORMATION, "File saved successfully!").showAndWait();
            } catch (IOException ex) {
                //Daca nu s-a putut scrie fisierul, informam utilizatorul printr-o fereastra de eroare.
                new Alert(Alert.AlertType.ERROR, "Error at saving your file. Try again!").showAndWait();
            }
        }
    }

    public void loadCanvas() {
        //La click pe butonul Load, se apeleaza aceasta functie.
        //De data aceasta FileChooser-ul are rolul de a afisa fereastra din windows pentru deschiderea unui fisier,
        //care va fi retinut in file, daca utilizatorul alege un fisier si nu da Cancel.
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                //Obtinem imaginea din fisier si o desenam.
                Image image = SwingFXUtils.toFXImage(ImageIO.read(file), null);
                graphicsContext.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());
            }
            catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Error at loading your file. Try again!").showAndWait();
            }
        }
    }

    public void resetCanvas() {
        //La click pe butonul Reset, se apeleaza aceasta functie.
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        shapes.clear();
    }

    public void exitProgram() {
        //La click pe butonul Exit, se apeleaza aceasta functie.
        System.exit(0);
    }

}
