package com.rvandoosselaer.jmemodeleditor;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A state that manages the lights in the editor.
 *
 * @author: rvandoosselaer
 */
@Slf4j
public class LightsState extends BaseAppState {

    private Node scene;
    private AmbientLight ambientLight;
    private DirectionalLight directionalLight;
    @Setter(AccessLevel.PRIVATE)
    private LightProbe lightProbe;
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

        CompletableFuture.supplyAsync(new LightProbeSupplier()).thenAccept(new LightProbeConsumer());
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
        if (lightProbe != null) {
            scene.removeLight(lightProbe);
        }
    }

    @Override
    public void update(float tpf) {
        if (updateLightDir) {
            directionalLight.setDirection(new Vector3f(camera.getDirection()));
        }
    }

    private class LightProbeSupplier implements Supplier<LightProbe> {

        @Override
        public LightProbe get() {
            String lightProbeLocation = "/Scene/lightProbe-white.j3o";
            log.trace("Loading {}", lightProbeLocation);
            LightProbe lightProbe = (LightProbe) getApplication().getAssetManager().loadAsset(lightProbeLocation);
            lightProbe.setPosition(new Vector3f(0, 0, 0));
            lightProbe.getArea().setRadius(100);
            return lightProbe;
        }

    }

    private class LightProbeConsumer implements Consumer<LightProbe> {

        @Override
        public void accept(LightProbe lightProbe) {
            log.trace("Adding {} to {}", lightProbe, scene);
            getApplication().enqueue(() -> {
                scene.addLight(lightProbe);
                setLightProbe(lightProbe);
            });
        }

    }

}
