package com.rvandoosselaer.jmemodeleditor.gui;

import com.jme3.scene.Spatial;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents an item in the Scene graph
 *
 * @author: rvandoosselaer
 */
@Getter
@RequiredArgsConstructor
public class SceneGraphItem {

    private final Spatial spatial;
    private final int depth;

}
