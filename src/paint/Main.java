/*
 * ===================================================
 *                      contents
 * ===================================================
 * 00- Free draw
 * 01- rubber
 * 02- draw Line
 * 03- Text
 *
 * ----------------------------------------------------
 *                     Features
 * ----------------------------------------------------
 * - the ability to change Line color
 * - the ability to change Fill color
 * - the ability to change Line width
 * - Open Image && save Image

 */

/**
 * @authr Nyi Nyi Myo Zin Htet
 */
package paint;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 * Main class
 */
public class Main extends Application {

    @Override
    /**
     * Main function which has instruction for all the require useage and variable are defined here.
     *@exception Main throws java.lang.Exception
     */
    public void start(Stage primaryStage) {


        ToggleButton drowbtn = new ToggleButton("Draw");
        ToggleButton rubberbtn = new ToggleButton("Rubber");
        ToggleButton linebtn = new ToggleButton("Line");
        ToggleButton textbtn = new ToggleButton("Text");

        ToggleButton[] toolsArr = {drowbtn, rubberbtn, linebtn, textbtn};

        ToggleGroup tools = new ToggleGroup();

        for (ToggleButton tool : toolsArr) {
            tool.setMinWidth(90);
            tool.setToggleGroup(tools);
            tool.setCursor(Cursor.HAND);
        }

        ColorPicker cpLine = new ColorPicker(Color.BLACK);
        ColorPicker cpFill = new ColorPicker(Color.TRANSPARENT);

        TextArea text = new TextArea();
        text.setPrefRowCount(1);

        Slider slider = new Slider(1, 50, 3);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);

        Label line_color = new Label("Line Color");
        Label fill_color = new Label("Fill Color");
        Label line_width = new Label("3.0");

        Button save = new Button("Save");
        Button open = new Button("Open");

        Button[] basicArr = {save, open};

        for(Button btn : basicArr) {
            btn.setMinWidth(90);
            btn.setCursor(Cursor.HAND);
            btn.setTextFill(Color.WHITE);
            btn.setStyle("-fx-background-color: #666;");
        }
        save.setStyle("-fx-background-color: #80334d;");
        open.setStyle("-fx-background-color: #80334d;");

        VBox btns = new VBox(15);
        btns.getChildren().addAll(drowbtn, rubberbtn, linebtn, textbtn, text, line_color, cpLine, fill_color, cpFill, line_width, slider, open, save);
        btns.setPadding(new Insets(5));
        btns.setStyle("-fx-background-color: white");
        btns.setPrefWidth(100);

        /* ----------Draw Canvas---------- */
        Canvas canvas = new Canvas(1080, 790);
        GraphicsContext gc;
        gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);

        Line line = new Line();

        canvas.setOnMousePressed(e-> {
            if (drowbtn.isSelected()) {
                gc.setStroke(cpLine.getValue());
                gc.beginPath();
                gc.lineTo(e.getX(), e.getY());
            } else if (rubberbtn.isSelected()) {
                double lineWidth = gc.getLineWidth();
                gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
            } else if (linebtn.isSelected()) {
                gc.setStroke(cpLine.getValue());
                line.setStartX(e.getX());
                line.setStartY(e.getY());
            }
            else if(textbtn.isSelected()) {
                gc.setLineWidth(1);
                gc.setFont(Font.font(slider.getValue()));
                gc.setStroke(cpLine.getValue());
                gc.setFill(cpFill.getValue());
                gc.fillText(text.getText(), e.getX(), e.getY());
                gc.strokeText(text.getText(), e.getX(), e.getY());
            }
        });

        canvas.setOnMouseDragged(e->{
            if(drowbtn.isSelected()) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            }
            else if(rubberbtn.isSelected()){
                double lineWidth = gc.getLineWidth();
                gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
            }
            });

        canvas.setOnMouseReleased(e->{
            if(drowbtn.isSelected()) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
                gc.closePath();
            }
            else if(rubberbtn.isSelected()) {
                double lineWidth = gc.getLineWidth();
                gc.clearRect(e.getX() - lineWidth / 2, e.getY() - lineWidth / 2, lineWidth, lineWidth);
            }
            else if(linebtn.isSelected()) {
                line.setEndX(e.getX());
                line.setEndY(e.getY());
                gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());

            }
        });
        // color picker
        cpLine.setOnAction(e->{
            gc.setStroke(cpLine.getValue());
        });
        cpFill.setOnAction(e->{
            gc.setFill(cpFill.getValue());
        });

        // slider
        slider.valueProperty().addListener(e->{
            double width = slider.getValue();
            if(textbtn.isSelected()){
                gc.setLineWidth(1);
                gc.setFont(Font.font(slider.getValue()));
                line_width.setText(String.format("%.1f", width));
                return;
            }
            line_width.setText(String.format("%.1f", width));
            gc.setLineWidth(width);
        });

        /*------- Save & Open ------*/
        // Open
        open.setOnAction((e)->{
            FileChooser openFile = new FileChooser();
            openFile.setTitle("Open File");
            File file = openFile.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    InputStream io = new FileInputStream(file);
                    Image img = new Image(io);
                    gc.drawImage(img, 0, 0);
                } catch (IOException ex) {
                    System.out.println("Error!");
                }
            }
        });

        // Save
        save.setOnAction((e)->{
            FileChooser savefile = new FileChooser();
            savefile.setTitle("Save File");

            File file = savefile.showSaveDialog(primaryStage);
            if (file != null) {
                try {
                    WritableImage writableImage = new WritableImage(1080, 790);
                    canvas.snapshot(null, writableImage);
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                    ImageIO.write(renderedImage, "png", file);
                } catch (IOException ex) {
                    System.out.println("Error!");
                }
            }

        });

        /* ----------STAGE & SCENE---------- */
        BorderPane pane = new BorderPane();
        pane.setLeft(btns);
        pane.setCenter(canvas);

        Scene scene = new Scene(pane, 1200, 800);

        primaryStage.setTitle("Paint");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * this kickstart the javaFx with launch(args).
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}