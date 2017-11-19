package com.beamcalculate.model.page_manager;

import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/**
 * Created by Ruolin on 19/11/2017 for BeamCalculator.
 */
public class PageScaleHandler {
    private final double SCALE_DELTA = 1.1;

    public void AddScaleListener(Node container, Pane scalabelPane, double initialHeight, double initialWidth){
        container.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                if (event.getDeltaY() == 0) {
                    return;
                }

                double scaleFactor =
                        (event.getDeltaY() > 0)
                                ? SCALE_DELTA
                                : 1 / SCALE_DELTA;

                scalabelPane.setScaleX(scalabelPane.getScaleX() * scaleFactor);
                scalabelPane.setScaleY(scalabelPane.getScaleY() * scaleFactor);
                scalabelPane.setMinHeight(scalabelPane.getHeight() * scaleFactor);
                scalabelPane.setMinWidth(scalabelPane.getWidth() * scaleFactor);
                event.consume();
            }
        });

        container.setOnMousePressed(event -> {
            if (event.getClickCount() == 2) {
                scalabelPane.setScaleX(1.0);
                scalabelPane.setScaleY(1.0);
                scalabelPane.setMinHeight(initialHeight);
                scalabelPane.setMinWidth(initialWidth);
            }
        });

        container.layoutBoundsProperty().addListener((observable, oldBounds, bounds) ->
                container.setClip(new Rectangle(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight()))
        );
    }
}
