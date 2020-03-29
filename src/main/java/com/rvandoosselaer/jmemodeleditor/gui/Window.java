package com.rvandoosselaer.jmemodeleditor.gui;

import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.event.CursorEventControl;
import com.simsilica.lemur.event.DragHandler;
import com.simsilica.lemur.style.ElementId;
import lombok.Getter;

/**
 * A draggable window with a title bar, content container and button container.
 *
 * @author: rvandoosselaer
 */
public class Window extends Container {

    public static final ElementId ELEMENT_ID = new ElementId("window");

    @Getter
    private Label title;
    @Getter
    private Container container;
    @Getter
    private Container buttonContainer;
    private Container draggable;
    private DragHandler dragHandler;

    public Window(String title) {
        super(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even), ELEMENT_ID);

        draggable = addChild(new Container(new SpringGridLayout(), ELEMENT_ID.child("title-wrapper")));
        this.title = draggable.addChild(new Label(title, ELEMENT_ID.child("title-wrapper").child("title")));
        this.container = addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Even, FillMode.Even), ELEMENT_ID.child("content-wrapper")));
        this.buttonContainer = addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.Even, FillMode.Even), ELEMENT_ID.child("button-wrapper")));
        this.dragHandler = new DragHandler(input -> this);

        CursorEventControl.addListenersToSpatial(draggable, dragHandler);
    }

    public void cleanup() {
        if (isAttached()) {
            removeFromParent();
        }
        CursorEventControl.removeListenersFromSpatial(draggable, dragHandler);
    }

    public boolean isAttached() {
        return getParent() != null;
    }

}
