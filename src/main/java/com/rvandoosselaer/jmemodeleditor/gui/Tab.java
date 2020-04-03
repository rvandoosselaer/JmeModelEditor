package com.rvandoosselaer.jmemodeleditor.gui;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.rvandoosselaer.jmeutils.ApplicationGlobals;
import com.rvandoosselaer.jmeutils.gui.GuiTranslations;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Checkbox;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.DefaultCheckboxModel;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.core.VersionedList;
import com.simsilica.lemur.list.CellRenderer;
import com.simsilica.lemur.style.ElementId;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author: rvandoosselaer
 */
@Slf4j
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
        TextField textField = wrapper.addChild(new TextField(value == null ? "" : value, getTextFieldElementId()));

        Button update = wrapper.addChild(new Button(GuiTranslations.getInstance().t("common.set"), getButtonElementId()));
        update.addClickCommands(cmd -> updateValueAction.execute(textField.getText()));

        return container;
    }

    protected Container createFloatInput(String string, float value, Command<Float> updateValueAction) {
        Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.ForcedEven, FillMode.Even));

        container.addChild(new Label(string, getLabelElementId()));

        Container wrapper = container.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.First, FillMode.Even)));
        TextField textField = wrapper.addChild(new TextField(Float.toString(value), getTextFieldElementId()));

        Button update = wrapper.addChild(new Button(GuiTranslations.getInstance().t("common.set"), getButtonElementId()));
        update.addClickCommands(cmd -> {
            try {
                String val = cleanFloat(textField);
                updateValueAction.execute(Float.valueOf(val));
            } catch (Exception e) {
                log.error("{}", e.getMessage());
            }
        });

        return container;
    }

    protected Container createIntInput(String string, int value, Command<Integer> updateValueAction) {
        Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.ForcedEven, FillMode.Even));

        container.addChild(new Label(string, getLabelElementId()));

        Container wrapper = container.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.First, FillMode.Even)));
        TextField textField = wrapper.addChild(new TextField(Float.toString(value), getTextFieldElementId()));

        Button update = wrapper.addChild(new Button(GuiTranslations.getInstance().t("common.set"), getButtonElementId()));
        update.addClickCommands(cmd -> {
            try {
                String val = cleanInt(textField);
                updateValueAction.execute(Integer.valueOf(val));
            } catch (Exception e) {
                log.error("{}", e.getMessage());
            }
        });

        return container;
    }

    protected Container createBooleanInput(String string, boolean value, Command<Boolean> updateValueAction) {
        Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.ForcedEven, FillMode.Even));
        container.addChild(new Label(string, getLabelElementId()));

        Checkbox checkbox = container.addChild(new Checkbox("", new DefaultCheckboxModel(value), getCheckboxElementId(), GuiState.STYLE));
        checkbox.addClickCommands(cmd -> updateValueAction.execute(checkbox.isChecked()));

        return container;
    }

    protected Container createLabelInput(String string, String value) {
        Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.ForcedEven, FillMode.Even));

        container.addChild(new Label(string, getLabelElementId()));
        container.addChild(new Label(value, getLabelROElementId()));

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
        update.addClickCommands(cmd -> {
            try {
                String valX = cleanFloat(x);
                String valY = cleanFloat(y);
                String valZ = cleanFloat(z);
                updateValueAction.execute(new Vector3f(Float.parseFloat(valX), Float.parseFloat(valY), Float.parseFloat(valZ)));
            } catch (Exception e) {
                log.error("{}", e.getMessage());
            }
        });

        return container;
    }

    protected Container createColorRGBAInput(String string, ColorRGBA value, Command<ColorRGBA> updateValueAction) {
        Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.ForcedEven, FillMode.Even));
        container.addChild(new Label(string, getLabelElementId()));

        Container values = container.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.First, FillMode.Even)));
        Container colors = values.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.ForcedEven, FillMode.Even)));
        TextField red = colors.addChild(new TextField(String.valueOf(value.r), getTextFieldElementId()));
        TextField green = colors.addChild(new TextField(String.valueOf(value.g), getTextFieldElementId()));
        TextField blue = colors.addChild(new TextField(String.valueOf(value.b), getTextFieldElementId()));
        TextField alpha = colors.addChild(new TextField(String.valueOf(value.a), getTextFieldElementId()));

        Button update = values.addChild(new Button(GuiTranslations.getInstance().t("common.set"), getButtonElementId()));
        update.addClickCommands(cmd -> {
            try {
                String valX = cleanFloat(red);
                String valY = cleanFloat(green);
                String valZ = cleanFloat(blue);
                String valW = cleanFloat(alpha);
                updateValueAction.execute(new ColorRGBA(Float.parseFloat(valX), Float.parseFloat(valY), Float.parseFloat(valZ), Float.parseFloat(valW)));
            } catch (Exception e) {
                log.error("{}", e.getMessage());
            }
        });

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
            try {
                String valX = cleanFloat(x);
                String valY = cleanFloat(y);
                String valZ = cleanFloat(z);
                float[] newAngles = new float[]{Float.parseFloat(valX), Float.parseFloat(valY), Float.parseFloat(valZ)};
                newAngles[0] *= FastMath.DEG_TO_RAD;
                newAngles[1] *= FastMath.DEG_TO_RAD;
                newAngles[2] *= FastMath.DEG_TO_RAD;
                updateValueAction.execute(newAngles);
            } catch (Exception e) {
                log.error("{}", e.getMessage());
            }
        });

        return container;
    }

    protected Container createStringListBox(String string, List<String> list) {
        Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.Last, FillMode.Even));
        Label label = container.addChild(new Label(string, getLabelElementId()));
        label.setTextVAlignment(VAlignment.Top);

        ListBox<String> listBox = container.addChild(new ListBox<>(new VersionedList<>(list), PropertiesPanel.ELEMENT_ID.child(ListBox.ELEMENT_ID), GuiState.STYLE));
        listBox.setVisibleItems(6);
        listBox.setCellRenderer(createStringCellRenderer());

        return container;
    }

    protected Panel createSeparator() {
        return new Panel(2, 2, PropertiesPanel.ELEMENT_ID.child("properties").child("separator"), GuiState.STYLE);
    }

    private static CellRenderer<String> createStringCellRenderer() {
        return new CellRenderer<String>() {

            private boolean odd;

            @Override
            public Panel getView(String value, boolean selected, Panel existing) {
                Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.Even, FillMode.Even), updateAlternatingRowElementId());
                container.addChild(new Label(value, SceneGraphItemRenderer.ELEMENT_ID));

                return container;
            }

            private ElementId updateAlternatingRowElementId() {
                ElementId elementId = SceneGraphItemRenderer.ELEMENT_ID.child(odd ? "odd" : "even");
                odd = !odd;

                return elementId;
            }
        };
    }

    private static String cleanFloat(TextField textField) {
        String value = textField.getText().replaceAll(",", ".");
        value = value.trim();
        return value.length() == 0 ? "0" : value;
    }

    private static String cleanInt(TextField textField) {
        String value = textField.getText();
        value = value.trim();
        return value.length() == 0 ? "0" : value;
    }

    private static ElementId getLabelElementId() {
        return PropertiesPanel.ELEMENT_ID.child("properties").child(Label.ELEMENT_ID);
    }

    private static ElementId getLabelROElementId() {
        return PropertiesPanel.ELEMENT_ID.child("properties").child("label-ro");
    }

    private static ElementId getButtonElementId() {
        return PropertiesPanel.ELEMENT_ID.child("properties").child(Button.ELEMENT_ID);
    }

    private static ElementId getTextFieldElementId() {
        return PropertiesPanel.ELEMENT_ID.child("properties").child(TextField.ELEMENT_ID);
    }

    private static ElementId getCheckboxElementId() {
        return PropertiesPanel.ELEMENT_ID.child("properties").child(Checkbox.ELEMENT_ID);
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
