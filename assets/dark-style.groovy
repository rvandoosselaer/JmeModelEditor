import com.jme3.math.Vector3f
import com.jme3.material.RenderState.BlendMode
import com.simsilica.lemur.Button
import com.simsilica.lemur.Button.ButtonAction
import com.simsilica.lemur.Command
import com.simsilica.lemur.Insets3f
import com.simsilica.lemur.component.DynamicInsetsComponent
import com.simsilica.lemur.HAlignment
import com.simsilica.lemur.VAlignment
import com.simsilica.lemur.component.QuadBackgroundComponent

//
// Global styling
//
selector("editor-style") {
    fontSize = 14
    color = color(0.894, 0.894, 0.894, 1)
    highlightColor = color(0.894, 0.894, 0.894, 1)
    focusColor = color(0.894, 0.894, 0.894, 1)
    shadowColor = color(0, 0, 0, 0.75f)
    highlightShadowColor = color(0, 0, 0, 0.75f)
    focusShadowColor = color(0, 0, 0, 0.75f)
    shadowOffset = new Vector3f(1, -1, -1);
}

//
// Button
//
def buttonPressedCmd = new Command<Button>() {
    @Override
    void execute(Button source) {
        if (source.isPressed()) {
            source.background = new QuadBackgroundComponent(color(0.416, 0.416, 0.416, 1))
        } else {
            if (source.isHighlightOn()) {
                source.background = new QuadBackgroundComponent(color(0.416, 0.416, 0.416, 1))
            } else {
                source.background = new QuadBackgroundComponent(color(0.345, 0.345, 0.345, 1))
            }
        }
    }
}
def buttonHoverCmd = new Command<Button>() {
    @Override
    void execute(Button source) {
        if (source.isHighlightOn()) {
            source.background = new QuadBackgroundComponent(color(0.416, 0.416, 0.416, 1))
        } else {
            source.background = new QuadBackgroundComponent(color(0.345, 0.345, 0.345, 1))
        }
    }
}
def buttonCmds = [
        (ButtonAction.Down)        : [buttonPressedCmd],
        (ButtonAction.Up)          : [buttonPressedCmd],
        (ButtonAction.HighlightOn) : [buttonHoverCmd],
        (ButtonAction.HighlightOff): [buttonHoverCmd]
]

selector("button", "editor-style") {
    background = new QuadBackgroundComponent(color(0.345, 0.345, 0.345, 1))
    insets = new Insets3f(6, 6, 6, 6)
    buttonCommands = buttonCmds
    textHAlignment = HAlignment.Center
    textVAlignment = VAlignment.Center
}

//
// primary button
//
def primaryButtonPressedCmd = new Command<Button>() {
    @Override
    void execute(Button source) {
        if (source.isPressed()) {
            source.background = new QuadBackgroundComponent(color(0.443, 0.62, 0.863, 1))
        } else {
            if (source.isHighlightOn()) {
                source.background = new QuadBackgroundComponent(color(0.443, 0.62, 0.863, 1))
            } else {
                source.background = new QuadBackgroundComponent(color(0.298, 0.498, 0.78, 1))
            }
        }
    }
}
def primaryButtonHoverCmd = new Command<Button>() {
    @Override
    void execute(Button source) {
        if (source.isHighlightOn()) {
            source.background = new QuadBackgroundComponent(color(0.443, 0.62, 0.863, 1))
        } else {
            source.background = new QuadBackgroundComponent(color(0.298, 0.498, 0.78, 1))
        }
    }
}
def primaryButtonCmds = [
        (ButtonAction.Down)        : [primaryButtonPressedCmd],
        (ButtonAction.Up)          : [primaryButtonPressedCmd],
        (ButtonAction.HighlightOn) : [primaryButtonHoverCmd],
        (ButtonAction.HighlightOff): [primaryButtonHoverCmd]
]
selector("primary-button", "editor-style") {
    background = new QuadBackgroundComponent(color(0.298, 0.498, 0.78, 1))
    insets = new Insets3f(6, 6, 6, 6)
    buttonCommands = primaryButtonCmds
    textHAlignment = HAlignment.Center
    textVAlignment = VAlignment.Center
}

//
// Listbox
//
selector("list.container", "editor-style") {
    insets = new Insets3f(6, 6, 6, 6)
}

selector("list.selector", "editor-style") {
    background = new QuadBackgroundComponent(color(0.282, 0.463, 0.718, 0.5))
    background.material.material.additionalRenderState.blendMode = BlendMode.AlphaAdditive;
}

//
// TextField
//
selector("textField", "editor-style") {
    insets = new Insets3f(6, 6, 6, 6)
    background = new QuadBackgroundComponent(color(0.114, 0.114, 0.114, 1))
}

//
// Toolbar
//
selector("toolbar", "editor-style") {
    background = new QuadBackgroundComponent(color(0.137, 0.137, 0.137, 1))
}

def toolbarPressedCmd = new Command<Button>() {
    @Override
    void execute(Button source) {
        if (source.isPressed()) {
            source.background = new QuadBackgroundComponent(color(0.282, 0.463, 0.718, 1))
        } else {
            if (source.isHighlightOn()) {
                source.background = new QuadBackgroundComponent(color(0.282, 0.463, 0.718, 1))
            } else {
                source.background = null
            }
        }
    }
}
def toolbarHoverCmd = new Command<Button>() {
    @Override
    void execute(Button source) {
        if (source.isHighlightOn()) {
            source.background = new QuadBackgroundComponent(color(0.282, 0.463, 0.718, 1))
        } else {
            source.background = null
        }
    }
}

def toolbarCmds = [
        (ButtonAction.Down)        : [toolbarPressedCmd],
        (ButtonAction.Up)          : [toolbarPressedCmd],
        (ButtonAction.HighlightOn) : [toolbarHoverCmd],
        (ButtonAction.HighlightOff): [toolbarHoverCmd]
]

selector("toolbar", "button", "editor-style") {
    background = new QuadBackgroundComponent(color(0.137, 0.137, 0.137, 1))
    insets = new Insets3f(6, 6, 6, 6)
    buttonCommands = toolbarCmds
}

//
// Window
//
selector("window", "editor-style") {
    background = new QuadBackgroundComponent(color(0.294, 0.294, 0.294, 1))
}

selector("window", "title-wrapper", "editor-style") {
    background = new QuadBackgroundComponent(color(0.2, 0.2, 0.2, 1))
}

selector("window", "title", "editor-style") {
    insets = new Insets3f(2, 0, 2, 0)
    textHAlignment = HAlignment.Center
    textVAlignment = VAlignment.Center
}

selector("window", "button-wrapper", "editor-style") {
    insetsComponent = new DynamicInsetsComponent(0.5, 1, 0.5, 0)
}

//
// Open file
//
selector("open-file", "list.container", "editor-style") {
    background = new QuadBackgroundComponent(color(0.2, 0.2, 0.2, 1))
}

selector("open-file.list", "label", "editor-style") {
    insets = new Insets3f(3, 6, 3, 6)
}