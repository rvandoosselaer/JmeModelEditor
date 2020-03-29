package com.rvandoosselaer.jmemodeleditor;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import lombok.Getter;
import lombok.Setter;

/**
 * A state that manages the lights in the editor.
 *
 * @author: rvandoosselaer
 */
public class LightsState extends BaseAppState {

    private Node scene;
    private AmbientLight ambientLight;
    private DirectionalLight directionalLight;
    @Getter
    @Setter
    private boolean updateLightDir = true;
    private Camera camera;

    @Override
    protected void initialize(Application app) {
        ambientLight = new AmbientLight(new ColorRGBA(0.3f, 0.3f, 0.3f, 1));
        directionalLight = new DirectionalLight(Vector3f.UNIT_Z.negate(), ColorRGBA.White);

        scene = getState(EditorState.class).getScene();
        camera = app.getCamera();
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        scene.addLight(ambientLight);
        scene.addLight(directionalLight);
    }

    @Override
    protected void onDisable() {
        scene.removeLight(ambientLight);
        scene.removeLight(directionalLight);
    }

    @Override
    public void update(float tpf) {
        if (updateLightDir) {
            directionalLight.setDirection(camera.getDirection());
        }
    }

}
