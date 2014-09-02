package org.cas.tool;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

//if multiple string all existing, then print file full name.
public class StringsFinder extends JFrame implements ComponentListener, WindowListener, ActionListener, WindowStateListener{

	private static final long serialVersionUID = 1L;
	private static int GAP = 20;
	private static int BUTTON_WIDTH = 200;
	private static int BUTTON_HEIGHT = 50;
	private static int LENGTH_LIMIT = Integer.MAX_VALUE/10;
	
	JTextArea helpText1;
	JButton btnBrowse;

	JLabel helpText2;
	JLabel lbTargetStr;
	JTextField targetStrFld;
	JCheckBox cbxAllExist;
	JCheckBox cbxIgnoreCase;
	JCheckBox cbxWholeWord;
	JLabel lbSuffix;
	JCheckBox isNotSuffix;
	JTextField suffixField;
	JProgressBar pbProgress;
	JButton btnSearch;
	JCheckBox showFolderPath;
	JTextArea resultArea;
	JScrollPane scrollPane;
	JPanel tContainer;

	String helpText;
	File[] files;
	static boolean isInSearching;
	static int curP;
	String[] targetStrs = null;
	String[] suffixes = null;
	ArrayList<Integer> positionAry = new ArrayList<Integer>();
	
	int folderDepth = 0;
	//main--------------------------------------------
	public static void main(String[] args) {
		StringsFinder s = new StringsFinder();
		s.setSize(500, 728);
		s.chooseFile();
	}
	
	public StringsFinder(){		
		//declare--------------------------------
		helpText1 = new JTextArea();
		btnBrowse = new JButton();
		helpText2 = new JLabel();
		lbTargetStr = new JLabel();
		targetStrFld = new JTextField();
		cbxAllExist = new JCheckBox();
		cbxIgnoreCase = new JCheckBox();
		cbxWholeWord = new JCheckBox();
		lbSuffix = new JLabel();
		isNotSuffix = new JCheckBox();
		suffixField = new JTextField();
		pbProgress = new JProgressBar();
		btnSearch = new JButton();
		showFolderPath = new JCheckBox();
		resultArea = new JTextArea();
		scrollPane = new JScrollPane(resultArea);
		tContainer = new JPanel();
		
		//properties-------------------------------
		setTitle("STRING FINDER1.0----info@ShareTheGoodOnes.com");
		helpText1.setOpaque(false);
		btnBrowse.setText("Set the Files and Folders");
		helpText2.setText("--------[ Please use '//' as '/', becuase single '/' is considered as seperator.]--------");
		lbTargetStr.setText("Strings to Search:");
		cbxAllExist.setText("All Exist");//String listed above must be ALL EXSIST
		cbxIgnoreCase.setText("Insensitive");
		cbxWholeWord.setText("Whole Word");
		lbSuffix.setText("Only when suffix is: (");
		isNotSuffix.setText("NOT)");
		btnSearch.setText("Start To Search");
		showFolderPath.setText("display folder paths");
		resultArea.setAutoscrolls(true);
		//construct-------------------------------
		tContainer.add(helpText1);
		tContainer.add(btnBrowse);
		tContainer.add(lbTargetStr);
		tContainer.add(helpText2);
		tContainer.add(targetStrFld);
		tContainer.add(cbxAllExist);
		tContainer.add(cbxIgnoreCase);
		tContainer.add(cbxWholeWord);
		tContainer.add(lbSuffix);
		tContainer.add(isNotSuffix);
		tContainer.add(suffixField);
		tContainer.add(pbProgress);
		tContainer.add(btnSearch);
		tContainer.add(showFolderPath);
		tContainer.add(scrollPane);
		getContentPane().add(tContainer);
		
		//layout---------------------------------
		tContainer.setLayout(null);		
		relayout();
		
		//listener-------------------------------
		btnBrowse.addActionListener(this);
		btnSearch.addActionListener(this);
		addWindowListener(this);
		addWindowStateListener(this);
		addComponentListener(this);
	}
	
