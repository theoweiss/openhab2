package org.openhab.binding.tinkerforge.internal;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.type.DynamicStateDescriptionProvider;
import org.eclipse.smarthome.core.types.StateDescription;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dynamic channel state description provider.
 * Overrides the state description for the controls, which receive its configuration in the runtime.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@NonNullByDefault
@Component(service = { DynamicStateDescriptionProvider.class,
        TFDynamicStateDescriptionProvider.class }, immediate = true)
public class TFDynamicStateDescriptionProvider implements DynamicStateDescriptionProvider {
    private final Map<ChannelUID, StateDescription> descriptions = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(TFDynamicStateDescriptionProvider.class);

    /**
     * Set a state description for a channel. This description will be used when preparing the channel state by
     * the framework for presentation. A previous description, if existed, will be replaced.
     *
     * @param channelUID
     *                        channel UID
     * @param description
     *                        state description for the channel
     */
    public void setDescription(ChannelUID channelUID, StateDescription description) {
        logger.trace("adding state description for channel {}", channelUID);
        descriptions.put(channelUID, description);
    }

    /**
     * remove all descriptions for a given thing
     *
     * @param thingUID the thing's UID
     */
    public void removeDescriptionsForThing(ThingUID thingUID) {
        logger.trace("removing state description for thing {}", thingUID);
        descriptions.entrySet().removeIf(entry -> entry.getKey().getThingUID().equals(thingUID));
    }

    @Override
    public @Nullable StateDescription getStateDescription(Channel channel,
            @Nullable StateDescription originalStateDescription, @Nullable Locale locale) {
        if (descriptions.containsKey(channel.getUID())) {
            logger.trace("returning new stateDescription for {}", channel.getUID());
            return descriptions.get(channel.getUID());
        } else {
            return originalStateDescription;
        }
    }

}
