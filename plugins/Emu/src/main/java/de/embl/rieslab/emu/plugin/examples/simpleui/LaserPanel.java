package de.embl.rieslab.emu.plugin.examples.simpleui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import de.embl.rieslab.emu.plugin.examples.components.TogglePower;
import de.embl.rieslab.emu.ui.ConfigurablePanel;
import de.embl.rieslab.emu.ui.swinglisteners.SwingUIListeners;
import de.embl.rieslab.emu.ui.uiparameters.BoolUIParameter;
import de.embl.rieslab.emu.ui.uiparameters.ColorUIParameter;
import de.embl.rieslab.emu.ui.uiparameters.StringUIParameter;
import de.embl.rieslab.emu.ui.uiproperties.RescaledUIProperty;
import de.embl.rieslab.emu.ui.uiproperties.TwoStateUIProperty;
import de.embl.rieslab.emu.utils.EmuUtils;
import de.embl.rieslab.emu.utils.exceptions.IncorrectUIParameterTypeException;
import de.embl.rieslab.emu.utils.exceptions.IncorrectUIPropertyTypeException;
import de.embl.rieslab.emu.utils.exceptions.UnknownUIParameterException;
import de.embl.rieslab.emu.utils.exceptions.UnknownUIPropertyException;

public class LaserPanel extends ConfigurablePanel {
	private static final long serialVersionUID = 1L;
	
	private JLabel label;
	private JSlider slider;
	private JToggleButton powerToggleButton;
	
	//////// Properties
	public final String LASER_PERCENTAGE = "power percentage";
	public final String LASER_OPERATION = "on/off";

	//////// Parameters
	public final String PARAM_TITLE = "Name";
	public final String PARAM_COLOR = "Color";	
	public final String PARAM_USEONOFF = "Enable power button";	

	public LaserPanel(String title) {
		super(title);

		initComponents();
	}
	
	// This function was generated by the Eclipse WindowBuilder.
	private void initComponents() {
		setBorder(new TitledBorder(null, "Laser", TitledBorder.LEFT, TitledBorder.TOP, null, null));
	
		// except this bit: make the font bold in the titledborder title
		((TitledBorder) this.getBorder()).setTitleFont(((TitledBorder) this.getBorder()).getTitleFont().deriveFont(Font.BOLD, 12));

		setLayout(null);
		
		label = new JLabel("70%");
		label.setBounds(10, 24, 100, 14);
		label.setFont(new Font("Tahoma", Font.BOLD, 12));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		add(label);
		
		slider = new JSlider();
		slider.setBounds(10, 49, 100, 172);
		slider.setMajorTickSpacing(20);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setOrientation(SwingConstants.VERTICAL);
		add(slider);
		
		powerToggleButton = new TogglePower();
		powerToggleButton.setBounds(10, 231, 100, 33);
		powerToggleButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		add(powerToggleButton);
	}

	private String getUIPropertyLabel(String property) {
		return getPanelLabel() + " " + property;
	}
	
	@Override
	protected void initializeProperties() {
		/* 
		 * In this method, we need to declare the UIProperties and 
		 * add them to the ConfigurableFrame using the method
		 * ConfigurableFrame.addUIProperty(UIProperty).
		 */
	
		String text1 = "Property changing the percentage of the laser. Set the slope to max/100 and the offset to 0, where max is the maximum value of the property, for instance if "
				+ "the property is 0-200 mW, then the slope is 2. If the device property is already a percentage, then leave the slope and offset as they are.";
		String text2 = "Property turning the laser on and off.";

		/* 
		 * A RescaledUIProperty allows having a power percentage even if the device property 
		 * (in Micro-Manager) is not a percentage (for instance "laser power (mW)").
		 */
		addUIProperty(new RescaledUIProperty(this, getUIPropertyLabel(LASER_PERCENTAGE), text1));
		
		/*
		 * A TwoStateUIProperty is appropriate for a on/off property as it takes only two states.
		 */
		addUIProperty(new TwoStateUIProperty(this, getUIPropertyLabel(LASER_OPERATION), text2));
	}

	@Override
	protected void initializeInternalProperties() {
		/* 
		 * In this method, we can declare the InternalProperties
		 * and add them to the ConfigurableFrame using the method
		 * ConfigurableFrame.addInternalProperty(InternalProperty).
		 */
		
		// In this example, we have none.
	}

	@Override
	protected void initializeParameters() {
		/* 
		 * We retrieve the panel label (defined in the SimpleUIFrame) to set the default
		 * of the StringUIParameter corresponding to the title parameter.
		 */
		addUIParameter(new StringUIParameter(this, PARAM_TITLE, "Panel title.",getPanelLabel()));
		
		// We declare a ColorUIParameter for the title color (with default being black)
		addUIParameter(new ColorUIParameter(this, PARAM_COLOR, "Panel title color.",Color.black));
		
		// We declare a BoolUIParameter to enable/disable the on/off button
		addUIParameter(new BoolUIParameter(this, PARAM_USEONOFF, "Enable power (on/off) button.",true));
	}

