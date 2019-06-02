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
package org.openhab.binding.tinkerforge.internal;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.types.Command;
import org.m1theo.tinkerforge.client.commands.OnOffCommand;
import org.m1theo.tinkerforge.client.commands.StringCommand;

/**
 * Convert openHAB commands to tinkerforge-client commands.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
public class CommandConverter {

	public static org.m1theo.tinkerforge.client.commands.Command convert(Command command) {
		if (command instanceof OnOffType) {
			OnOffType cmd = (OnOffType) command;
			return cmd == OnOffType.ON ? OnOffCommand.on() : OnOffCommand.off();
		} else if (command instanceof StringType) {
			StringType cmd = (StringType) command;
			return new StringCommand(cmd.toFullString());
		} else {
			return null;
		}
	}
}
