/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
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
 * LCD128x64Bricklet actions.
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

    @RuleAction(label = "writePixels", description = "writes pixels")
    public void writePixels(@ActionInput(name = "xStart") int xStart, @ActionInput(name = "yStart") int yStart,
            @ActionInput(name = "xEnd") int xEnd, @ActionInput(name = "yEnd") int yEnd,
            @ActionInput(name = "pixels") boolean[] pixels) {
        if (handler != null) {
            DisplayChannel channel = handler.getDisplayChannel();
            if (channel != null) {
                channel.writePixels(xStart, yStart, xEnd, yEnd, pixels);
            }
        } else {
            logger.error("thinghandler is null");
        }
    }

    public static void writePixels(@Nullable ThingActions actions, int xStart, int yStart, int xEnd, int yEnd,
            boolean[] pixels) {
        if (actions instanceof LCD128x64BrickletActions) {
            ((LCD128x64BrickletActions) actions).writePixels(xStart, yStart, xEnd, yEnd, pixels);
        } else {
            throw new IllegalArgumentException("Instance is not an LCD128x64BrickletActions class.");
        }
    }

    @RuleAction(label = "clearDisplay", description = "clear the display")
    public void clearDisplay() {
        if (handler != null) {
            DisplayChannel channel = handler.getDisplayChannel();
            if (channel != null) {
                channel.clearDisplay();
            }
        } else {
            logger.error("thinghandler is null");
        }
    }

    public static void clearDisplay(@Nullable ThingActions actions) {
        if (actions instanceof LCD128x64BrickletActions) {
            ((LCD128x64BrickletActions) actions).clearDisplay();
        } else {
            throw new IllegalArgumentException("Instance is not an LCD128x64BrickletActions class.");
        }
    }

    @RuleAction(label = "writeLine", description = "writes a line")
    public void writeLine(@ActionInput(name = "line") int line, @ActionInput(name = "position") int position,
            @ActionInput(name = "text") String text) {
        if (handler != null) {
            DisplayChannel channel = handler.getDisplayChannel();
            if (channel != null) {
                channel.writeLine(line, position, text);
            }
        } else {
            logger.error("thinghandler is null");
        }
    }

    public static void writeLine(@Nullable ThingActions actions, int line, int position, String text) {
        if (actions instanceof LCD128x64BrickletActions) {
            ((LCD128x64BrickletActions) actions).writeLine(line, position, text);
        } else {
            throw new IllegalArgumentException("Instance is not an LCD128x64BrickletActions class.");
        }
    }

    @RuleAction(label = "drawBufferedFrame", description = "draw a buffered frame")
    public void drawBufferedFrame(@ActionInput(name = "forceCompleteRedraw") boolean forceCompleteRedraw) {
        if (handler != null) {
            DisplayChannel channel = handler.getDisplayChannel();
            if (channel != null) {
                channel.drawBufferedFrame(forceCompleteRedraw);
            }
        } else {
            logger.error("thinghandler is null");
        }
    }

    public static void drawBufferedFrame(@Nullable ThingActions actions, boolean forceCompleteRedraw) {
        if (actions instanceof LCD128x64BrickletActions) {
            ((LCD128x64BrickletActions) actions).drawBufferedFrame(forceCompleteRedraw);
        } else {
            throw new IllegalArgumentException("Instance is not an LCD128x64BrickletActions class.");
        }
    }

    @RuleAction(label = "drawLine", description = "draw a line")
    public void drawLine(@ActionInput(name = "positionXStart") int positionXStart,
            @ActionInput(name = "positionYStart") int positionYStart,
            @ActionInput(name = "positionXEnd") int positionXEnd, @ActionInput(name = "positionYEnd") int positionYEnd,
            @ActionInput(name = "color") boolean color) {
        if (handler != null) {
            DisplayChannel channel = handler.getDisplayChannel();
            if (channel != null) {
                channel.drawLine(positionXStart, positionYStart, positionXEnd, positionYEnd, color);
            }
        } else {
            logger.error("thinghandler is null");
        }
    }

    public static void drawLine(@Nullable ThingActions actions, int positionXStart, int positionYStart,
            int positionXEnd, int positionYEnd, boolean color) {
        if (actions instanceof LCD128x64BrickletActions) {
            ((LCD128x64BrickletActions) actions).drawLine(positionXStart, positionYStart, positionXEnd, positionYEnd,
                    color);
        } else {
            throw new IllegalArgumentException("Instance is not an LCD128x64BrickletActions class.");
        }
    }

    @RuleAction(label = "drawBox", description = "draw a box")
    public void drawBox(@ActionInput(name = "positionXStart") int positionXStart,
            @ActionInput(name = "positionYStart") int positionYStart,
            @ActionInput(name = "positionXEnd") int positionXEnd, @ActionInput(name = "positionYEnd") int positionYEnd,
            @ActionInput(name = "fill") boolean fill, @ActionInput(name = "color") boolean color) {
        if (handler != null) {
            DisplayChannel channel = handler.getDisplayChannel();
            if (channel != null) {
                channel.drawBox(positionXStart, positionYStart, positionXEnd, positionYEnd, fill, color);
            }
        } else {
            logger.error("thinghandler is null");
        }
    }

    public static void drawBox(@Nullable ThingActions actions, int positionXStart, int positionYStart, int positionXEnd,
            int positionYEnd, boolean fill, boolean color) {
        if (actions instanceof LCD128x64BrickletActions) {
            ((LCD128x64BrickletActions) actions).drawBox(positionXStart, positionYStart, positionXEnd, positionYEnd,
                    fill, color);
        } else {
            throw new IllegalArgumentException("Instance is not an LCD128x64BrickletActions class.");
        }
    }

    @RuleAction(label = "drawText", description = "draw a text")
    public void drawText(@ActionInput(name = "positionX") int positionX, @ActionInput(name = "positionY") int positionY,
            @ActionInput(name = "font") int font, @ActionInput(name = "color") boolean color,
            @ActionInput(name = "text") String text) {
        if (handler != null) {
            DisplayChannel channel = handler.getDisplayChannel();
            if (channel != null) {
                channel.drawText(positionX, positionY, font, color, text);
            }
        } else {
            logger.error("thinghandler is null");
        }
    }

    public static void drawText(@Nullable ThingActions actions, int positionX, int positionY, int font, boolean color,
            String text) {
        if (actions instanceof LCD128x64BrickletActions) {
            ((LCD128x64BrickletActions) actions).drawText(positionX, positionY, font, color, text);
        } else {
            throw new IllegalArgumentException("Instance is not an LCD128x64BrickletActions class.");
        }
    }

    @RuleAction(label = "setGUIButton", description = "adds a gui button")
    public void setGUIButton(@ActionInput(name = "index") int index, @ActionInput(name = "positionX") int positionX,
            @ActionInput(name = "positionY") int positionY, @ActionInput(name = "width") int width,
            @ActionInput(name = "height") int height, @ActionInput(name = "text") String text) {
        if (handler != null) {
            DisplayChannel channel = handler.getDisplayChannel();
            if (channel != null) {
                channel.setGUIButton(index, positionX, positionY, width, height, text);
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
            throw new IllegalArgumentException("Instance is not an LCD128x64BrickletActions class.");
        }
    }

    @RuleAction(label = "removeGUIButton", description = "removes a gui button")
    public void removeGUIButton(@ActionInput(name = "index") int index) {
        if (handler != null) {
            DisplayChannel channel = handler.getDisplayChannel();
            if (channel != null) {
                channel.removeGUIButton(index);
            }
        } else {
            logger.error("thinghandler is null");
        }
    }

    public static void removeGUIButton(@Nullable ThingActions actions, int index) {
        if (actions instanceof LCD128x64BrickletActions) {
            ((LCD128x64BrickletActions) actions).removeGUIButton(index);
        } else {
            throw new IllegalArgumentException("Instance is not an LCD128x64BrickletActions class.");
        }
    }

    @RuleAction(label = "setGUISlider", description = "adds a gui button")
    public void setGUISlider(@ActionInput(name = "index") int index, @ActionInput(name = "positionX") int positionX,
            @ActionInput(name = "positionY") int positionY, @ActionInput(name = "length") int length,
            @ActionInput(name = "direction") int direction, @ActionInput(name = "value") int value) {
        if (handler != null) {
            DisplayChannel channel = handler.getDisplayChannel();
            if (channel != null) {
                channel.setGUISlider(index, positionX, positionY, length, direction, value);
            }
        } else {
            logger.error("thinghandler is null");
        }
    }

    public static void setGUISlider(@Nullable ThingActions actions, int index, int positionX, int positionY, int length,
            int direction, int value) {
        if (actions instanceof LCD128x64BrickletActions) {
            ((LCD128x64BrickletActions) actions).setGUISlider(index, positionX, positionY, length, direction, value);
        } else {
            throw new IllegalArgumentException("Instance is not an LCD128x64BrickletActions class.");
        }
    }

    @RuleAction(label = "removeGUISlider", description = "removes a gui button")
    public void removeGUISlider(@ActionInput(name = "index") int index) {
        if (handler != null) {
            DisplayChannel channel = handler.getDisplayChannel();
            if (channel != null) {
                channel.removeGUISlider(index);
            }
        } else {
            logger.error("thinghandler is null");
        }
    }

    public static void removeGUISlider(@Nullable ThingActions actions, int index) {
        if (actions instanceof LCD128x64BrickletActions) {
            ((LCD128x64BrickletActions) actions).removeGUISlider(index);
        } else {
            throw new IllegalArgumentException("Instance is not an LCD128x64BrickletActions class.");
        }
    }

    @RuleAction(label = "setGUITabText", description = "adds a gui tab text style")
    public void setGUITabText(@ActionInput(name = "index") int index, @ActionInput(name = "text") String text) {
        if (handler != null) {
            DisplayChannel channel = handler.getDisplayChannel();
            if (channel != null) {
                channel.setGUITabText(index, text);
            }
        } else {
            logger.error("thinghandler is null");
        }
    }

    public static void setGUITabText(@Nullable ThingActions actions, int index, String text) {
        if (actions instanceof LCD128x64BrickletActions) {
            ((LCD128x64BrickletActions) actions).setGUITabText(index, text);
        } else {
            throw new IllegalArgumentException("Instance is not an LCD128x64BrickletActions class.");
        }
    }

    @RuleAction(label = "setGUITabIcon", description = "adds a gui tab icon style")
    public void setGUITabIcon(@ActionInput(name = "index") int index, @ActionInput(name = "icon") boolean[] icon) {
        if (handler != null) {
            DisplayChannel channel = handler.getDisplayChannel();
            if (channel != null) {
                channel.setGUITabIcon(index, icon);
            }
        } else {
            logger.error("thinghandler is null");
        }
    }

    public static void setGUITabIcon(@Nullable ThingActions actions, int index, boolean[] icon) {
        if (actions instanceof LCD128x64BrickletActions) {
            ((LCD128x64BrickletActions) actions).setGUITabIcon(index, icon);
        } else {
            throw new IllegalArgumentException("Instance is not an LCD128x64BrickletActions class.");
        }
    }

    @RuleAction(label = "removeGUITab", description = "removes a gui tab")
    public void removeGUITab(@ActionInput(name = "index") int index) {
        if (handler != null) {
            DisplayChannel channel = handler.getDisplayChannel();
            if (channel != null) {
                channel.removeGUITab(index);
            }
        } else {
            logger.error("thinghandler is null");
        }
    }

    public static void removeGUITab(@Nullable ThingActions actions, int index) {
        if (actions instanceof LCD128x64BrickletActions) {
            ((LCD128x64BrickletActions) actions).removeGUITab(index);
        } else {
            throw new IllegalArgumentException("Instance is not an LCD128x64BrickletActions class.");
        }
    }

    @RuleAction(label = "setGUIGraphConfiguration", description = "sets gui graph configuration")
    public void setGUIGraphConfiguration(@ActionInput(name = "index") int index,
            @ActionInput(name = "graphType") int graphType, @ActionInput(name = "positionX") int positionX,
            @ActionInput(name = "positionY") int positionY, @ActionInput(name = "width") int width,
            @ActionInput(name = "height") int height, @ActionInput(name = "textX") String textX,
            @ActionInput(name = "textY") String textY) {
        if (handler != null) {
            DisplayChannel channel = handler.getDisplayChannel();
            if (channel != null) {
                channel.setGUIGraphConfiguration(index, graphType, positionX, positionY, width, height, textX, textY);
            }
        } else {
            logger.error("thinghandler is null");
        }
    }

    public static void setGUIGraphConfiguration(@Nullable ThingActions actions, int index, int graphType, int positionX,
            int positionY, int width, int height, String textX, String textY) {
        if (actions instanceof LCD128x64BrickletActions) {
            ((LCD128x64BrickletActions) actions).setGUIGraphConfiguration(index, graphType, positionX, positionY, width,
                    height, textX, textY);
        } else {
            throw new IllegalArgumentException("Instance is not an LCD128x64BrickletActions class.");
        }
    }

    @RuleAction(label = "setGUIGraphData", description = "sets gui graph data")
    public void setGUIGraphData(@ActionInput(name = "index") int index, @ActionInput(name = "data") int[] data) {
        if (handler != null) {
            DisplayChannel channel = handler.getDisplayChannel();
            if (channel != null) {
                channel.setGUIGraphData(index, data);
            }
        } else {
            logger.error("thinghandler is null");
        }
    }

    public static void setGUIGraphData(@Nullable ThingActions actions, int index, int[] data) {
        if (actions instanceof LCD128x64BrickletActions) {
            ((LCD128x64BrickletActions) actions).setGUIGraphData(index, data);
        } else {
            throw new IllegalArgumentException("Instance is not an LCD128x64BrickletActions class.");
        }
    }

    @RuleAction(label = "removeGUIGraph", description = "removes a gui graph")
    public void removeGUIGraph(@ActionInput(name = "index") int index) {
        if (handler != null) {
            DisplayChannel channel = handler.getDisplayChannel();
            if (channel != null) {
                channel.removeGUIGraph(index);
            }
        } else {
            logger.error("thinghandler is null");
        }
    }

    public static void removeGUIGraph(@Nullable ThingActions actions, int index) {
        if (actions instanceof LCD128x64BrickletActions) {
            ((LCD128x64BrickletActions) actions).removeGUIGraph(index);
        } else {
            throw new IllegalArgumentException("Instance is not an LCD128x64BrickletActions class.");
        }
    }

    @RuleAction(label = "removeAllGUI", description = "removes all gui elements")
    public void removeAllGUI() {
        if (handler != null) {
            DisplayChannel channel = handler.getDisplayChannel();
            if (channel != null) {
                channel.removeAllGUI();
            }
        } else {
            logger.error("thinghandler is null");
        }
    }

    public static void removeAllGUI(@Nullable ThingActions actions) {
        if (actions instanceof LCD128x64BrickletActions) {
            ((LCD128x64BrickletActions) actions).removeAllGUI();
        } else {
            throw new IllegalArgumentException("Instance is not an LCD128x64BrickletActions class.");
        }
    }

}