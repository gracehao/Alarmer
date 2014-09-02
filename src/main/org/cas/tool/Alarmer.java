package org.cas.tool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.sun.xml.internal.ws.util.StringUtils;

public class Alarmer extends JFrame implements ComponentListener, WindowListener, ActionListener, WindowStateListener{
	public static float SAMPLE_RATE = 8000f;
	
	private static final long serialVersionUID = 1L;
	private static int GAP = 20;
	private static int BUTTON_WIDTH = 200;
	private static int BUTTON_HEIGHT = 50;
	private static int LENGTH_LIMIT = Integer.MAX_VALUE/10;

	final String TL1 = "seconds";
	final String TL2 = "minutes";
	final String TL3 = "hours";
	final String TL4 = "days";
	
	final String ST1 = "gui jiao";
	final String ST2 = "zhu jiao";
	final String ST3 = "gou jiao";
	final String ST4 = "mao jiao";
	final String ST5 = "ren jiao";
	
	JLabel helpText2;
	JLabel lbTargetStr;
	JTextField waitTime;
	JComboBox<String> timeUnit;
	JCheckBox cbxPlaySound;
	JCheckBox cbxRunApp;	
	JLabel lbJobToDo;
	JLabel lbSoundType;
	JLabel lbRepeatTime;
	JComboBox<String> soundType;
	JTextField repeatTime;
	JTextArea appToRun;
	JButton btnBrowse;

	JProgressBar pbProgress;
	JButton btnStart;
	JPanel tContainer;

	String helpText;
	File[] files;
	static boolean isInWaiting;
	static int curP;
	ArrayList<Integer> positionAry = new ArrayList<Integer>();

	//main--------------------------------------------
	public static void main(String[] args) {
		Alarmer s = new Alarmer();
		s.setBounds(300, 200, 500, 400);
		s.setVisible(true);
	}
	
	public Alarmer(){		
		//declare--------------------------------
		helpText2 = new JLabel();
		lbTargetStr = new JLabel();
		waitTime = new JTextField();
		timeUnit = new JComboBox<>();
		cbxPlaySound = new JCheckBox();
		soundType = new JComboBox<>();
		cbxRunApp = new JCheckBox();
		lbJobToDo = new JLabel();
		lbRepeatTime = new JLabel();
		lbSoundType = new JLabel();
		repeatTime = new JTextField();
		appToRun = new JTextArea();
		btnBrowse = new JButton();
		pbProgress = new JProgressBar();
		btnStart = new JButton();
		tContainer = new JPanel();
		
		//properties-------------------------------
		setTitle("ALARMER[0.1] --info@ShareTheGoodOnes.com");
		appToRun.setOpaque(false);
		helpText2.setText("Please select the time to wait, the thing to do, then click Start!");
		lbTargetStr.setText("Time to wait:");
		waitTime.setText("60");
		timeUnit.addItem(TL1);
		timeUnit.addItem(TL2);
		timeUnit.addItem(TL3);
		timeUnit.addItem(TL4);
		timeUnit.setSelectedItem(TL2);
		cbxPlaySound.setText("Play sound");
		cbxPlaySound.setSelected(true);
		lbSoundType.setText("Sound Type");
		soundType.addItem(ST1);
		soundType.addItem(ST2);
		soundType.addItem(ST3);
		soundType.addItem(ST4);
		soundType.addItem(ST5);
		repeatTime.setText("10");
		
		cbxRunApp.setText("Run application");
		cbxRunApp.setSelected(true);
		btnBrowse.setText("Select apps to run...");
		lbJobToDo.setText("Job to do:--------------------------------------------------------------------------------");
		lbRepeatTime.setText("Repeat Times");
		pbProgress.setStringPainted(true);
		btnStart.setText("Start!");
		
		//construct-------------------------------
		tContainer.add(appToRun);
		tContainer.add(btnBrowse);
		tContainer.add(lbTargetStr);
		tContainer.add(helpText2);
		tContainer.add(waitTime);
		tContainer.add(timeUnit);
		tContainer.add(cbxPlaySound);
		tContainer.add(cbxRunApp);
		tContainer.add(lbJobToDo);
		tContainer.add(lbSoundType);
		tContainer.add(soundType);
		tContainer.add(lbRepeatTime);
		tContainer.add(repeatTime);
		tContainer.add(pbProgress);
		tContainer.add(btnStart);
		getContentPane().add(tContainer);
		
		//layout---------------------------------
		tContainer.setLayout(null);		
		relayout();
		
		//listener-------------------------------
		cbxPlaySound.addActionListener(this);
		cbxRunApp.addActionListener(this);
		btnBrowse.addActionListener(this);
		btnStart.addActionListener(this);
		addWindowListener(this);
		addWindowStateListener(this);
		addComponentListener(this);
	}
	
