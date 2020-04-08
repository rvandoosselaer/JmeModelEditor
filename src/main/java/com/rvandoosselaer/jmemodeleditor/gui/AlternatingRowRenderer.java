package com.rvandoosselaer.jmemodeleditor.gui;

import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.list.CellRenderer;
import com.simsilica.lemur.style.ElementId;

/**
 * A CellRenderer that toggles the ElementId depending on the row. Call {@link #getAlternatingRowElementId()} to get
 * and update the row elementId.
 *
 * @author: rvandoosselaer
 */
public abstract class AlternatingRowRenderer<T> implements CellRenderer<T> {

    public static final ElementId ELEMENT_ID = new ElementId(ListBox.ELEMENT_ID).child("row");

    private boolean odd;

    protected ElementId getAlternatingRowElementId() {
        ElementId elementId = ELEMENT_ID.child(odd ? "odd" : "even");
        odd = !odd;

        return elementId;
    }

}
