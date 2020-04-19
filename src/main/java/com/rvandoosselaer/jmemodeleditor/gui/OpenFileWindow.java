package com.rvandoosselaer.jmemodeleditor.gui;

import com.jme3.input.KeyInput;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
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
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.core.VersionedList;
import com.simsilica.lemur.event.KeyAction;
import com.simsilica.lemur.event.KeyModifiers;
import com.simsilica.lemur.event.MouseEventControl;
import com.simsilica.lemur.style.ElementId;
import lombok.RequiredArgsConstructor;
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
    private PathListBox fileBrowser;
    private PathListBox bookmarkLocations;
    private PathListBox recentLocations;
    private GuiState guiState;
    private TooltipState tooltipState;

    public OpenFileWindow() {
        super(GuiTranslations.getInstance().t("window.open-file.title"));

        guiState = ApplicationGlobals.getInstance().getApplication().getStateManager().getState(GuiState.class);
        tooltipState = ApplicationGlobals.getInstance().getApplication().getStateManager().getState(TooltipState.class);

        buildGUI();
    }

    @Override
    protected void setParent(Node parent) {
        super.setParent(parent);

        if (parent != null) {
            onAttached();
        }
    }

    private void buildGUI() {
        Container mainContainer = getContainer().addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.Last, FillMode.Even)));
        Container locationsContainer = mainContainer.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even)));
        locationsContainer.addChild(createBookmarkLocationsList());
        locationsContainer.addChild(createRecentLocationsList());

        Container fileBrowserContainer = mainContainer.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Even, FillMode.Even), ELEMENT_ID.child("fileBrowser").child(Container.ELEMENT_ID)));
        fileBrowserContainer.addChild(createTopBar());

        fileBrowser = fileBrowserContainer.addChild(new PathListBox(new VersionedList<>(), new FileNameRenderer()));
        fileBrowser.setVisibleItems(20);
        MouseEventControl.addListenersToSpatial(fileBrowser, new PathListBoxDoubleClickListener(fileBrowser));

        setDirectory(getStartFolder());

        getButtonContainer().addChild(createButtonBar());
    }

    /**
     * Creates the top bar of the open file modal containing the full path textfield and a directory buttons
     */
    private Container createTopBar() {
        ElementId elementId = ELEMENT_ID.child("fileBrowser.controls");
        Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.Last, FillMode.Even), elementId.child(Container.ELEMENT_ID));
        Button homeDir = container.addChild(createButton("/Interface/home.png", elementId.child(Button.ELEMENT_ID)));
        homeDir.addClickCommands(source -> setDirectory(getHomeDirectory()));
        tooltipState.addTooltip(homeDir, GuiTranslations.getInstance().t("window.open-file.home.tooltip"));
        Button upDir = container.addChild(createButton("/Interface/up-arrow.png", elementId.child(Button.ELEMENT_ID)));
        upDir.addClickCommands(source -> goDirectoryUp());
        tooltipState.addTooltip(upDir, GuiTranslations.getInstance().t("window.open-file.up.tooltip"));

        currentDirTextField = container.addChild(new TextField(getStartFolder().toAbsolutePath().toString(), ELEMENT_ID.child(TextField.ELEMENT_ID)));
        currentDirTextField.getActionMap().put(new KeyAction(KeyInput.KEY_RETURN), (textEntryComponent, key) -> onReturnDirTextField());
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

    private Container createRecentLocationsList() {
        ElementId elementId = ELEMENT_ID.child("recent");
        Container container = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even), elementId.child(Container.ELEMENT_ID));
        container.addChild(new Label(GuiTranslations.getInstance().t("window.open-file.recent"), elementId.child("title")));

        Container locations = container.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.First, FillMode.Even)));
        recentLocations = locations.addChild(new PathListBox(new VersionedList<>(guiState.getRecentLocations()), new FileNameRenderer()));
        recentLocations.setVisibleItems(6);
        recentLocations.addSelectionCommand(this::setDirectory);
        recentLocations.addClickCommands(listBox -> recentLocations.getSelection().ifPresent(this::setDirectory));

        Container buttons = locations.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.None)));
        Button clearRecentLocations = buttons.addChild(new Button("x", elementId.child("button")));
        tooltipState.addTooltip(clearRecentLocations, GuiTranslations.getInstance().t("window.open-file.recent.clear"));
        clearRecentLocations.addClickCommands(source -> onClearRecentLocations());
        clearRecentLocations.setPreferredSize(clearRecentLocations.getPreferredSize().setX(clearRecentLocations.getPreferredSize().y));

        // make the container a bit wider when the listBox is empty
        if (recentLocations.getModel().isEmpty()) {
            container.setPreferredSize(container.getPreferredSize().multLocal(1.5f, 1, 1));
        }
        return container;
    }

    private Container createBookmarkLocationsList() {
        ElementId elementId = ELEMENT_ID.child("bookmarks");
        Container bookmarks = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even), elementId.child(Container.ELEMENT_ID));
        bookmarks.addChild(new Label(GuiTranslations.getInstance().t("window.open-file.bookmarks"), elementId.child("title")));

        Container locations = bookmarks.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.First, FillMode.Even)));
        bookmarkLocations = locations.addChild(new PathListBox(new VersionedList<>(guiState.getBookmarks()), new FileNameRenderer()));
        bookmarkLocations.setVisibleItems(6);
        bookmarkLocations.addSelectionCommand(this::setDirectory);
        bookmarkLocations.addClickCommands(source -> bookmarkLocations.getSelection().ifPresent(this::setDirectory));

        Container buttons = locations.addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.None)));
        Button addLocation = buttons.addChild(new Button("+", elementId.child("button")));
        tooltipState.addTooltip(addLocation, GuiTranslations.getInstance().t("window.open-file.bookmarks.add"));
        addLocation.addClickCommands(source -> onAddBookmark());
        addLocation.setPreferredSize(addLocation.getPreferredSize().setX(addLocation.getPreferredSize().y));
        Button removeLocation = buttons.addChild(new Button("-", elementId.child("button")));
        tooltipState.addTooltip(removeLocation, GuiTranslations.getInstance().t("window.open-file.bookmarks.remove"));
        removeLocation.addClickCommands(source -> onRemoveBookmark());
        removeLocation.setPreferredSize(removeLocation.getPreferredSize().setX(removeLocation.getPreferredSize().y));

        // make the container a bit wider when the listBox is empty
        if (bookmarkLocations.getModel().isEmpty()) {
            bookmarks.setPreferredSize(bookmarks.getPreferredSize().multLocal(1.5f, 1, 1));
        }
        return bookmarks;
    }

    private void onOpen() {
        Optional<Path> selection = fileBrowser.getSelection();
        if (selection.isPresent()) {
            if (Files.isDirectory(selection.get())) {
                setDirectory(selection.get());
            } else {
                guiState.loadModel(selection.get());
                closeWindow();
                guiState.addRecentLocation(currentDir);
            }
        }
    }

    private void onCancel() {
        closeWindow();
    }

    private void onAddBookmark() {
        guiState.addBookmark(currentDir);
        refreshBookmarks();
    }

    private void onRemoveBookmark() {
        bookmarkLocations.getSelection().ifPresent(guiState::removeBookmark);
        refreshBookmarks();
    }

    private void onClearRecentLocations() {
        guiState.clearRecentLocations();
        refreshRecentLocations();
    }

    private void onReturnDirTextField() {
        GuiState.parsePath(currentDirTextField.getText()).ifPresent(path -> {
            setDirectory(path);
            GuiGlobals.getInstance().releaseFocus(currentDirTextField);
        });
    }

    private void refreshBookmarks() {
        bookmarkLocations.getModel().clear();
        bookmarkLocations.getModel().addAll(guiState.getBookmarks());
        bookmarkLocations.deselect();
    }

    private void refreshRecentLocations() {
        recentLocations.getModel().clear();
        recentLocations.getModel().addAll(guiState.getRecentLocations());
        recentLocations.deselect();
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
            fileBrowser.deselect();
            currentDirTextField.setText(path.toString());
            currentDir = path;
        }
    }

    private void closeWindow() {
        fileBrowser.deselect();
        bookmarkLocations.deselect();
        recentLocations.deselect();
        removeFromParent();
    }

    private void onAttached() {
        refreshBookmarks();
        refreshRecentLocations();
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
        return Paths.get(System.getProperty("user.home"));
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

    private static Button createButton(String iconPath, ElementId elementId) {
        Button button = new Button("", elementId);
        IconComponent icon = new IconComponent(iconPath);
        icon.setMargin(2, 2);
        icon.setHAlignment(HAlignment.Center);
        icon.setVAlignment(VAlignment.Center);
        button.setIcon(icon);

        return button;
    }

    @RequiredArgsConstructor
    private class PathListBoxDoubleClickListener extends DoubleClickMouseListener {

        private final PathListBox listBox;

        @Override
        protected void doubleClick(MouseButtonEvent event, Spatial target, Spatial capture) {
            if (event.getButtonIndex() == 0) {
                listBox.getSelection().ifPresent(OpenFileWindow.this::setDirectory);
            }
        }

    }

}
