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
public class CameraState extends BaseAppState implements AnalogFunctionListener, StateFunctionListener {

    public static final String INPUT_GROUP = "camera.input";
    public static final FunctionId FUNCTION_X_ROTATE = new FunctionId(INPUT_GROUP, "x-rotate");
    public static final FunctionId FUNCTION_Y_ROTATE = new FunctionId(INPUT_GROUP, "y-rotate");
    public static final FunctionId FUNCTION_ZOOM = new FunctionId(INPUT_GROUP, "zoom");
    public static final FunctionId FUNCTION_DRAG = new FunctionId(INPUT_GROUP, "drag");
    public static final FunctionId FUNCTION_MOVE = new FunctionId(INPUT_GROUP, "move");
    public static final FunctionId FUNCTION_STRAFE = new FunctionId(INPUT_GROUP, "strafe");
    public static final FunctionId FUNCTION_BOUNCE = new FunctionId(INPUT_GROUP, "bounce");
    public static final FunctionId FUNCTION_CENTER = new FunctionId(INPUT_GROUP, "center");

    private float distance = 10f;
    private float minDistance = 1.1f;
    private float maxDistance = Float.MAX_VALUE;
    @Getter
    @Setter
    private float rotationSpeed = 4f;
    private float maximumRotationSpeed = 8f;
    @Getter
    @Setter
    private float zoomSpeed = 15f;
    private float yaw = 45 * FastMath.DEG_TO_RAD; // rotation on the Y axis
    private float pitch = 30 * FastMath.DEG_TO_RAD; // rotation on the X axis
    private final Vector3f cameraPosition = new Vector3f();
    private boolean dragToRotate = true;
    private InputMapper inputMapper;
    private Camera camera;
    private Vector3f upVector;
    private Vector3f center = new Vector3f(0, 0, 0);
    private Vector3f pivotPoint = new Vector3f();
    private boolean dragging = false;
    @Getter
    @Setter
    private boolean invertX = false;
    @Getter
    @Setter
    private boolean invertY = true;
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
        inputMapper.map(FUNCTION_DRAG, Button.MOUSE_BUTTON2);
        inputMapper.map(FUNCTION_MOVE, KeyInput.KEY_W);
        inputMapper.map(FUNCTION_MOVE, InputState.Negative, KeyInput.KEY_S);
        inputMapper.map(FUNCTION_STRAFE, InputState.Negative, KeyInput.KEY_D);
        inputMapper.map(FUNCTION_STRAFE, KeyInput.KEY_A);
        inputMapper.map(FUNCTION_BOUNCE, KeyInput.KEY_Q);
        inputMapper.map(FUNCTION_BOUNCE, InputState.Negative, KeyInput.KEY_Z);
        inputMapper.map(FUNCTION_CENTER, KeyInput.KEY_C);

        inputMapper.addAnalogListener(this, FUNCTION_X_ROTATE, FUNCTION_Y_ROTATE, FUNCTION_ZOOM, FUNCTION_MOVE, FUNCTION_STRAFE, FUNCTION_BOUNCE);
        inputMapper.addStateListener(this, FUNCTION_DRAG, FUNCTION_CENTER);
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
        inputMapper.getMappings(FUNCTION_CENTER).forEach(mapping -> inputMapper.removeMapping(mapping));

