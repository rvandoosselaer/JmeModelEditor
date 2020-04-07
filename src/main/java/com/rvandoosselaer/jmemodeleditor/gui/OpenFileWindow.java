package com.rvandoosselaer.jmemodeleditor.gui;

import com.jme3.input.KeyInput;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.rvandoosselaer.jmeutils.ApplicationGlobals;
import com.rvandoosselaer.jmeutils.gui.GuiTranslations;
import com.rvandoosselaer.jmeutils.input.DoubleClickMouseListener;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.core.VersionedList;
import com.simsilica.lemur.event.KeyAction;
import com.simsilica.lemur.event.KeyModifiers;
import com.simsilica.lemur.event.MouseEventControl;
import com.simsilica.lemur.list.CellRenderer;
import com.simsilica.lemur.style.ElementId;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.Sys;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A window to browse the file system and load the selected model into the editor.
 *
 * @author: rvandoosselaer
 */
@Slf4j
public class OpenFileWindow extends Window {

    public static final ElementId ELEMENT_ID = new ElementId("open-file");

    private Path currentDir;
    private TextField currentDirTextField;
    private ListBox<Path> fileBrowser;
    private GuiState guiState;
    private TooltipState tooltipState;

    public OpenFileWindow() {
        super(GuiTranslations.getInstance().t("window.open-file.title"));

        guiState = ApplicationGlobals.getInstance().getApplication().getStateManager().getState(GuiState.class);
        tooltipState = ApplicationGlobals.getInstance().getApplication().getStateManager().getState(TooltipState.class);

        buildGUI();
    }

