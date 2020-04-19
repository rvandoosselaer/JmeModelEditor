package com.rvandoosselaer.jmemodeleditor.gui;

import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.component.SpringGridLayout;

/**
 * An alternating row renderer for strings.
 *
 * @author: rvandoosselaer
 */
public class StringRenderer extends AlternatingRowRenderer<String> {

    @Override
    public Panel getView(String value, boolean selected, Panel existing) {
        if (existing == null) {
            Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.Even, FillMode.Even), getAlternatingRowElementId());
            container.addChild(new Label(value, ELEMENT_ID));

            return container;
        }

        Label label = (Label) ((SpringGridLayout) (((Container) existing).getLayout())).getChild(0, 0);
        label.setText(value);

        return existing;
    }

}
