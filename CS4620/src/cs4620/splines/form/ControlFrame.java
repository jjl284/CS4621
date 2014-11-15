package cs4620.splines.form;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.lwjgl.opengl.Display;

import cs4620.mesh.MeshData;
import cs4620.mesh.OBJParser;
import cs4620.splines.BSpline;
import cs4620.splines.SplineApp;
import egl.math.Vector2;

public class ControlFrame  extends JFrame {
	private static final long serialVersionUID = -5641957456264997948L;

	private SplineApp owner;

	private JPanel splinePanel;// one panel to rule them all

	// Tolerance sliders
	private JPanel tolerancePanel;// container for all sliders
	private ToleranceChangeListener toleranceListener;
	private ToleranceSliderPanel leftTolerance, centerTolerance, rightTolerance;

	// lwsb
	private JPanel load_savePanel;
	private JButton load, save;
	private SaveLoadListener lwsbListen;
	
	// configurations
	private JPanel configPanel;
	private OptionsListener opts;
	
	private JCheckBox displayNormals;
	public static boolean drawNormals= true;
	
	private JCheckBox displayTangents;
	public static boolean drawTangents =true;
	
	private JCheckBox closeLeft;
	public static boolean leftClosed = true;
	
	private JCheckBox closeCenter;
	public static boolean centerClosed = false;
	
	private JCheckBox enableResize;
	private boolean resizeEnabled= true;
	
	private JCheckBox realTimeRender;
	public static boolean REAL_TIME= true;

	public ControlFrame(String title, SplineApp owner) {
		super(title);
		this.owner= owner;
		init_display();
	}

