package com.rvandoosselaer.jmemodeleditor.gui;

import com.rvandoosselaer.jmeutils.gui.GuiTranslations;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.component.SpringGridLayout;

/**
 * The scene tab
 *
 * @author: rvandoosselaer
 */
public class SceneTab extends Tab {

    public static final String ID = "scene-tab";

    public SceneTab(Command<Tab> tabClickCommand) {
        super(null, tabClickCommand);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected String getTabIconPath() {
        return "/Interface/scene.png";
    }

    @Override
    protected String getTabTooltip() {
        return GuiTranslations.getInstance().t("panel.properties.scene.tooltip");
    }

    @Override
    public Panel getContent() {
        Container container = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));
        container.addChild(new Label(GuiTranslations.getInstance().t("panel.properties.scene.title"), PropertiesPanel.ELEMENT_ID.child("title")));

        return container;
    }

}