	private void relayout(){
		helpText1.setBounds(GAP, GAP, this.getWidth() - (int)(GAP*2.5), helpText1.getPreferredSize().height);
		btnBrowse.setBounds(getWidth() - btnBrowse.getPreferredSize().width - (int)(GAP * 1.5), helpText1.getY() + helpText1.getHeight() + (int)(GAP/2),
				btnBrowse.getPreferredSize().width, btnBrowse.getPreferredSize().height);
		
		helpText2.setBounds(helpText1.getX(), btnBrowse.getY() + btnBrowse.getHeight() + GAP, helpText2.getPreferredSize().width, helpText2.getPreferredSize().height);
		lbTargetStr.setBounds(helpText2.getX(), helpText2.getY() + helpText2.getHeight() + GAP, lbTargetStr.getPreferredSize().width, lbTargetStr.getPreferredSize().height);
		cbxAllExist.setBounds(helpText1.getWidth() - cbxAllExist.getPreferredSize().width + GAP, lbTargetStr.getY() - 4, 
				cbxAllExist.getPreferredSize().width, cbxAllExist.getPreferredSize().height);
		targetStrFld.setBounds(helpText1.getX() + lbTargetStr.getWidth() + GAP, lbTargetStr.getY(), 
				cbxAllExist.getX() - lbTargetStr.getX() - lbTargetStr.getWidth() - GAP, targetStrFld.getPreferredSize().height);
		
		lbSuffix.setBounds(lbTargetStr.getX(), lbTargetStr.getY() + lbTargetStr.getHeight() + GAP, lbSuffix.getPreferredSize().width, lbSuffix.getPreferredSize().height);
		isNotSuffix.setBounds(lbSuffix.getX() + lbSuffix.getWidth(), lbSuffix.getY() - 4, isNotSuffix.getPreferredSize().width, isNotSuffix.getPreferredSize().height);
		cbxWholeWord.setBounds(helpText1.getWidth() - cbxWholeWord.getPreferredSize().width + GAP, isNotSuffix.getY(), cbxWholeWord.getPreferredSize().width, cbxWholeWord.getPreferredSize().height);
		cbxIgnoreCase.setBounds(cbxWholeWord.getX() - cbxIgnoreCase.getPreferredSize().width, isNotSuffix.getY(), cbxIgnoreCase.getPreferredSize().width, cbxIgnoreCase.getPreferredSize().height);
		suffixField.setBounds(isNotSuffix.getX() + isNotSuffix.getWidth(), lbSuffix.getY(),
				cbxIgnoreCase.getX() - isNotSuffix.getX() - isNotSuffix.getWidth() - GAP, suffixField.getPreferredSize().height);
				
		pbProgress.setBounds(lbSuffix.getX(),cbxIgnoreCase.getY() + cbxIgnoreCase.getHeight() + GAP, helpText1.getWidth(), pbProgress.getPreferredSize().height);
		btnSearch.setBounds(pbProgress.getX() + (helpText1.getWidth() - BUTTON_WIDTH)/2, pbProgress.getY() + pbProgress.getHeight() + GAP, BUTTON_WIDTH, BUTTON_HEIGHT);
		showFolderPath.setBounds(btnSearch.getX() + btnSearch.getWidth(), btnSearch.getY() + 10,
				showFolderPath.getPreferredSize().width, showFolderPath.getPreferredSize().height);
		scrollPane.setBounds(pbProgress.getX(),btnSearch.getY() + btnSearch.getHeight() + GAP, helpText1.getWidth(), this.getHeight() - btnSearch.getY() - btnSearch.getHeight() - (int)(GAP*4));
	}
	
	private void chooseFile(){
		JFileChooser tFileChooser = new JFileChooser();
		tFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		tFileChooser.setMultiSelectionEnabled(true);		
		int rVal = tFileChooser.showOpenDialog(this);
	    if(rVal == JFileChooser.APPROVE_OPTION) {
	    	files = tFileChooser.getSelectedFiles();
	    	StringBuilder tSB = new StringBuilder("WILL SEARCH STRING FROM THESE LOCATIONS:\n");
	    	for(int i = 0; i < files.length; i++){
	    		tSB.append("       ");
	    		tSB.append(files[i].getAbsolutePath());
	    		tSB.append("\n");
	    	}
	    	helpText = tSB.toString();
			helpText1.setText(helpText);//helpText can be changed
	    	setVisible(true);
	    }else{
	    	System.exit(0);
	    }
	}
	
