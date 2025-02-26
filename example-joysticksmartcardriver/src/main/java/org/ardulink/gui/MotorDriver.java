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

import static java.lang.String.format;
import static org.ardulink.util.Preconditions.checkNotNull;

import java.awt.Point;
import java.io.IOException;

import org.ardulink.core.Link;
import org.ardulink.gui.event.PositionEvent;
import org.ardulink.gui.event.PositionListener;
import org.ardulink.util.Throwables;

/**
 * [ardulinktitle] [ardulinkversion]
 * 
 * project Ardulink http://www.ardulink.org/
 * 
 * [adsense]
 *
 */
public class MotorDriver implements PositionListener, Linkable {

	private Link link;

	private int x;
	private int y;
	private String id = "none";

	private int rightPower;
	private int leftPower;
	private String rightDirection = "F";
	private String leftDirection = "F";

	@Override
	public void setLink(Link link) {
		this.link = checkNotNull(link, "link must not be null");
	}

	@Override
	public void positionChanged(PositionEvent e) {
		synchronized (this) {
			e.getMaxSize();
			Point p = e.getPosition();
			x = p.x;
			y = p.y;
			id = e.getId();

			computeMotorPower();
			sendMessage();
		}
	}

	private void computeMotorPower() {
		// Motor power is computed with a simple Linear Transformation with this matrix
		// -1 1
		// 1 1

		rightPower = -x + y;
		leftPower = x + y;
		rightDirection = "F";
		if (rightPower < 0) {
			rightDirection = "B";
			rightPower = -rightPower;
		}
		leftDirection = "F";
		if (leftPower < 0) {
			leftDirection = "B";
			leftPower = -leftPower;
		}
		rightPower = Math.min(255, rightPower);
		leftPower = Math.min(255, leftPower);
	}

	private void sendMessage() {
		String message = format("%s(%s%d)[%s%d]", id, leftDirection, leftPower, rightDirection, rightPower);
		System.out.println(message);
		try {
			link.sendCustomMessage(message);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}
}
