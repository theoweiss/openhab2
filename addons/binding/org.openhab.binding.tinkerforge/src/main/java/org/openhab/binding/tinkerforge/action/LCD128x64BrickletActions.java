package org.openhab.binding.tinkerforge.action;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.binding.ThingActions;
import org.eclipse.smarthome.core.thing.binding.ThingActionsScope;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.m1theo.tinkerforge.client.devices.lcd128x64.DisplayChannel;
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
public class LCD128x64BrickletActions implements ThingActions {
    private final Logger logger = LoggerFactory.getLogger(LCD128x64BrickletActions.class);
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
    public void setGUIButton(@ActionInput(name = "index") int index, @ActionInput(name = "positionX") int positionX,
            @ActionInput(name = "positionY") int positionY, @ActionInput(name = "width") int width,
            @ActionInput(name = "height") int height, @ActionInput(name = "text") String text) {
        if (handler != null) {
            DisplayChannel display = handler.getDisplayChannel();
            if (display != null) {
                display.setGUIButton(index, positionX, positionY, width, height, text);
            }
        } else {
            logger.error("thinghandler is null");
        }
    }

    public static void setGUIButton(@Nullable ThingActions actions, int index, int positionX, int positionY, int width,
            int height, String text) {
        if (actions instanceof LCD128x64BrickletActions) {
            ((LCD128x64BrickletActions) actions).setGUIButton(index, positionX, positionY, width, height, text);
        } else {
            throw new IllegalArgumentException("Instance is not an LCD128x64Actions class.");
        }
    }
}