	//for folder-----
	private void searchPath(File f, boolean ignore) {
		if (f.isDirectory()) {
			folderDepth ++;
			if(showFolderPath.isSelected())
				resultArea.append(getFolderProfix() + f.getAbsolutePath() + "-----------------------\n");
			File[] fileList = f.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				if(!isInSearching)
					return;
				
				if (fileList[i].isDirectory()){//if is folder, go deeper.
					searchPath(fileList[i], ignore);
				} else {
					searchFile(fileList[i], ignore);
				}
			}
			folderDepth --;
		} else if(f.isFile()){
			searchFile(f, ignore);
		} else{
			resultArea.append("!" + getFolderProfix() + "the location " + f.getAbsolutePath() + " dose not exist anymore!\n");
		}
	}

	//for file-------
	private void searchFile(File f, boolean ignore) {
		String fileName = f.getAbsolutePath();
		if(notMatchSuffixCondition(fileName))
			return;
		if(f.length() == 0)
			return;

		try{
			Thread.yield();
		}catch(Exception e){
			//do nothing, just leave a chance to stop the progress.
		}
		
		char[] content = null;
		if(f.length() > LENGTH_LIMIT){
			resultArea.append("!----" + fileName + "is too huge, and was not searched completely!\n"); 
			content = new char[LENGTH_LIMIT];
		}else{
			content = new char[(int)f.length()];
		}
		
		String str = null;
		FileReader fr = null;
		try{
			fr = new FileReader(f);
			int r = fr.read(content);
			if(r < 1){
				resultArea.append("!----" + fileName + "No content was read into byte[].\n");
				return;
			}
			str = new String(content);
		}catch(FileNotFoundException fnfe){
			resultArea.append("!----" + fileName + "can not be found!\n");
			return;
		}catch(IOException ioe){
			resultArea.append("!----Exception occured when reading " + fileName + "\n");
			return;
		}finally{
			try{
				fr.close();
			}catch(IOException e){
				resultArea.append("!----exception occurred when closing file" + e + "\n");
			}
		}
		
		boolean isFileNameNotPrintedYet = true;
		for (int i = 0; i < targetStrs.length; i++) {
			String condition = targetStrs[i].toString();
			int t = 0;
			int p = ignore ? str.toLowerCase().indexOf(condition.toLowerCase()) : str.indexOf(condition);
			while(p != -1){
				if(!isInSearching)
					return;

				t += p;
				if(isFileNameNotPrintedYet){
					resultArea.append(fileName + "-----------------------\n");
					isFileNameNotPrintedYet = false;
				}
				resultArea.append("Y----" + condition + " was found at position:" + t + "\n");
				str = str.substring(p + condition.length());
				p = str.indexOf(condition);
			}
			pbProgress.setValue(pbProgress.getValue() + 1);
		}
	}
	
	private boolean notMatchSuffixCondition(String fileName){
		if (suffixes == null || suffixes.length == 0)
			return false;
		else{
			fileName = fileName.toLowerCase();
			for(int i = 0; i < suffixes.length; i++){
				if(fileName.endsWith(suffixes[i].trim())){
					return isNotSuffix.isSelected();
				}
			}
			return !isNotSuffix.isSelected();
		}
	}

	private String getFolderProfix(){
		StringBuilder tSB = new StringBuilder();
		for (int i = 0; i < folderDepth; i++){
			tSB.append("---------");
		}
		tSB.append(" in folder ");
		return tSB.toString();
	}

	private int countFiles(File f) {
		int num = 0;
		if (f.isDirectory()) {
			File[] fileList = f.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isDirectory()){//if is folder, go deeper.
					num += countFiles(fileList[i]);
				} else {
					num ++;
				}
			}
		} else{				// if f.isFile()
			num ++;
		}
		return num;
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
		}else if(e.getSource() == btnSearch){
			if(files == null || files.length == 0){
				JOptionPane.showMessageDialog(this, "You havent's select any file or folder!");
			}else{
				if(isInSearching){
					isInSearching = false;
				}else{
					String NON_DUPLICATE_STR1 = "z334Vz";	//Can neiter use "[","(","+".... because the string will be considered as an expression in split method, those character have special
					String NON_DUPLICATE_STR2 = "z433Vz";
					String tTgarGetStr = targetStrFld.getText().replaceAll("//", NON_DUPLICATE_STR1);
					tTgarGetStr = tTgarGetStr.replaceAll("/", NON_DUPLICATE_STR2);
					tTgarGetStr = tTgarGetStr.replaceAll(NON_DUPLICATE_STR1, "/");
					targetStrs = tTgarGetStr.split(NON_DUPLICATE_STR2);
					if(targetStrs == null || targetStrs.length == 0){
						JOptionPane.showMessageDialog(this, "You havent's input the string s to search!");
					}else{
						tTgarGetStr = suffixField.getText().toLowerCase().replaceAll("//", NON_DUPLICATE_STR1);
						tTgarGetStr = tTgarGetStr.replaceAll("/", NON_DUPLICATE_STR2);
						tTgarGetStr = tTgarGetStr.replaceAll(NON_DUPLICATE_STR1, "/");
						suffixes = tTgarGetStr.split(NON_DUPLICATE_STR2);
						
						resultArea.append("****************************************************************************************\n");
						resultArea.append("**********            Search Start on "+ Calendar.getInstance().getTime() +"            ***********\n");
						resultArea.append("****************************************************************************************\n");

						curP = resultArea.getText().length();
						final String calculatingNotice = "Calculating how many files to search.......\n";
						resultArea.append(calculatingNotice);
						
						//Excute the countFile in a new thread
						Thread fildCounter = new Thread(new Runnable() {
							public void run() {
								int num = 0;
								for(int i = 0; i < files.length; i++){
									num += countFiles(files[i]);
								}
								resultArea.insert(String.valueOf(num), curP + calculatingNotice.length() - 1);
								pbProgress.setMaximum(num);
							}
						});
						fildCounter.start();
						
						//Execute in a new thread.
						Thread a = new Thread(new Runnable() {
							public void run() {
								//start to search!
								isInSearching = true;
								btnSearch.setText("Stop Searching");

								for(int i = 0; i < files.length; i++){
									if(!isInSearching)
										break;
									searchPath(files[i], cbxIgnoreCase.isSelected());
								}
								//reset......
								folderDepth = 0;
								isInSearching = false;
								btnSearch.setText("Start To Search");
								pbProgress.setValue(0);
							}
						});
						a.start();
					}
				}
			}
		}
	}
}
