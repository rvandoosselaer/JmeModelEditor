package com.rvandoosselaer.jmemodeleditor.gui;

import com.jme3.math.Quaternion;
import com.jme3.scene.Node;
import com.rvandoosselaer.jmeutils.gui.GuiTranslations;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.component.SpringGridLayout;

/**
 * @author: rvandoosselaer
 */
public class ObjectTab extends Tab {

    public static final String ID = "object-tab";

    private final Command<Void> refreshSceneGraphCommand;

    public ObjectTab(SceneGraphItem sceneGraphItem, Command<Tab> tabClickCommand, Command<Void> refreshSceneGraphCommand) {
        super(sceneGraphItem, tabClickCommand);
        this.refreshSceneGraphCommand = refreshSceneGraphCommand;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected String getTabIconPath() {
        return "/Interface/object.png";
    }

    @Override
    protected String getTabTooltip() {
        return  GuiTranslations.getInstance().t("panel.properties.object.tooltip");
    }

    @Override
    public Panel getContent() {
        Container container = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));
        String title = sceneGraphItem.getSpatial() instanceof Node ? GuiTranslations.getInstance().t("common.node") : GuiTranslations.getInstance().t("common.geometry");
        container.addChild(new Label(title, PropertiesPanel.ELEMENT_ID.child("title")));

        // name
        container.addChild(createTextFieldInput(GuiTranslations.getInstance().t("panel.properties.object.name"),
                sceneGraphItem.getSpatial().getName(), source -> {
                    sceneGraphItem.getSpatial().setName(source);
                    if (refreshSceneGraphCommand != null) {
                        refreshSceneGraphCommand.execute(null);
                    }
                }));
        container.addChild(createSeparator());
        // location
        container.addChild(createVector3fInput(GuiTranslations.getInstance().t("panel.properties.object.location"),
                sceneGraphItem.getSpatial().getLocalTranslation(),
                source -> sceneGraphItem.getSpatial().setLocalTranslation(source)));
        container.addChild(createSeparator());
        // rotation
        container.addChild(createQuaternionInput(GuiTranslations.getInstance().t("panel.properties.object.rotation"),
                sceneGraphItem.getSpatial().getLocalRotation().toAngles(new float[3]),
                source -> sceneGraphItem.getSpatial().setLocalRotation(new Quaternion(source))));
        container.addChild(createSeparator());
        // scale
        container.addChild(createVector3fInput(GuiTranslations.getInstance().t("panel.properties.object.scale"),
                sceneGraphItem.getSpatial().getLocalScale(),
                source -> sceneGraphItem.getSpatial().setLocalScale(source)));
        container.addChild(createSeparator());
        // cullhint
        container.addChild(createLabelInput(GuiTranslations.getInstance().t("panel.properties.object.cullhint"), sceneGraphItem.getSpatial().getCullHint().toString()));

        return container;
    }
}
