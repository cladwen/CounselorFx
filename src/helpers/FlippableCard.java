/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.*;
import javafx.util.Duration;

/**
 *
 * FlippableImage
 *
 * Two images back to back that can be flipped 180 degrees, such as turning over a playing card.
 *
 * @author Randy A. Meyer
 *
 * March 2013
 *
 */
public class FlippableCard extends StackPane {

    private final Timeline flipForward;
    private final Timeline flipBackward;
    private boolean isFlipped = false;
    private final double FLIP_SECS = 0.3;

    public static FlippableCard createInstance(Image urlFront, Image urlBack, Point2D point) {
        return new FlippableCard(urlFront, urlBack, point);
    }

    private FlippableCard(Image imgFront, Image imgBack, Point2D point) {
        this.setRotationAxis(Rotate.Y_AXIS);

        final ImageView back = new ImageView(imgBack);
        final ImageView front = new ImageView(imgFront);

        //setting behavior
        front.setPreserveRatio(true);
        double zoomFactorCurrent = 1d;
        front.setFitHeight(12 * zoomFactorCurrent);
        front.setX((point.getX() + 46) * zoomFactorCurrent);
        front.setY((point.getY() + 30) * zoomFactorCurrent);
        back.setPreserveRatio(true);

        Rotate backRot = new Rotate(180, Rotate.Y_AXIS);
        backRot.setPivotX(back.prefWidth(USE_PREF_SIZE) / 2);
        back.getTransforms().add(backRot);
        this.getChildren().addAll(back, front);

        flipForward = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(this.rotateProperty(), 0d)),
                new KeyFrame(Duration.seconds(FLIP_SECS / 2), (ActionEvent t) -> {
                    back.toFront();
                },
                        new KeyValue(this.rotateProperty(), 90d)),
                new KeyFrame(Duration.seconds(FLIP_SECS), new KeyValue(this.rotateProperty(), 180d)));

        flipBackward = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(this.rotateProperty(), 180d)),
                new KeyFrame(Duration.seconds(FLIP_SECS / 2), (ActionEvent t) -> {
                    front.toFront();
                }, new KeyValue(this.rotateProperty(), 90d)),
                new KeyFrame(Duration.seconds(FLIP_SECS), new KeyValue(this.rotateProperty(), 0d)));
    }

    public void flip() {
        if (isFlipped) {
            flipBackward.play();
        } else {
            flipForward.play();
        }
        isFlipped = !isFlipped;
    }

    /*
    Call as
                RotateTransition rotator = createRotator(combatSprite);
                rotator.play();
     */
    private RotateTransition createRotator(Node card) {
        RotateTransition rotator = new RotateTransition(Duration.millis(10000), card);
        rotator.setAxis(Rotate.Y_AXIS);
        rotator.setFromAngle(0);
        rotator.setToAngle(360);
        rotator.setInterpolator(Interpolator.LINEAR);
        rotator.setCycleCount(10);

        return rotator;
    }
}
