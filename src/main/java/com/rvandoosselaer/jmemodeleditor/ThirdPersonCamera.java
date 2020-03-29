package com.rvandoosselaer.jmemodeleditor;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.KeyInput;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.input.AnalogFunctionListener;
import com.simsilica.lemur.input.Axis;
import com.simsilica.lemur.input.Button;
import com.simsilica.lemur.input.FunctionId;
import com.simsilica.lemur.input.InputMapper;
import com.simsilica.lemur.input.InputState;
import com.simsilica.lemur.input.StateFunctionListener;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: rvandoosselaer
 */
@Slf4j
public class ThirdPersonCamera extends BaseAppState implements AnalogFunctionListener, StateFunctionListener {

    public static final String INPUT_GROUP = "camera.input";
    public static final FunctionId FUNCTION_X_ROTATE = new FunctionId(INPUT_GROUP, "x-rotate");
    public static final FunctionId FUNCTION_Y_ROTATE = new FunctionId(INPUT_GROUP, "y-rotate");
    public static final FunctionId FUNCTION_ZOOM = new FunctionId(INPUT_GROUP, "zoom");
    public static final FunctionId FUNCTION_DRAG = new FunctionId(INPUT_GROUP, "drag");
    public static final FunctionId FUNCTION_MOVE = new FunctionId(INPUT_GROUP, "move");
    public static final FunctionId FUNCTION_STRAFE = new FunctionId(INPUT_GROUP, "strafe");
    public static final FunctionId FUNCTION_BOUNCE = new FunctionId(INPUT_GROUP, "bounce");

    @Getter
    @Setter
    private float distance = 5f;
    @Getter
    @Setter
    private float minDistance = 0;
    @Getter
    @Setter
    private float maxDistance = Float.MAX_VALUE;
    @Getter
    @Setter
    private float rotationSpeed = 4f;
    @Getter
    @Setter
    private float maximumRotationSpeed = 8f;
    @Getter
    @Setter
    private float zoomSpeed = 8f;
    @Getter
    @Setter
    private float yaw = 0; // rotation on the Y axis
    @Getter
    @Setter
    private float pitch = 30 * FastMath.DEG_TO_RAD; // rotation on the X axis
    @Getter
    @Setter
    private float minPitch = -FastMath.PI;
    @Getter
    @Setter
    private float maxPitch = FastMath.PI;
    private final Vector3f cameraPosition = new Vector3f();
    @Getter
    private boolean dragToRotate = true;
    private InputMapper inputMapper;
    private Camera camera;
    private Vector3f upVector;
    @Getter
    @Setter
    private Vector3f offset = new Vector3f();
    @Getter
    @Setter
    private Vector3f targetLocation = new Vector3f();
    private boolean dragging = false;
    @Getter
    @Setter
    private boolean invertX = false;
    @Getter
    @Setter
    private boolean invertY = false;
    @Getter
    @Setter
    private float moveSpeed = 2f;

    @Override
    protected void initialize(Application app) {
        camera = app.getCamera();
        upVector = camera.getUp(new Vector3f());

        inputMapper = GuiGlobals.getInstance().getInputMapper();

        //TODO: move to a separate class -> all mappings should be registered at the same place
        inputMapper.map(FUNCTION_X_ROTATE, Axis.MOUSE_X);
        inputMapper.map(FUNCTION_Y_ROTATE, Axis.MOUSE_Y);
        inputMapper.map(FUNCTION_ZOOM, Axis.MOUSE_WHEEL);
        inputMapper.map(FUNCTION_ZOOM, InputState.Positive, KeyInput.KEY_PGUP);
        inputMapper.map(FUNCTION_ZOOM, InputState.Negative, KeyInput.KEY_PGDN);
        inputMapper.map(FUNCTION_DRAG, Button.MOUSE_BUTTON1);
        inputMapper.map(FUNCTION_MOVE, KeyInput.KEY_W);
        inputMapper.map(FUNCTION_MOVE, InputState.Negative, KeyInput.KEY_S);
        inputMapper.map(FUNCTION_STRAFE, InputState.Negative, KeyInput.KEY_D);
        inputMapper.map(FUNCTION_STRAFE, KeyInput.KEY_A);
        inputMapper.map(FUNCTION_BOUNCE, KeyInput.KEY_Q);
        inputMapper.map(FUNCTION_BOUNCE, InputState.Negative, KeyInput.KEY_Z);

        inputMapper.addAnalogListener(this, FUNCTION_X_ROTATE, FUNCTION_Y_ROTATE, FUNCTION_ZOOM, FUNCTION_MOVE, FUNCTION_STRAFE, FUNCTION_BOUNCE);
        inputMapper.addStateListener(this, FUNCTION_DRAG);
    }

