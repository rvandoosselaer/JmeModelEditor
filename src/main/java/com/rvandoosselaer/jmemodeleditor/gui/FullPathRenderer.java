package com.rvandoosselaer.jmemodeleditor.gui;

import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.component.SpringGridLayout;

import java.nio.file.Path;

/**
 * An alternating row renderer that displays the full path name of a path.
 *
 * @author: rvandoosselaer
 */
public class FullPathRenderer extends AlternatingRowRenderer<Path> {

    @Override
    public Panel getView(Path value, boolean selected, Panel existing) {
        if (existing == null) {
            existing = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.Even, FillMode.Even), getAlternatingRowElementId());
            ((Container) existing).addChild(new Label(value.toAbsolutePath().toString(), ELEMENT_ID));

            return existing;
        }

        Label label = (Label) ((SpringGridLayout) (((Container) existing).getLayout())).getChild(0, 0);
        label.setText(value.toAbsolutePath().toString());

        return existing;
    }

}
