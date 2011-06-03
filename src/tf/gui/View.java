/*
 * View.java
 */

package tf.gui;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableColumn;
import org.jdesktop.application.Task;

import tf.api.controller.TrafficSimulator;
import tf.api.model.Direction;
import tf.api.model.ModelAndLight;
import tf.api.model.TrafficLight;
import tf.api.model.TrafficModel;

/**
 * The application's main frame.
 */
public class View extends FrameView {

	public View(SingleFrameApplication app) {
		super(app);

		initComponents();

		// status bar initialization - message timeout, idle icon and busy animation, etc
		ResourceMap resourceMap = getResourceMap();
		int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
		messageTimer = new Timer(messageTimeout, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				statusMessageLabel.setText("");
			}
		});
		messageTimer.setRepeats(false);
		int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
		for (int i = 0; i < busyIcons.length; i++) {
			busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
		}
		busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
				statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
			}
		});
		idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
		statusAnimationLabel.setIcon(idleIcon);
		progressBar.setVisible(false);

		// connecting action tasks to status bar via TaskMonitor
		TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
		taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
			public void propertyChange(java.beans.PropertyChangeEvent evt) {
				String propertyName = evt.getPropertyName();
				if ("started".equals(propertyName)) {
					if (!busyIconTimer.isRunning()) {
						statusAnimationLabel.setIcon(busyIcons[0]);
						busyIconIndex = 0;
						busyIconTimer.start();
					}
					progressBar.setVisible(true);
					progressBar.setIndeterminate(true);
				} else if ("done".equals(propertyName)) {
					busyIconTimer.stop();
					statusAnimationLabel.setIcon(idleIcon);
					progressBar.setVisible(false);
					progressBar.setValue(0);
				} else if ("message".equals(propertyName)) {
					String text = (String)(evt.getNewValue());
					statusMessageLabel.setText((text == null) ? "" : text);
					messageTimer.restart();
				} else if ("progress".equals(propertyName)) {
					int value = (Integer)(evt.getNewValue());
					progressBar.setVisible(true);
					progressBar.setIndeterminate(false);
					progressBar.setValue(value);
				}
			}
		});

		myInit();
	}

	private void myInit() {
		// Spinners.
		Long value1 = new Long(100);
		Long min1 = new Long(0);
		Long max1 = Long.MAX_VALUE;
		Long step1 = new Long(1);
		SpinnerNumberModel model1 = new SpinnerNumberModel(value1, min1, max1, step1);
		splStepDur.setModel(model1);

		Double value2 = new Double(0.15);
		Double min2 = new Double(0.01);
		Double max2 = new Double(0.99);
		Double step2 = new Double(0.1);
		SpinnerNumberModel model2 = new SpinnerNumberModel(value2, min2, max2, step2);
		spdIntensity.setModel(model2);

		// Start with default simulator and save to session.
		useSimulator(new TrafficSimulator());
		updateTrafficLightStatus();
	}

	/**
	 * Show traffic light state at status message.
	 * The traffic light is obtained from the session.
	 */
	private void updateTrafficLightStatus() {
		AppSessionData session = App.getApplication().getSessionData();
		TrafficLight li = session.getTfLight();
		displayTrafficLight(li);
	}

	/**
	 * Update session with new Simulator, Model and Lights.
	 * @param simulator The new simulator to use. The model is obtained
	 * using the getInitialTrafficModel() method. The light is
	 * obtained using the getInitialTrafficLight() method.
	 */
	private void useSimulator(TrafficSimulator simulator) {
		// Start with default model and save to session.
		double trafficIntensity = ((Double)spdIntensity.getValue()).doubleValue();
		simulator.setTrafficIntensity(trafficIntensity);
		TrafficModel model = simulator.getInitialTrafficModel();
		TrafficLight lights = simulator.getInitialTrafficLight();

		AppSessionData session = App.getApplication().getSessionData();
		session.setTfLight(lights);
		session.setTfModel(model);
		session.setTfSimulator(simulator);

		// Replace table with new one.
		tblDisplay = null; // Discard default JTable.
		TfTableModel tableModel = new TfTableModel(model);
		TfTableColumnModel tableColumnModel = new TfTableColumnModel();

		for (int count = tableModel.getColumnCount(), i = 0; i < count; i++) {
			TableColumn c = new TableColumn(i);
			c.setHeaderValue(tableModel.getColumnName(i));
			tableColumnModel.addColumn(c);
		}

		tblDisplay = new JTable(tableModel, tableColumnModel);
		tblDisplay.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
		tblDisplay.setAutoCreateColumnsFromModel(false);
		tblDisplay.getTableHeader().setReorderingAllowed(false);
		tblDisplay.setRowSelectionAllowed(false);
		tblDisplay.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblDisplay.setColumnSelectionAllowed(false);
		scrDisplay.setViewportView(tblDisplay);
		scrDisplay.setColumnHeaderView(null); // Hide column header.

		// Display simulator class on window header.
		this.getFrame().setTitle(simulator.getClass().getName());
	}

	/**
	 * Display status of traffic model.
	 * @param model Traffic model.
	 */
	public void displayTrafficModel(TrafficModel model) {
		TfTableModel tableModel = (TfTableModel)tblDisplay.getModel();
		tableModel.update(model);
	}

	/**
	 * Display status of given traffic lights.
	 * @param li Traffic lights.
	 */
	public void displayTrafficLight(TrafficLight li) {
		String msgEW = "[" + TfTableModel.CAR_EW + " " + li.getSignal(Direction.EAST_WEST) + "]";
		String msgWE = " [" + TfTableModel.CAR_WE + " " + li.getSignal(Direction.WEST_EAST) + "]";
		String msgNS = " [" + TfTableModel.CAR_NS + " " + li.getSignal(Direction.NORTH_SOUTH) + "]";
		String msgSN = " [" + TfTableModel.CAR_SN + " " + li.getSignal(Direction.SOUTH_NORTH) + "]";
		statusMessageLabel.setText(msgEW + msgWE + msgNS + msgSN);
	}

	@Action
	public void showAboutBox() {
		if (aboutBox == null) {
			JFrame mainFrame = App.getApplication().getMainFrame();
			aboutBox = new AboutBox(mainFrame);
			aboutBox.setLocationRelativeTo(mainFrame);
		}
		App.getApplication().show(aboutBox);
	}

	/**
	 * Ask the user how many epochs to do and start simulation.
	 * @return Simulation task.
	 */
	@Action
	public Task startSimulation() {
		String runs = JOptionPane.showInputDialog("Enter the number of steps to run.", "100");
		int run;
		try {
			run = Integer.parseInt(runs);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.toString());
			return null;
		}
		// Get duration.
		long delay = ((Long) splStepDur.getValue()).longValue();
		return new SimulateTask(this, run, delay);
	}


	@Action
	public void nextStepAction() {
		// Extract from session and process step.
		AppSessionData session = App.getApplication().getSessionData();
		TrafficModel tfModel = session.getTfModel();
		TrafficLight tfLights = session.getTfLight();
		TrafficSimulator tfSim = session.getTfSimulator();

		ModelAndLight newEnv = tfSim.processStep(tfModel, tfLights);
		session.setTfModel(newEnv.getTfModel());
		session.setTfLight(newEnv.getTfLight());

		// Update display.
		displayTrafficModel(newEnv.getTfModel());
		displayTrafficLight(newEnv.getTfLight());
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		mainPanel = new javax.swing.JPanel();
		pnlDisplay = new javax.swing.JPanel();
		scrDisplay = new javax.swing.JScrollPane();
		tblDisplay = new javax.swing.JTable();
		pnlControls = new javax.swing.JPanel();
		lblStepDur = new javax.swing.JLabel();
		splStepDur = new javax.swing.JSpinner();
		lblIntensity = new javax.swing.JLabel();
		spdIntensity = new javax.swing.JSpinner();
		btnNext = new javax.swing.JButton();
		menuBar = new javax.swing.JMenuBar();
		javax.swing.JMenu fileMenu = new javax.swing.JMenu();
		mniSimClass = new javax.swing.JMenuItem();
		mniStartSim = new javax.swing.JMenuItem();
		jSeparator1 = new javax.swing.JSeparator();
		javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
		javax.swing.JMenu helpMenu = new javax.swing.JMenu();
		javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
		statusPanel = new javax.swing.JPanel();
		javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
		statusMessageLabel = new javax.swing.JLabel();
		statusAnimationLabel = new javax.swing.JLabel();
		progressBar = new javax.swing.JProgressBar();

		mainPanel.setName("mainPanel"); // NOI18N
		mainPanel.setLayout(new java.awt.BorderLayout());

		pnlDisplay.setName("pnlDisplay"); // NOI18N
		pnlDisplay.setLayout(new java.awt.GridLayout(1, 1));

		scrDisplay.setName("scrDisplay"); // NOI18N

		tblDisplay.setModel(new javax.swing.table.DefaultTableModel(
				new Object [][] {
						{null, null, null, null},
						{null, null, null, null},
						{null, null, null, null},
						{null, null, null, null}
				},
				new String [] {
						"Title 1", "Title 2", "Title 3", "Title 4"
				}
		));
		tblDisplay.setName("tblDisplay"); // NOI18N
		scrDisplay.setViewportView(tblDisplay);

		pnlDisplay.add(scrDisplay);

		mainPanel.add(pnlDisplay, java.awt.BorderLayout.CENTER);

		pnlControls.setName("pnlControls"); // NOI18N

		org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(tf.gui.App.class).getContext().getResourceMap(View.class);
		lblStepDur.setText(resourceMap.getString("lblStepDur.text")); // NOI18N
		lblStepDur.setName("lblStepDur"); // NOI18N
		pnlControls.add(lblStepDur);

		splStepDur.setMaximumSize(new java.awt.Dimension(70, 32767));
		splStepDur.setMinimumSize(new java.awt.Dimension(70, 20));
		splStepDur.setName("splStepDur"); // NOI18N
		splStepDur.setPreferredSize(new java.awt.Dimension(70, 20));
		pnlControls.add(splStepDur);

		lblIntensity.setText(resourceMap.getString("lblIntensity.text")); // NOI18N
		lblIntensity.setName("lblIntensity"); // NOI18N
		pnlControls.add(lblIntensity);

		spdIntensity.setMaximumSize(new java.awt.Dimension(60, 32767));
		spdIntensity.setMinimumSize(new java.awt.Dimension(60, 20));
		spdIntensity.setName("spdIntensity"); // NOI18N
		spdIntensity.setPreferredSize(new java.awt.Dimension(60, 20));
		pnlControls.add(spdIntensity);

		javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(tf.gui.App.class).getContext().getActionMap(View.class, this);
		btnNext.setAction(actionMap.get("nextStepAction")); // NOI18N
		btnNext.setText(resourceMap.getString("btnNext.text")); // NOI18N
		btnNext.setName("btnNext"); // NOI18N
		pnlControls.add(btnNext);

		mainPanel.add(pnlControls, java.awt.BorderLayout.SOUTH);

		menuBar.setName("menuBar"); // NOI18N

		fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
		fileMenu.setName("fileMenu"); // NOI18N

		mniSimClass.setText(resourceMap.getString("mniSimClass.text")); // NOI18N
		mniSimClass.setName("mniSimClass"); // NOI18N
		mniSimClass.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				mniSimClassActionPerformed(evt);
			}
		});
		fileMenu.add(mniSimClass);

		mniStartSim.setAction(actionMap.get("startSimulation")); // NOI18N
		mniStartSim.setText(resourceMap.getString("mniStartSim.text")); // NOI18N
		mniStartSim.setName("mniStartSim"); // NOI18N
		fileMenu.add(mniStartSim);

		jSeparator1.setName("jSeparator1"); // NOI18N
		fileMenu.add(jSeparator1);

		exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
		exitMenuItem.setName("exitMenuItem"); // NOI18N
		fileMenu.add(exitMenuItem);

		menuBar.add(fileMenu);

		helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
		helpMenu.setName("helpMenu"); // NOI18N

		aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
		aboutMenuItem.setName("aboutMenuItem"); // NOI18N
		helpMenu.add(aboutMenuItem);

		menuBar.add(helpMenu);

		statusPanel.setName("statusPanel"); // NOI18N

		statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

		statusMessageLabel.setName("statusMessageLabel"); // NOI18N

		statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

		progressBar.setName("progressBar"); // NOI18N

		javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
		statusPanel.setLayout(statusPanelLayout);
		statusPanelLayout.setHorizontalGroup(
				statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
				.addGroup(statusPanelLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(statusMessageLabel)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 378, Short.MAX_VALUE)
						.addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(statusAnimationLabel)
						.addContainerGap())
		);
		statusPanelLayout.setVerticalGroup(
				statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(statusPanelLayout.createSequentialGroup()
						.addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(statusMessageLabel)
								.addComponent(statusAnimationLabel)
								.addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(3, 3, 3))
		);

		setComponent(mainPanel);
		setMenuBar(menuBar);
		setStatusBar(statusPanel);
	}// </editor-fold>//GEN-END:initComponents

	private void mniSimClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniSimClassActionPerformed
		// TODO: Update this list as new algorithms are added.
		String[] clazzes = new String[] {
				"tf.api.controller.TrafficSimulator",
				"tf.api.controller.learning.QLearningBasic",
				"tf.api.controller.learning.QLearningBoltzmann",
				"tf.api.controller.learning.QLearningEligibilityTrace"
		};

		String className = (String)JOptionPane.showInputDialog(null, // parent component.
				"Select a simulation class.",
				"Input",
				JOptionPane.QUESTION_MESSAGE,
				null, // icon
				clazzes,
				clazzes[0]);
		if (className != null) {
			// Create an instace.
			try {
				Class clazz = Class.forName(className);
				TrafficSimulator sim = (TrafficSimulator)clazz.newInstance();
				useSimulator(sim);
				updateTrafficLightStatus();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, ex.toString());
			}
		}
	}//GEN-LAST:event_mniSimClassActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnNext;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JLabel lblIntensity;
	private javax.swing.JLabel lblStepDur;
	private javax.swing.JPanel mainPanel;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JMenuItem mniSimClass;
	private javax.swing.JMenuItem mniStartSim;
	private javax.swing.JPanel pnlControls;
	private javax.swing.JPanel pnlDisplay;
	private javax.swing.JProgressBar progressBar;
	private javax.swing.JScrollPane scrDisplay;
	private javax.swing.JSpinner spdIntensity;
	private javax.swing.JSpinner splStepDur;
	private javax.swing.JLabel statusAnimationLabel;
	private javax.swing.JLabel statusMessageLabel;
	private javax.swing.JPanel statusPanel;
	private javax.swing.JTable tblDisplay;
	// End of variables declaration//GEN-END:variables

	private final Timer messageTimer;
	private final Timer busyIconTimer;
	private final Icon idleIcon;
	private final Icon[] busyIcons = new Icon[15];
	private int busyIconIndex = 0;

	private JDialog aboutBox;
}
