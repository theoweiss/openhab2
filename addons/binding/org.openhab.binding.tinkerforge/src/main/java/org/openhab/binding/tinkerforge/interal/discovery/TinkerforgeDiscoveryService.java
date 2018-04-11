package org.openhab.binding.tinkerforge.interal.discovery;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.osgi.service.component.annotations.Component;

@Component(service = DiscoveryService.class, immediate = true, configurationPid = "discovery.tinkerforge")
public class TinkerforgeDiscoveryService extends AbstractDiscoveryService {

    public TinkerforgeDiscoveryService() throws IllegalArgumentException {
        super(1000);
        // TODO Auto-generated constructor stub
    }

    public TinkerforgeDiscoveryService(int timeout) throws IllegalArgumentException {
        super(timeout);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void startScan() {
        // TODO Auto-generated method stub

    }

}
