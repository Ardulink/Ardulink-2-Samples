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
import static java.lang.String.format;
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

			private static Direction directionOf(int power) {
				return power >= 0 ? Direction.FORWARD : Direction.BACKWARDS;
			}
		}

		@LapsedWith(value = JDK14, module = "records")
		private static class MotorSetting {

			private final int power;
			private final Direction direction;

			public MotorSetting(int power) {
				this.power = abs(power);
				this.direction = Direction.directionOf(power);
			}

		}

		private final MotorSetting left, right;

		public MotorPower(int x, int y) {
			// Motor power is computed with a simple Linear Transformation with this matrix
			// 1 1
			// -1 1
			left = new MotorSetting(x + y);
			right = new MotorSetting(-x + y);
		}

	}

	private static final Logger logger = LoggerFactory.getLogger(MotorDriver.class);

	private Link link;

	@Override
	public void setLink(Link link) {
		this.link = link;
	}

	@Override
	public void positionChanged(PositionEvent event) {
		Point point = event.position();
		try {
			// TODO shoudn't we check event.maxSize()?
			MotorPower motorPower = new MotorPower(point.x, point.y);
			String message = format("%s(%s)[%s]", event.id(), toString(motorPower.left), toString(motorPower.right));
			logger.info(message);
			link.sendCustomMessage(message);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	private static String toString(MotorPower.MotorSetting motorSetting) {
		return format("%s%d", motorSetting.direction.value, motorSetting.power);
	}

}
