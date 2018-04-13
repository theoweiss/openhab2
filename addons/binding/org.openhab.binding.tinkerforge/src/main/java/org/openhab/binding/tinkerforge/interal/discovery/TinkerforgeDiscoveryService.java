package org.openhab.binding.tinkerforge.interal.discovery;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.m1theo.tinkerforge.client.DeviceAdminListener;
import org.m1theo.tinkerforge.client.DeviceChangeType;
import org.m1theo.tinkerforge.client.DeviceInfo;
import org.openhab.binding.tinkerforge.handler.BrickdBridgeHandler;

@NonNullByDefault
public class TinkerforgeDiscoveryService extends AbstractDiscoveryService implements DeviceAdminListener {

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
        brickdBridgeHandler.registerDeviceStatusListener(this);
    }

    @Override
    public void deactivate() {
        // removeOlderResults(new Date().getTime(), brickdBridgeHandler.getThing().getUID());
        brickdBridgeHandler.unregisterDeviceStatusListener(this);
    }

    @Override
    public void deviceChanged(@Nullable DeviceChangeType deviceChangeType, @Nullable DeviceInfo deviceInfo) {
        if (deviceChangeType == DeviceChangeType.ADD) {

        } else if (deviceChangeType == DeviceChangeType.REMOVE) {

        }
    }

}
