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

package org.ardulink.gui;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.String.format;
import static org.ardulink.util.Preconditions.checkNotNull;
import static org.ardulink.util.anno.LapsedWith.JDK14;

import java.io.IOException;

import org.ardulink.core.Link;
import org.ardulink.gui.event.PositionEvent;
import org.ardulink.gui.event.PositionEvent.Point;
import org.ardulink.gui.event.PositionListener;
import org.ardulink.util.Throwables;
import org.ardulink.util.anno.LapsedWith;
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
public class MotorDriver implements PositionListener, Linkable {

	@LapsedWith(value = JDK14, module = "records")
	private static class MotorPower {

		private static enum Direction {
			FORWARD('F'), BACKWARDS('B');

			private char value;

			private Direction(char value) {
				this.value = value;
			}
		}

		private final int rightPower;
		private final int leftPower;
		private final Direction rightDirection;
		private final Direction leftDirection;

		public MotorPower(int x, int y) {
			// Motor power is computed with a simple Linear Transformation with this matrix
			// -1 1
			// 1 1

			int localRightPower = -x + y;
			int localLeftPower = x + y;
			rightDirection = direction(localRightPower);
			leftDirection = direction(localLeftPower);
			rightPower = min(255, abs(localRightPower));
			leftPower = min(255, abs(localLeftPower));
		}

		private static Direction direction(int power) {
			return power >= 0 ? Direction.FORWARD : Direction.BACKWARDS;
		}

	}

	private static final Logger logger = LoggerFactory.getLogger(MotorDriver.class);

	private Link link;

	@Override
	public void setLink(Link link) {
		this.link = checkNotNull(link, "link must not be null");
	}

	@Override
	public void positionChanged(PositionEvent event) {
		Point point = event.getPosition();
		// TODO shoudn't we check event.getMaxSize()?
		sendMessage(event.getId(), new MotorPower(point.x, point.y));
	}

	private void sendMessage(String id, MotorPower motorPower) {
		String message = format("%s(%s%d)[%s%d]", id, motorPower.leftDirection.value, motorPower.leftPower,
				motorPower.rightDirection.value, motorPower.rightPower);
		logger.info(message);
		try {
			link.sendCustomMessage(message);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

}
