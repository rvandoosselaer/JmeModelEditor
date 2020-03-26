import com.simsilica.lemur.Button
import com.simsilica.lemur.Button.ButtonAction
import com.simsilica.lemur.Command
import com.simsilica.lemur.Insets3f
import com.simsilica.lemur.component.QuadBackgroundComponent

selector( "editor-style" ) {
    fontSize = 14
}

selector( "toolbar", "editor-style" ) {
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
];


selector( "toolbar", "button", "editor-style" ) {
    insets = new Insets3f(6, 6, 6, 6)
    buttonCommands = toolbarCmds
}

//
//import com.simsilica.lemur.*;
//import com.simsilica.lemur.Button.ButtonAction;
//import com.simsilica.lemur.component.*;
//
//def gradient = TbtQuadBackgroundComponent.create(
//        texture( name:"/com/simsilica/lemur/icons/bordered-gradient.png",
//                generateMips:false ),
//        1, 1, 1, 126, 126,
//        1f, false );
//
//def bevel = TbtQuadBackgroundComponent.create(
//        texture( name:"/com/simsilica/lemur/icons/bevel-quad.png",
//                generateMips:false ),
//        0.125f, 8, 8, 119, 119,
//        1f, false );
//
//def border = TbtQuadBackgroundComponent.create(
//        texture( name:"/com/simsilica/lemur/icons/border.png",
//                generateMips:false ),
//        1, 1, 1, 6, 6,
//        1f, false );
//def border2 = TbtQuadBackgroundComponent.create(
//        texture( name:"/com/simsilica/lemur/icons/border.png",
//                generateMips:false ),
//        1, 2, 2, 6, 6,
//        1f, false );
//
//def doubleGradient = new QuadBackgroundComponent( color(0.5, 0.75, 0.85, 0.5) );
//doubleGradient.texture = texture( name:"/com/simsilica/lemur/icons/double-gradient-128.png",
//        generateMips:false )
//
//
//selector( "label", "editor-style" ) {
//    insets = new Insets3f( 2, 2, 0, 2 );
//    color = color(0.5, 0.75, 0.75, 0.85)
//}
//
//selector( "container", "editor-style" ) {
//    background = gradient.clone()
//    background.setColor(color(0.25, 0.5, 0.5, 0.5))
//}
//
//selector( "slider", "editor-style" ) {
//    background = gradient.clone()
//    background.setColor(color(0.25, 0.5, 0.5, 0.5))
//}
//
//def toolbarPressedCmd = new Command<Button>() {
//    public void execute( Button source ) {
//        if( source.isPressed() ) {
//            source.move(1, -1, 0);
//        } else {
//            source.move(-1, 1, 0);
//        }
//    }
//};
//
//def repeatCommand = new Command<Button>() {
//    private long startTime;
//    private long lastClick;
//
//    public void execute( Button source ) {
//        // Only do the repeating click while the mouse is
//        // over the button (and pressed of course)
//        if( source.isPressed() && source.isHighlightOn() ) {
//            long elapsedTime = System.currentTimeMillis() - startTime;
//            // After half a second pause, click 8 times a second
//            if( elapsedTime > 500 ) {
//                if( elapsedTime - lastClick > 125 ) {
//                    source.click();
//
//                    // Try to quantize the last click time to prevent drift
//                    lastClick = ((elapsedTime - 500) / 125) * 125 + 500;
//                }
//            }
//        } else {
//            startTime = System.currentTimeMillis();
//            lastClick = 0;
//        }
//    }
//};
//
//def toolbarCmds = [
//        (ButtonAction.Down):[toolbarPressedCmd],
//        (ButtonAction.Up):[toolbarPressedCmd]
//];
//
//def sliderButtonCommands = [
//        (ButtonAction.Hover):[repeatCommand]
//];
//
//selector( "title", "editor-style" ) {
//    color = color(0.8, 0.9, 1, 0.85f)
//    highlightColor = color(1, 0.8, 1, 0.85f)
//    shadowColor = color(0, 0, 0, 0.75f)
//    shadowOffset = new com.jme3.math.Vector3f(2, -2, -1);
//    background = new QuadBackgroundComponent( color(0.5, 0.75, 0.85, 0.5) );
//    background.texture = texture( name:"/com/simsilica/lemur/icons/double-gradient-128.png",
//            generateMips:false )
//    insets = new Insets3f( 2, 2, 2, 2 );
//
//    buttonCommands = toolbarCmds;
//}
//
//
//selector( "button", "editor-style" ) {
//    background = gradient.clone()
//    color = color(0.8, 0.9, 1, 0.85f)
//    background.setColor(color(0, 0.75, 0.75, 0.5))
//    insets = new Insets3f( 2, 2, 2, 2 );
//
//    buttonCommands = toolbarCmds;
//}
//
//selector( "slider", "editor-style" ) {
//    insets = new Insets3f( 1, 3, 1, 2 );
//}
//
//selector( "slider", "button", "editor-style" ) {
//    background = doubleGradient.clone()
//    background.setColor(color(0.5, 0.75, 0.75, 0.5))
//    insets = new Insets3f( 0, 0, 0, 0 );
//}
//
//selector( "slider.thumb.button", "editor-style" ) {
//    text = "[]"
//    color = color(0.6, 0.8, 0.8, 0.85)
//}
//
//selector( "slider.left.button", "editor-style" ) {
//    text = "-"
//    background = doubleGradient.clone()
//    background.setColor(color(0.5, 0.75, 0.75, 0.5))
//    background.setMargin(5, 0);
//    color = color(0.6, 0.8, 0.8, 0.85)
//
//    buttonCommands = sliderButtonCommands;
//}
//
//selector( "slider.right.button", "editor-style" ) {
//    text = "+"
//    background = doubleGradient.clone()
//    background.setColor(color(0.5, 0.75, 0.75, 0.5))
//    background.setMargin(4, 0);
//    color = color(0.6, 0.8, 0.8, 0.85)
//
//    buttonCommands = sliderButtonCommands;
//}
//
//selector( "slider.up.button", "editor-style" ) {
//    buttonCommands = sliderButtonCommands;
//}
//
//selector( "slider.down.button", "editor-style" ) {
//    buttonCommands = sliderButtonCommands;
//}
//
//selector( "checkbox", "editor-style" ) {
//    def on = new IconComponent( "/com/simsilica/lemur/icons/Glass-check-on.png", 1f,
//            0, 0, 1f, false );
//    on.setColor(color(0.5, 0.9, 0.9, 0.9))
//    on.setMargin(5, 0);
//    def off = new IconComponent( "/com/simsilica/lemur/icons/Glass-check-off.png", 1f,
//            0, 0, 1f, false );
//    off.setColor(color(0.6, 0.8, 0.8, 0.8))
//    off.setMargin(5, 0);
//
//    onView = on;
//    offView = off;
//
//    color = color(0.8, 0.9, 1, 0.85f)
//}
//
//selector( "rollup", "editor-style" ) {
//    background = gradient.clone()
//    background.setColor(color(0.25, 0.5, 0.5, 0.5))
//}
//
//selector( "tabbedPanel", "editor-style" ) {
//    activationColor = color(0.8, 0.9, 1, 0.85f)
//}
//
//selector( "tabbedPanel.container", "editor-style" ) {
//    background = null
//}
//
//selector( "tab.button", "editor-style" ) {
//    background = gradient.clone()
//    background.setColor(color(0.25, 0.5, 0.5, 0.5))
//    color = color(0.4, 0.45, 0.5, 0.85f)
//    insets = new Insets3f( 4, 2, 0, 2 );
//
//    buttonCommands = toolbarCmds;
//}
//
//
