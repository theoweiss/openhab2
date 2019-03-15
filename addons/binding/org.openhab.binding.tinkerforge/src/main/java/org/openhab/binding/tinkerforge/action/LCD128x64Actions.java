package org.openhab.binding.tinkerforge.action;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.binding.ThingActions;
import org.eclipse.smarthome.core.thing.binding.ThingActionsScope;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.tinkerforge.handler.LCD128x64BrickletHandler;
import org.openhab.core.automation.annotation.ActionInput;
import org.openhab.core.automation.annotation.RuleAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the automation engine action handler service for the
 * LCD128x64 actions.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@ThingActionsScope(name = "tinkerforge")
@NonNullByDefault
public class LCD128x64Actions implements ThingActions {
    private final Logger logger = LoggerFactory.getLogger(LCD128x64Actions.class);
    private @Nullable LCD128x64BrickletHandler handler;

    @Override
    public void setThingHandler(@Nullable ThingHandler handler) {
        if (handler instanceof LCD128x64BrickletHandler) {
            this.handler = (LCD128x64BrickletHandler) handler;
        }
    }

    @Override
    public @Nullable ThingHandler getThingHandler() {
        return this.handler;
    }

    @RuleAction(label = "setGUIButton", description = "Adds a gui button")
    public void setGUIButton(@ActionInput(name = "uid") String uid, @ActionInput(name = "index") int index,
            @ActionInput(name = "positionX") int positionX, @ActionInput(name = "positionY") int positionY,
            @ActionInput(name = "width") int width, @ActionInput(name = "height") int height,
            @ActionInput(name = "text") String text) {
        if (handler == null) {
            logger.error("thinghandler is null");
            return;
        }
        handler.getDisplayChannel().setGUIButton(index, positionX, positionY, width, height, text);
        ;
        // Channel<?, ?, ?> channel =
        // if (channel instanceof DisplayChannel) {
        // DisplayChannel display = (DisplayChannel) channel;
        // if (display.isEnabled()) {
        // display.setGUIButton(index, positionX, positionY, width, height, text);
        // }
        // }
    }

    public static void setGUIButton(@Nullable ThingActions actions, String uid, int index, int positionX, int positionY,
            int width, int height, String text) {
        if (actions instanceof LCD128x64Actions) {
            ((LCD128x64Actions) actions).setGUIButton(uid, index, positionX, positionY, width, height, text);
        } else {
            throw new IllegalArgumentException("Instance is not an LCD128x64Actions class.");
        }
    }
}
