package com.rvandoosselaer.jmemodeleditor.gui;

import com.rvandoosselaer.jmemodeleditor.LightsState;
import com.rvandoosselaer.jmeutils.ApplicationGlobals;
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

    private LightsState lightsState;

    public SceneTab(Command<Tab> tabClickCommand) {
        super(null, tabClickCommand);
        lightsState = ApplicationGlobals.getInstance().getApplication().getStateManager().getState(LightsState.class);
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

        container.addChild(createBooleanInput(GuiTranslations.getInstance().t("panel.properties.lights.ambient"), lightsState.isAmbientLightEnabled(), checkBox -> lightsState.setAmbientLightEnabled(checkBox)));
        container.addChild(createColorRGBAInput(GuiTranslations.getInstance().t("panel.properties.lights.ambient.color"), lightsState.getAmbientLightColor(), color -> lightsState.setAmbientLightColor(color)));
        container.addChild(createSeparator());

        container.addChild(createBooleanInput(GuiTranslations.getInstance().t("panel.properties.lights.directional"), lightsState.isDirectionalLightEnabled(), checkBox -> lightsState.setDirectionalLightEnabled(checkBox)));
        container.addChild(createBooleanInput(GuiTranslations.getInstance().t("panel.properties.lights.directional.dir"), lightsState.isUpdateLightDir(), checkBox -> lightsState.setUpdateLightDir(checkBox)));
        container.addChild(createColorRGBAInput(GuiTranslations.getInstance().t("panel.properties.lights.directional.color"), lightsState.getDirectionalLightColor(), color -> lightsState.setDirectionalLightColor(color)));
        container.addChild(createSeparator());

        container.addChild(createBooleanInput(GuiTranslations.getInstance().t("panel.properties.lights.probe"), lightsState.isLightProbeEnabled(), checkBox -> lightsState.setLightProbeEnabled(checkBox)));
        container.addChild(createVector3fInput(GuiTranslations.getInstance().t("panel.properties.lights.probe.location"), lightsState.getLightProbeLocation(), location -> lightsState.setLightProbeLocation(location)));
        container.addChild(createFloatInput(GuiTranslations.getInstance().t("panel.properties.lights.probe.radius"), lightsState.getLightProbeRadius(), radius -> lightsState.setLightProbeRadius(radius)));
        container.addChild(createBooleanInput(GuiTranslations.getInstance().t("panel.properties.lights.probe.nature"), !LightsState.LightProbes.White.equals(lightsState.getCurrentProbe()), checkBox -> lightsState.loadLightProbe(checkBox ? LightsState.LightProbes.Nature : LightsState.LightProbes.White)));
        container.addChild(createBooleanInput(GuiTranslations.getInstance().t("panel.properties.lights.probe.debug"), lightsState.isLightsDebugEnabled(), checkBox -> lightsState.setLightsDebugEnabled(checkBox)));

        return container;
    }

}
