import com.jme3.math.Vector3f
import com.jme3.material.RenderState.BlendMode
import com.simsilica.lemur.Button
import com.simsilica.lemur.Button.ButtonAction
import com.simsilica.lemur.Command
import com.simsilica.lemur.Insets3f
import com.simsilica.lemur.component.DynamicInsetsComponent
import com.simsilica.lemur.component.IconComponent
import com.simsilica.lemur.HAlignment
import com.simsilica.lemur.VAlignment
import com.simsilica.lemur.component.QuadBackgroundComponent

//
// Colors
//
def textColor = color(0.894, 0.894, 0.894, 1)
def textHighlightColor = color(0.894, 0.894, 0.894, 1)
def textFocusColor = color(0.894, 0.894, 0.894, 1)
def textShadowColor = color(0, 0, 0, 0.75f)
def textShadowHighlightColor = color(0, 0, 0, 0.75f)
def textShadowFocusColor = color(0, 0, 0, 0.75f)

def buttonColor = color(0.345, 0.345, 0.345, 1)
def buttonPressedColor = color(0.416, 0.416, 0.416, 1)
def buttonHighlightColor = color(0.416, 0.416, 0.416, 1)

def buttonPrimaryColor = color(0.298, 0.498, 0.78, 1)
def buttonPrimaryPressedColor = color(0.443, 0.62, 0.863, 1)
def buttonPrimaryHighlightColor = color(0.443, 0.62, 0.863, 1)

def listColor = color(0.2, 0.2, 0.2, 1)
def listSelectionColor = color(0.282, 0.463, 0.718, 0.5)
def listItemEvenColor = color(0.157, 0.157, 0.157, 1)
def listItemOddColor = color(0.176, 0.176, 0.176, 1)

def textFieldColor = color(0.114, 0.114, 0.114, 1)

def toolBarColor = color(0.137, 0.137, 0.137, 1)

def buttonToolBarPressedColor = color(0.282, 0.463, 0.718, 1)
def buttonToolBarHighlightColor = color(0.282, 0.463, 0.718, 1)
def buttonToolBarColor = color(0, 0, 0, 0)

def windowColor = color(0.294, 0.294, 0.294, 1)
def windowTitleColor = color(0.2, 0.2, 0.2, 1)

def sliderColor = color(0.18, 0.18, 0.18, 1)
def sliderThumbColor = color(0.345, 0.345, 0.345, 1)

def panelColor = color(0.137, 0.137, 0.137, 1)
def panelTitleColor = color(0.259, 0.259, 0.259, 1)
def panelContainerColor = color(0.204, 0.204, 0.204, 1)

def tooltipBackgroundColor = color(0.106, 0.106, 0.106, 1)

def separatorColor = color(0.137, 0.137, 0.137, 1)

def tabActiveColor = color(0.282, 0.463, 0.718, 1)
def tabColor = color(0.345, 0.345, 0.345, 1)
def tabPressedColor = color(0.416, 0.416, 0.416, 1)
def tabHighlightColor = color(0.416, 0.416, 0.416, 1)

//
// Global styling
//
selector("editor-style") {
    fontSize = 14
    color = textColor
    highlightColor = textHighlightColor
    focusColor = textFocusColor
    shadowColor = textShadowColor
    highlightShadowColor = textShadowHighlightColor
    focusShadowColor = textShadowFocusColor
    shadowOffset = new Vector3f(1, -1, -1);
}


//
// Tooltip
//
selector("tooltip", "editor-style") {
    background = new QuadBackgroundComponent(tooltipBackgroundColor)
    background.setMargin(6, 6)
}

