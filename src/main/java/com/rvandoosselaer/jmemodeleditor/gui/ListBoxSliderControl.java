package com.rvandoosselaer.jmemodeleditor.gui;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.simsilica.lemur.ListBox;

/**
 * A control that shows or hides the slider of a listbox, based on the visible and available items.
 *
 * @author: rvandoosselaer
 */
public class ListBoxSliderControl extends AbstractControl {

    private ListBox<?> listBox;

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial instanceof ListBox) {
            listBox = (ListBox<?>) spatial;
        } else {
            throw new IllegalArgumentException("ListBoxSliderControl can only be attached to a ListBox!");
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        boolean showSlider = listBox.getVisibleItems() < listBox.getModel().size();

        listBox.getSlider().setCullHint(showSlider ? Spatial.CullHint.Inherit : Spatial.CullHint.Always);

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

}
