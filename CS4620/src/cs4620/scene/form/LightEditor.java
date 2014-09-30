package cs4620.scene.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.NumberFormatter;

import cs4620.common.Scene;
import cs4620.common.SceneLight;


/*
 * Light editor is a JPanel that supports editing of parameters for lights, which includes the
 * object's transformation matrix and color
 */
public class LightEditor extends ObjectEditor implements ValueUpdatable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6996814537432621062L;

	SceneLight myLight;
	
	LightEditPanel myLightEditPanel;
	
	//This will be a boxlayout of JPanels
	//The first panel supports matrix editing
	//The second panel supports mesh/material string editing
	public LightEditor(Scene s, SceneLight lightIn) {
		super(s, lightIn);
		myLight = lightIn;
		myLightEditPanel = new LightEditPanel();
		add(myLightEditPanel);
//		add(new TransformationMatrixEditPanel(myLight));
	}
	
	//Light editing panel
	class LightEditPanel extends JPanel implements ValueUpdatable {
		/**
		 * UID
		 */
		private static final long serialVersionUID = -534967362076424185L;

		JFormattedTextField[] lightColorFields = new JFormattedTextField[4];

		public LightEditPanel() {
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			JLabel panelLabel = new JLabel("Light Editing");
			panelLabel.setAlignmentX(CENTER_ALIGNMENT);
			this.add(panelLabel);
			
			//LIGHT UPDATE PANEL
			JPanel lightUpdatePanel = new JPanel();
			lightUpdatePanel.setLayout(new BoxLayout(lightUpdatePanel, BoxLayout.X_AXIS));
			
			
			NumberFormat format = NumberFormat.getInstance();
		    NumberFormatter formatter = new NumberFormatter(format);
		    formatter.setValueClass(Double.class);
		    formatter.setMinimum(0);
		    formatter.setMaximum(255);
			
			for(int colorIndex = 0; colorIndex < 3; ++colorIndex) {
				JFormattedTextField curField = new JFormattedTextField(formatter);
				curField.setColumns(6);
				curField.setValue(myLight.intensity.get(colorIndex));
				lightColorFields[colorIndex] = curField;
				lightUpdatePanel.add(curField);
			}
			
			//BUTTON PANEL
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
			JButton transformButton = new JButton("Update Light");
			JButton revertButton = new JButton("Revert Changes");
			buttonPanel.add(transformButton);
			buttonPanel.add(revertButton);
			
			//update action listener
			ActionListener update = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for(int colorIndex = 0; colorIndex < 3; ++colorIndex) {
						myLight.intensity.set(colorIndex, ((Number)lightColorFields[colorIndex].getValue()).doubleValue());
					}
				}
			};
			
			//revert action listener
			ActionListener revert = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for(int colorIndex = 0; colorIndex < 3; ++colorIndex) {
						lightColorFields[colorIndex].setValue(myLight.intensity.get(colorIndex));
					}
				}
			};
			transformButton.addActionListener(update);
			revertButton.addActionListener(revert);
			
			this.add(lightUpdatePanel);
			this.add(buttonPanel);
		}

		@Override
		public void updateValues() {
			for(int colorIndex = 0; colorIndex < 3; ++colorIndex) {
				JFormattedTextField curField = lightColorFields[colorIndex];
				if(!curField.isFocusOwner())
					curField.setValue(myLight.intensity.get(colorIndex));
			}
			matrixEditor.updateValues();
			
		}
	}

	@Override
	public void updateValues() {
		myLightEditPanel.updateValues();
		matrixEditor.updateValues();
	}
}
