/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.ozonekenyahmis;

import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.ModuleException;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.InitializerService;
import org.openmrs.module.initializer.api.loaders.Loader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class OzoneKenyaHMISActivator extends BaseModuleActivator {
	
	private static final Logger log = LoggerFactory.getLogger("org.openmrs.api");
	
	// contextRefreshed() can be called multiple times. We only run on the first one.
	private boolean started = false;
	
	@Override
	public void willStart() {
		Context.getAdministrationService().setGlobalProperty(InitializerConstants.PROPS_STARTUP_LOAD,
		    InitializerConstants.PROPS_STARTUP_LOAD_DISABLED);
	}
	
	@Override
	public void contextRefreshed() {
		if (!started) {
			log.info("Started Ozone KenyaHMIS");
			try {
				InitializerService iniz = getInitializerService();
				
				for (Loader loader : iniz.getLoaders()) {
					if (!loader.getDomainName().equals(Domain.LIQUIBASE.getName())) {
						continue;
					}
					
					loader.load();
					break;
				}
			}
			catch (Exception e) {
				log.error("Exception caught while trying to load KenyaHMIS liquibase configuration", e);
				throw new ModuleException("Exception caught while trying to load KenyaHMIS liquibase configuration", e);
			}
			finally {
				started = true;
			}
		}
	}
	
	/**
	 * @see #started()
	 */
	public void started() {
		log.info("Started Ozone KenyaHMIS");
		try {
			InitializerService iniz = getInitializerService();
			iniz.loadUnsafe(true, true);
		}
		catch (Exception e) {
			log.error("Exception caught while loading non-Liquibase initializer configuration", e);
			throw new ModuleException(
			        "Exception caught Exception caught while loading non-Liquibase initializer configuration", e);
		}
	}
	
	/**
	 * @see #shutdown()
	 */
	public void shutdown() {
		log.info("Shutdown Ozone KenyaHMIS");
	}
	
	protected InitializerService getInitializerService() {
		List<InitializerService> initializerServices = Context.getRegisteredComponents(InitializerService.class);
		if (initializerServices == null || initializerServices.isEmpty()) {
			throw new ModuleException("ozonekenyahmis could not get a copy of the Initializer service");
		}
		
		return initializerServices.get(0);
	}
}
