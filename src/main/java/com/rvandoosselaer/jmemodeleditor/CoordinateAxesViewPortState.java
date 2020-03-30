package com.rvandoosselaer.jmemodeleditor;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import lombok.Getter;

/**
 * An appstate that handles the lifecycle of the coordinate axes viewport. The camera of the viewport is updated based
 * on the direction of the main viewport's camera.
 *
 * @author: rvandoosselaer
 */
public class CoordinateAxesViewPortState extends BaseAppState {

    @Getter
    private Node node;
    private Camera viewPortCamera;
    private ViewPort viewPort;

    @Override
    protected void initialize(Application app) {
        node = new Node("root-coordinate");
        node.setCullHint(Spatial.CullHint.Never);

        viewPortCamera = new Camera(80, 80);
        viewPortCamera.setFrustumPerspective(45f, 1, 1f, 5f);
        viewPortCamera.setLocation(new Vector3f(0, 0, 3));
        viewPortCamera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        viewPortCamera.setViewPort(0, 1, 0, 1);

        viewPort = app.getRenderManager().createMainView("Overlay-coordinate", viewPortCamera);
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
        Vector3f dir = getApplication().getCamera().getDirection();
        dir.negateLocal().multLocal(3);

        viewPortCamera.setLocation(dir);
        viewPortCamera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

        node.updateLogicalState(tpf);
    }

    @Override
    public void render(RenderManager rm) {
        node.updateGeometricState();
    }

}
