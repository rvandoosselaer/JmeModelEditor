package com.rvandoosselaer.jmemodeleditor.gui;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.Joint;
import com.jme3.anim.SkinningControl;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.rvandoosselaer.jmeutils.ApplicationGlobals;
import com.rvandoosselaer.jmeutils.gui.GuiTranslations;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.component.SpringGridLayout;
import jme3utilities.debug.SkeletonVisualizer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: rvandoosselaer
 */
public class ControlsTab extends Tab {

    private static final String ID = "controls-tab";

    public ControlsTab(SceneGraphItem sceneGraphItem, Command<Tab> tabClickCommand) {
        super(sceneGraphItem, tabClickCommand);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Button getTab() {
        return createTab("/Interface/control.png", GuiTranslations.getInstance().t("panel.properties.controls.tooltip"));
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
                final ListBox<String>[] listBox = new ListBox[1];
                container.depthFirstTraversal(spatial -> {
                    if (spatial instanceof ListBox) {
                        listBox[0] = (ListBox<String>) spatial;
                    }
                });

                Button play = container.addChild(new Button("play"));
                play.addClickCommands(cmd -> {
                    Integer index = listBox[0].getSelectionModel().getSelection();
                    if (index != null && index >= 0 && index < listBox[0].getModel().size()) {
                        ((AnimComposer) control).setCurrentAction(listBox[0].getModel().get(index));
                    }
                });
                Button stop = container.addChild(new Button("stop"));
                stop.addClickCommands(cmd -> ((AnimComposer) control).reset());
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
            container.addChild(createSeparator());
        }
        return container;
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
