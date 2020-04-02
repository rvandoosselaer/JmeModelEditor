package com.rvandoosselaer.jmemodeleditor.gui;

import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.rvandoosselaer.jmeutils.gui.GuiTranslations;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.core.VersionedList;
import com.simsilica.lemur.list.CellRenderer;
import com.simsilica.lemur.style.ElementId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author: rvandoosselaer
 */
public class PropertiesPanel extends Container {

    public static final ElementId ELEMENT_ID = new ElementId("properties");

    private Container sceneGraph;
    private SceneGraphListBox sceneGraphListBox;

    public PropertiesPanel() {
        super(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even), ELEMENT_ID);

        buildSceneGraph();
    }

    public void setModel(Spatial spatial) {
        sceneGraphListBox.getModel().clear();

        addSceneGraphEntries(spatial, 0);
    }

    private void buildSceneGraph() {
        sceneGraph = addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even), ELEMENT_ID.child("scenegraph")));
        sceneGraph.addChild(new Label(GuiTranslations.getInstance().t("properties.scenegraph"), ELEMENT_ID.child("title")));

        sceneGraphListBox = sceneGraph.addChild(new SceneGraphListBox());
        sceneGraphListBox.setVisibleItems(10);
        sceneGraphListBox.addControl(new ListBoxSliderControl());
    }

    private void addSceneGraphEntries(Spatial spatial, int depth) {
        sceneGraphListBox.addItem(new SceneGraphItem(spatial, depth));

        if (spatial instanceof Node) {
            depth++;
            for (Spatial s : ((Node) spatial).getChildren()) {
                addSceneGraphEntries(s, depth);
            }
        }
    }

    private static class SceneGraphListBox extends ListBox<SceneGraphItem> {

        public static final ElementId ELEMENT_ID = PropertiesPanel.ELEMENT_ID.child(ListBox.ELEMENT_ID);

        public SceneGraphListBox() {
            super(new VersionedList<>(), ELEMENT_ID, GuiState.STYLE);
            setCellRenderer(new SceneGraphItemRenderer());
        }

        public void addItem(SceneGraphItem item) {
            getModel().add(item);
        }

    }

    private static class SceneGraphItemRenderer implements CellRenderer<SceneGraphItem> {

        private boolean odd;
        public static final ElementId ELEMENT_ID = SceneGraphListBox.ELEMENT_ID.child("item");
        private final int INDENT_SIZE = 10;

        @Override
        public Panel getView(SceneGraphItem value, boolean selected, Panel existing) {
            Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.First, FillMode.Even), updateAlternatingRowElementId());

            Label label = container.addChild(new Label(value.getSpatial().getName(), ELEMENT_ID));
            label.setIcon(createSpatialIcon(value.getSpatial()));
            QuadBackgroundComponent background = new QuadBackgroundComponent(ColorRGBA.BlackNoAlpha);
            background.setMargin(INDENT_SIZE * value.getDepth(), 0);
            label.setBackground(background);

            if (hasControls(value.getSpatial())) {
                Label controls = container.addChild(new Label(""));
                controls.setIcon(createControlIcon());
                container.addChild(controls);
            }

            return container;
        }

        private boolean hasControls(Spatial spatial) {
            return spatial.getNumControls() > 0;
        }

        private IconComponent createSpatialIcon(Spatial spatial) {
            String iconPath = spatial instanceof Node ? "/Interface/node.png" : "/Interface/geometry.png";
            IconComponent icon = new IconComponent(iconPath);
            icon.setMargin(4, 2);
            icon.setHAlignment(HAlignment.Left);
            icon.setVAlignment(VAlignment.Center);

            return icon;
        }

        private IconComponent createControlIcon() {
            IconComponent icon = new IconComponent("/Interface/control.png");
            icon.setMargin(10, 2);
            icon.setHAlignment(HAlignment.Center);
            icon.setVAlignment(VAlignment.Center);

            return icon;
        }

        private ElementId updateAlternatingRowElementId() {
            ElementId elementId = ELEMENT_ID.child(odd ? "odd" : "even");
            odd = !odd;

            return elementId;
        }

    }

    @Getter
    @RequiredArgsConstructor
    private static class SceneGraphItem {

        private final Spatial spatial;
        private final int depth;

    }

}
