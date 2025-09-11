package Ass1;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HeaderBar extends HBox {
    private final Label nowLabel = new Label();//for current time
    private final Label startLabel = new Label();//for program start time
    private final Timeline timeline;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public HeaderBar(String titleText) {
        super(12);
        Text title = new Text(titleText);
        title.setFont(Font.font("System", FontWeight.BOLD, 22));

        nowLabel.setStyle("-fx-font-size: 14px; -fx-opacity: 0.85;");
        startLabel.setStyle("-fx-font-size: 14px; -fx-opacity: 0.85;");

        //Separate the title and time
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        this.getChildren().addAll(
                title, spacer,
                new Label("Program start time:"), startLabel,
                new Label("Current Date & Time:"), nowLabel
        );
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(10));
        this.setStyle(
                "-fx-background-color: linear-gradient(to right, #f6f8fa, #eef3ff);" +
                "-fx-border-color: #cfd8ff; -fx-border-width: 0 0 1 0;"
        );

        // Set startup time
        startLabel.setText(LocalDateTime.now().format(FMT));

        // Time line updates UI on FX thread safely
        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> nowLabel.setText(LocalDateTime.now().format(FMT))),//update time immediately upon startup
                new KeyFrame(Duration.seconds(1))//execute every second
        );
        timeline.setCycleCount(Timeline.INDEFINITE);//infinite loop
    }

    public void startClock() { timeline.play(); }//starting renew time
    public void stopClock()  { timeline.stop(); }//ending renew time
}
