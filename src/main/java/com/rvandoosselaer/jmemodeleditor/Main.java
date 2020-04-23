package com.rvandoosselaer.jmemodeleditor;

import com.jme3.app.SimpleApplication;
import com.jme3.material.TechniqueDef;
import com.jme3.system.AppSettings;
import com.rvandoosselaer.jmemodeleditor.gui.CoordinateAxesState;
import com.rvandoosselaer.jmemodeleditor.gui.GuiState;
import com.rvandoosselaer.jmemodeleditor.gui.TooltipState;
import com.rvandoosselaer.jmeutils.ApplicationGlobals;
import com.rvandoosselaer.jmeutils.Resolution;
import com.rvandoosselaer.jmeutils.ResolutionHelper;
import com.rvandoosselaer.jmeutils.util.LogUtils;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author: rvandoosselaer
 */
@Slf4j
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        LogUtils.forwardJULToSlf4j();

        handleJVMArgs();

        Main main = new Main();
        main.start();
    }

    public Main() {
        super(new ViewPortsState(),
                new LightsState(),
                new GuiState(),
                new EditorState(),
                new CoordinateAxesState(),
                new TooltipState(),
                new CameraState());

        setSettings(createSettings());

        setShowSettings(false);
    }

    @Override
    public void simpleInitApp() {
        GuiGlobals.initialize(this);
        ApplicationGlobals.initialize(this);

        removeDefaultMappings();

        loadEditorStyle();

        renderManager.setPreferredLightMode(TechniqueDef.LightMode.SinglePass);
    }

    @Override
    public void requestClose(boolean esc) {
        super.requestClose(esc);
    }

    public static Preferences getPreferences() {
        return Preferences.userNodeForPackage(Main.class);
    }

    public static void clearPreferences() {
        try {
            getPreferences().clear();
        } catch (BackingStoreException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void handleJVMArgs() {
        if (System.getProperty("clearPreferences") != null) {
            log.info("Removing preferences");
            clearPreferences();
        }
    }

    private AppSettings createSettings() {
        AppSettings settings = new AppSettings(true);
        settings.setRenderer(AppSettings.LWJGL_OPENGL32);
        settings.setGammaCorrection(true);
        settings.setResizable(true);
        settings.setTitle(String.format("JmeModelEditor - v%s", VersionHelper.getVersion()));
        Resolution resolution = ResolutionHelper.getFirstHDResolution();
        settings.setResolution(resolution.getWidth(), resolution.getHeight());
        settings.setBitsPerPixel(resolution.getBpp());
        try {
            settings.setIcons(new BufferedImage[]{
                    ImageIO.read(Main.class.getResourceAsStream("/Icons/icon-16.png")),
                    ImageIO.read(Main.class.getResourceAsStream("/Icons/icon-32.png")),
                    ImageIO.read(Main.class.getResourceAsStream("/Icons/icon-128.png")),
                    ImageIO.read(Main.class.getResourceAsStream("/Icons/icon-256.png"))
            });
        } catch (IOException e) {
            log.error("Unable to load icons: {}", e.getMessage());
        }

        log.debug("Settings: {}", settings);

        return settings;
    }

    private void loadEditorStyle() {
        BaseStyles.loadStyleResources(GuiState.DARK_STYLE_RESOURCE);
        GuiGlobals.getInstance().getStyles().setDefaultStyle(GuiState.STYLE);
    }

    private void removeDefaultMappings() {
        getInputManager().deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);
    }

}
