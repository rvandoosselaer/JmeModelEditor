package com.rvandoosselaer.jmemodeleditor.gui;

import com.simsilica.lemur.Container;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.SpringGridLayout;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * An alternating row renderer that displays the filename of a path. Depending on the type of file (folder, file, ...) a
 * different icon is used.
 *
 * @author: rvandoosselaer
 */
public class FileNameRenderer extends AlternatingRowRenderer<Path> {

    @Override
    public Panel getView(Path value, boolean selected, Panel existing) {
        if (existing == null) {
            existing = new Container(new SpringGridLayout(), getAlternatingRowElementId());

            Label label = ((Container) existing).addChild(new Label(value.getFileName().toString(), ELEMENT_ID));
            label.setIcon(createIcon(value));

            return existing;
        }

        Label label = (Label) ((SpringGridLayout) ((Container) existing).getLayout()).getChild(0, 0);
        label.setText(value.getFileName().toString());
        label.setIcon(createIcon(value));

        return existing;
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

}
