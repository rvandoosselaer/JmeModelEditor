package com.rvandoosselaer.jmemodeleditor.gui;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.Joint;
import com.jme3.anim.SkinningControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.rvandoosselaer.jmeutils.ApplicationGlobals;
import com.rvandoosselaer.jmeutils.gui.GuiTranslations;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.SpringGridLayout;
import jme3utilities.debug.SkeletonVisualizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: rvandoosselaer
 */
public class ControlsTab extends Tab {

    public static final String ID = "controls-tab";

    public ControlsTab(SceneGraphItem sceneGraphItem, Command<Tab> tabClickCommand) {
        super(sceneGraphItem, tabClickCommand);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected String getTabIconPath() {
        return "/Interface/control.png";
    }

    @Override
    protected String getTabTooltip() {
        return GuiTranslations.getInstance().t("panel.properties.controls.tooltip");
    }

    @Override
    public Panel getContent() {
        Container container = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));
        container.addChild(new Label(GuiTranslations.getInstance().t("panel.properties.controls.title"), PropertiesPanel.ELEMENT_ID.child("title")));
        container.addChild(createSeparator());

        for (int i = 0; i < sceneGraphItem.getSpatial().getNumControls(); i++) {
            Control control = sceneGraphItem.getSpatial().getControl(i);
            if (control instanceof AnimComposer) {
                container.addChild(new Label(GuiTranslations.getInstance().t("common.animcomposer"), PropertiesPanel.ELEMENT_ID.child("title")));

                container.addChild(createStringListBox(GuiTranslations.getInstance().t("panel.properties.controls.anim.names"), new ArrayList<>(((AnimComposer) control).getAnimClipsNames())));

                Optional<ListBox<String>> listBox = getListBox(container);
                if (listBox.isPresent()) {
                    Container animButtons = container.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.First, FillMode.None))); // add in the seconds column
                    animButtons.addChild(new Panel(1, 1, ColorRGBA.BlackNoAlpha)); // spacer
                    Button play = animButtons.addChild(createIconButton("/Interface/play.png"));
                    play.addClickCommands(cmd -> getListBoxSelection(listBox.get()).ifPresent(((AnimComposer) control)::setCurrentAction));
                    Button stop = animButtons.addChild(createIconButton("/Interface/stop.png"));
                    stop.addClickCommands(cmd -> ((AnimComposer) control).reset());
                    // add a right margin to the last button, the size of the slider.
                    stop.setInsets(new Insets3f(2, 0, 2, 2 + listBox.get().getSlider().getPreferredSize().x));
                }
            } else if (control instanceof SkinningControl) {
                container.addChild(new Label(GuiTranslations.getInstance().t("common.skinningcontrol"), PropertiesPanel.ELEMENT_ID.child("title")));
                container.addChild(createBooleanInput(GuiTranslations.getInstance().t("panel.properties.controls.skinning.skeleton"),
                        isSkeletonVisible((SkinningControl) control), source -> showSkeleton(source, (SkinningControl) control)));

                Button bindPose = container.addChild(new Button(GuiTranslations.getInstance().t("panel.properties.controls.skinning.bindpose")));
                bindPose.addClickCommands(cmd -> ((SkinningControl) control).getArmature().applyBindPose());
                bindPose.setInsets(new Insets3f(4, 4, 4, 4));

                List<String> joints = ((SkinningControl) control).getArmature().getJointList().stream()
                        .map(Joint::getName)
                        .collect(Collectors.toList());
                container.addChild(createStringListBox(GuiTranslations.getInstance().t("panel.properties.controls.skinning.joints"), joints));
            }
        }
        return container;
    }

    private static Button createIconButton(String iconPath) {
        Button button = new Button("");
        button.setInsets(new Insets3f(2, 0, 2, 2));
        IconComponent icon = new IconComponent(iconPath);
        icon.setIconSize(new Vector2f(12, 12));
        icon.setMargin(4, 4);
        icon.setHAlignment(HAlignment.Center);
        icon.setVAlignment(VAlignment.Center);
        button.setIcon(icon);

        return button;
    }

    private Optional<ListBox<String>> getListBox(Spatial spatial) {
        if (spatial instanceof ListBox) {
            ListBox<String> listBox = (ListBox<String>) spatial;
            return Optional.of(listBox);
        }

        if (spatial instanceof Node) {
            for (Spatial s : ((Node) spatial).getChildren()) {
                //return getListBox(s);
                Optional<ListBox<String>> result = getListBox(s);
                if (result.isPresent()) {
                    return result;
                }
            }
        }

        return Optional.empty();
    }

    private Optional<String> getListBoxSelection(ListBox<String> listBox) {
        Integer index = listBox.getSelectionModel().getSelection();
        return Optional.ofNullable(index != null ? listBox.getModel().get(index) : null);
    }

    private void showSkeleton(boolean visible, SkinningControl skinningControl) {
        Spatial spatial = skinningControl.getSpatial();
        SkeletonVisualizer skeletonVisualizer = spatial.getControl(SkeletonVisualizer.class);
        if (skeletonVisualizer == null) {
            skeletonVisualizer = new SkeletonVisualizer(ApplicationGlobals.getInstance().getApplication().getAssetManager(), skinningControl);
            if (spatial instanceof Geometry) {
                spatial.getParent().addControl(skeletonVisualizer);
            } else {
                spatial.addControl(skeletonVisualizer);
            }
        }

        skeletonVisualizer.setEnabled(visible);
    }

    private boolean isSkeletonVisible(SkinningControl skinningControl) {
        Spatial spatial = skinningControl.getSpatial();
        SkeletonVisualizer skeletonVisualizer = spatial.getControl(SkeletonVisualizer.class);
        if (skeletonVisualizer == null) {
            skeletonVisualizer = spatial.getParent().getControl(SkeletonVisualizer.class);
        }

        return skeletonVisualizer != null && skeletonVisualizer.isEnabled();
    }
}
