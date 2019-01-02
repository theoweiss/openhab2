/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.avmfritz.internal.hardware.callbacks;

import static org.eclipse.jetty.http.HttpMethod.GET;

import java.io.StringReader;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.openhab.binding.avmfritz.internal.ahamodel.templates.TemplateListModel;
import org.openhab.binding.avmfritz.internal.handler.AVMFritzBaseBridgeHandler;
import org.openhab.binding.avmfritz.internal.hardware.FritzAhaWebInterface;
import org.openhab.binding.avmfritz.internal.util.JAXBUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Callback implementation for updating templates from a xml response.
 *
 * @author Christoph Weitkamp - Initial contribution
 */
public class FritzAhaUpdateTemplatesCallback extends FritzAhaReauthCallback {

    private final Logger logger = LoggerFactory.getLogger(FritzAhaUpdateTemplatesCallback.class);

    private static final String WEBSERVICE_COMMAND = "switchcmd=gettemplatelistinfos";

    private final AVMFritzBaseBridgeHandler handler;

    /**
     * Constructor
     *
     * @param webInterface web interface to FRITZ!Box
     * @param handler handler that will update things
     */
    public FritzAhaUpdateTemplatesCallback(FritzAhaWebInterface webInterface, AVMFritzBaseBridgeHandler handler) {
        super(WEBSERVICE_PATH, WEBSERVICE_COMMAND, webInterface, GET, 1);
        this.handler = handler;
    }

    @Override
    public void execute(int status, String response) {
        super.execute(status, response);
        logger.trace("Received response '{}'", response);
        if (isValidRequest()) {
            try {
                Unmarshaller unmarshaller = JAXBUtils.JAXBCONTEXT_TEMPLATES.createUnmarshaller();
                TemplateListModel model = (TemplateListModel) unmarshaller.unmarshal(new StringReader(response));
                if (model != null) {
                    handler.addTemplateList(model.getTemplates());
                } else {
                    logger.debug("no template in response");
                }
            } catch (JAXBException e) {
                logger.error("Exception creating Unmarshaller: {}", e.getLocalizedMessage(), e);
            }
        } else {
            logger.debug("request is invalid: {}", status);
        }
    }
}
