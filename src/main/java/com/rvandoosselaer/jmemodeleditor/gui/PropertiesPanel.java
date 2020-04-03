package com.rvandoosselaer.jmemodeleditor.gui;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.rvandoosselaer.jmeutils.gui.GuiTranslations;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.style.ElementId;

import java.util.Objects;

/**
 * @author: rvandoosselaer
 */
public class PropertiesPanel extends Container {

    public static final ElementId ELEMENT_ID = new ElementId("panel");

    private Spatial spatial;
    private Tab currentTab;
    private Container tabContent;
    private Container tabs;
    private SceneGraphListBox sceneGraphListBox;

    public PropertiesPanel() {
        super(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even), ELEMENT_ID);

        buildSceneGraph();
        buildProperties();
    }

    public void setModel(Spatial spatial) {
        this.spatial = spatial;
        refreshSceneGraph(spatial);
    }

    private void buildSceneGraph() {
        Container sceneGraph = addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even), ELEMENT_ID.child("scenegraph")));
        sceneGraph.addChild(new Label(GuiTranslations.getInstance().t("panel.scenegraph.title"), ELEMENT_ID.child("title")));

        sceneGraphListBox = sceneGraph.addChild(new SceneGraphListBox());
        sceneGraphListBox.setVisibleItems(8);
        sceneGraphListBox.addControl(new ListBoxSliderControl());
        sceneGraphListBox.addSelectItemCommand(listBox -> onSelectSceneGraphItem(listBox.getSelectedItem()));
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

    private void buildProperties() {
        Container properties = addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even), ELEMENT_ID.child("properties")));

        tabs = properties.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.None, FillMode.Even), properties.getElementId().child("tabs")));
        tabContent = properties.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even), properties.getElementId().child("content")));

        // create the tabs
        updateTabs(null, true);
    }

    private void refreshSceneGraph(Spatial spatial) {
        sceneGraphListBox.getModel().clear();
        addSceneGraphEntries(spatial, 0);
    }

    private void onSelectSceneGraphItem(SceneGraphItem sceneGraphItem) {
        updateTabs(sceneGraphItem, false);
        refreshCurrentTab();
    }

    /**
     * Update the tabs based on the specified scene graph item. When null is passed, only the scene tab is created.
     *
     * @param sceneGraphItem
     * @param selectDefault true if the default (scene) tab should be selected
     */
    private void updateTabs(SceneGraphItem sceneGraphItem, boolean selectDefault) {
        // clear the tabs
        tabs.getLayout().clearChildren();

        Tab scene = new SceneTab(this::selectTab);
        tabs.addChild(scene.getTab());

        if (sceneGraphItem != null) {
            // object tab
            ObjectTab object = new ObjectTab(sceneGraphItem, this::selectTab);
            object.setRefreshSceneGraphCommand(cmd -> refreshSceneGraph(spatial));
            tabs.addChild(object.getTab());
            // material tab
            if (sceneGraphItem.getSpatial() instanceof Geometry) {
                MaterialTab material = new MaterialTab(sceneGraphItem, this::selectTab);
                tabs.addChild(material.getTab());
            }
        }

        if (selectDefault) {
            selectTab(scene);
        }
    }

    private void selectTab(Tab tab) {
        if (Objects.equals(currentTab, tab)) {
            return;
        }

        currentTab = tab;

        refreshCurrentTab();
    }

    private void refreshCurrentTab() {
        if (currentTab == null) {
            return;
        }

        tabContent.getLayout().clearChildren();

        currentTab.setSceneGraphItem(sceneGraphListBox.getSelectedItem());
        tabContent.addChild(currentTab.getContent());
    }

}
