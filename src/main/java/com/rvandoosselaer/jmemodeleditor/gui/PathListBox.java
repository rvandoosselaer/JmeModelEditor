package com.rvandoosselaer.jmemodeleditor.gui;

import com.simsilica.lemur.Command;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.core.VersionedList;

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

}