    @Override
    protected void cleanup(Application app) {
        //TODO: move to a separate class
        inputMapper.getMappings(FUNCTION_X_ROTATE).forEach(mapping -> inputMapper.removeMapping(mapping));
        inputMapper.getMappings(FUNCTION_Y_ROTATE).forEach(mapping -> inputMapper.removeMapping(mapping));
        inputMapper.getMappings(FUNCTION_ZOOM).forEach(mapping -> inputMapper.removeMapping(mapping));
        inputMapper.getMappings(FUNCTION_DRAG).forEach(mapping -> inputMapper.removeMapping(mapping));
        inputMapper.getMappings(FUNCTION_MOVE).forEach(mapping -> inputMapper.removeMapping(mapping));
        inputMapper.getMappings(FUNCTION_STRAFE).forEach(mapping -> inputMapper.removeMapping(mapping));
        inputMapper.getMappings(FUNCTION_BOUNCE).forEach(mapping -> inputMapper.removeMapping(mapping));

        inputMapper.removeAnalogListener(this, FUNCTION_X_ROTATE, FUNCTION_Y_ROTATE, FUNCTION_ZOOM, FUNCTION_MOVE, FUNCTION_STRAFE, FUNCTION_BOUNCE);
        inputMapper.removeStateListener(this, FUNCTION_DRAG);

        // reset the camera position
        camera.setLocation(new Vector3f(0, 0, 10));
        camera.lookAt(Vector3f.ZERO, upVector);
    }

    @Override
    protected void onEnable() {
        inputMapper.activateGroup(INPUT_GROUP);

        if (!dragToRotate) {
            GuiGlobals.getInstance().setCursorEventsEnabled(false);

            // A 'bug' in Lemur causes it to miss turning the cursor off if
            // we are enabled before the MouseAppState is initialized.
            getApplication().getInputManager().setCursorVisible(false);
        }
    }

    @Override
    protected void onDisable() {
        inputMapper.deactivateGroup(INPUT_GROUP);

        if (!dragToRotate) {
            GuiGlobals.getInstance().setCursorEventsEnabled(true);
            getApplication().getInputManager().setCursorVisible(true);
        }
    }

    @Override
    public void valueActive(FunctionId func, double value, double tpf) {
        if (func == FUNCTION_X_ROTATE && (dragging || !dragToRotate)) {
            // for yaw movement we don't clamp the value
            yaw += value * tpf * rotationSpeed * (invertX ? -1 : 1);
            if (yaw < 0) {
                yaw += FastMath.TWO_PI;
            }
            if (yaw > FastMath.TWO_PI) {
                yaw -= FastMath.TWO_PI;
            }
        } else if (func == FUNCTION_Y_ROTATE && (dragging || !dragToRotate)) {
            // try to scale the input value, when you make a sudden mouse movement, the value can become very high
            double normalizedValue = value > 0 ? Math.min(value, maximumRotationSpeed) : Math.max(value, -maximumRotationSpeed);
            pitch += normalizedValue * tpf * rotationSpeed * (invertY ? -1 : 1);
            if (pitch > FastMath.TWO_PI) {
                pitch -= FastMath.TWO_PI;
            }
            if (pitch < 0) {
                pitch += FastMath.TWO_PI;
            }
        } else if (func == FUNCTION_ZOOM) {
            distance += -value * tpf * zoomSpeed;
            distance = FastMath.clamp(distance, minDistance, maxDistance);
        } else if (func == FUNCTION_MOVE) {
            Vector3f movement = camera.getDirection().mult((float) (value * moveSpeed * tpf));
            targetLocation.addLocal(movement);
        } else if (func == FUNCTION_STRAFE) {
            Quaternion rotation = new Quaternion().lookAt(camera.getDirection(), Vector3f.UNIT_Y);
            Vector3f leftDir = rotation.mult(Vector3f.UNIT_X);

            targetLocation.addLocal(leftDir.mult((float) (value * moveSpeed * tpf)));
        } else if (func == FUNCTION_BOUNCE) {
            Quaternion rotation = new Quaternion().lookAt(camera.getDirection(), Vector3f.UNIT_Y);
            Vector3f upDir = rotation.mult(Vector3f.UNIT_Y);

            targetLocation.addLocal(upDir.mult((float) (value * moveSpeed * tpf)));
        }
    }

    @Override
    public void valueChanged(FunctionId func, InputState value, double tpf) {
        if (func == FUNCTION_DRAG && dragToRotate) {
            // update the dragging boolean and set the cursor accordingly
            dragging = value != InputState.Off;
            GuiGlobals.getInstance().setCursorEventsEnabled(!dragging);
        }
    }

    @Override
    public void update(float tpf) {
        Vector3f target = targetLocation.add(offset);

        float hDistance = (distance) * FastMath.sin(FastMath.HALF_PI - pitch);
        cameraPosition.set(hDistance * FastMath.cos(yaw), (distance) * FastMath.sin(pitch), hDistance * FastMath.sin(yaw));

        // add the target position
        cameraPosition.addLocal(target);

        // set camera location and facing
        camera.setLocation(cameraPosition);
        camera.lookAt(target, upVector);
    }

    public ThirdPersonCamera setDragToRotate(boolean dragToRotate) {
        this.dragToRotate = dragToRotate;
        if (isInitialized()) {
            GuiGlobals.getInstance().setCursorEventsEnabled(dragToRotate);
            getApplication().getInputManager().setCursorVisible(dragToRotate);
        }
        return this;
    }

}
