package com.rvandoosselaer.jmemodeleditor.gui;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.rvandoosselaer.jmemodeleditor.ViewPortsState;
import com.rvandoosselaer.jmeutils.util.GeometryUtils;

/**
 * An appstate that displays the coordinate axes.
 *
 * @author: rvandoosselaer
 */
public class CoordinateAxesState extends BaseAppState {

    private Node coordinateAxesNode;
    private Spatial coordinateAxes;
    private Camera editorCamera;
    private Camera coordinateAxesCamera;

    @Override
    protected void initialize(Application app) {
        coordinateAxesNode = getState(ViewPortsState.class).getCoordinateAxesNode();
        coordinateAxes = GeometryUtils.createCoordinateAxes();
        editorCamera = getState(ViewPortsState.class).getEditorCamera();
        coordinateAxesCamera = getState(ViewPortsState.class).getCoordinateAxesCamera();
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        coordinateAxesNode.attachChild(coordinateAxes);
    }

    @Override
    protected void onDisable() {
        coordinateAxes.removeFromParent();
    }

    @Override
    public void update(float tpf) {
        Vector3f dir = new Vector3f(editorCamera.getDirection());

        coordinateAxesCamera.setLocation(dir.negateLocal().mult(3));
        coordinateAxesCamera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }

}
