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

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static org.ardulink.core.NullLink.NULL_LINK;
import static org.ardulink.gui.facility.LAFUtil.setLookAndFeel;
import static org.ardulink.gui.util.LinkReplacer.withConnectionListener;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.ardulink.core.ConnectionListener;
import org.ardulink.core.Link;
import org.ardulink.gui.connectionpanel.ConnectionPanel;
import org.ardulink.gui.customcomponents.SignalButton;
import org.ardulink.gui.customcomponents.ToggleSignalButton;
import org.ardulink.util.Throwables;
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
public class ButtonQuest extends JFrame implements Linkable {

	private static final long serialVersionUID = 1402473246181814940L;

	private static final Logger logger = LoggerFactory.getLogger(ButtonQuest.class);

	private final JPanel contentPane;
	private Link link;

	private final ConnectionPanel genericConnectionPanel;
	private final JButton btnConnect;
	private final JButton btnDisconnect;
	private final JPanel controlPanel;
	private final JTabbedPane tabbedPane;
	private final JPanel buttonPanel;
	private final JPanel setupPanel;
	private final JPanel resultPanel;
	private final SignalButton resultButton;

	private final ConnectionListener connectionListener = new ConnectionListener() {

		@Override
		public void reconnected() {
			connected(true);
		}

		@Override
		public void connectionLost() {
			connected(false);
		}

		private void connected(boolean connected) {
			genericConnectionPanel.setEnabled(!connected);
			btnConnect.setEnabled(!connected);
			btnDisconnect.setEnabled(connected);
		}

	};

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				setLookAndFeel("Nimbus");
				ButtonQuest frame = new ButtonQuest();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ButtonQuest() {
		setTitle("Button Quest");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 735, 407);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		tabbedPane = new JTabbedPane(SwingConstants.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		JPanel connectionPanel = new JPanel();
		tabbedPane.addTab("Connection", null, connectionPanel, null);
		connectionPanel.setLayout(new BorderLayout(0, 0));

		genericConnectionPanel = new ConnectionPanel();
		connectionPanel.add(genericConnectionPanel, BorderLayout.CENTER);

		buttonPanel = new JPanel();
		connectionPanel.add(buttonPanel, BorderLayout.SOUTH);

		btnConnect = new JButton("Connect");
		buttonPanel.add(btnConnect);

		btnDisconnect = new JButton("Disconnect");
		buttonPanel.add(btnDisconnect);
		btnDisconnect.addActionListener(e -> disconnect());
		btnDisconnect.setEnabled(false);

		ConnectionStatus connectionStatus = new ConnectionStatus();
		buttonPanel.add(connectionStatus);
		btnConnect.addActionListener(__ -> {
			try {
				setLink((genericConnectionPanel.createLink()));
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(ButtonQuest.this, ex.getMessage(), "Error", ERROR_MESSAGE);
			}
		});

		controlPanel = new JPanel();
		tabbedPane.addTab("Control", null, controlPanel, null);
		controlPanel.setLayout(new BorderLayout(0, 0));

		setupPanel = new JPanel();
		controlPanel.add(setupPanel, BorderLayout.NORTH);

		getToggleSignalButton(1);
		getToggleSignalButton(2);
		getToggleSignalButton(3);
		getToggleSignalButton(4);

		resultPanel = new JPanel();
		controlPanel.add(resultPanel, BorderLayout.SOUTH);

		resultButton = new SignalButton();
		resultPanel.add(resultButton);

		resultButton.setButtonText("Get Result");
		resultButton.setId("getResult");
		resultButton.setValueVisible(false);

		setLink(NULL_LINK);
	}

	private ToggleSignalButton getToggleSignalButton(int index) {
		ToggleSignalButton button = new ToggleSignalButton();
		setupPanel.add(button);
		button.setValueOnVisible(false);
		button.setValueOffVisible(false);
		button.setButtonTextOn("On Button " + index);
		button.setButtonTextOff("Off Button " + index);

		button.setId("setbutton");
		button.setValueOn("on/" + index);
		button.setValueOff("off/" + index);
		return button;
	}

	private void disconnect() {
		try {
			this.link.close();
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
		logger.info("Connection closed");
		setLink(NULL_LINK);
	}

	@Override
	public void setLink(Link link) {
		this.link = withConnectionListener(connectionListener).replace(this.link).with(link);
	}

}