    private void buildGUI() {
        Container mainContainer = getContainer().addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.Last, FillMode.Even)));
        Container locationsContainer = mainContainer.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even)));
        locationsContainer.addChild(createFavoriteLocationsList());

        Container fileBrowserContainer = mainContainer.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Even, FillMode.Even)));
        fileBrowserContainer.addChild(createTopBar());

        fileBrowser = fileBrowserContainer.addChild(new ListBox<>(new VersionedList<>(), ELEMENT_ID.child(ListBox.ELEMENT_ID), GuiState.STYLE));
        fileBrowser.setCellRenderer(new FileBrowserItemRenderer());
        fileBrowser.setVisibleItems(20);
        fileBrowser.addControl(new ListBoxSliderControl());
        MouseEventControl.addListenersToSpatial(fileBrowser, new FileBrowserItemClickListener());

        setDirectory(getStartFolder());

        getButtonContainer().addChild(createButtonBar());
    }

    /**
     * Creates the top bar of the open file modal containing the full path textfield and a directory buttons
     */
    private Container createTopBar() {
        Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.Last, FillMode.Even));
        Button homeDir = container.addChild(createButton("/Interface/home.png"));
        homeDir.addClickCommands(source -> setDirectory(getHomeDirectory()));
        tooltipState.addTooltip(homeDir, GuiTranslations.getInstance().t("window.open-file.home.tooltip"));
        Button upDir = container.addChild(createButton("/Interface/up-arrow.png"));
        upDir.addClickCommands(source -> goDirectoryUp());
        tooltipState.addTooltip(upDir, GuiTranslations.getInstance().t("window.open-file.up.tooltip"));

        currentDirTextField = container.addChild(new TextField(getStartFolder().toAbsolutePath().toString(), ELEMENT_ID.child(TextField.ELEMENT_ID)));
        currentDirTextField.getActionMap().put(new KeyAction(KeyInput.KEY_RETURN), (textEntryComponent, key) -> {
            setDirectory(Paths.get(textEntryComponent.getText()));
            GuiGlobals.getInstance().releaseFocus(currentDirTextField);
        });
        KeyAction pasteKeyCombo = new KeyAction(KeyInput.KEY_V, KeyModifiers.CONTROL_DOWN);
        currentDirTextField.getActionMap().put(pasteKeyCombo, (textEntryComponent, keyAction) -> textEntryComponent.setText(Sys.getClipboard() != null ? Sys.getClipboard() : ""));

        return container;
    }

    /**
     * Creates the button bar with the open and cancel buttons
     */
    private Container createButtonBar() {
        Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.Last, FillMode.Even));
        Button cancel = container.addChild(new Button(GuiTranslations.getInstance().t("common.cancel"), ELEMENT_ID.child(Button.ELEMENT_ID)));
        Vector3f size = cancel.getPreferredSize();
        cancel.setPreferredSize(size.mult(new Vector3f(3, 1.2f, 1)));
        cancel.addClickCommands(source -> onCancel());

        Button open = container.addChild(new Button(GuiTranslations.getInstance().t("common.open"), ELEMENT_ID.child("primary-button")));
        size = open.getPreferredSize();
        open.setPreferredSize(size.mult(new Vector3f(3, 1.2f, 1)));
        open.addClickCommands(source -> onOpen());

        return container;
    }

    private Container createFavoriteLocationsList() {
        ElementId elementId = ELEMENT_ID.child("favorites");
        Container favorites = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even), elementId.child(Container.ELEMENT_ID));
        Label title = favorites.addChild(new Label("Favorites", elementId.child("title")));

        Container locations = favorites.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.First, FillMode.Even)));
        ListBox<Path> favoriteLocationsListBox = locations.addChild(new ListBox<>(new VersionedList<>(), new FileBrowserItemRenderer(), GuiState.STYLE));
        favoriteLocationsListBox.setVisibleItems(6);
        favoriteLocationsListBox.addControl(new ListBoxSliderControl());

        Container buttons = locations.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.None)));
        Button addLocation = buttons.addChild(new Button("+", elementId.child("button")));
        Button removeLocation = buttons.addChild(new Button("-", elementId.child("button")));

        // make the container a bit wider when the listBox is empty
        if (favoriteLocationsListBox.getModel().isEmpty()) {
            favorites.setPreferredSize(favorites.getPreferredSize().multLocal(1.5f, 1, 1));
        }
        return favorites;
    }

    private void onOpen() {
        Optional<Path> selection = getSelectedItem();
        if (selection.isPresent()) {
            if (Files.isDirectory(selection.get())) {
                setDirectory(selection.get());
            } else {
                guiState.loadModel(selection.get());
                closeWindow();
            }
        }
    }

    private void onCancel() {
        closeWindow();
    }

    /**
     * Set the directory to load in the file browser
     *
     * @param path
     */
    private void setDirectory(Path path) {
        if (path != null && Files.isDirectory(path)) {
            fileBrowser.getModel().clear();
            fileBrowser.getModel().addAll(getFiles(path));
            clearSelection();
            currentDirTextField.setText(path.toString());
            currentDir = path;
        }
    }

    private void closeWindow() {
        clearSelection();
        removeFromParent();
    }

    /**
     * Remove the selected item in the file browser
     */
    private void clearSelection() {
        fileBrowser.getSelectionModel().setSelection(-1);
    }

    private void goDirectoryUp() {
        setDirectory(currentDir.getParent());
    }

    private Path getHomeDirectory() {
        return Paths.get(System.getProperty("user.home"));
    }

    /**
     * @return the folder to open when the window is created
     */
    private Path getStartFolder() {
        //return Paths.get(System.getProperty("user.home"));
        return Paths.get("/Users/remy/Projects/rvandoosselaer/desolated-woods/assets/Models/Human");
    }

    /**
     * @param path
     * @return the files in the given path sorted alphabetically
     */
    private List<Path> getFiles(Path path) {
        if (Files.isDirectory(path)) {
            try {
                return Files.list(path)
                        .filter(p -> !p.getFileName().toString().startsWith("."))
                        .sorted((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getFileName().toString(), o2.getFileName().toString()))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }

        return Collections.emptyList();
    }

    private Optional<Path> getSelectedItem() {
        Integer index = fileBrowser.getSelectionModel().getSelection();
        if (index == null || fileBrowser.getModel().isEmpty() || index >= fileBrowser.getModel().size()) {
            return Optional.empty();
        }

        return Optional.of(fileBrowser.getModel().get(index));
    }

    private static Button createButton(String iconPath) {
        Button button = new Button("", ELEMENT_ID.child(Button.ELEMENT_ID));
        IconComponent icon = new IconComponent(iconPath);
        icon.setIconSize(new Vector2f(16, 16));
        icon.setMargin(2, 2);
        icon.setHAlignment(HAlignment.Center);
        icon.setVAlignment(VAlignment.Center);
        button.setIcon(icon);

        return button;
    }

    private static class FileBrowserItemRenderer implements CellRenderer<Path> {

        public static final ElementId ELEMENT_ID = OpenFileWindow.ELEMENT_ID.child(ListBox.ELEMENT_ID).child("item");
        private boolean odd;

        @Override
        public Panel getView(Path item, boolean selected, Panel existing) {
            Container container = new Container(new SpringGridLayout(), updateAlternatingRowElementId());

            Label label = container.addChild(new Label(item.getFileName().toString(), ELEMENT_ID));
            label.setTextVAlignment(VAlignment.Center);
            label.setIcon(createIcon(item));

            return container;
        }

        private IconComponent createIcon(Path item) {
            IconComponent icon = new IconComponent(getIconPath(item));
            icon.setMargin(4, 2);
            icon.setHAlignment(HAlignment.Left);
            icon.setVAlignment(VAlignment.Center);
            return icon;
        }

        private String getIconPath(Path item) {
            return Files.isDirectory(item) ? "/Interface/folder.png" : isJ3o(item) ? "/Interface/jme-monkey.png" : "/Interface/file.png";
        }

        private boolean isJ3o(Path item) {
            return item.getFileName().toString().toLowerCase().endsWith(".j3o");
        }

        private ElementId updateAlternatingRowElementId() {
            ElementId elementId = ELEMENT_ID.child(odd ? "odd" : "even");
            odd = !odd;

            return elementId;
        }

    }

    private class FileBrowserItemClickListener extends DoubleClickMouseListener {

        @Override
        protected void doubleClick(MouseButtonEvent event, Spatial target, Spatial capture) {
            if (event.getButtonIndex() == 0) {
                getSelectedItem().ifPresent(OpenFileWindow.this::setDirectory);
            }
        }

    }

}