	@Override
	protected void addComponentListeners() {
		/*
		 * In this method we can add Swing actionListeners to the 
		 * JComponents or call the methods from SwingUIListeners.
		 */

		// The JSlider will update the UIProperty (percentage) and the JLabel (with a "%" suffix)
		SwingUIListeners.addActionListenerOnIntegerValue(this, getUIPropertyLabel(LASER_PERCENTAGE), slider, label, "", "%");

		/*
		 *  The JToggleButton will set the TwoStateUIProperty (on/off) to its on / off state when 
		 *  it is selected / unselected respectively.
		 */
		try {
			SwingUIListeners.addActionListenerToTwoState(this, getUIPropertyLabel(LASER_OPERATION), powerToggleButton);
		} catch (IncorrectUIPropertyTypeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void internalpropertyhasChanged(String propertyName) {
		/*
		 * This method is called when an InternalProperty has changed.
		 * Here we can modify the UI to reflect this change.
		 */
		
		// Here, we do not have to do anything.
	}

	@Override
	protected void propertyhasChanged(String propertyName, String newvalue) {
		/*
		 * This method is called when an UIProperty has changed.
		 * Here we can modify the UI to reflect this change.
		 */
		
		if(getUIPropertyLabel(LASER_PERCENTAGE).equals(propertyName)) { // if the change concerns the laser percentage
			// Let's test if the value is a number
			if(EmuUtils.isNumeric(newvalue)) {
				// JSlider accept only an integer, in case it is a double, we round it up
				int val = (int) Double.parseDouble(newvalue);
				
				// We make sure it is a value between 0 and 100
				if (val >= 0 && val <= 100) {
					// sets the value of the JSLider
					slider.setValue(val);
					
					// change the text of the JLabel to reflect the change
					label.setText(String.valueOf(val) + "%");
				}
			}
		} else if(getUIPropertyLabel(LASER_OPERATION).equals(propertyName)) { // if the change pertains to the laser on/off
			// the try/catch clause is necessary in case we call an unknown UIProperty
			try {
				// Gets the value of the TwoStateUIProperty's ON value.
				String onValue = ((TwoStateUIProperty) getUIProperty(getUIPropertyLabel(LASER_OPERATION))).getOnStateValue();
				
				// Selects the JToggleButton if the new value is the TwoStateUIProperty's ON value,
				// unselects it otherwise.
				powerToggleButton.setSelected(newvalue.equals(onValue));
			} catch (UnknownUIPropertyException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void parameterhasChanged(String parameterName) {
		/*
		 * This method is called when a UIProperty has changed.
		 * Here we can modify the UI to reflect this change. It
		 * is only called when the plugin is loaded or the configuration
		 * changed.
		 */
		
		if(PARAM_TITLE.equals(parameterName)){
			try {
				// retrieves the title as a String
				String title = getStringUIParameterValue(PARAM_TITLE);	
				
				// gets the TitledBorder and change its title, then updates the panel
				TitledBorder border = (TitledBorder) this.getBorder();
				border.setTitle(title);
				this.repaint();
				
			} catch (UnknownUIParameterException e) {
				e.printStackTrace();
			}
		} else if(PARAM_COLOR.equals(parameterName)){
			try {
				// retrieves the color at a Color type
				Color color = getColorUIParameterValue(PARAM_COLOR);
				
				// gets the TitledBorder and change its title color, then updates the panel
				TitledBorder border = (TitledBorder) this.getBorder();
				border.setTitleColor(color);
				this.repaint();
			} catch (IncorrectUIParameterTypeException | UnknownUIParameterException e) {
				e.printStackTrace();
			}
		} if(PARAM_USEONOFF.equals(parameterName)){
			try {
				// retrieves the value of the boolean parameter
				boolean enable = getBoolUIParameterValue(PARAM_USEONOFF);
				
				// enable/disable the power button
				powerToggleButton.setEnabled(enable);
			} catch (IncorrectUIParameterTypeException | UnknownUIParameterException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void shutDown() {
		/*
		 * This method is called upon closing the plugin and can be 
		 * used to reset some properties or stop running threads.
		 */
		
		// Here do nothing.
	}

	@Override
	public String getDescription() {
		/*
		 * Here, we return the description of the ConfigurablePanel,
		 * this description is used to help the user understand how the 
		 * panel works.
		 */
		return "Laser panels control each a single laser and allow for rapid on/off and power percentage changes. "
				+ "If the laser does not have a power percentage property, the UI property parameters SLOPE and OFFSET can be used "
				+ "to rescale the power property to a percentage. The name and color of the laser can also be modified using the "
				+ "corresponding parameters.";
	}
}