	private void relayout(){
		helpText2.setBounds(GAP, GAP, helpText2.getPreferredSize().width, helpText2.getPreferredSize().height);
		
		lbTargetStr.setBounds(helpText2.getX(), helpText2.getY() + helpText2.getHeight() + GAP, lbTargetStr.getPreferredSize().width, lbTargetStr.getPreferredSize().height);
		timeUnit.setBounds(appToRun.getWidth() - timeUnit.getPreferredSize().width + GAP, lbTargetStr.getY() - 4, 
				timeUnit.getPreferredSize().width, timeUnit.getPreferredSize().height);
		waitTime.setBounds(appToRun.getX() + lbTargetStr.getWidth() + GAP, lbTargetStr.getY(), 
				timeUnit.getX() - lbTargetStr.getX() - lbTargetStr.getWidth() - GAP*2, waitTime.getPreferredSize().height);
		
		lbJobToDo.setBounds(lbTargetStr.getX(), lbTargetStr.getY() + lbTargetStr.getHeight() + GAP, lbJobToDo.getPreferredSize().width, lbJobToDo.getPreferredSize().height);
		
		cbxPlaySound.setBounds(lbJobToDo.getX(), lbJobToDo.getY() + lbJobToDo.getHeight() + GAP, cbxPlaySound.getPreferredSize().width, cbxPlaySound.getPreferredSize().height);
		lbSoundType.setBounds(lbJobToDo.getX() + cbxPlaySound.getWidth() + GAP, cbxPlaySound.getY() + 4, lbSoundType.getPreferredSize().width, lbSoundType.getPreferredSize().height);
		soundType.setBounds(lbSoundType.getX() + lbSoundType.getWidth(), lbSoundType.getY() - 4, soundType.getPreferredSize().width, soundType.getPreferredSize().height);
		lbRepeatTime.setBounds(soundType.getX() + soundType.getWidth() + GAP, cbxPlaySound.getY() + 4, lbRepeatTime.getPreferredSize().width, lbRepeatTime.getPreferredSize().height);
		repeatTime.setBounds(lbRepeatTime.getX() + lbRepeatTime.getWidth(), lbRepeatTime.getY(), 50, repeatTime.getPreferredSize().height);
		
		cbxRunApp.setBounds(cbxPlaySound.getX(), cbxPlaySound.getY() + cbxPlaySound.getHeight() + GAP, cbxRunApp.getPreferredSize().width, cbxRunApp.getPreferredSize().height);
		btnBrowse.setBounds(cbxRunApp.getX() + cbxRunApp.getWidth() + GAP, cbxRunApp.getY(), btnBrowse.getPreferredSize().width, btnBrowse.getPreferredSize().height);
		appToRun.setBounds(cbxRunApp.getX(), cbxRunApp.getY() + cbxRunApp.getHeight() + GAP, this.getWidth() - (int)(GAP*2.5), appToRun.getPreferredSize().height);
		

		pbProgress.setBounds(lbJobToDo.getX(), appToRun.getY() + appToRun.getHeight() + GAP, appToRun.getWidth(), pbProgress.getPreferredSize().height);
		btnStart.setBounds(pbProgress.getX() + (appToRun.getWidth() - BUTTON_WIDTH)/2, pbProgress.getY() + pbProgress.getHeight() + GAP, BUTTON_WIDTH, BUTTON_HEIGHT);
	}
	
	public static void tone(int hz, int msecs) throws LineUnavailableException {
		tone(hz, msecs, 1.0);
	}

	public static void tone(int hz, int msecs, double vol) throws LineUnavailableException {
		byte[] buf = new byte[1];
		AudioFormat af = new AudioFormat(SAMPLE_RATE, // sampleRate
				8, // sampleSizeInBits
				1, // channels
				true, // signed
				false); // bigEndian
		SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
		sdl.open(af);
		sdl.start();
		for (int i = 0; i < msecs * 8; i++) {
			double angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
			buf[0] = (byte) (Math.sin(angle) * 127.0 * vol);
			sdl.write(buf, 0, 1);
		}
		sdl.drain();
		sdl.stop();
		sdl.close();
	}

