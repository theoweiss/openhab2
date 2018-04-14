package org.openhab.binding.tinkerforge.interal.discovery;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.m1theo.tinkerforge.client.DeviceAdminListener;
import org.m1theo.tinkerforge.client.DeviceChangeType;
import org.m1theo.tinkerforge.client.DeviceInfo;
import org.openhab.binding.tinkerforge.TinkerforgeBindingConstants;
import org.openhab.binding.tinkerforge.handler.BrickdBridgeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NonNullByDefault
public class TinkerforgeDiscoveryService extends AbstractDiscoveryService implements DeviceAdminListener {

    private final Logger logger = LoggerFactory.getLogger(TinkerforgeDiscoveryService.class);

    private static int TIMEOUT = 60;
    private BrickdBridgeHandler brickdBridgeHandler;

    public TinkerforgeDiscoveryService(BrickdBridgeHandler brickdBridgeHandler) {
        super(TIMEOUT);
        this.brickdBridgeHandler = brickdBridgeHandler;
    }

    @Override
    protected void startScan() {
        // not needed
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
        if (deviceChangeType == null || deviceInfo == null) {
            return;
        }
        if (deviceChangeType == DeviceChangeType.ADD) {
            ThingUID thingUID = getThingUID(deviceInfo);
            ThingTypeUID thingTypeUID = getThingTypeUID(deviceInfo);
            if (thingUID != null) {
                ThingUID bridgeUID = brickdBridgeHandler.getThing().getUID();
                Map<String, Object> properties = new HashMap<>(1);
                properties.put(TinkerforgeBindingConstants.DEVICE_UID_PARAM, deviceInfo.getUid());
                DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withThingType(thingTypeUID)
                        .withProperties(properties).withBridge(bridgeUID)
                        .withRepresentationProperty(TinkerforgeBindingConstants.DEVICE_UID_PARAM)
                        .withLabel(deviceInfo.getDeviceType().name()).build();
                thingDiscovered(discoveryResult);
            } else {
                logger.debug("discovered unsupported device {} with uid {}", deviceInfo.getDeviceType().name(),
                        deviceInfo.getUid());
            }
        } else if (deviceChangeType == DeviceChangeType.REMOVE) {
            // TODO
        }
    }

    private @Nullable ThingUID getThingUID(DeviceInfo deviceInfo) {
        ThingUID bridgeUID = brickdBridgeHandler.getThing().getUID();
        ThingTypeUID thingTypeUID = getThingTypeUID(deviceInfo);
        if (TinkerforgeBindingConstants.SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID)) {
            return new ThingUID(thingTypeUID, bridgeUID, deviceInfo.getUid());
        } else {
            return null;
        }
    }

    private ThingTypeUID getThingTypeUID(DeviceInfo deviceInfo) {
        return new ThingTypeUID(TinkerforgeBindingConstants.BINDING_ID, deviceInfo.getDeviceType().name());
    }
}
