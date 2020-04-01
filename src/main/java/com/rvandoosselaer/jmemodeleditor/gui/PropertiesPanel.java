package com.rvandoosselaer.jmemodeleditor.gui;

import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.rvandoosselaer.jmeutils.gui.GuiTranslations;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
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

        public static final ElementId ELEMENT_ID = new ElementId("item");
        private final int INDENT_SIZE = 10;

        @Override
        public Panel getView(SceneGraphItem value, boolean selected, Panel existing) {
            Button button = new Button(value.getSpatial().getName(), SceneGraphListBox.ELEMENT_ID.child(ELEMENT_ID));
            button.setIcon(createIcon(value.getSpatial()));
            QuadBackgroundComponent background = new QuadBackgroundComponent(ColorRGBA.BlackNoAlpha);
            background.setMargin(INDENT_SIZE * value.getDepth(), 0);
            button.setBackground(background);

            return button;
        }

        private IconComponent createIcon(Spatial spatial) {
            String path = spatial instanceof Node ? "/Interface/node.png" : "/Interface/geometry.png";
            IconComponent icon = new IconComponent(path);
            icon.setMargin(4, 2);
            icon.setHAlignment(HAlignment.Left);
            icon.setVAlignment(VAlignment.Center);

            return icon;
        }

        private String getIndent(int depth) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < depth; i++) {
                sb.append(" ");
            }

            return sb.toString();
        }

    }

    @Getter
    @RequiredArgsConstructor
    private static class SceneGraphItem {

        private final Spatial spatial;
        private final int depth;

    }

}