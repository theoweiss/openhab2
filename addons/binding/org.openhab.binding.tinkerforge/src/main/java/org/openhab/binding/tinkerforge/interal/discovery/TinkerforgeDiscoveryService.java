package org.openhab.binding.tinkerforge.interal.discovery;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.openhab.binding.tinkerforge.handler.BrickdBridgeHandler;

@NonNullByDefault
public class TinkerforgeDiscoveryService extends AbstractDiscoveryService {

    private static int TIMEOUT = 60;
    private BrickdBridgeHandler brickdBridgeHandler;

    public TinkerforgeDiscoveryService(BrickdBridgeHandler brickdBridgeHandler) {
        super(TIMEOUT);
        this.brickdBridgeHandler = brickdBridgeHandler;
    }

    @Override
    protected void startScan() {
        // TODO Auto-generated method stub

    }

    public void activate() {
        // brickdBridgeHandler.registerDeviceStatusListener(this);
    }

    @Override
    public void deactivate() {
        // removeOlderResults(new Date().getTime(), brickdBridgeHandler.getThing().getUID());
        // brickdBridgeHandler.unregisterDeviceStatusListener(this);
    }

}
