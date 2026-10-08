package org.ardulink.gui;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;

import org.ardulink.core.Link;
import org.ardulink.gui.event.PositionEvent;
import org.ardulink.gui.event.PositionEvent.Point;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MotorDriverTest {

	Link linkMock = mock(Link.class);
	MotorDriver sut = createSut();

	private MotorDriver createSut() {
		MotorDriver sut = new MotorDriver();
		sut.setLink(linkMock);
		return sut;
	}

	@ParameterizedTest
	@CsvSource({ //
			"0,  0,someId(F0)[F0]", //
			"0, 31,someId(F31)[F31]", //
			"12,31,someId(F43)[F19]", //
			"31, 0,someId(F31)[B31]", //

			"  0,-31,someId(B31)[B31]", //
			" 12,-31,someId(B19)[B43]", //
			"-12,-31,someId(B43)[B19]", //
			"-12, 31,someId(F19)[F43]", //
			"-31,  0,someId(B31)[F31]", //
	})
	void customMessage(int x, int y, String message) throws IOException {
		sut.positionChanged(new PositionEvent(new Point(x, y), 255, "someId"));
		verify(linkMock).sendCustomMessage(message);
		verifyNoMoreInteractions(linkMock);
	}

}
