package com.rvandoosselaer.jmemodeleditor;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
    private Vec2i viewPortSize = new Vec2i(80, 80);

    @Override
    protected void initialize(Application app) {
        node = new Node("root-coordinate");
        node.setCullHint(Spatial.CullHint.Never);

        viewPortCamera = createViewPortCamera(viewPortSize);
        viewPort = createViewPort(viewPortCamera, node);
    }

    @Override
    protected void cleanup(Application app) {
        destroyViewPort(viewPort);
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
        if (isViewPortResized()) {
            onViewPortResized();
        }

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

    private boolean isViewPortResized() {
        return !Vec2i.fromCamera(viewPortCamera).equals(viewPortSize);
    }

    /**
     * When the window (and viewport) is resized, we need to recreate it. By default it gets the size of window.
     */
    private void onViewPortResized() {
        destroyViewPort(viewPort);
        viewPortCamera = createViewPortCamera(viewPortSize);
        viewPort = createViewPort(viewPortCamera, node);
    }

    private void destroyViewPort(ViewPort viewPort) {
        getApplication().getRenderManager().removeMainView(viewPort);
    }

    private Camera createViewPortCamera(Vec2i viewPortSize) {
        Camera viewPortCamera = new Camera(viewPortSize.x, viewPortSize.y);
        viewPortCamera.setFrustumPerspective(45f, (float) viewPortSize.x / viewPortSize.y, 1f, 5f);
        viewPortCamera.setLocation(new Vector3f(0, 0, 3));
        viewPortCamera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        viewPortCamera.setViewPort(0, 1, 0, 1);

        return viewPortCamera;
    }

    private ViewPort createViewPort(Camera viewPortCamera, Node node) {
        ViewPort viewPort = getApplication().getRenderManager().createMainView("Overlay-coordinate", viewPortCamera);
        viewPort.setClearFlags(false, true, false);
        viewPort.attachScene(node);

        return viewPort;
    }

    @Getter
    @EqualsAndHashCode
    @RequiredArgsConstructor
    private static class Vec2i {

        private final int x;
        private final int y;

        public static Vec2i fromCamera(Camera camera) {
            return new Vec2i(camera.getWidth(), camera.getHeight());
        }

    }

}
