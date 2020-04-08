package com.rvandoosselaer.jmemodeleditor.gui;

import com.jme3.input.KeyInput;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.rvandoosselaer.jmeutils.ApplicationGlobals;
import com.rvandoosselaer.jmeutils.gui.GuiTranslations;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.core.VersionedList;
import com.simsilica.lemur.event.KeyAction;
import com.simsilica.lemur.event.KeyModifiers;
import com.simsilica.lemur.list.CellRenderer;
import com.simsilica.lemur.style.ElementId;
import org.lwjgl.Sys;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

/**
 * @author: rvandoosselaer
 */
public class SettingsWindow extends Window {

    public static final ElementId ELEMENT_ID = new ElementId("settings");

    private ListBox<Path> assetRootPaths;
    private TextField assetPath;
    private TooltipState tooltipState;
    private GuiState guiState;

    public SettingsWindow() {
        super(GuiTranslations.getInstance().t("window.settings.title"));

        guiState = ApplicationGlobals.getInstance().getApplication().getStateManager().getState(GuiState.class);
        tooltipState = ApplicationGlobals.getInstance().getApplication().getStateManager().getState(TooltipState.class);

        Container container = getContainer().addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even), ELEMENT_ID.child(Container.ELEMENT_ID)));
        container.addChild(createAssetRootsPanel());

        getButtonContainer().addChild(createButtonBar());
    }

    @Override
    protected void setParent(Node parent) {
        super.setParent(parent);

        if (parent != null) {
            onAttached();
        }
    }

    private Container createButtonBar() {
        Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.Last, FillMode.Even));
        Button cancel = container.addChild(new Button(GuiTranslations.getInstance().t("common.cancel"), ELEMENT_ID.child(Button.ELEMENT_ID)));
        Vector3f size = cancel.getPreferredSize();
        cancel.setPreferredSize(size.mult(new Vector3f(3, 1.2f, 1)));
        cancel.addClickCommands(source -> onCancel());

        Button open = container.addChild(new Button(GuiTranslations.getInstance().t("common.ok"), ELEMENT_ID.child("primary-button")));
        open.setPreferredSize(cancel.getPreferredSize());
        open.addClickCommands(source -> onOk());

        return container;
    }

    private Container createAssetRootsPanel() {
        ElementId elementId = ELEMENT_ID.child("assets");
        Container wrapper = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));
        Container assetPaths = wrapper.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even), elementId.child(Container.ELEMENT_ID)));
        assetPaths.addChild(new Label(GuiTranslations.getInstance().t("window.settings.assets.title"), elementId.child("title")));

        assetRootPaths = assetPaths.addChild(new ListBox<>(new VersionedList<>(), new AssetRootPathRenderer(), ELEMENT_ID.child(ListBox.ELEMENT_ID), GuiState.STYLE));
        assetRootPaths.getModel().addAll(guiState.getAssetRootPaths());
        assetRootPaths.setVisibleItems(4);
        assetRootPaths.addControl(new ListBoxSliderControl());

        Button removeAssetPath = assetPaths.addChild(new Button("-", elementId.child(Button.ELEMENT_ID)));
        tooltipState.addTooltip(removeAssetPath, GuiTranslations.getInstance().t("window.settings.assets.remove.tooltip"));
        removeAssetPath.addClickCommands(source -> getSelectedItem(assetRootPaths).ifPresent(this::onRemovePath));

        Container addAssetPathContainer = wrapper.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.First, FillMode.Even), elementId.child("add").child(Container.ELEMENT_ID)));
        assetPath = addAssetPathContainer.addChild(new TextField(""));
        KeyAction pasteKeyCombo = new KeyAction(KeyInput.KEY_V, KeyModifiers.CONTROL_DOWN);
        assetPath.getActionMap().put(pasteKeyCombo, (textEntryComponent, keyAction) -> textEntryComponent.setText(Sys.getClipboard() != null ? Sys.getClipboard() : ""));
        Button addAssetPath = addAssetPathContainer.addChild(new Button("Add", elementId.child("add").child(Button.ELEMENT_ID)));
        addAssetPath.addClickCommands(source -> onAddAssetPath(assetPath.getText()));
        Vector3f size = addAssetPath.getPreferredSize();
        addAssetPath.setPreferredSize(size.multLocal(1.5f, 1.2f, 1f));

        return wrapper;
    }

    private void onCancel() {
        closeWindow();
    }

    private void onOk() {
        guiState.setAssetRootPaths(new ArrayList<Path>(assetRootPaths.getModel()));
        closeWindow();
    }

    private void closeWindow() {
        removeFromParent();
    }

    private void onRemovePath(Path path) {
        assetRootPaths.getModel().remove(path);
    }

    private void onAddAssetPath(String text) {
        Path path = Paths.get(text);
        //TODO: check for null!
        assetRootPaths.getModel().add(Files.isDirectory(path) ? path : path.getParent());
        assetPath.setText("");
    }

    private void onAttached() {
        assetRootPaths.getSelectionModel().setSelection(-1);
        assetRootPaths.getModel().clear();
        assetRootPaths.getModel().addAll(guiState.getAssetRootPaths());
    }

    public static Optional<Path> getSelectedItem(ListBox<Path> listBox) {
        Integer index = listBox.getSelectionModel().getSelection();
        if (index != null && index >= 0 && index < listBox.getModel().size()) {
            return Optional.of(listBox.getModel().get(index));
        }

        return Optional.empty();
    }

    private static class AssetRootPathRenderer implements CellRenderer<Path> {

        public static final ElementId ELEMENT_ID = SettingsWindow.ELEMENT_ID.child(ListBox.ELEMENT_ID).child("item");

        private boolean odd;

        @Override
        public Panel getView(Path item, boolean selected, Panel existing) {
            Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.Even, FillMode.Even), updateAlternatingRowElementId());
            container.addChild(new Label(item.toAbsolutePath().toString(), ELEMENT_ID));

            return container;
        }

        private ElementId updateAlternatingRowElementId() {
            ElementId elementId = ELEMENT_ID.child(odd ? "odd" : "even");
            odd = !odd;

            return elementId;
        }

    }

}
