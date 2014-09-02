package org.cas.tool;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import org.apache.commons.io.FileUtils;


public class FileEncodingTransformer extends JFrame implements ComponentListener, WindowListener, ActionListener, WindowStateListener{

	private static final long serialVersionUID = 1L;
	private static int GAP = 20;
	private static int BUTTON_WIDTH = 200;
	private static int BUTTON_HEIGHT = 50;
	private static int LENGTH_LIMIT = Integer.MAX_VALUE/10;
	private static String[] CODES = {"GBK","UTF-8"};
	
	JTextArea helpText1;
	JButton btnBrowse;

	JLabel helpText2;
	JLabel lbFromStr;
	JComboBox<String> cmbFrom;
	JLabel lbToStr;
	JComboBox<String> cmbTo;
	JCheckBox isNotSuffix;
	JLabel lbSuffix;
	JTextField suffixField;
	JProgressBar pbProgress;
	JButton btnSearch;
	JTextArea resultArea;
	JScrollPane scrollPane;
	JPanel tContainer;

	String helpText;
	File[] files;
	boolean isSearching;
	String[] suffixes = null;
	
	int folderDepth = 0;
	
	private FileEncodingTransformer(){
		
		//declare--------------------------------
		helpText1 = new JTextArea();
		btnBrowse = new JButton();
		helpText2 = new JLabel();
		lbFromStr = new JLabel();
		cmbFrom = new JComboBox<String>(CODES);
		lbToStr = new JLabel();
		cmbTo = new JComboBox<String>(CODES);
		isNotSuffix = new JCheckBox();
		lbSuffix = new JLabel();
		suffixField = new JTextField();
		pbProgress = new JProgressBar();
		btnSearch = new JButton();
		resultArea = new JTextArea();
		scrollPane = new JScrollPane(resultArea);
		tContainer = new JPanel();
		
		//properties-------------------------------
		setTitle("CODE CONVERTER1.0----info@ShareTheGoodOnes.com");
		helpText1.setOpaque(false);
		btnBrowse.setText("Re Select the Files and Folders");
		helpText2.setText("* Pls use '//' as '/', becuase single '/' is considered as seperator.");
		lbFromStr.setText("From:");
		lbToStr.setText("To:");
		isNotSuffix.setText("is NOT");
		lbSuffix.setText("Suffixs: ");
		btnSearch.setText("Start To Convert");
		resultArea.setAutoscrolls(true);
		cmbTo.setSelectedIndex(1);
		
		//construct-------------------------------
		tContainer.add(helpText1);
		tContainer.add(btnBrowse);
		tContainer.add(lbFromStr);
		tContainer.add(helpText2);
		tContainer.add(cmbFrom);
		tContainer.add(lbToStr);
		tContainer.add(cmbTo);
		tContainer.add(isNotSuffix);
		tContainer.add(lbSuffix);
		tContainer.add(suffixField);
		tContainer.add(pbProgress);
		tContainer.add(btnSearch);
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
		
		lbFromStr.setBounds(helpText1.getX(), btnBrowse.getY() + btnBrowse.getHeight() + GAP, lbFromStr.getPreferredSize().width, lbFromStr.getPreferredSize().height);
		cmbFrom.setBounds(helpText1.getX() + lbFromStr.getWidth() + GAP, lbFromStr.getY(), 
				helpText1.getWidth() - lbFromStr.getWidth() - GAP, cmbFrom.getPreferredSize().height);
		
		lbToStr.setBounds(lbFromStr.getX(), lbFromStr.getY() + lbFromStr.getHeight() + GAP, lbFromStr.getWidth(), lbToStr.getPreferredSize().height);
		cmbTo.setBounds(lbToStr.getX() + lbToStr.getWidth() + GAP, lbToStr.getY(),
				helpText1.getWidth() - lbToStr.getX() - lbToStr.getWidth(), cmbTo.getPreferredSize().height);
		
		helpText2.setBounds(lbToStr.getX(), lbToStr.getY() + lbToStr.getHeight() + GAP, helpText2.getPreferredSize().width, helpText2.getPreferredSize().height);

		lbSuffix.setBounds(helpText2.getX(), helpText2.getY() + helpText2.getHeight() + GAP, lbSuffix.getPreferredSize().width, lbSuffix.getPreferredSize().height);
		isNotSuffix.setBounds(lbSuffix.getX() + lbSuffix.getWidth() + GAP, lbSuffix.getY() - 4, isNotSuffix.getPreferredSize().width, isNotSuffix.getPreferredSize().height);
		suffixField.setBounds(isNotSuffix.getX() + isNotSuffix.getWidth() + GAP, isNotSuffix.getY() + 4,
				helpText1.getWidth() - isNotSuffix.getX() - isNotSuffix.getWidth(), suffixField.getPreferredSize().height);

		pbProgress.setBounds(lbToStr.getX(), isNotSuffix.getY() + isNotSuffix.getHeight() + GAP, helpText1.getWidth(), pbProgress.getPreferredSize().height);
		btnSearch.setBounds(pbProgress.getX() + (helpText1.getWidth() - BUTTON_WIDTH)/2, pbProgress.getY() + pbProgress.getHeight() + GAP, BUTTON_WIDTH, BUTTON_HEIGHT);
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
	public void searchPath(File f) {
		if (f.isDirectory()) {
			folderDepth ++;
			resultArea.append(getFolderProfix() + f.getAbsolutePath() + "\n");
			File[] fileList = f.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isDirectory()){//if is folder, go deeper.
					searchPath(fileList[i]);
				} else {
					convertFile(fileList[i]);
				}
			}
			folderDepth --;
		} else if(f.isFile()){
			convertFile(f);
		} else{
			resultArea.append("!" + getFolderProfix() + "the location " + f.getAbsolutePath() + " dose not exist anymore!\n");
		}
	}
	
	//for file-------
	public void convertFile(File f) {
		String fileName = f.getAbsolutePath();
		if(!isSearching)
			return;
		if(notMatchSuffixCondition(fileName))
			return;
		if(f.length() == 0)
			return;
		if(f.length() > LENGTH_LIMIT){
			resultArea.append("!----" + fileName + "is too huge, and was not converted completely!\n"); 
			return;
		}

		//apach.comman.lang solution.
		File f2 = new File("c:/temp" + fileName.substring(2));
		try{
			FileUtils.writeLines(f2, cmbTo.getSelectedItem().toString(), FileUtils.readLines(f, cmbFrom.getSelectedItem().toString()));        		
		}catch(IOException e){
			resultArea.append("!----exception occurred when converting file" + fileName + "\n" + e + "\n");
		}
		Thread.yield();
		
//		//My own solution!
//		byte[] content = new byte[(int)f.length()];
//		boolean isFileNameNotPrintedYet = true;
//		RandomAccessFile fr = null;
//		try{
//			fr = new RandomAccessFile(f, "rw");
//			int r = fr.read(content);
//			if(r < 1){
//				resultArea.append(getFolderProfix() + fileName + "-----------------------\n");
//				isFileNameNotPrintedYet = false;
//				resultArea.append("!----No content was read into byte[].\n");
//			}
//			String str = new String(content, cmbFrom.getSelectedItem().toString());
//			content = str.getBytes(Charset.forName(cmbTo.getSelectedItem().toString()));
//			str = new String(content, cmbTo.getSelectedItem().toString());
//			fr.write(str.getBytes(cmbTo.getSelectedItem().toString()));
//		}catch(FileNotFoundException fnfe){
//			resultArea.append("!----" + fileName + "can not be found!\n");
//		}catch(IOException ioe){
//			resultArea.append("!----Exception occured when reading " + fileName + "\n");
//		}finally{
//			try{
//				fr.close();
//			}catch(IOException e){
//				resultArea.append("!----exception occurred when closing file" + e + "\n");
//			}
//		}
//		
//		try{
//			Thread.currentThread().wait(100);
//		}catch(Exception e){
//			//do nothing, just leave a chance to stop the progress.
//		}
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
	
	//event interface implementation=============================================================
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
				if(isSearching){
					isSearching = false;
				}else{
					String tFromCode = cmbFrom.getSelectedItem().toString();
					String tToCode = cmbTo.getSelectedItem().toString();
					
					if(tFromCode == null || tFromCode.length() == 0){
						JOptionPane.showMessageDialog(this, "You havent's input the original code format!");
					}else if(tToCode == null || tToCode.length() == 0){
						JOptionPane.showMessageDialog(this, "You havent's input the target code format!");
					}else if(tFromCode.equals(tToCode)){
						JOptionPane.showMessageDialog(this, "The input and target code format are the same!");
					}else{
						String NON_DUPLICATE_STR1 = "z334Vz";	//Can neiter use "[","(","+".... because the string will be considered as an expression in split method, those character have special
						String NON_DUPLICATE_STR2 = "z433Vz";
						String limitations = suffixField.getText().toLowerCase().replaceAll("//", NON_DUPLICATE_STR1);
						limitations = limitations.replaceAll("/", NON_DUPLICATE_STR2);
						limitations = limitations.replaceAll(NON_DUPLICATE_STR1, "/");
						suffixes = limitations.split(NON_DUPLICATE_STR2);
						isSearching = true;
						btnSearch.setText("Stop Searching");
						setSize(this.getWidth(), 728);
						resultArea.append("****************************************************************************************\n");
						resultArea.append("**********            Convert Start on "+ Calendar.getInstance().getTime() +"            ***********\n");

						Thread workThread = new Thread(new Runnable() {
							public void run() {
								for(int i = 0; i < files.length; i++){
									if(!isSearching)
										break;
									searchPath(files[i]);
								}
								//reset......
								folderDepth = 0;
								isSearching = false;
								btnSearch.setText("Start To Search");
							}
						});
						workThread.start();
						
						resultArea.append("**********            Convert finished! Please go to c:/temp for the result.     ***********\n");
						resultArea.append("****************************************************************************************\n");
					}
				}
			}
		}
	}
	
	//main--------------------------------------------
	public static void main(String[] args) {
		FileEncodingTransformer s = new FileEncodingTransformer();
		s.setSize(500, 440);
		s.chooseFile();
	}
	
	public void readFile() throws IOException{
	    RandomAccessFile f = new RandomAccessFile("test.txt", "r");
	    byte[] b = new byte[(int)f.length()];
	    f.read(b);
	    
	    String input = new String(b, "utf-8");

	    Pattern pattern = Pattern.compile("\\d+(\\.\\d+)?");	    //can match all numbers
	    Matcher match = pattern.matcher(input);
	    while(match.find()) {
	        System.out.println(match.group(0));	        //match.group(0)is the one
	    }
	    f.close();
	}
	
	private void writeBinaryFile(byte[] pBytes, String pPath) {
		Path path = Paths.get(pPath);

		if (pBytes == null || pBytes.length == 0) {
			// can we make a huge one? you know the int.Maxvalue limited the
			// byte[] size.
			// long length = 1000000000L;
			// pBytes = new byte[length];
			pBytes[0] = 80;
			pBytes[1] = 81;
			pBytes[2] = 82;
		}

		try {
			Files.write(path, pBytes); // creates, overwrites
		} catch (IOException ioe) {
			TaoUtil.log("IOException occured when writing file!" + ioe);
		} catch (Exception e) {
			TaoUtil.log("Unexpected Exception occured when writing file! " + e);
		}
	}
}