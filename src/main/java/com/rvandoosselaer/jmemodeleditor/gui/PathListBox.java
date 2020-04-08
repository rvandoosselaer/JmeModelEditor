package com.rvandoosselaer.jmemodeleditor.gui;

import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.core.VersionedList;
import com.simsilica.lemur.list.CellRenderer;
import com.simsilica.lemur.style.ElementId;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author: rvandoosselaer
 */
public class PathListBox extends ListBox<Path> {

    private Path selection;
    private List<Command<Path>> selectionCommands = new ArrayList<>();

    public PathListBox(VersionedList<Path> model) {
        super(model);

        addControl(new ListBoxSliderControl());
    }

    @Override
    public void updateLogicalState(float tpf) {
        super.updateLogicalState(tpf);

        getSelection().ifPresent(this::setSelection);
    }

    public Optional<Path> getSelection() {
        Integer index = getSelectionModel().getSelection();
        if (index != null && index >= 0 && index < getModel().size()) {
            return Optional.of(getModel().get(index));
        }

        return Optional.empty();
    }

    public void deselect() {
        getSelectionModel().setSelection(-1);
    }

    public void onSelection(Path selection) {
        selectionCommands.forEach(cmd -> cmd.execute(selection));
    }

    public void addSelectionCommand(Command<Path> cmd) {
        selectionCommands.add(cmd);
    }

    private void setSelection(Path selection) {
        if (selection.equals(this.selection)) {
            return;
        }

        onSelection(selection);

        this.selection = selection;
    }

    public static class FileNameRenderer implements CellRenderer<Path> {

        public static final ElementId ELEMENT_ID = new ElementId(ListBox.ELEMENT_ID).child("row");

        private boolean odd;

        @Override
        public Panel getView(Path value, boolean selected, Panel existing) {
            Container container = new Container(new SpringGridLayout(), getAlternatingRowElementId());

            Label label = container.addChild(new Label(value.getFileName().toString(), ELEMENT_ID));
            label.setIcon(createIcon(value));

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

        private ElementId getAlternatingRowElementId() {
            ElementId elementId = ELEMENT_ID.child(odd ? "odd" : "even");
            odd = !odd;

            return elementId;
        }

    }

}
