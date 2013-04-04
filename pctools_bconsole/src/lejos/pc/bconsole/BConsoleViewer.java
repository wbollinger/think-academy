package lejos.pc.bconsole;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lejos.pc.comm.NXTConnector;
import lejos.pc.tools.ConsoleDebugDisplay;
import lejos.pc.tools.ConsoleViewComms;
import lejos.pc.tools.ConsoleViewerSwingUI;
import lejos.pc.tools.ConsoleViewerUI;
import lejos.pc.tools.LCDDisplay;

import com.centralnexus.input.*;

/**
 * Downloads data over a BlueTooth connection to a NXT.<br>
 * Uses RConsole by default, or a bidirectional Bluetooth command connection if
 * selected from buttons. If using Custom, you can get a quicker connection
 * entering the name or address of your NXT.<br>
 * Do NOT click "connect" unless the NXT displays the correct "Console" message.
 * Status field shows messages.
 * 
 * @author Roger Glassey 6.1.2008
 */
public class BConsoleViewer extends JFrame implements ActionListener, ChangeListener, ConsoleViewerUI {
	private static final int LCD_WIDTH = 100;
	private static final int LCD_HEIGHT = 64;
	private static final String S_CONNECT = "Connect";
	private static final long serialVersionUID = -4789857573625988062L;

	private JButton connectButton = new JButton(S_CONNECT);
	private JRadioButton rconsoleButton = new JRadioButton("RConsole");
	private JRadioButton btButton = new JRadioButton("Command BT");
	private JCheckBox doJoystick = new JCheckBox("Use Joystick control", true);

	private JLabel statusField = new JLabel();

	private JTextField nameField = new JTextField(10);
	private JTextField addrField = new JTextField(12);
	private JScrollPane theScrollPane;

	private NXTConnector cmdConn;
	private DataOutputStream btOut;
	private DataInputStream btIn;

	private ConsoleViewComms rconComm;

	private static final String USING_RCONSOLE = "Using RConsole";
	private static final String USING_BT = "Using Command Bluetooth";
	// Screen area to hold the downloaded data
	private JTextArea theLog;
	private JConsole theConsole;
	// private JTextArea theCmd;
	private LCDDisplay lcd;
	
	Joystick joy;
	KeyboardPoller poll;

	/**
	 * Constructor builds GUI
	 * 
	 * @param debugFile
	 *            File containing debug information.
	 * @throws IOException
	 */
	public BConsoleViewer(String debugFile) throws IOException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("View BT Debug output from NXT");

		setSize(700, 600);

		statusField.setPreferredSize(new Dimension(200, 20));

		buildGui();
		ConsoleViewerUI ui = new ConsoleViewerSwingUI(this);
		ConsoleDebugDisplay debug = new ConsoleDebugDisplay(ui, debugFile);

