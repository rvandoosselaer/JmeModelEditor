package com.rvandoosselaer.jmemodeleditor;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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

    @RequiredArgsConstructor
    public enum LightProbes {
        White("/Scene/lightProbe-white.j3o"),
        Nature("/Scene/lightProbe.j3o");

        @Getter
        private final String path;
    }

    @Getter
    private LightProbes currentProbe;
    private Node editorNode;
    private AmbientLight ambientLight;
    private DirectionalLight directionalLight;
    private LightProbe lightProbe;
    @Getter
    private boolean ambientLightEnabled;
    @Getter
    private boolean directionalLightEnabled;
    @Getter
    private boolean lightProbeEnabled = true;
    @Getter
    @Setter
    private boolean updateLightDir = true;
    private Camera camera;
    @Getter
    @Setter
    private boolean lightsDebugEnabled;
    private Geometry lightsDebugGeometry;

    @Override
    protected void initialize(Application app) {
        ambientLight = new AmbientLight(ColorRGBA.White);
        directionalLight = new DirectionalLight(Vector3f.UNIT_Z.negate(), ColorRGBA.White);

        editorNode = getState(ViewPortsState.class).getEditorNode();
        camera = getState(ViewPortsState.class).getEditorCamera();

        loadLightProbe(LightProbes.White);
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        if (ambientLightEnabled) {
            editorNode.addLight(ambientLight);
        }
        if (directionalLightEnabled) {
            editorNode.addLight(directionalLight);
        }
        if (lightProbeEnabled && lightProbe != null) {
            editorNode.addLight(lightProbe);
        }
    }

    @Override
    protected void onDisable() {
        if (ambientLightEnabled) {
            editorNode.removeLight(ambientLight);
        }
        if (directionalLightEnabled) {
            editorNode.removeLight(directionalLight);
        }
        if (lightProbeEnabled && lightProbe != null) {
            editorNode.removeLight(lightProbe);
        }
        if (lightsDebugEnabled && lightsDebugGeometry != null) {
            lightsDebugGeometry.removeFromParent();
        }
    }

    @Override
    public void update(float tpf) {
        if (updateLightDir && directionalLightEnabled) {
            directionalLight.setDirection(new Vector3f(camera.getDirection()));
        }

        if (lightProbe != null && lightsDebugEnabled) {
            updateDebugProbe();
        }

        if (lightsDebugEnabled && lightsDebugGeometry != null && lightsDebugGeometry.getParent() == null) {
            editorNode.attachChild(lightsDebugGeometry);
        } else if (!lightsDebugEnabled && lightsDebugGeometry != null && lightsDebugGeometry.getParent() != null) {
            lightsDebugGeometry.removeFromParent();
        }
    }

    public void setAmbientLightEnabled(boolean ambientLightEnabled) {
        if (this.ambientLightEnabled == ambientLightEnabled) {
            return;
        }

        if (ambientLightEnabled) {
            editorNode.addLight(ambientLight);
        } else {
            editorNode.removeLight(ambientLight);
        }
        this.ambientLightEnabled = ambientLightEnabled;
    }

    public void setDirectionalLightEnabled(boolean directionalLightEnabled) {
        if (this.directionalLightEnabled == directionalLightEnabled) {
            return;
        }

        if (directionalLightEnabled) {
            editorNode.addLight(directionalLight);
        } else {
            editorNode.removeLight(directionalLight);
        }
        this.directionalLightEnabled = directionalLightEnabled;
    }

    public void setLightProbeEnabled(boolean lightProbeEnabled) {
        if (this.lightProbeEnabled == lightProbeEnabled) {
            return;
        }

        if (lightProbeEnabled) {
            editorNode.addLight(lightProbe);
        } else {
            editorNode.removeLight(lightProbe);
        }
        this.lightProbeEnabled = lightProbeEnabled;
    }

    public ColorRGBA getAmbientLightColor() {
        return ambientLight.getColor();
    }

    public void setAmbientLightColor(ColorRGBA color) {
        ambientLight.setColor(color);
    }

    public ColorRGBA getDirectionalLightColor() {
        return directionalLight.getColor();
    }

    public void setDirectionalLightColor(ColorRGBA color) {
        directionalLight.setColor(color);
    }

    public Vector3f getLightProbeLocation() {
        return lightProbe != null ? lightProbe.getPosition() : new Vector3f();
    }

    public void setLightProbeLocation(Vector3f location) {
        if (lightProbe != null) {
            lightProbe.setPosition(location);
        }
    }

    public float getLightProbeRadius() {
        return lightProbe != null ? lightProbe.getArea().getRadius() : -1;
    }

    public void setLightProbeRadius(float radius) {
        if (lightProbe != null) {
            lightProbe.getArea().setRadius(radius);
        }
    }

    public void loadLightProbe(LightProbes lightProbes) {
        this.currentProbe = lightProbes;
        CompletableFuture.supplyAsync(new LightProbeSupplier(lightProbes)).thenAccept(new LightProbeConsumer());
    }

    private void setLightProbe(LightProbe lightProbe) {
        if (this.lightProbe != null && lightProbeEnabled) {
            editorNode.removeLight(this.lightProbe);
        }
        if (lightProbeEnabled) {
            editorNode.addLight(lightProbe);
        }
        this.lightProbe = lightProbe;
    }

    private void updateDebugProbe() {
        if (lightsDebugGeometry == null) {
            lightsDebugGeometry = createDebugProbe();
        }

        lightsDebugGeometry.getMaterial().setTexture("CubeMap", lightProbe.getPrefilteredEnvMap());
        lightsDebugGeometry.setLocalTranslation(lightProbe.getPosition());
    }

    private Geometry createDebugProbe() {
        Geometry geometry = new Geometry("lightProbe - debug", new Sphere(16, 16, 0.25f));
        geometry.setMaterial(new Material(getApplication().getAssetManager(), "/Common/MatDefs/Misc/reflect.j3md"));

        return geometry;
    }

    @RequiredArgsConstructor
    private class LightProbeSupplier implements Supplier<LightProbe> {

        private final LightProbes lightProbes;

        @Override
        public LightProbe get() {
            log.trace("Loading {}", lightProbes.getPath());
            LightProbe lightProbe = (LightProbe) getApplication().getAssetManager().loadAsset(lightProbes.getPath());
            lightProbe.setPosition(new Vector3f(0, 0, 0));
            lightProbe.getArea().setRadius(100);
            return lightProbe;
        }

    }

    private class LightProbeConsumer implements Consumer<LightProbe> {

        @Override
        public void accept(LightProbe lightProbe) {
            getApplication().enqueue(() -> {
                setLightProbe(lightProbe);
            });
        }

    }

}
