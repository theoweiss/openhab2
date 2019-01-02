/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.freebox.internal.api.model;

import java.util.List;

import org.openhab.binding.freebox.internal.api.FreeboxException;

/**
 * The {@link FreeboxAirMediaReceiversResponse} is the Java class used to map the
 * response of the available AirMedia receivers API
 * https://dev.freebox.fr/sdk/os/airmedia/#
 *
 * @author Laurent Garnier - Initial contribution
 */
public class FreeboxAirMediaReceiversResponse extends FreeboxResponse<List<FreeboxAirMediaReceiver>> {
    @Override
    public void evaluate() throws FreeboxException {
        super.evaluate();
        if (getResult() == null) {
            throw new FreeboxException("Missing result data in available AirMedia receivers API response", this);
        }
    }
}