		cmdConn = new NXTConnector();
		rconComm = new ConsoleViewComms(ui, debug, true);
		//joy = Joystick.createInstance();
		
	}

	public void buildGui() {
		JPanel connectPanel1 = new JPanel(); // holds text fields
		JPanel connectPanel2 = new JPanel(); // holds buttons
		ButtonGroup choiceGroup = new ButtonGroup();
		btButton.setSelected(true);
		btButton.addChangeListener(this);
		choiceGroup.add(btButton);
		choiceGroup.add(rconsoleButton);
		// rconsoleButton.setSelected(true);
		rconsoleButton.addChangeListener(this);

		connectPanel2.add(btButton);
		connectPanel2.add(rconsoleButton);
		connectPanel2.add(doJoystick);
		connectPanel1.add(new JLabel(" Name"));
		connectPanel1.add(nameField);
		connectButton.addActionListener(this);
		connectPanel1.add(new JLabel("Addr"));
		connectPanel1.add(addrField);

		// holds label and text field
		JPanel statusPanel = new JPanel(new BorderLayout());
		statusPanel.add(new JLabel("Status: "), BorderLayout.LINE_START);
		statusPanel.add(statusField, BorderLayout.CENTER);

		JPanel topLeftPanel = new JPanel(); // North area of the frame
		topLeftPanel.setLayout(new GridLayout(3, 1));
		topLeftPanel.add(connectPanel1);
		topLeftPanel.add(connectButton);
		topLeftPanel.add(connectPanel2);
		lcd = new LCDDisplay();
		lcd.clear();
		// screen.add(new JLabel("Screen"));
		lcd.setMinimumSize(new Dimension(LCD_WIDTH * 2, LCD_HEIGHT * 2));
		lcd.setEnabled(true);
		lcd.setPreferredSize(lcd.getMinimumSize());
		FlowLayout topPanelLayout = new FlowLayout();
		topPanelLayout.setHgap(20);
		JPanel topPanel = new JPanel(topPanelLayout);
		topPanel.add(topLeftPanel);
		topPanel.add(lcd);
		add(topPanel, BorderLayout.NORTH);

		BorderLayout centerLayout = new BorderLayout();
		JPanel centerPanel = new JPanel(centerLayout);
		// theConsole = new JTextArea(); // Center area of the frame
		theLog = null;
		theConsole = null; // new JConsole();
		theScrollPane = new JScrollPane();
		centerPanel.add(theScrollPane, BorderLayout.CENTER);
		// theCmd = new JTextArea();
		// theCmd.setRows(1);
		// centerPanel.add(theCmd, BorderLayout.SOUTH);
		add(centerPanel, BorderLayout.CENTER);

		add(statusPanel, BorderLayout.SOUTH);

		statusField.setText(USING_BT);
	}

	// set the various component's cursor
	private void setTheCursor(int cursor) {
		Cursor c1 = Cursor.getPredefinedCursor(cursor);
		this.setCursor(c1);
		if (cursor == Cursor.DEFAULT_CURSOR)
			c1 = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
		if (theLog != null)
			theLog.setCursor(c1);
		if (theConsole != null)
			theConsole.setCursor(c1);
		nameField.setCursor(c1);
		addrField.setCursor(c1);
	}

	private void connectButtonState(final String label, final boolean enabled) {
		connectButton.setText(label);
		connectButton.setEnabled(enabled);
	}

	/**
	 * Required by action listener. Used by Connect button
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == connectButton) {
			final String name = nameField.getText();
			final String address = addrField.getText();
			final boolean _useRConsole = rconsoleButton.isSelected();
			final boolean useJoystick = doJoystick.isSelected();

			// the thread is so that the GUI will update the button label, etc.
			// while the connection is being established.
			Runnable connectWorker = new Runnable() {
				public void run() {
					// try to establish a connection
					boolean rc;
					if (_useRConsole) {
						rc = rconComm.connectTo(name, address, false/* notUSB */, false/* LCD */);
					} else {
						// Connect to any NXT over Bluetooth
						rc = cmdConn.connectTo("btspp://" + name);

						if (rc == true) {
							btOut = new DataOutputStream(
									cmdConn.getOutputStream());
							btIn = new DataInputStream(cmdConn.getInputStream());
							//
							theConsole = new JConsole(btIn, btOut); 
							theScrollPane.setViewportView(theConsole);
							if (useJoystick) {
								try {
									poll = new KeyboardPoller(btOut);
								} catch (IOException e) {
									e.printStackTrace();
								}
								poll.startPolling();
							}
						} else {
							btOut = null;
							btIn = null;
						}
					}
					final boolean result = rc;

					Runnable guiWorker = new Runnable() {
						public void run() {
							if (result) {
								connectButtonState("Dis" + S_CONNECT.toLowerCase(), true);
								if (theLog != null) {
									theLog.setText("");
									lcd.clear();
									setTheCursor(Cursor.DEFAULT_CURSOR);
								}
							} else {
								statusField.setText(S_CONNECT + "ion Failed!");
								connectButtonState(statusField.getText(), false);
								JOptionPane.showMessageDialog(BConsoleViewer.this, "Sorry... Bluetooth did not connect. \n"
										+ "You might want to check:\n" + " Is the dongle plugged in?\n"
										+ " Is the NXT turned on?\n" + " Does it display  'BT Console....'? ",
										"We have a connection problem.", JOptionPane.PLAIN_MESSAGE);
								// reset state
								connectButtonState(S_CONNECT, true);
								setTheCursor(Cursor.DEFAULT_CURSOR);
							}
						}
					};
					SwingUtilities.invokeLater(guiWorker);
				}
			};

			if (connectButton.getText().equals(S_CONNECT)) {
				setTheCursor(Cursor.WAIT_CURSOR);
				// try to make a connection
				statusField.setText(S_CONNECT + "ing...");
				connectButtonState(statusField.getText(), false);
				new Thread(connectWorker).start();
			} else {
				// assume the button is "Disconnect" so lets do so
				setTheCursor(Cursor.WAIT_CURSOR);
				if (_useRConsole) {
					rconComm.close();
				} else {
					// clean up BT connection
					try {
						btIn.close();
						btOut.close();
						cmdConn.close();
					} catch (IOException ioe) {
						System.err.println("IOException closing BT connection:");
						System.err.println(ioe.getMessage());
					}

				}
				// reset state
				connectButtonState(S_CONNECT, true);
				setTheCursor(Cursor.DEFAULT_CURSOR);
			}
		}
	}

	/**
	 * Initialize the display Frame <br>
	 */
	public static void main(String[] args) {
		// was: ToolStarter.startSwingTool(BConsoleViewer.class, args);
		final String[] fargs = args;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				startTool(BConsoleViewer.class, fargs);
			}
		});
	}

	public static void startTool(Class<?> c, String[] args) {
		int r;
		try {
			Method m = c.getDeclaredMethod("start", String[].class);
			if (m.getReturnType() != int.class)
				throw new NoSuchMethodException("start should return int");
			if (!Modifier.isStatic(m.getModifiers()))
				throw new NoSuchMethodException("start should be static");

			r = ((Integer) m.invoke(null, (Object) args)).intValue();
		} catch (Exception e) {
			Throwable e2 = e;
			if (e2 instanceof InvocationTargetException)
				e2 = ((InvocationTargetException) e).getTargetException();

			e2.printStackTrace(System.err);
			r = 1;
		}

		if (r != 0) {
			System.exit(r);
		}
	}

	public static int start(String[] args) throws IOException {
		String debugFile = null; // AbstractCommandLineParser.getLastOptVal(commandLine,
									// "di");
		if (args.length > 0) {
			// suppress warning
		}
		BConsoleViewer frame = new BConsoleViewer(debugFile);
		frame.setVisible(true);
		return 0;
	}

	/**
	 * Update the status field when RConsole or "Command Bluetooth" radio
	 * buttons selected
	 */
	public void stateChanged(ChangeEvent e) {
		if (rconsoleButton.isSelected()) {
			statusField.setText(USING_RCONSOLE);
		} else {
			statusField.setText(USING_BT);
		}
	}

	/**
	 * Messages generated by ConsoleViewComms show in the status Field
	 */
	public void setStatus(final String s) {
		statusField.setText(s);
	}

	public void connectedTo(final String name, final String address) {
		nameField.setText(name);
		addrField.setText(address);
		statusField.setText(S_CONNECT + "ed to " + name);
	}

	public void append(String s) {
		if (theLog != null) {
			theLog.append(s);
			theLog.setCaretPosition(theLog.getDocument().getLength());
		}
		// if (theConsole != null)
	}

	public void updateLCD(final byte[] buffer) {
		lcd.update(buffer);
	}

	/**
	 * Log a progress message
	 */
	public void logMessage(String msg) {
		System.out.println(msg);
	}
}
