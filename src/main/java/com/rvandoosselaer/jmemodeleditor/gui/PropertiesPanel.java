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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author: rvandoosselaer
 */
public class PropertiesPanel extends Container {

    public static final ElementId ELEMENT_ID = new ElementId("panel");

    private Tab currentTab;
    private List<Tab> tabs = new ArrayList<>();
    private Spatial spatial;
    private Container tabContainer;
    private Container contentContainer;
    private SceneGraphListBox sceneGraphListBox;

    public PropertiesPanel() {
        super(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even), ELEMENT_ID);

        buildSceneGraph();
        buildProperties();
    }

    public void setModel(Spatial spatial) {
        this.spatial = spatial;
        refreshSceneGraph(spatial);
        //TODO: update tabs and tab content
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

        tabContainer = properties.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.None, FillMode.Even), properties.getElementId().child("tabs")));
        contentContainer = properties.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even), properties.getElementId().child("content")));

        createTabs();
        tabs.stream().filter(tab -> SceneTab.ID.equals(tab.getId())).findFirst().ifPresent(this::selectTab);
    }

    private void refreshSceneGraph(Spatial spatial) {
        sceneGraphListBox.getModel().clear();
        addSceneGraphEntries(spatial, 0);
    }

    private void onSelectSceneGraphItem(SceneGraphItem sceneGraphItem) {
        updateTabs(sceneGraphItem);
        refreshCurrentTab();
    }

    private void createTabs() {
        Tab scene = new SceneTab(this::selectTab);
        tabContainer.addChild(scene.getTab());
        tabs.add(scene);
    }

    private void updateTabs(SceneGraphItem sceneGraphItem) {
        tabs.forEach(tab -> tab.getTab().removeFromParent());
        tabs.clear();

        Tab scene = new SceneTab(this::selectTab);
        tabs.add(scene);
        tabContainer.addChild(scene.getTab());

        if (sceneGraphItem == null) {
            return;
        }

        Tab object = new ObjectTab(sceneGraphItem, this::selectTab, cmd -> refreshSceneGraph(spatial));
        tabs.add(object);
        tabContainer.addChild(object.getTab());

        if (sceneGraphItem.getSpatial() instanceof Geometry) {
            Tab material = new MaterialTab(sceneGraphItem, this::selectTab);
            tabs.add(material);
            tabContainer.addChild(material.getTab());
        }

        if (sceneGraphItem.getSpatial().getNumControls() > 0) {
            Tab controls = new ControlsTab(sceneGraphItem, this::selectTab);
            tabs.add(controls);
            tabContainer.addChild(controls.getTab());
        }

//        Tab scene = new SceneTab(this::selectTab);
//        tabContainer.addChild(scene.getTab());
//
//        if (sceneGraphItem != null) {
//            // object tab
//            ObjectTab object = new ObjectTab(sceneGraphItem, this::selectTab);
//            object.setRefreshSceneGraphCommand(cmd -> refreshSceneGraph(spatial));
//            tabContainer.addChild(object.getTab());
//            // material tab
//            if (sceneGraphItem.getSpatial() instanceof Geometry) {
//                MaterialTab material = new MaterialTab(sceneGraphItem, this::selectTab);
//                tabContainer.addChild(material.getTab());
//            }
//            // controls tab
//            // TODO: only show when we have controls
//            ControlsTab controls = new ControlsTab(sceneGraphItem, this::selectTab);
//            tabContainer.addChild(controls.getTab());
//        }

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

        contentContainer.getLayout().clearChildren();

        currentTab.setSceneGraphItem(sceneGraphListBox.getSelectedItem());
        contentContainer.addChild(currentTab.getContent());
    }

}
