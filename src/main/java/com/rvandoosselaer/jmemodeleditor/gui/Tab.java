package com.rvandoosselaer.jmemodeleditor.gui;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.rvandoosselaer.jmeutils.ApplicationGlobals;
import com.rvandoosselaer.jmeutils.gui.GuiTranslations;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.style.ElementId;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * @author: rvandoosselaer
 */
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Tab {

    public static final ElementId ELEMENT_ID = PropertiesPanel.ELEMENT_ID.child("properties").child("tabs");

    @Setter
    @EqualsAndHashCode.Include
    protected SceneGraphItem sceneGraphItem;
    protected Command<Tab> tabClickCommand;

    @EqualsAndHashCode.Include
    public abstract String getId();

    public abstract Button getTab();

    public abstract Panel getContent();

    protected Button createTab(String iconPath, String tooltip) {
        Button button = new Button("", ELEMENT_ID.child(Button.ELEMENT_ID));
        button.setIcon(createTabIcon(iconPath));
        button.addClickCommands(cmd -> tabClickCommand.execute(this));
        getTooltipState().addTooltip(button, tooltip);

        return button;
    }

    protected Container createTextFieldInput(String string, String value, Command<String> updateValueAction) {
        Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.ForcedEven, FillMode.Even));

        container.addChild(new Label(string, getLabelElementId()));

        Container wrapper = container.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.First, FillMode.Even)));
        TextField textField = wrapper.addChild(new TextField(value, getTextFieldElementId()));

        Button update = wrapper.addChild(new Button(GuiTranslations.getInstance().t("common.set"), getButtonElementId()));
        update.addClickCommands(cmd -> updateValueAction.execute(textField.getText()));

        return container;
    }

    protected Container createVector3fInput(String string, Vector3f value, Command<Vector3f> updateValueAction) {
        Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.ForcedEven, FillMode.Even));

        Container labels = container.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Even, FillMode.Even)));
        labels.addChild(new Label(string + " x", getLabelElementId()));
        labels.addChild(new Label("y", getLabelElementId()));
        labels.addChild(new Label("z", getLabelElementId()));

        Container values = container.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.First, FillMode.Even)));
        Container vectorWrapper = values.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Even, FillMode.Even)));
        TextField x = vectorWrapper.addChild(new TextField(String.valueOf(value.x), getTextFieldElementId()));
        TextField y = vectorWrapper.addChild(new TextField(String.valueOf(value.y), getTextFieldElementId()));
        TextField z = vectorWrapper.addChild(new TextField(String.valueOf(value.z), getTextFieldElementId()));

        Button update = values.addChild(new Button(GuiTranslations.getInstance().t("common.set"), getButtonElementId()));
        update.addClickCommands(cmd -> updateValueAction.execute(new Vector3f(Float.parseFloat(x.getText()), Float.parseFloat(y.getText()), Float.parseFloat(z.getText()))));

        return container;
    }

    protected Container createQuaternionInput(String string, float[] angles, Command<float[]> updateValueAction) {
        Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.ForcedEven, FillMode.Even));

        Container labels = container.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Even, FillMode.Even)));
        labels.addChild(new Label(string + " x", getLabelElementId()));
        labels.addChild(new Label("y", getLabelElementId()));
        labels.addChild(new Label("z", getLabelElementId()));

        Container values = container.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.First, FillMode.Even)));
        Container vectorWrapper = values.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Even, FillMode.Even)));
        TextField x = vectorWrapper.addChild(new TextField(String.valueOf(angles[0] * FastMath.RAD_TO_DEG), getTextFieldElementId()));
        TextField y = vectorWrapper.addChild(new TextField(String.valueOf(angles[1] * FastMath.RAD_TO_DEG), getTextFieldElementId()));
        TextField z = vectorWrapper.addChild(new TextField(String.valueOf(angles[2] * FastMath.RAD_TO_DEG), getTextFieldElementId()));

        Button update = values.addChild(new Button(GuiTranslations.getInstance().t("common.set"), getButtonElementId()));
        update.addClickCommands(cmd -> {
            float[] newAngles = new float[]{Float.parseFloat(x.getText()), Float.parseFloat(y.getText()), Float.parseFloat(z.getText())};
            newAngles[0] *= FastMath.DEG_TO_RAD;
            newAngles[1] *= FastMath.DEG_TO_RAD;
            newAngles[2] *= FastMath.DEG_TO_RAD;
            updateValueAction.execute(newAngles);
        });

        return container;
    }

    protected Panel createSeparator() {
        return new Panel(2, 2, PropertiesPanel.ELEMENT_ID.child("properties").child("separator"), GuiState.STYLE);
    }

    private static ElementId getLabelElementId() {
        return PropertiesPanel.ELEMENT_ID.child("properties").child(Label.ELEMENT_ID);
    }

    private static ElementId getButtonElementId() {
        return PropertiesPanel.ELEMENT_ID.child("properties").child(Button.ELEMENT_ID);
    }

    private static ElementId getTextFieldElementId() {
        return PropertiesPanel.ELEMENT_ID.child("properties").child(TextField.ELEMENT_ID);
    }

    private static IconComponent createTabIcon(String iconPath) {
        IconComponent icon = new IconComponent(iconPath);
        icon.setIconSize(new Vector2f(20, 20));
        icon.setMargin(4, 4);
        icon.setHAlignment(HAlignment.Center);
        icon.setVAlignment(VAlignment.Center);
        return icon;
    }

    private TooltipState getTooltipState() {
        return ApplicationGlobals.getInstance().getApplication().getStateManager().getState(TooltipState.class);
    }

}
