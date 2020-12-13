package socialnetwork.controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;


public class SpeechBox extends HBox {
    private Color DEFAULT_SENDER_COLOR = Color.GOLD;
    private Color DEFAULT_RECEIVER_COLOR = Color.LIMEGREEN;
    private Background DEFAULT_SENDER_BACKGROUND, DEFAULT_RECEIVER_BACKGROUND;
    private HBox container_reciver;
    private HBox container_sender;
    private String message;
    private SpeechDirection direction;
    private Integer TypeMessage;
    private Label displayedText;
    private SVGPath directionIndicator;
    private Long id_message;
    public SpeechBox(String message, SpeechDirection direction,Long IdM){
        this.message = message;
        this.direction = direction;
        this.id_message = IdM;
        initialiseDefaults();
        setupElements();
    }

    private void initialiseDefaults(){
        DEFAULT_SENDER_BACKGROUND = new Background(
                new BackgroundFill(DEFAULT_SENDER_COLOR, new CornerRadii(5,0,5,5,false), Insets.EMPTY));
        DEFAULT_RECEIVER_BACKGROUND = new Background(
                new BackgroundFill(DEFAULT_RECEIVER_COLOR, new CornerRadii(0,5,5,5,false), Insets.EMPTY));
    }

    private void setupElements(){
        displayedText = new Label(message);
        displayedText.wrapTextProperty().set(true);
        displayedText.setPadding(new Insets(5));
        displayedText.setWrapText(true);
        directionIndicator = new SVGPath();

        if(direction == SpeechDirection.LEFT){
            configureForReceiver();
        }
        else{
            configureForSender();
        }
    }
    private void configureForSender(){
        TypeMessage = 0;
        displayedText.setBackground(DEFAULT_SENDER_BACKGROUND);
        displayedText.setAlignment(Pos.CENTER_RIGHT);
        directionIndicator.setContent("M10 0 L0 10 L0 0 Z");
        directionIndicator.setFill(DEFAULT_SENDER_COLOR);

        container_sender = new HBox(displayedText, directionIndicator);
        //Use at most 75% of the width provided to the SpeechBox for displaying the message
        container_sender.maxWidthProperty().bind(widthProperty().multiply(0.75));
        getChildren().setAll(container_sender);
        setAlignment(Pos.CENTER_RIGHT);
    }

    private void configureForReceiver(){
        TypeMessage = 1;
        displayedText.setBackground(DEFAULT_RECEIVER_BACKGROUND);
        displayedText.setAlignment(Pos.CENTER_LEFT);
        directionIndicator.setContent("M0 0 L10 0 L10 10 Z");
        directionIndicator.setFill(DEFAULT_RECEIVER_COLOR);

        container_reciver = new HBox(directionIndicator, displayedText);
        //Use at most 75% of the width provided to the SpeechBox for displaying the message
        container_reciver.maxWidthProperty().bind(widthProperty().multiply(0.75));
        getChildren().setAll(container_reciver);
        setAlignment(Pos.CENTER_LEFT);
    }
    public String getMessage(){
        return message;
    }
    public HBox getContainerReceiver(){
        return container_reciver;
    }
    public HBox getContainer_sender(){
        return container_sender;
    }
    public Label getLabel(){
        return displayedText;
    }
    public SVGPath getDirectionIndicator(){
        return directionIndicator;
    }
    public Integer getTypeMessage(){
        return TypeMessage;
    }
    public Long getIdMessage(){
        return id_message;
    }
    public void setBackGroundColor(SpeechBox sp){
        if(sp.getTypeMessage() == 0){

            sp.getLabel().setBackground(DEFAULT_SENDER_BACKGROUND);
            sp.getDirectionIndicator().setFill(DEFAULT_SENDER_COLOR);
        }
        else{
            sp.getLabel().setBackground(DEFAULT_RECEIVER_BACKGROUND);
            sp.getDirectionIndicator().setFill(DEFAULT_RECEIVER_COLOR);
        }
    }

}