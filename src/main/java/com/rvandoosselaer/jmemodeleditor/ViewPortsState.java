package com.rvandoosselaer.jmemodeleditor;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.rvandoosselaer.jmeutils.gui.GuiUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * An appstate that handles the lifecycle of all the available viewports in the editor.
 * - mainViewPort : the main 3D viewport.
 * - overlayViewPort : an overlay on the mainViewPort
 * - coordinateAxesViewPort: a viewport in the bottom right of the window that displays the coordinate axes
 * <p>
 * The same camera is used for the mainViewPort and the overlayViewPort!
 *
 * @author: rvandoosselaer
 */
public class ViewPortsState extends BaseAppState {

    @Getter
    private ViewPort editorViewPort;
    @Getter
    private Camera editorCamera;
    @Getter
    private Node editorNode = new Node("editor");
    @Getter
    private ViewPort overlayViewPort;
    @Getter
    private Node overlayNode = new Node("overlay");
    @Getter
    private ViewPort coordinateAxesViewPort;
    @Getter
    private Camera coordinateAxesCamera;
    @Getter
    private Node coordinateAxesNode = new Node("coordinate-axes");
    private Vec2i coordinateAxesViewPortSize = new Vec2i(80, 80);
    private Vec2i originalWindowSize; // used to check if the window is resized

    @Override
    protected void initialize(Application app) {
        editorNode.setCullHint(Spatial.CullHint.Never);
        overlayNode.setCullHint(Spatial.CullHint.Never);
        coordinateAxesNode.setCullHint(Spatial.CullHint.Never);

        editorCamera = createEditorCamera();
        editorViewPort = createEditorViewPort();

        overlayViewPort = createOverlayViewPort();

        coordinateAxesCamera = createCoordinateAxesCamera();
        coordinateAxesViewPort = createCoordinateAxesViewPort();

        originalWindowSize = Vec2i.fromCamera(app.getCamera());
    }

    @Override
    protected void cleanup(Application app) {
        editorNode.detachAllChildren();
        overlayNode.detachAllChildren();
        coordinateAxesNode.detachAllChildren();

        destroyViewPort(editorViewPort, overlayViewPort, coordinateAxesViewPort);
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    @Override
    public void update(float tpf) {
        if (isWindowResized()) {
            onWindowResized();
        }

        editorNode.updateLogicalState(tpf);
        overlayNode.updateLogicalState(tpf);
        coordinateAxesNode.updateLogicalState(tpf);
    }

    @Override
    public void render(RenderManager rm) {
        editorNode.updateGeometricState();
        overlayNode.updateGeometricState();
        coordinateAxesNode.updateGeometricState();
    }

    /**
     * A utility method to check if the cursor is currently over the given viewport.
     *
     * @param viewPort
     * @return true if the cursor is over the viewport
     */
    public boolean isMouseOverViewPort(ViewPort viewPort) {
        int width = viewPort.getCamera().getWidth();
        int height = viewPort.getCamera().getHeight();

        float x1 = viewPort.getCamera().getViewPortLeft();
        float x2 = viewPort.getCamera().getViewPortRight();
        float y1 = viewPort.getCamera().getViewPortBottom();
        float y2 = viewPort.getCamera().getViewPortTop();

        float mouseX = getApplication().getInputManager().getCursorPosition().x;
        float mouseY = getApplication().getInputManager().getCursorPosition().y;

        return mouseX >= x1 * width && mouseX <= x2 * width && mouseY >= y1 * height && mouseY <= y2 * height;
    }

    private void destroyViewPort(ViewPort... viewPorts) {
        Arrays.stream(viewPorts).forEach(vp -> getApplication().getRenderManager().removeMainView(vp));
    }

    private Camera createEditorCamera() {
        // or we set the camera size to (0.75, 1) and the viewport location to (0, 1, 0, 1). In this case we need to
        // handle the resizing of the viewport.
        // another option is to set the camera size to (1, 1) and position the viewport to (0, 0.75, 0, 1). In this case
        // we don't need to take care of the resizing of the viewport. A caveat in this scenario is that we should adapt
        // the aspect of the camera! We created it with a size of (1, 1) although it's only (0.75, 1). That's why we update
        // the frustum!
        Camera camera = new Camera(GuiUtils.getWidth(), GuiUtils.getHeight());
        camera.setFrustumPerspective(45, (camera.getWidth() * 0.75f) / camera.getHeight(), 0.1f, 1000f);
        camera.setLocation(new Vector3f(0, 0, 10));
        camera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        camera.setViewPort(0, 0.75f, 0, 1);

        return camera;
    }

    private Camera createCoordinateAxesCamera() {
        Camera camera = new Camera(coordinateAxesViewPortSize.x, coordinateAxesViewPortSize.y);
        camera.setFrustumPerspective(45, (float) camera.getWidth() / camera.getHeight(), 1f, 5f);
        camera.setLocation(new Vector3f(0, 0, 3));
        camera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        camera.setViewPort(0, 1, 0, 1);

        return camera;
    }

    private ViewPort createEditorViewPort() {
        ViewPort viewPort = getApplication().getRenderManager().createMainView(editorNode.getName(), editorCamera);
        viewPort.setClearFlags(true, true, true);
        viewPort.attachScene(editorNode);

        return viewPort;
    }

    private ViewPort createOverlayViewPort() {
        ViewPort viewPort = getApplication().getRenderManager().createMainView(overlayNode.getName(), editorCamera);
        viewPort.setClearFlags(false, true, false);
        viewPort.attachScene(overlayNode);

        return viewPort;
    }

    private ViewPort createCoordinateAxesViewPort() {
        ViewPort viewPort = getApplication().getRenderManager().createMainView(coordinateAxesNode.getName(), coordinateAxesCamera);
        viewPort.setClearFlags(false, true, false);
        viewPort.attachScene(coordinateAxesNode);

        return viewPort;
    }

    private boolean isWindowResized() {
        Vec2i currentSize = Vec2i.fromCamera(getApplication().getCamera());
        if (!currentSize.equals(originalWindowSize)) {
            originalWindowSize = currentSize;
            return true;
        }

        return false;
    }

    /**
     * When the window is resized, the camera is set to the size of the window.
     * This is a problem for the coordinateAxesViewPort; we recreate it here.
     */
    private void onWindowResized() {
        destroyViewPort(coordinateAxesViewPort);
        coordinateAxesCamera = createCoordinateAxesCamera();
        coordinateAxesViewPort = createCoordinateAxesViewPort();
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
