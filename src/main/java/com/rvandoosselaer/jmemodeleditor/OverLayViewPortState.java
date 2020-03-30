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
    private Node root;
    private Camera overlayCamera;
    private ViewPort viewPort;

    @Override
    protected void initialize(Application app) {
        root = new Node("root-overlay");
        root.setCullHint(Spatial.CullHint.Never);

        overlayCamera = app.getCamera().clone();

        viewPort = app.getRenderManager().createMainView("Overlay", overlayCamera);
        viewPort.setClearFlags(false, true, false);
        viewPort.attachScene(root);
    }

    @Override
    protected void cleanup(Application app) {
        app.getRenderManager().removeMainView(viewPort);
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    @Override
    public void update(float tpf) {
        overlayCamera.setLocation(new Vector3f(getApplication().getCamera().getLocation()));
        overlayCamera.setRotation(new Quaternion(getApplication().getCamera().getRotation()));

        root.updateLogicalState(tpf);
    }

    @Override
    public void render(RenderManager rm) {
        root.updateGeometricState();
    }

}
