package com.rvandoosselaer.jmemodeleditor.gui;

import com.rvandoosselaer.jmeutils.gui.GuiTranslations;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.component.SpringGridLayout;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: rvandoosselaer
 */
public class ObjectTab extends Tab {

    private static final String ID = "object-tab";

    @Getter
    @Setter
    private Command<Void> refreshSceneGraphCommand;

    public ObjectTab(SceneGraphItem sceneGraphItem, Command<Tab> tabClickCommand) {
        super(sceneGraphItem, tabClickCommand);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Button getTab() {
        return createTab("/Interface/object.png", GuiTranslations.getInstance().t("panel.properties.object.tooltip"));
    }

    @Override
    public Panel getContent() {
        Container container = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));
        container.addChild(new Label(GuiTranslations.getInstance().t("panel.properties.object.title"), PropertiesPanel.ELEMENT_ID.child("title")));

        container.addChild(createTextField(GuiTranslations.getInstance().t("panel.properties.object.name"),
                sceneGraphItem.getSpatial().getName(), source -> {
                    sceneGraphItem.getSpatial().setName(source);
                    if (refreshSceneGraphCommand != null) {
                        refreshSceneGraphCommand.execute(null);
                    }
                }));

        return container;
    }
}
