package com.rvandoosselaer.jmemodeleditor;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Sphere;
import com.rvandoosselaer.jmeutils.util.GeometryUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

/**
 * The state for rendering 3D scene
 *
 * @author: rvandoosselaer
 */
@Slf4j
public class EditorState extends BaseAppState {

    private Geometry grid;
    private Geometry centerPoint;
    private Node overlayNode;
    private Node editorNode;
    private Spatial model;
    private AssetManager assetManager;

    @Override
    protected void initialize(Application app) {
        grid = createGrid(new Vector2f(20, 20), 0.25f);
        centerPoint = createCenterPoint();

        overlayNode = getState(ViewPortsState.class).getOverlayNode();
        editorNode = getState(ViewPortsState.class).getEditorNode();

        assetManager = app.getAssetManager();
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        editorNode.attachChild(grid);
        overlayNode.attachChild(centerPoint);
    }

    @Override
    protected void onDisable() {
        grid.removeFromParent();
        centerPoint.removeFromParent();
        removeModel();
    }

    public void loadModel(Path path) {
        log.info("Opening {}", path);

        // remove the previous model
        removeModel();

        // add the folder of the model to the asset manager; load the model; remove the folder from the asset manager
        Path parent = path.getParent();
        assetManager.registerLocator(parent.toAbsolutePath().toString(), FileLocator.class);
        model = assetManager.loadModel(path.getFileName().toString());
        assetManager.unregisterLocator(parent.toAbsolutePath().toString(), FileLocator.class);

//        model.depthFirstTraversal(new SceneGraphVisitor() {
//            @Override
//            public void visit(Spatial spatial) {
//                AnimComposer animComposer = spatial.getControl(AnimComposer.class);
//                if (animComposer != null) {
//                    log.info("Animations: {}", animComposer.getAnimClipsNames());
//                    animComposer.setCurrentAction("walk");
//                }
//            }
//        });

        editorNode.attachChild(model);
    }

    private void removeModel() {
        if (model != null) {
            model.removeFromParent();
        }
    }

    private Geometry createCenterPoint() {
        return GeometryUtils.createGeometry(new Sphere(32, 32, 0.05f), new ColorRGBA(1, 0, 0, 0.1f), false);
    }

    private Geometry createGrid(Vector2f gridSize, float lineDistance) {
        int linesX = (int) (gridSize.x / lineDistance);
        int linesY = (int) (gridSize.y / lineDistance);

        Geometry grid = GeometryUtils.createGeometry(new Grid(linesX, linesY, lineDistance), new ColorRGBA().setAsSrgb(0.294f, 0.294f, 0.294f, 1), false);
        grid.setLocalTranslation(gridSize.x * -0.5f, 0, gridSize.y * -0.5f);

        return grid;
    }

}
