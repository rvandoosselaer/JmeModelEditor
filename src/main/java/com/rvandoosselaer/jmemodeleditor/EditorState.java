package com.rvandoosselaer.jmemodeleditor;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Sphere;
import com.rvandoosselaer.jmemodeleditor.gui.GuiState;
import com.rvandoosselaer.jmeutils.util.GeometryUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * The state for rendering 3D scene
 *
 * @author: rvandoosselaer
 */
@Slf4j
public class EditorState extends BaseAppState {

    private static final String ASSET_ROOT_FORMAT = "assetPath.%d";

    private Geometry grid;
    private Geometry centerPoint;
    private Node overlayNode;
    private Node editorNode;
    @Getter
    private Spatial model;
    private Path modelPath;
    private AssetManager assetManager;

    @Override
    protected void initialize(Application app) {
        grid = createGrid(new Vector2f(20, 20), 0.25f);
        centerPoint = createCenterPoint();

        overlayNode = getState(ViewPortsState.class).getOverlayNode();
        editorNode = getState(ViewPortsState.class).getEditorNode();

        assetManager = app.getAssetManager();
        getAssetRootPaths().forEach(this::registerLocator);

        setBackgroundColor();
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

    public Spatial loadModel(Path path) {
        if (path == null) {
            return null;
        }

        log.info("Opening {}", path);

        // make sure there is no previous loaded model in the cache
        assetManager.deleteFromCache(new ModelKey(path.getFileName().toString()));

        // remove the previous model
        removeModel();

        // add the folder of the model to the asset manager; load the model; remove the folder from the asset manager
        Path parent = path.getParent();
        assetManager.registerLocator(parent.toAbsolutePath().toString(), FileLocator.class);
        model = assetManager.loadModel(path.getFileName().toString());
        assetManager.unregisterLocator(parent.toAbsolutePath().toString(), FileLocator.class);

        editorNode.attachChild(model);

        modelPath = path;

        return model;
    }

    public boolean reloadModel() {
        return loadModel(modelPath) != null;
    }

    public boolean saveModel() {
        if (modelPath != null && model != null) {
            log.info("Saving {} to {}", model, modelPath.toAbsolutePath().toString());
            try {
                BinaryExporter.getInstance().save(model, modelPath.toFile());
                return true;
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }

        return false;
    }

    public List<Path> getAssetRootPaths() {
        List<Path> assetRootPaths = new ArrayList<>();

        int i = 0;
        while (Main.getPreferences().get(String.format(ASSET_ROOT_FORMAT, i), null) != null) {
            String stringPath = Main.getPreferences().get(String.format(ASSET_ROOT_FORMAT, i), null);
            GuiState.parsePath(stringPath).ifPresent(assetRootPaths::add);
            i++;
        }

        return assetRootPaths;
    }

    public void setAssetRootPaths(List<Path> paths) {
        List<Path> assetRootPaths = getAssetRootPaths();
        // remove all paths
        for (int i = 0; i < assetRootPaths.size(); i++) {
            String key = String.format(ASSET_ROOT_FORMAT, i);
            Main.getPreferences().remove(key);
            log.trace("Removing preferences {}", key);
            unregisterLocator(assetRootPaths.get(i));
        }

        // set new paths
        for (int i = 0; i < paths.size(); i++) {
            String key = String.format(ASSET_ROOT_FORMAT, i);
            Main.getPreferences().put(key, paths.get(i).toAbsolutePath().toString());
            log.trace("Saving preference {} = {}", key, paths.get(i).toAbsolutePath());
            registerLocator(paths.get(i));
        }
    }

    private void registerLocator(Path path) {
        log.trace("Registering {} to assetmanager", path);
        assetManager.registerLocator(path.toAbsolutePath().toString(), FileLocator.class);
    }

    private void unregisterLocator(Path path) {
        log.trace("Removing {} from assetmanager", path);
        assetManager.unregisterLocator(path.toAbsolutePath().toString(), FileLocator.class);
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

    private void setBackgroundColor() {
        getState(ViewPortsState.class).getEditorViewPort().setBackgroundColor(new ColorRGBA().setAsSrgb(0.22f, 0.22f, 0.22f, 0));
    }

}
