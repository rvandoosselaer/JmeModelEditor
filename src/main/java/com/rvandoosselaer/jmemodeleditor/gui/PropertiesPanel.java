package com.rvandoosselaer.jmemodeleditor.gui;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.rvandoosselaer.jmeutils.gui.GuiTranslations;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.style.ElementId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        initializeTabs();
    }

    private void buildSceneGraph() {
        Container sceneGraph = addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even), ELEMENT_ID.child("scenegraph")));
        sceneGraph.addChild(new Label(GuiTranslations.getInstance().t("panel.scenegraph.title"), ELEMENT_ID.child("title")));

        sceneGraphListBox = sceneGraph.addChild(new SceneGraphListBox());
        sceneGraphListBox.setVisibleItems(8);
        sceneGraphListBox.addControl(new ListBoxSliderControl());
        sceneGraphListBox.addSelectItemCommand(listBox -> onSelectSceneGraphItem(listBox.getSelectedItem()));
    }

    private void buildProperties() {
        Container properties = addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even), ELEMENT_ID.child("properties")));

        tabContainer = properties.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.None, FillMode.Even), properties.getElementId().child("tabs")));
        contentContainer = properties.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even), properties.getElementId().child("content")));

        initializeTabs();
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

    private void refreshSceneGraph(Spatial spatial) {
        sceneGraphListBox.getModel().clear();
        addSceneGraphEntries(spatial, 0);
    }

    private void onSelectSceneGraphItem(SceneGraphItem sceneGraphItem) {
        updateTabs(sceneGraphItem);
        selectTab(currentTab.getId());
    }

    private void initializeTabs() {
        createTabs();
        selectDefaultTab();
    }

    private void createTabs() {
        tabs.forEach(tab -> tab.getTab().removeFromParent());
        tabs.clear();

        addTab(new SceneTab(tab -> selectTab(tab.getId())));
    }

    private void selectDefaultTab() {
        selectTab(SceneTab.ID);
    }

    private void selectTab(String tabId) {
        Optional<Tab> tabOptional = tabs.stream().filter(t -> t.getId().equals(tabId)).findFirst();
        // try to find the tab based on the ID. If it is not found, select the default tab
        if (tabOptional.isPresent()) {
            Tab tab = tabOptional.get();
            tabs.forEach(t -> t.setActive(false));
            tab.setActive(true);
            tab.setSceneGraphItem(sceneGraphListBox.getSelectedItem());

            updateTabContent(tab);

            // a gui 'hack'. This way we force to run the button command that updates the background
            tabs.forEach(t -> t.getTab().getCommands(Button.ButtonAction.HighlightOff).forEach(cmd -> cmd.execute(t.getTab())));

            currentTab = tab;
        } else {
            selectDefaultTab();
        }
    }

    private void updateTabs(SceneGraphItem sceneGraphItem) {
        tabs.forEach(tab -> tab.getTab().removeFromParent());
        tabs.clear();

        addTab(new SceneTab(tab -> selectTab(tab.getId())));

        if (sceneGraphItem == null) {
            return;
        }

        addTab(new ObjectTab(sceneGraphItem, tab -> selectTab(tab.getId()), cmd -> refreshSceneGraph(spatial)));

        if (sceneGraphItem.getSpatial() instanceof Geometry) {
            addTab(new MaterialTab(sceneGraphItem, tab -> selectTab(tab.getId())));
        }

        if (sceneGraphItem.getSpatial().getNumControls() > 0) {
            addTab(new ControlsTab(sceneGraphItem, tab -> selectTab(tab.getId())));
        }

    }

    private void updateTabContent(Tab tab) {
        contentContainer.getLayout().clearChildren();
        contentContainer.addChild(tab.getContent());
    }

    private void addTab(Tab tab) {
        tabs.add(tab);
        tabContainer.addChild(tab.getTab());
    }

}