//
// Button
//
def buttonPressedCmd = new Command<Button>() {
    @Override
    void execute(Button source) {
        if (source.pressed) {
            source.background = new QuadBackgroundComponent(buttonPressedColor)
        } else {
            if (source.highlightOn) {
                source.background = new QuadBackgroundComponent(buttonHighlightColor)
            } else {
                source.background = new QuadBackgroundComponent(buttonColor)
            }
        }
    }
}
def buttonHoverCmd = new Command<Button>() {
    @Override
    void execute(Button source) {
        if (source.highlightOn) {
            source.background = new QuadBackgroundComponent(buttonHighlightColor)
        } else {
            source.background = new QuadBackgroundComponent(buttonColor)
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
    background = new QuadBackgroundComponent(buttonColor)
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
        if (source.pressed) {
            source.background = new QuadBackgroundComponent(buttonPrimaryPressedColor)
        } else {
            if (source.highlightOn) {
                source.background = new QuadBackgroundComponent(buttonPrimaryHighlightColor)
            } else {
                source.background = new QuadBackgroundComponent(buttonPrimaryColor)
            }
        }
    }
}
def primaryButtonHoverCmd = new Command<Button>() {
    @Override
    void execute(Button source) {
        if (source.highlightOn) {
            source.background = new QuadBackgroundComponent(buttonPrimaryHighlightColor)
        } else {
            source.background = new QuadBackgroundComponent(buttonPrimaryColor)
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
    background = new QuadBackgroundComponent(buttonPrimaryColor)
    buttonCommands = primaryButtonCmds
    textHAlignment = HAlignment.Center
    textVAlignment = VAlignment.Center
}

//
// Listbox
//
selector("list.selector", "editor-style") {
    background = new QuadBackgroundComponent(listSelectionColor)
    background.material.material.additionalRenderState.blendMode = BlendMode.AlphaAdditive;
}

//
// Slider
//
def repeatCommand = new Command<Button>() {
    private long startTime;
    private long lastClick;

    public void execute(Button source) {
        // Only do the repeating click while the mouse is
        // over the button (and pressed of course)
        if (source.pressed && source.highlightOn) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            // After half a second pause, click 8 times a second
            if (elapsedTime > 500) {
                if (elapsedTime - lastClick > 125) {
                    source.click()

                    // Try to quantize the last click time to prevent drift
                    lastClick = ((elapsedTime - 500) / 125) * 125 + 500
                }
            }
        } else {
            startTime = System.currentTimeMillis()
            lastClick = 0
        }
    }
}

def sliderButtonCommands = [
        (ButtonAction.Hover): [repeatCommand]
]

selector("slider", "editor-style") {
    background = new QuadBackgroundComponent(sliderColor)
    insets = new Insets3f(2, 0, 2, 0)
}

selector("slider.up.button", "editor-style") {
    text = ""
    insets = null
    buttonCommands = sliderButtonCommands
    icon = new IconComponent("/Interface/scroll-up.png", 1f, 2, 2, 1f, false)
}

selector("slider.down.button", "editor-style") {
    text = ""
    insets = null
    buttonCommands = sliderButtonCommands
    icon = new IconComponent("/Interface/scroll-down.png", 1f, 2, 2, 1f, false)
}

selector("slider.thumb.button", "editor-style") {
    text = ""
    insets = new Insets3f(4, 4, 4, 4)
    buttonCommands = null
    preferredSize = vec3(20, 60, 2)
    background = new QuadBackgroundComponent(sliderThumbColor)
}

//
// TextField
//
selector("textField", "editor-style") {
    background = new QuadBackgroundComponent(textFieldColor)
}

//
// Toolbar
//
selector("toolbar", "editor-style") {
    background = new QuadBackgroundComponent(toolBarColor)
}

def toolbarPressedCmd = new Command<Button>() {
    @Override
    void execute(Button source) {
        if (source.pressed) {
            source.background = new QuadBackgroundComponent(buttonToolBarPressedColor)
        } else {
            if (source.highlightOn) {
                source.background = new QuadBackgroundComponent(buttonToolBarHighlightColor)
            } else {
                source.background = new QuadBackgroundComponent(buttonToolBarColor)
            }
        }
    }
}
def toolbarHoverCmd = new Command<Button>() {
    @Override
    void execute(Button source) {
        if (source.highlightOn) {
            source.background = new QuadBackgroundComponent(buttonToolBarHighlightColor)
        } else {
            source.background = new QuadBackgroundComponent(buttonToolBarColor)
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
    insets = new Insets3f(4, 4, 4, 0)
    background = new QuadBackgroundComponent(buttonToolBarColor)
    buttonCommands = toolbarCmds
}

//
// Window
//
selector("window", "editor-style") {
    background = new QuadBackgroundComponent(windowColor)
}

selector("window", "title-wrapper", "editor-style") {
    background = new QuadBackgroundComponent(windowTitleColor)
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
selector("open-file", "button", "editor-style") {
    insets = new Insets3f(8, 8, 8, 0)
}

selector("open-file", "primary-button", "editor-style") {
    insets = new Insets3f(8, 8, 8, 8)
}

selector("open-file", "textField", "editor-style") {
    insets = new Insets3f(8, 8, 8, 8)
}

selector("open-file", "list.container", "editor-style") {
    insets = new Insets3f(0, 8, 0, 8)
    background = new QuadBackgroundComponent(listColor)
}

selector("open-file.list", "item", "editor-style") {
    insets = new Insets3f(4, 4, 4, 4)
}

selector("open-file.list.item", "even", "editor-style") {
    background = new QuadBackgroundComponent(listItemEvenColor)
}

selector("open-file.list.item", "odd", "editor-style") {
    background = new QuadBackgroundComponent(listItemOddColor)
}

selector("open-file.favorites", "container", "editor-style") {
    insets = new Insets3f(8, 8, 8, 0)
    background = new QuadBackgroundComponent(panelContainerColor)
}

selector("open-file.favorites", "button", "editor-style") {
    insets = new Insets3f(2, 2, 0, 2)
    textHAlignment = HAlignment.Center
    textVAlignment = VAlignment.Center
}

selector("open-file.favorites", "title", "editor-style") {
    background = new QuadBackgroundComponent(panelTitleColor)
    background.setMargin(10, 4)
}
//
// panel
//
selector("panel", "editor-style") {
    background = new QuadBackgroundComponent(panelColor)
}

selector("panel", "scenegraph", "editor-style") {
    insets = new Insets3f(0, 2, 0, 2)
    background = new QuadBackgroundComponent(panelContainerColor)
}

selector("panel", "title", "editor-style") {
    background = new QuadBackgroundComponent(panelTitleColor)
    background.setMargin(10, 4)
}

selector("panel.list", "item", "editor-style") {
    insets = new Insets3f(4, 4, 4, 4)
}

selector("panel.list.item", "even", "editor-style") {
    background = new QuadBackgroundComponent(listItemEvenColor)
}

selector("panel.list.item", "odd", "editor-style") {
    background = new QuadBackgroundComponent(listItemOddColor)
}

selector("panel", "properties", "editor-style") {
    insets = new Insets3f(2, 2, 0, 2)
}

selector("panel.properties", "tabs", "editor-style") {
    // tabs container
}

def tabButtonCmd = new Command<Button>() {
    @Override
    void execute(Button source) {
        if (source.getUserData("active")) {
            source.background = new QuadBackgroundComponent(tabActiveColor)
        } else {
            if (source.pressed) {
                source.background = new QuadBackgroundComponent(tabPressedColor)
            } else {
                if (source.highlightOn) {
                    source.background = new QuadBackgroundComponent(tabHighlightColor)
                } else {
                    source.background = new QuadBackgroundComponent(tabColor)
                }
            }
        }
        source.background.setMargin(2, 2)
    }
}

selector("panel.properties.tabs", "button", "editor-style") {
    insets = new Insets3f(0, 0, 0, 2)
    buttonCommands = [
            (ButtonAction.Down)        : [tabButtonCmd],
            (ButtonAction.Up)          : [tabButtonCmd],
            (ButtonAction.HighlightOn) : [tabButtonCmd],
            (ButtonAction.HighlightOff): [tabButtonCmd]
    ]
}

selector("panel.properties", "content", "editor-style") {
    // tab content container
    insets = new Insets3f(2, 0, 0, 0)
    background = new QuadBackgroundComponent(panelContainerColor)
}

selector("panel.properties", "separator", "editor-style") {
    background = new QuadBackgroundComponent(separatorColor)
    insets = new Insets3f(0, 4, 0, 4)
}

selector("panel.properties", "label", "editor-style") {
    // key of an item
    insets = new Insets3f(4, 4, 4, 4)
    textHAlignment = HAlignment.Right
    textVAlignment = VAlignment.Center

}

selector("panel.properties", "label-ro", "editor-style") {
    insets = new Insets3f(4, 4, 4, 4)
    textHAlignment = HAlignment.Left
    textVAlignment = VAlignment.Center
    background = new QuadBackgroundComponent(textFieldColor);
}

def setButtonCmd = new Command<Button>() {
    @Override
    void execute(Button source) {
        if (source.pressed) {
            source.background = new QuadBackgroundComponent(buttonPressedColor)
        } else {
            if (source.highlightOn) {
                source.background = new QuadBackgroundComponent(buttonHighlightColor)
            } else {
                source.background = new QuadBackgroundComponent(buttonColor)
            }
        }
        source.background.setMargin(2, 2)
    }
}

selector("panel.properties", "button", "editor-style") {
    insets = new Insets3f(2, 0, 2, 2)
    background = new QuadBackgroundComponent(buttonColor)
    background.setMargin(2, 2)
    buttonCommands = [
            (ButtonAction.Down)        : [setButtonCmd],
            (ButtonAction.Up)          : [setButtonCmd],
            (ButtonAction.HighlightOn) : [setButtonCmd],
            (ButtonAction.HighlightOff): [setButtonCmd]
    ]
}

selector("panel.properties", "textField", "editor-style") {
    // textField value of an item
    insets = new Insets3f(2, 2, 2, 2)
    textHAlignment = HAlignment.Left
    textVAlignment = VAlignment.Center
    singleLine = true
}

selector("panel.properties", "checkbox", "editor-style") {
    def on = new IconComponent("/Interface/checkbox-on.png", 1f, 0, 0, 1f, false)
    on.setMargin(2, 0);

    def off = new IconComponent("/Interface/checkbox-off.png", 1f, 0, 0, 1f, false);
    off.setMargin(2, 0);

    onView = on;
    offView = off;
}