	private void chooseFile(){
		JFileChooser tFileChooser = new JFileChooser();
		tFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		tFileChooser.setMultiSelectionEnabled(true);		
		int rVal = tFileChooser.showOpenDialog(this);
	    if(rVal == JFileChooser.APPROVE_OPTION) {
	    	files = tFileChooser.getSelectedFiles();
	    	StringBuilder tSB = new StringBuilder("WILL EXECUTE FOLLOWING APPS WHEN TIME UP:\n");
	    	for(int i = 0; i < files.length; i++){
	    		tSB.append("       ");
	    		tSB.append(files[i].getAbsolutePath());
	    		tSB.append("\n");
	    	}
	    	helpText = tSB.toString();
			appToRun.setText(helpText);//helpText can be changed
	    	setVisible(true);
	    }else{
	    	System.exit(0);
	    }
	}
	
	private void enableRelevantFields(boolean enabled){
		waitTime.setEnabled(enabled);
		timeUnit.setEnabled(enabled);
	}
	private int getRate(String timeUnit){
		switch(timeUnit){
		case TL1:
			return 1;
		case TL2:
			return 60;
		case TL3:
			return 3600;
		case TL4:
			return 86400;
		}
		return 1;
	}
	private boolean isValidNumber(String s){
		double d;
		try{
			d = Double.parseDouble(s);
			return d > 0;
		}catch(Exception nan){
			return false;
		}
	}
	//event interface implementation------------------
	//windows
	public void windowOpened(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}
	public void windowClosed(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}

    public void windowStateChanged(WindowEvent e){
    	if(e.getNewState() == 6 || e.getNewState() == 0){
    		relayout();
    	}
    }
	//component
	public void componentResized(ComponentEvent e){
		relayout();
	}   
	public void componentMoved(ComponentEvent e){}
	public void componentShown(ComponentEvent e){}
	public void componentHidden(ComponentEvent e){}
	
	//action
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnBrowse){
			setVisible(false);
			chooseFile();
		}else if(e.getSource() == cbxPlaySound){
			lbSoundType.setEnabled(cbxPlaySound.isSelected());
			soundType.setEnabled(cbxPlaySound.isSelected());
			lbRepeatTime.setEnabled(cbxPlaySound.isSelected());
			repeatTime.setEnabled(cbxPlaySound.isSelected());
		}else if(e.getSource() == cbxRunApp){
			btnBrowse.setEnabled(cbxRunApp.isSelected());
			appToRun.setEnabled(cbxRunApp.isSelected());
		}else if(e.getSource() == btnStart){
			if(isInWaiting){
				isInWaiting = false;
			}else{
				String tTgarGetStr = waitTime.getText();
				if(tTgarGetStr == null || tTgarGetStr.length() == 0){
					JOptionPane.showMessageDialog(this, "You havent's input the time value to wait!");
				}else if(!isValidNumber(tTgarGetStr)){
					JOptionPane.showMessageDialog(this, "Please input a positive number in the time field!");
				}else if(!isValidNumber(repeatTime.getText())){
					JOptionPane.showMessageDialog(this, "Please input a positive number in the times field!");
				}else{
					//Execute in a new thread.
					final int times = (int)Double.parseDouble(repeatTime.getText());
					final int value = (int)Double.parseDouble(tTgarGetStr) * getRate(timeUnit.getSelectedItem().toString());
					
					
					pbProgress.setMaximum(value);
					
					Thread a = new Thread(new Runnable() {
						public void run() {
							//start to search!
							isInWaiting = true;
							//disable all input fields
							enableRelevantFields(false);
							btnStart.setText("Cancel!");

							for(int i = value; i > -1 ; i--){
								if(!isInWaiting){
									break;
								}else{
									pbProgress.setValue(i);
									pbProgress.setString(String.valueOf(i));
									try{
										Thread.sleep(1000);
									}catch(Exception e){}
								}
							}
							if(isInWaiting){
								//do the job.
								try{
									switch(soundType.getSelectedItem().toString()){
									case ST1:
										for(int i = 0; i < times; i++){
											Alarmer.tone(1000, 100);
											Thread.sleep(1000);
										}
										break;
									case ST2:
										for(int i = 0; i < times; i++){
											Alarmer.tone(100, 1000);
											Thread.sleep(1000);
										}
										break;
									case ST3:
										for(int i = 0; i < times; i++){
											Alarmer.tone(5000, 100);
											Thread.sleep(1000);
										}
										break;
									case ST4:
										for(int i = 0; i < times; i++){
											Alarmer.tone(400, 500);
											Thread.sleep(1000);
										}
										break;
									case ST5:
										for(int i = 0; i < times; i++){
											Alarmer.tone(400, 500, 0.2);
											Thread.sleep(1000);
										}
										break;
									}
									
								}catch(Exception e){}
							}
							//reset......
							isInWaiting = false;
							enableRelevantFields(true);
							btnStart.setText("Start!");
						}
					});
					a.start();
				}
			}
		}
	}
}