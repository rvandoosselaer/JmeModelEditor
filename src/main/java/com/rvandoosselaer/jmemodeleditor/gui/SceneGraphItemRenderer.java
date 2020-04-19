package com.rvandoosselaer.jmemodeleditor.gui;

import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.SpringGridLayout;

/**
 * Renders a {@link SceneGraphItem} in the {@link SceneGraphListBox}
 *
 * @author: rvandoosselaer
 */
public class SceneGraphItemRenderer extends AlternatingRowRenderer<SceneGraphItem> {

    private static final int INDENT_SIZE = 10;

    @Override
    public Panel getView(SceneGraphItem value, boolean selected, Panel existing) {
        if (existing == null) {
            existing = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.First, FillMode.Even), getAlternatingRowElementId());

            Label label = ((Container) existing).addChild(new Label(value.getSpatial().getName(), ELEMENT_ID));
            label.setIcon(createSpatialIcon(value.getSpatial()));
            QuadBackgroundComponent background = new QuadBackgroundComponent(ColorRGBA.BlackNoAlpha);
            background.setMargin(INDENT_SIZE * value.getDepth(), 0);
            label.setBackground(background);

            if (hasControls(value.getSpatial())) {
                Label controls = ((Container) existing).addChild(new Label(""));
                controls.setIcon(createControlIcon());
            }

            return existing;
        }

        Label label = (Label) ((SpringGridLayout) ((Container) existing).getLayout()).getChild(0, 0);
        label.setText(value.getSpatial().getName());
        label.setIcon(createSpatialIcon(value.getSpatial()));
        QuadBackgroundComponent background = (QuadBackgroundComponent) label.getBackground();
        background.setMargin(INDENT_SIZE * value.getDepth(), 0);

        if (hasControls(value.getSpatial())) {
            Label controls = ((Container) existing).addChild(new Label(""), 1, 0);
            controls.setIcon(createControlIcon());
        } else {
            Label controls = (Label) ((SpringGridLayout) ((Container) existing).getLayout()).getChild(1, 0);
            if (controls != null) {
                controls.removeFromParent();
            }
        }

        return existing;
    }

    private boolean hasControls(Spatial spatial) {
        return spatial.getNumControls() > 0;
    }

    private IconComponent createSpatialIcon(Spatial spatial) {
        String iconPath = spatial instanceof Node ? "/Interface/node.png" : "/Interface/geometry.png";
        IconComponent icon = new IconComponent(iconPath);
        icon.setMargin(4, 2);
        icon.setHAlignment(HAlignment.Left);
        icon.setVAlignment(VAlignment.Center);

        return icon;
    }

    private IconComponent createControlIcon() {
        IconComponent icon = new IconComponent("/Interface/control.png");
        icon.setMargin(10, 2);
        icon.setHAlignment(HAlignment.Center);
        icon.setVAlignment(VAlignment.Center);

        return icon;
    }

}
