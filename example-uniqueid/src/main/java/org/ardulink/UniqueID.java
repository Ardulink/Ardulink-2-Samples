/**
Copyright 2013 project Ardulink http://www.ardulink.org/

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 */

package org.ardulink;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.ardulink.util.Preconditions.checkNotNull;

import java.util.UUID;

import org.ardulink.core.Link;
import org.ardulink.core.convenience.Links;
import org.ardulink.core.events.RplyEvent;
import org.ardulink.core.qos.ResponseAwaiter;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [ardulinktitle] [ardulinkversion]
 * 
 * project Ardulink http://www.ardulink.org/
 * 
 * [adsense]
 *
 */
public class UniqueID {

	private static final String GET_UNIQUE_ID_CUSTOM_MESSAGE = "getUniqueID";
	private static final String UNIQUE_ID_PARAMETER_VALUE_KEY = "UniqueID";

	@Option(name = "-connection", usage = "Connection URI to the arduino")
	private String connectionUri;

	private static final Logger logger = LoggerFactory.getLogger(UniqueID.class);

	public static void main(String[] args) throws Exception {
		new UniqueID().doMain(args);
	}

	private void doMain(String[] args) throws Exception {
		CmdLineParser cmdLineParser = new CmdLineParser(this);
		try {
			cmdLineParser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			cmdLineParser.printUsage(System.err);
			return;
		}
		try (Link link = connectionUri == null ? Links.getDefault() : Links.getLink(connectionUri)) {
			work(link);
		}
	}

	private void work(Link link) throws Exception {
		logger.info("Asking ID...");
		String uniqueIdSugested = UUID.randomUUID().toString();
		RplyEvent rplyEvent = ResponseAwaiter.onLink(link).withTimeout(500, MILLISECONDS)
				.waitForResponse(link.sendCustomMessage(GET_UNIQUE_ID_CUSTOM_MESSAGE, uniqueIdSugested));

		if (rplyEvent.isOk()) {
			String uniqueIdResponded = checkNotNull(rplyEvent.getParameterValue(UNIQUE_ID_PARAMETER_VALUE_KEY),
					"Reply doesn't contain value for key '%s', keys found: %s", UNIQUE_ID_PARAMETER_VALUE_KEY,
					rplyEvent.getParameterNames()).toString();

			logger.info(uniqueIdSugested.equals(uniqueIdResponded) //
					? format("Device hadn't an ID. Now it is set to: %s", uniqueIdResponded) //
					: format("Device ID is: %s", uniqueIdResponded));
		} else {
			logger.info("Not-OK response from Arduino.");
		}
	}

}
