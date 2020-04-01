package com.rvandoosselaer.jmemodeleditor.gui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.rvandoosselaer.jmemodeleditor.EditorState;
import com.rvandoosselaer.jmeutils.gui.GuiUtils;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.core.VersionedHolder;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.style.ElementId;

import java.nio.file.Path;

/**
 * The state that manages all GUI elements and user interactions.
 *
 * @author: rvandoosselaer
 */
public class GuiState extends BaseAppState {

    public static final String STYLE = "editor-style";
    public static final String DARK_STYLE_RESOURCE = "dark-style.groovy";

    private Node guiNode;
    private Container toolbar;
    private OpenFileWindow openFileWindow;
    private PropertiesPanel propertiesPanel;
    private float zIndex = 99;
    private EditorState editorState;
    private Label fpsLabel;
    private VersionedHolder<Integer> fps = new VersionedHolder<>(0);
    private VersionedReference<Integer> fpsRef = fps.createReference();
    private int frameCounter;
    private float timeCounter;

    @Override
    protected void initialize(Application app) {
        toolbar = createToolbar();
        fpsLabel = createFpsLabel();
        openFileWindow = createOpenFileWindow();
        propertiesPanel = createPropertiesPanel();

        editorState = getState(EditorState.class);
        guiNode = ((SimpleApplication) app).getGuiNode();
    }

    @Override
    protected void cleanup(Application app) {
        openFileWindow.cleanup();
    }

    @Override
    protected void onEnable() {
        guiNode.attachChild(toolbar);
        guiNode.attachChild(fpsLabel);
        guiNode.attachChild(propertiesPanel);
    }

    @Override
    protected void onDisable() {
        toolbar.removeFromParent();
        fpsLabel.removeFromParent();
        propertiesPanel.removeFromParent();
        if (openFileWindow.getParent() != null) {
            openFileWindow.removeFromParent();
        }
    }

    @Override
    public void update(float tpf) {
        calculateFps(tpf);
        if (fpsRef.update()) {
            fpsLabel.setText(String.format("%d", fpsRef.get()));
        }
        refreshLayout();
    }

    public void loadModel(Path path) {
        Spatial model = editorState.loadModel(path);
        propertiesPanel.setModel(model);
    }

    private void onOpenFile() {
        if (!openFileWindow.isAttached()) {
            // the Lemur ListBox has soms 'quirks'. It needs to be rendered before it's size is correctly calculated.
            // that's why we layout the modal 1 frame after it's attached
            getApplication().enqueue(() -> layoutOpenFileWindow(openFileWindow, GuiUtils.getWidth(), GuiUtils.getHeight()));
            guiNode.attachChild(openFileWindow);
        }
    }

    private void refreshLayout() {
        int w = GuiUtils.getWidth();
        int h = GuiUtils.getHeight();

        layoutToolbar(toolbar, w, h);
        layoutFpsLabel(fpsLabel, w, h);
        layoutPropertiesPanel(propertiesPanel, w, h);
    }

    private void layoutToolbar(Container toolbar, int width, int height) {
        toolbar.setPreferredSize(toolbar.getPreferredSize().setX(width));
        toolbar.setLocalTranslation(0, height, zIndex);
    }

    private void layoutFpsLabel(Label label, int width, int height) {
        label.setLocalTranslation(width - label.getPreferredSize().x, height, zIndex + 1);
    }

    private void layoutPropertiesPanel(PropertiesPanel propertiesPanel, int width, int height) {
        propertiesPanel.setPreferredSize(propertiesPanel.getPreferredSize().setX(width * 0.25f).setY(height - toolbar.getPreferredSize().y));
        propertiesPanel.setLocalTranslation(width * 0.75f, height - toolbar.getPreferredSize().y, zIndex);
    }

    private void layoutOpenFileWindow(OpenFileWindow window, int width, int height) {
        window.setPreferredSize(window.getPreferredSize().setX(width * 0.8f));
        GuiUtils.center(window);
        window.move(0, 0, zIndex + 10);
    }

    private void calculateFps(float tpf) {
        timeCounter += tpf;
        frameCounter++;
        if (timeCounter >= 1.0f) {
            fps.setObject((int) (frameCounter / timeCounter));
            timeCounter = 0;
            frameCounter = 0;
        }
    }

    private PropertiesPanel createPropertiesPanel() {
        PropertiesPanel propertiesPanel = new PropertiesPanel();
        return propertiesPanel;
    }

    private Label createFpsLabel() {
        return new Label("0");
    }

    private Container createToolbar() {
        Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.None, FillMode.None), new ElementId("toolbar"));
        Button open = container.addChild(createToolbarButton("/Interface/open.png"));
        open.addClickCommands(source -> onOpenFile());
        Button save = container.addChild(createToolbarButton("/Interface/save.png"));

        return container;
    }

    private OpenFileWindow createOpenFileWindow() {
        return new OpenFileWindow();
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
