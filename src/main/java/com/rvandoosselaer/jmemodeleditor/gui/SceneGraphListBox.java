package com.rvandoosselaer.jmemodeleditor.gui;

import com.simsilica.lemur.Command;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.core.VersionedList;
import com.simsilica.lemur.style.ElementId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The Scene graph component
 *
 * @author: rvandoosselaer
 */
public class SceneGraphListBox extends ListBox<SceneGraphItem> {

    public static final ElementId ELEMENT_ID = PropertiesPanel.ELEMENT_ID.child(ListBox.ELEMENT_ID);

    private SceneGraphItem selected;
    private List<Command<SceneGraphListBox>> selectItemCommands = new ArrayList<>();

    public SceneGraphListBox() {
        super(new VersionedList<>(), new SceneGraphItemRenderer(), ELEMENT_ID, GuiState.STYLE);
    }

    public void addItem(SceneGraphItem item) {
        getModel().add(item);
    }

    @Override
    public void updateLogicalState(float tpf) {
        super.updateLogicalState(tpf);

        // Sometimes the click listener doesn't fire.
        // we need to find the selected item by hooking into the update loop.
        setSelectedItem(getSelectedItem());
    }

    public SceneGraphItem getSelectedItem() {
        Integer index = getSelectionModel().getSelection();
        if (index != null && index >= 0 && index < getModel().size()) {
            return getModel().get(index);
        }

        return null;
    }

    public void addSelectItemCommand(Command<SceneGraphListBox> selectItemCommand) {
        this.selectItemCommands.add(selectItemCommand);
    }

    private void setSelectedItem(SceneGraphItem item) {
        if (Objects.equals(selected, item)) {
            return;
        }

        selected = item;
        onItemSelected();
    }

    private void onItemSelected() {
        selectItemCommands.forEach(cmd -> cmd.execute(this));
    }

}