	private void init_display() {
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		// setup tolerance sliders and panel
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		toleranceListener= new ToleranceChangeListener();
		leftTolerance= new ToleranceSliderPanel(-1.35f, -0.25f, toleranceListener);
		centerTolerance= new ToleranceSliderPanel(-1.35f, -0.25f, toleranceListener);
		rightTolerance= new ToleranceSliderPanel(-1.35f, -0.25f, toleranceListener);
		toleranceListener.setPanels(leftTolerance, centerTolerance, rightTolerance);
		
		leftTolerance.setMinimumSize(SplineApp.tolSlideDim);
		centerTolerance.setMinimumSize(SplineApp.tolSlideDim);
		rightTolerance.setMaximumSize(SplineApp.tolSlideDim);
		
		leftTolerance.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(new Color(150,0,150),
						                       1,
						                       true),
						                       "Left"));
		centerTolerance.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(new Color(150,0,150),
						                       1,
						                       true),
						                       "Center"));
		rightTolerance.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(new Color(150,0,150),
						                       1,
						                       true),
						                       "Scale"));
		
		tolerancePanel= new JPanel(new GridLayout(1,3));
		tolerancePanel.setPreferredSize(SplineApp.tolPanelDim);
		tolerancePanel.setMinimumSize(SplineApp.tolPanelDim);
		tolerancePanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(new Color(150,0,150),
						                       1,
						                       true),
						                       "Tolerance"));
		
		SplineScreen.tol1= leftTolerance.getTolerance();
		SplineScreen.tol2= centerTolerance.getTolerance();
		
		tolerancePanel.add(leftTolerance);
		tolerancePanel.add(centerTolerance);
		tolerancePanel.add(rightTolerance);
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		// setup lwsb panel
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		load_savePanel= new JPanel(new GridLayout(2,1));
		load_savePanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(new Color(150,0,150),
						                       1,
						                       true),
						                       "Load/Save"));
		load= new JButton("Load");
		save= new JButton("Save Sweep Mesh");
		lwsbListen= new SaveLoadListener(this);
		load.addActionListener(lwsbListen);
		save.addActionListener(lwsbListen);
		load_savePanel.add(load);
		load_savePanel.add(save);
		load_savePanel.setPreferredSize(SplineApp.lwsbDim);
		load_savePanel.setMinimumSize(SplineApp.lwsbDim);
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		// setup configurations panel
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		configPanel= new JPanel(new GridLayout(6,1));
		configPanel.setPreferredSize(SplineApp.configDim);
		configPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(new Color(150,0,150),
						                       1,
						                       true),
						                       "Configurations..."));
		
		opts= new OptionsListener(this);
		
		displayNormals= new JCheckBox();
		displayNormals.setText("Display Normals");
		displayNormals.setSelected(drawNormals);
		displayNormals.addActionListener(opts);
		
		displayTangents= new JCheckBox();
		displayTangents.setText("Display Tangents");
		displayTangents.setSelected(drawTangents);
		displayTangents.addActionListener(opts);
		
		closeLeft= new JCheckBox();
		closeLeft.setText("Close Left Spline");
		closeLeft.setSelected(leftClosed);
		closeLeft.addActionListener(opts);
		
		closeCenter= new JCheckBox();
		closeCenter.setText("Close Center Spline");
		closeCenter.setSelected(centerClosed);
		closeCenter.addActionListener(opts);
		
		enableResize= new JCheckBox();
		enableResize.setText("Enable Display Resize");
		enableResize.setSelected(resizeEnabled);
		enableResize.addActionListener(opts);
		
		realTimeRender= new JCheckBox();
		realTimeRender.setText("Render in Real Time");
		realTimeRender.setSelected(REAL_TIME);
		realTimeRender.addActionListener(opts);
		
		configPanel.add(displayNormals);
		configPanel.add(displayTangents);
		configPanel.add(closeLeft);
		configPanel.add(closeCenter);
		configPanel.add(enableResize);
		configPanel.add(realTimeRender);
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		// setup spline panel
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		splinePanel= new JPanel();
		splinePanel.setLayout(new BoxLayout(splinePanel, BoxLayout.Y_AXIS));
		splinePanel.setPreferredSize(SplineApp.optionsDim);
		splinePanel.setMinimumSize(SplineApp.optionsDim);
		
		splinePanel.add(tolerancePanel);
		splinePanel.add(load_savePanel);
		splinePanel.add(configPanel);
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		// add components to Frame and modify display
		///////////////////////////////////////////////////////////////////////////////////////////////////////
		this.setPreferredSize(SplineApp.optionsDim);
		this.setSize(SplineApp.optionsDim);
		this.setMinimumSize(SplineApp.optionsDim);
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		this.add(splinePanel);
		
		Display.setLocation(SplineApp.optionsDim.width, 0);
		Display.setResizable(true);
		
		this.pack();
		this.setVisible(true);
	}
	
	
	public float getScale() {
		return rightTolerance.getTolerance();
	}

	private class ToleranceChangeListener implements ChangeListener {
		private ToleranceSliderPanel left, center, right;
		private boolean initialized= false;

		private void setPanels(ToleranceSliderPanel left,
				ToleranceSliderPanel center,
				ToleranceSliderPanel right) {
			this.left= left;
			this.center= center;
			this.right= right;
			// if any of them are null, none will update.  use this class only as specified.
			initialized= left != null && center != null && right != null;
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			if(!initialized) return;
			JSlider src= (JSlider)e.getSource();
			if (!src.getValueIsAdjusting()) {
				if(src.getParent() == left) {
					float newVal = left.getTolerance();
					SplineScreen.tol1 = newVal;
					((TwoDimSplinePanel) SplineScreen.panels[0]).spline.modifyEpsilon(newVal);
					if(REAL_TIME)
						owner.scrView.newSweep();
				}
				else if(src.getParent() == center) {
					float newVal = center.getTolerance();
					SplineScreen.tol2 = newVal;
					((TwoDimSplinePanel) SplineScreen.panels[1]).spline.modifyEpsilon(newVal);
					if(REAL_TIME)
						owner.scrView.newSweep();
				}
				else if(src.getParent() == right){
					owner.scrView.generator.setScale(right.getTolerance());
					if(REAL_TIME)
						owner.scrView.newSweep();
				}
			}
		}

	}
	
	private class SaveLoadListener implements ActionListener {
		private ControlFrame owner;
		
		public SaveLoadListener(ControlFrame owner) {
			this.owner= owner;
		}
		
		@SuppressWarnings("resource")
		@Override
		public void actionPerformed(ActionEvent e) {
			if(owner == null) return;
			Object src= e.getSource();
			if(!(src instanceof JButton)) return;
			if(src == load) {
				try {
					FileDialog fd = new FileDialog(owner);
					fd.setVisible(true);
					for(File f : fd.getFiles()) {
						String file = f.getAbsolutePath();
						BufferedReader br = new BufferedReader(new FileReader(file));
						String line = null;
						
						ArrayList<Vector2> firstSplineCP = new ArrayList<Vector2>();
						boolean firstSplineClosed= true;
						float firstEpsilon= 0.0f;
						ArrayList<Vector2> secondSplineCP = new ArrayList<Vector2>();
						boolean secondSplineClosed= false;
						float secondEpsilon= 0.0f;
						
						float scale= 0.0f;
						
						boolean next_line_configs= false;
						boolean configs_done= false;
						boolean next_lines_cross_section= false;
						boolean next_lines_swept_along= false;
						/* finished is  fail-safe:
						 *   - 0 for none
						 *   - 1 for configs parsed
						 *   - 2 for configs and cross-section
						 *   - 3 for configs, cross-section, and swept-along
						 */
						int finished= 0;// fail-safe: 0 for none, 1 for configs, 
						while ((line = br.readLine()) != null) {
							if(line.charAt(0) != '#') {
								finished++;
								break;// done parsing all comments
							}
							if(next_line_configs) {
								String[] configs= line.split(" ");
								// cross-section
								if(configs[1].equals("true,"))
									firstSplineClosed= true;
								else
									firstSplineClosed= false;
								try {
									firstEpsilon= Float.parseFloat(configs[2]);
								} catch(Exception nf) {
									System.err.println("Could not parse cross-section epsilon value.");
									return;
								}
								// sweep-along
								if(configs[4].equals("true,"))
									secondSplineClosed= true;
								else
									secondSplineClosed= false;
								try {
									secondEpsilon= Float.parseFloat(configs[5]);
								} catch(Exception nf) {
									System.err.println("Could not parse swept-along epsilon value.");
									return;
								}
								// scale
								try {
									scale= Float.parseFloat(configs[7]);
								} catch(Exception nf) {
									System.err.println("Could not parse the scale of the cross-section.");
									return;
								}
								next_line_configs= false;
								configs_done= true;
								finished++;
								continue;
							}

							if(line.contains(">>+<<") && !next_line_configs) {
								next_line_configs= true;
								continue;
							}
							
							if(line.contains("Cross-section") && configs_done) {
								next_lines_cross_section= true;
								continue;
							}
							if(line.contains("Swept-along") && configs_done) {
								next_lines_swept_along= true;
								continue;
							}
						   if(line.length() < 3 && next_lines_cross_section) {
							   next_lines_cross_section= false;
							   finished++;
							   continue;
						   }
						   
						   // control points!
						   if(next_lines_cross_section || next_lines_swept_along) {
							   Vector2 curPoint = new Vector2();
							   String[] tokens = line.substring(2).split(" ");
							   try {
								   curPoint.x = Float.parseFloat(tokens[0]);
								   curPoint.y = Float.parseFloat(tokens[1]);
							   } catch(Exception nf) {
								   System.err.println("Could not parse a control point; line that was a problem: "+line);
								   return;
							   }
							   if(next_lines_cross_section)
								   firstSplineCP.add(curPoint);
							   if(next_lines_swept_along)
								   secondSplineCP.add(curPoint);
						   }
						}
						
						if(finished == 3) {
							owner.leftTolerance.setTolerance(firstEpsilon);
							SplineScreen.tol1= firstEpsilon;
							
							owner.centerTolerance.setTolerance(secondEpsilon);
							SplineScreen.tol2= secondEpsilon;
							
							owner.rightTolerance.setTolerance(scale);
							owner.owner.scrView.generator.setScale(scale);
							
							leftClosed= firstSplineClosed;
							owner.closeLeft.setSelected(firstSplineClosed);
							
							centerClosed= secondSplineClosed;
							owner.closeCenter.setSelected(secondSplineClosed);
							
							owner.owner.leftPoints = firstSplineCP;
							owner.owner.centerPoints = secondSplineCP;
							((TwoDimSplinePanel)SplineScreen.panels[0]).spline= new BSpline(firstSplineCP, firstSplineClosed, firstEpsilon);
							((TwoDimSplinePanel)SplineScreen.panels[1]).spline= new BSpline(secondSplineCP, secondSplineClosed, secondEpsilon);
							
							owner.owner.scrView.generator.setSplineToSweep(((TwoDimSplinePanel)SplineScreen.panels[0]).spline);
							owner.owner.scrView.generator.setSplineToSweepAlong(((TwoDimSplinePanel)SplineScreen.panels[1]).spline);
							owner.owner.scrView.newSweep();
						} else {
							System.err.println("Could not load all data, please make sure you are loading a mesh generated with this app and try again.");
						}
						
					}
				}
				catch (Exception e1) {
					e1.printStackTrace();
					System.err.println("Could not load all data, please make sure you are loading a mesh generated with this app and try again.");
				}
			}
			else if(src == save) {
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showSaveDialog(owner) == JFileChooser.APPROVE_OPTION) {
				  File file = fileChooser.getSelectedFile();
				  if (!file.exists()) {
						try {
							file.createNewFile();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
				  }
				  FileWriter fw = null;
				try {
					fw = new FileWriter(file.getAbsoluteFile());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				  PrintWriter pw = new PrintWriter(fw);
				  try {
					  MeshData myData = new MeshData();
					  owner.owner.scrView.generator.generate(myData, null);
					  // preliminary information
					  pw.write("# This mesh is a \"Sweep Spline\": the comments below describe a Spline (cross-section) swept along\n");
					  pw.write("# another spline (swept-along) that generated this mesh.\n");
					  pw.write("#\n");
					  pw.write("# Cross-section: closed, epsilon >>+<< Swept-along: closed, epsilon >>+<< Scale of Cross-section\n");
					  pw.write("# "+((TwoDimSplinePanel) SplineScreen.panels[0]).spline.isClosed() + ", " + 
							  		((TwoDimSplinePanel) SplineScreen.panels[0]).spline.getEpsilon() + " >>+<< " + 
							  		((TwoDimSplinePanel) SplineScreen.panels[1]).spline.isClosed() + ", " + 
							  		((TwoDimSplinePanel) SplineScreen.panels[1]).spline.getEpsilon() + " >>+<< " +
							  		owner.rightTolerance.getTolerance() + "\n");
					  
					  // cross-section
					  pw.write("#\n");
					  pw.write("# Cross-section Spline control points: \n");
					  for(Vector2 v : ((TwoDimSplinePanel) SplineScreen.panels[0]).spline.getControlPoints())
					  {
						  pw.write("# " + v.x + " " + v.y + "\n");
					  }
					  
					  // spline swept along
					  pw.write("# " + "\n");
					  pw.write("# Swept-along Spline control points:\n");
					  for(Vector2 v : ((TwoDimSplinePanel) SplineScreen.panels[1]).spline.getControlPoints())
					  {
						  pw.write("# " + v.x + " " + v.y + "\n");
					  }
					  
					  OBJParser.write(pw, OBJParser.convert(myData));
					  
					  pw.close();
					  
					  
				  }
				  catch(Exception e1) {
					  e1.printStackTrace();
				  }
				}
			}
		}
		
	}


	private class OptionsListener implements ActionListener {
		private ControlFrame owner;
		
		public OptionsListener(ControlFrame owner) {
			this.owner= owner;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			Object src= e.getSource();
			if(!(src instanceof JCheckBox)) return;
			
			JCheckBox jb= (JCheckBox) src;
			
			if(jb == displayNormals) {
				drawNormals= jb.isSelected();
			} else if(jb == displayTangents) {
				drawTangents= jb.isSelected();
			} else if(jb == closeLeft) {
				leftClosed= jb.isSelected();
				boolean success= ((TwoDimSplinePanel) SplineScreen.panels[0]).spline.setClosed(leftClosed);
				if(!success) {
					jb.setSelected(!leftClosed);
					leftClosed= !leftClosed;
				} else {
					owner.owner.scrView.newSweep();
				}
			} else if(jb == closeCenter) {
				centerClosed= jb.isSelected();
				boolean success= ((TwoDimSplinePanel) SplineScreen.panels[1]).spline.setClosed(centerClosed);
				if(!success) {
					jb.setSelected(!centerClosed);
					leftClosed= !centerClosed;
				} else {
					owner.owner.scrView.newSweep();
				}
			} else if(jb == enableResize) {
				resizeEnabled= jb.isSelected();
				Display.setResizable(resizeEnabled);
			} else if(jb == realTimeRender) {
				REAL_TIME= realTimeRender.isSelected();
				if(!REAL_TIME)
					JOptionPane.showMessageDialog(owner, "Hit the P key to render the sweep mesh.");
			}
		}
	}
}