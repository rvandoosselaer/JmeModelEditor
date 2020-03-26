package com.rvandoosselaer.jmemodeleditor.gui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.rvandoosselaer.jmeutils.gui.GuiUtils;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.style.ElementId;

/**
 * @author: rvandoosselaer
 */
public class GuiState extends BaseAppState {

    public static final String STYLE = "editor-style";
    public static final String DARK_STYLE_RESOURCE = "dark-style.groovy";

    private Node guiNode;
    private Container toolbar;
    private float zIndex = 99;

    @Override
    protected void initialize(Application app) {
        guiNode = ((SimpleApplication) app).getGuiNode();
        toolbar = createToolbar();
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        guiNode.attachChild(toolbar);
    }

    @Override
    protected void onDisable() {
        toolbar.removeFromParent();
    }

    @Override
    public void update(float tpf) {
        refreshLayout();
    }

    private void refreshLayout() {
        int w = GuiUtils.getWidth();
        int h = GuiUtils.getHeight();

        layoutToolbar(toolbar, w, h);
    }

    private void layoutToolbar(Container toolbar, int width, int height) {
        toolbar.setPreferredSize(toolbar.getPreferredSize().setX(width));
        toolbar.setLocalTranslation(0, height, zIndex);
    }

    private Container createToolbar() {
        Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.None, FillMode.None), new ElementId("toolbar"));
        Button open = container.addChild(createToolbarButton("/Interface/open.png"));
        Button save = container.addChild(createToolbarButton("/Interface/save.png"));

        return container;
    }

    private static Button createToolbarButton(String iconPath) {
        Button button = new Button("", new ElementId("toolbar").child("button"));
        IconComponent icon = new IconComponent(iconPath);
        icon.setIconSize(new Vector2f(16, 16));
        icon.setMargin(2, 2);
        icon.setHAlignment(HAlignment.Center);
        icon.setVAlignment(VAlignment.Center);
        button.setIcon(icon);

        return button;
    }

}
