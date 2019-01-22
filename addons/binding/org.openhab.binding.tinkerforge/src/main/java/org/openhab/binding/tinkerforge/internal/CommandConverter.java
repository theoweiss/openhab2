package org.openhab.binding.tinkerforge.internal;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.types.Command;
import org.m1theo.tinkerforge.client.commands.OnOffCommand;

public class CommandConverter {

    public static org.m1theo.tinkerforge.client.commands.Command convert(Command command) {
        if (command instanceof OnOffType) {
            OnOffType cmd = (OnOffType) command;
            return cmd == OnOffType.ON ? OnOffCommand.on() : OnOffCommand.off();
        } else {
            return null;
        }
    }
}
