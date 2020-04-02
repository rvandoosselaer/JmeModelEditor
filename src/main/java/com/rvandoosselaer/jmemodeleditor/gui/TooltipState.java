package com.rvandoosselaer.jmemodeleditor.gui;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.bounding.BoundingBox;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.event.DefaultMouseListener;
import com.simsilica.lemur.event.MouseEventControl;
import com.simsilica.lemur.style.ElementId;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

/**
 * An appstate that shows a tooltip on a spatial. The tooltip is rendered at the boundingbox center location of the
 * spatial.
 *
 * @author: rvandoosselaer
 */
public class TooltipState extends BaseAppState {

    private final Collection<Spatial> tooltipListeners = new ArrayList<>();

    private Node guiNode;
    private float delay = 500; // delay in milliseconds before showing the tooltip
    private Vector3f tooltipOffset = new Vector3f(0, -25, 0); // add an additional offset to the tooltip location
    private Function<String, Panel> tooltipRenderer = new DefaultTooltipRenderer();

    @Override
    protected void initialize(Application app) {
        guiNode = getState(GuiState.class).getGuiNode();
    }

    @Override
    protected void cleanup(Application app) {
        tooltipListeners.forEach(this::removeTooltipListener);
        tooltipListeners.clear();
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    public void addTooltip(Spatial spatial, String tooltip) {
        tooltipListeners.add(spatial);
        MouseEventControl.addListenersToSpatial(spatial, new TooltipListener(tooltip));
    }

    public void removeTooltip(Spatial spatial) {
        removeTooltipListener(spatial);
        tooltipListeners.remove(spatial);
    }

    private void removeTooltipListener(Spatial spatial) {
        MouseEventControl mouseEventControl = spatial.getControl(MouseEventControl.class);
        if (mouseEventControl == null) {
            return;
        }

        TooltipListener tooltipListener = mouseEventControl.getMouseListener(TooltipListener.class);
        if (tooltipListener != null) {
            mouseEventControl.removeMouseListener(tooltipListener);
        }
    }

    @RequiredArgsConstructor
    private class TooltipListener extends DefaultMouseListener {

        private final String tooltipText;
        private Panel tooltip;
        private long hoverStartTimestamp;

        @Override
        public void mouseEntered(MouseMotionEvent event, Spatial target, Spatial capture) {
            hoverStartTimestamp = System.currentTimeMillis();
            if (tooltip == null) {
                tooltip = createTooltip(target);
            }
        }

        @Override
        public void mouseMoved(MouseMotionEvent event, Spatial target, Spatial capture) {
            long currentTimestamp = System.currentTimeMillis();
            boolean showTooltip = currentTimestamp - hoverStartTimestamp >= delay;

            if (showTooltip && tooltip.getParent() == null) {
                guiNode.attachChild(tooltip);
            }
        }

        @Override
        public void mouseExited(MouseMotionEvent event, Spatial target, Spatial capture) {
            if (tooltip != null && tooltip.getParent() != null) {
                tooltip.removeFromParent();
            }

            hoverStartTimestamp = -1;
        }

        private Panel createTooltip(Spatial spatial) {
            Panel tooltip = tooltipRenderer.apply(tooltipText);

            Vector3f spatialCenter = new Vector3f(spatial.getWorldBound().getCenter());
            Vector3f spatialExtent = ((BoundingBox) spatial.getWorldBound()).getExtent(new Vector3f());

            // position the tooltip in the center of the spatial and add the tooltip offset
            Vector3f tooltipLocation = spatialCenter.addLocal(0, 0, spatialExtent.z + 10);
            tooltipLocation.addLocal(tooltipOffset);
            tooltip.setLocalTranslation(tooltipLocation);

            return tooltip;
        }

    }

    private static class DefaultTooltipRenderer implements Function<String, Panel> {

        @Override
        public Panel apply(String s) {
            return new Label(s, new ElementId("tooltip"));
        }

    }

}