        inputMapper.removeAnalogListener(this, FUNCTION_X_ROTATE, FUNCTION_Y_ROTATE, FUNCTION_ZOOM, FUNCTION_MOVE, FUNCTION_STRAFE, FUNCTION_BOUNCE);
        inputMapper.removeStateListener(this, FUNCTION_DRAG, FUNCTION_CENTER);

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
            calculateYaw(value, tpf);
        } else if (func == FUNCTION_Y_ROTATE && (dragging || !dragToRotate)) {
            calculatePitch(value, tpf);
        } else if (func == FUNCTION_ZOOM) {
            calculateZoom(value, tpf);
        } else if (func == FUNCTION_MOVE) {
            calculateMove(value, tpf);
        } else if (func == FUNCTION_STRAFE) {
            calculateStrafe(value, tpf);
        } else if (func == FUNCTION_BOUNCE) {
            calculateBounce(value, tpf);
        }
    }

    @Override
    public void valueChanged(FunctionId func, InputState value, double tpf) {
        if (func == FUNCTION_DRAG && dragToRotate) {
            // update the dragging boolean and set the cursor accordingly
            dragging = value != InputState.Off;
            GuiGlobals.getInstance().setCursorEventsEnabled(!dragging);
        } else if (func == FUNCTION_CENTER && value == InputState.Positive) {
            pivotPoint.set(center);
        }
    }

    @Override
    public void update(float tpf) {
        float hDistance = (distance) * FastMath.sin(FastMath.HALF_PI - pitch);
        cameraPosition.set(hDistance * FastMath.cos(yaw), (distance) * FastMath.sin(pitch), hDistance * FastMath.sin(yaw));

        // add the target position
        cameraPosition.addLocal(pivotPoint);

        // set camera location and facing
        camera.setLocation(cameraPosition);
        camera.lookAt(pivotPoint, upVector);
    }

    /**
     * Calculate the up/down movement
     */
    private void calculateBounce(double value, double tpf) {
        Quaternion rotation = new Quaternion().lookAt(camera.getDirection(), Vector3f.UNIT_Y);
        Vector3f upDir = rotation.mult(Vector3f.UNIT_Y);

        pivotPoint.addLocal(upDir.mult((float) (value * moveSpeed * tpf)));
    }

    /**
     * calculate strafe movement
     */
    private void calculateStrafe(double value, double tpf) {
        Quaternion rotation = new Quaternion().lookAt(camera.getDirection(), Vector3f.UNIT_Y);
        Vector3f leftDir = rotation.mult(Vector3f.UNIT_X);

        pivotPoint.addLocal(leftDir.mult((float) (value * moveSpeed * tpf)));
    }

    /**
     * calculate the forward/backward movement
     */
    private void calculateMove(double value, double tpf) {
        Vector3f movement = camera.getDirection().mult((float) (value * moveSpeed * tpf));
        pivotPoint.addLocal(movement);
    }

    /**
     * calculate zoom value
     */
    private void calculateZoom(double value, double tpf) {
        distance += -value * tpf * zoomSpeed;
        distance = FastMath.clamp(distance, minDistance, maxDistance);
    }

    /**
     * calculate pitch value (rotation of the x-axis)
     * when translated to an aircraft, a positive pitch value will raise the nose up, a negative value will lower the
     * nose down.
     */
    private void calculatePitch(double value, double tpf) {
        // try to scale the input value, when you make a sudden mouse movement, the value can become very high
        double normalizedValue = value > 0 ? Math.min(value, maximumRotationSpeed) : Math.max(value, -maximumRotationSpeed);
        pitch += normalizedValue * tpf * rotationSpeed * (invertY ? -1 : 1);
        if (pitch > FastMath.TWO_PI) {
            pitch -= FastMath.TWO_PI;
        }
        if (pitch < 0) {
            pitch += FastMath.TWO_PI;
        }
    }

    /**
     * calculate yaw value (rotation of the y-axis)
     * when translated to an aircraft, a positive value turns the nose to the right, a negative value turns the nose to
     * the left.
     */
    private void calculateYaw(double value, double tpf) {
        // for yaw movement we don't clamp the value
        yaw += value * tpf * rotationSpeed * (invertX ? -1 : 1);
        if (yaw < 0) {
            yaw += FastMath.TWO_PI;
        }
        if (yaw > FastMath.TWO_PI) {
            yaw -= FastMath.TWO_PI;
        }
    }

    public CameraState setDragToRotate(boolean dragToRotate) {
        this.dragToRotate = dragToRotate;
        if (isInitialized()) {
            GuiGlobals.getInstance().setCursorEventsEnabled(dragToRotate);
            getApplication().getInputManager().setCursorVisible(dragToRotate);
        }
        return this;
    }

}
