package com.rvandoosselaer.jmemodeleditor;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import lombok.Getter;

/**
 * An appstate that handles the lifecycle of the overlay viewport. The location and rotation of the main viewport camera
 * is copied to the camera of the overlay viewport.
 *
 * @author: rvandoosselaer
 */
public class OverLayViewPortState extends BaseAppState {

    @Getter
    private Node node;
    private Camera viewPortCamera;
    private ViewPort viewPort;

    @Override
    protected void initialize(Application app) {
        node = new Node("root-overlay");
        node.setCullHint(Spatial.CullHint.Never);

        viewPortCamera = app.getCamera().clone();

        viewPort = app.getRenderManager().createMainView("Overlay", viewPortCamera);
        viewPort.setClearFlags(false, true, false);
        viewPort.attachScene(node);
    }

    @Override
    protected void cleanup(Application app) {
        app.getRenderManager().removeMainView(viewPort);
        node.detachAllChildren();
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    @Override
    public void update(float tpf) {
        viewPortCamera.setLocation(new Vector3f(getApplication().getCamera().getLocation()));
        viewPortCamera.setRotation(new Quaternion(getApplication().getCamera().getRotation()));

        node.updateLogicalState(tpf);
    }

    @Override
    public void render(RenderManager rm) {
        node.updateGeometricState();
    }

}
