package com.rvandoosselaer.jmemodeleditor;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

/**
 * The state for rendering 3D scene
 *
 * @author: rvandoosselaer
 */
@Slf4j
public class EditorState extends BaseAppState {

    private Node scene = new Node("scene");

    @Override
    protected void initialize(Application app) {
        setBackgroundColor(app);
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        ((SimpleApplication) getApplication()).getRootNode().attachChild(scene);
    }

    @Override
    protected void onDisable() {
        scene.removeFromParent();
    }

    public void loadModel(Path path) {
        log.info("Opening {}", path);

        // add the folder of the model to the asset manager; load the model; remove the folder from the asset manager
        Path parent = path.getParent();
        AssetManager assetManager = getApplication().getAssetManager();
        assetManager.registerLocator(parent.toAbsolutePath().toString(), FileLocator.class);
        Spatial model = assetManager.loadModel(path.getFileName().toString());
        assetManager.unregisterLocator(parent.toAbsolutePath().toString(), FileLocator.class);

        resetScene();
        scene.attachChild(model);
    }

    private void resetScene() {
        scene.detachAllChildren();
    }

    private void setBackgroundColor(Application app) {
        app.getViewPort().setBackgroundColor(new ColorRGBA().setAsSrgb(0.22f, 0.22f, 0.22f, 1));
    }

}
