package com.rvandoosselaer.jmemodeleditor.gui;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.shader.VarType;
import com.jme3.texture.Texture2D;
import com.rvandoosselaer.jmeutils.gui.GuiTranslations;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.component.SpringGridLayout;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: rvandoosselaer
 */
@Slf4j
public class MaterialTab extends Tab {

    private static final String ID = "material-tab";

    public MaterialTab(SceneGraphItem sceneGraphItem, Command<Tab> tabClickCommand) {
        super(sceneGraphItem, tabClickCommand);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Button getTab() {
        return createTab("/Interface/material.png", GuiTranslations.getInstance().t("panel.properties.material.tooltip"));
    }

    @Override
    public Panel getContent() {
        Container container = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));
        if (!(sceneGraphItem.getSpatial() instanceof Geometry)) {
            return container;
        }
        container.addChild(new Label(GuiTranslations.getInstance().t("panel.properties.material.title"), PropertiesPanel.ELEMENT_ID.child("title")));

        Material material = ((Geometry) sceneGraphItem.getSpatial()).getMaterial();
        // material def
        container.addChild(createLabelInput(GuiTranslations.getInstance().t("panel.properties.material.def"), material.getMaterialDef().getName()));
        container.addChild(createSeparator());
        // material name
        container.addChild(createTextFieldInput(GuiTranslations.getInstance().t("panel.properties.material.name"),
                material.getName(),
                material::setName));
        // material params
        for (int i = 0; i < material.getParamsMap().size(); i++) {
            String key = material.getParamsMap().getKey(i);
            MatParam value = material.getParamsMap().getValue(i);

            switch (value.getVarType()) {
                case Texture2D:
                    container.addChild(createLabelInput(key, ((Texture2D) value.getValue()).getName()));
                    break;
                case Float:
                    container.addChild(createFloatInput(key, (float) value.getValue(),
                            source -> material.setParam(key, VarType.Float, source)));
                    break;
                case Int:
                    container.addChild(createIntInput(key, (int) value.getValue(),
                            source -> material.setParam(key, VarType.Int, source)));
                    break;
                case Boolean:
                    container.addChild(createBooleanInput(key, (boolean) value.getValue(),
                            source -> material.setParam(key, VarType.Boolean, source)));
                    break;
                case Vector4:
                    if (value.getValue() instanceof ColorRGBA) {
                        container.addChild(createColorRGBAInput(key, (ColorRGBA) value.getValue(),
                                source -> material.setParam(key, VarType.Vector4, source)));
                    }
                    break;
                default:
                    log.warn("VarType {} not supported.", value.getVarType());
            }
        }

        return container;
    }
}
