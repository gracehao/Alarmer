package org.cas.tool;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Find sensitive and insensitive strings from a huge file.
 * @author TaoJiang
 *
 */
public class FindInHugeFile {
	static String filePath = "c:/temp/test.bin";	//default path, will be replace by command line.
	static String targetStr1 = "Nexsan";
	static String targetStr2 = "Nexsan Technologies";
	static int CACH_SIZE = 19;//4*1024;

	String subTargetStr = null;						//in case that the matched string span over the edge of cach.

	String targetStr1_lc = "nexsan";				//temporal use, for matching string ignore case. 
	String targetStr2_lc = "nexsan technologies";
	String targetStr2_partial = " Technologies";
	String targetStr2_partial_lc = " technologies";
	
	boolean theEndOfLastStrIsOK = true;	//temporal use for caching the last character of last search.
	boolean needToCheckTheStartOfNextCatch = false;	//temporal use for caching the last character of last search.
	
	boolean partialTar1_sen = false;
	boolean partialTar1_insen = false;
	boolean partialTar2_sen = false;
	boolean partialTar2_insen = false;
	
	static int numTarg1_sens = 0;
	static int numTarg2_sens = 0;
	static int numTarg1_insens = 0;
	static int numTarg2_insens = 0;
	
	public static void main(String[] args) {
		if(args != null && args.length > 0)
			filePath = args[0];
		
		FindInHugeFile finder = new FindInHugeFile();
		finder.findStringFromFile(filePath);

		System.out.println("Number of case sensitive match of Nexasan is: " + numTarg1_sens);
		System.out.println("Number of case sensitive match of Nexasan Technologies is: " + numTarg2_sens);
		System.out.println("Number of case insensitive match of Nexasan is: " + numTarg1_insens);
		System.out.println("Number of case insensitive match of Nexasan Technologies is: " + numTarg2_insens);
		
	}

	private void findStringFromFile(String pPath) {
		RandomAccessFile f = null;
		try{
		    f = new RandomAccessFile(pPath, "r");
		    
		    long times = f.length()/CACH_SIZE;	//the times we will read.
		   
		    for(long i = 0; i < times; i++){
		    	byte[] b = new byte[CACH_SIZE];
		    	f.read(b);
		    	checkTagetString(b);
		    }
		    
		    //todo: do the left part. which has not been handled.
		    processTheTailString();
		    
		}catch(IOException e){
			System.out.println("uexpected IOException:" + e);
		}finally{					//put close here to make sure the file will be closed even if exception occurred.
			if(f != null){
				try{
					f.close();
				}catch(IOException ee){
					System.out.println("exception occured when closing the file!");
				}
			}
		}
	}
	
	private void checkTagetString(byte[] tBs){
		
		if(subTargetStr == null){		//if not half-match, then use targetStr1 to search 
			subTargetStr = targetStr1_lc;
		}
		String tSt = new String(tBs);
		String tSt_lc = tSt.toLowerCase(); //check the ignore case first.
		
		if(needToCheckTheStartOfNextCatch){//this mark means last catch ends with "nexsan".
			if(tBs[0] > 'z' || tBs[0] < '0'){
				if(partialTar1_sen){
					numTarg1_sens ++;
					partialTar1_sen = false;
				}else{
					numTarg1_insens ++;
					partialTar1_insen = false;
				}
			}
			
			if(tSt_lc.startsWith(targetStr2_partial_lc) && (tBs[targetStr2_partial_lc.length()] > 'z' || tBs[targetStr2_partial_lc.length()] < '0')){
				if(tSt.startsWith(targetStr2_partial)){
					numTarg2_sens ++;
				}else{
					numTarg2_insens ++;
				}
			}
			subTargetStr = targetStr1_lc;
			needToCheckTheStartOfNextCatch = false;
		}
		
		int i = 0;
		while (true){
			i = tSt_lc.indexOf(subTargetStr.toLowerCase());
			if(i > -1){								//matched!
				//check the character at two ends.
				boolean shouldIgnore = false;
				if(i == 0){											//found at beginning, check flag.
					if(!theEndOfLastStrIsOK){
						shouldIgnore = true;
					}
				}else if(i + targetStr1.length() == tBs.length){	//found at end, leave flag. break;
					shouldIgnore = true;
					
					if(tSt.indexOf(subTargetStr) == i){
						partialTar1_sen = true;
					}else{
						partialTar1_insen = true;
					}
					needToCheckTheStartOfNextCatch = true;
					break;
				}else{												//found at middle, check two chars.
					char headChar = tSt_lc.charAt(i - 1);
					if(headChar >= '0' && headChar <= 'z'){
						shouldIgnore = true;
					}
					char endChar = tSt_lc.charAt(i + targetStr1.length());
					if(endChar >= '0' && endChar <= 'z'){
						shouldIgnore = true;
					}
				}
				
				if(!shouldIgnore){ //count the numbers
					if(tSt.indexOf(targetStr1) == i){//check if it's sensitive math.
						numTarg1_sens ++;
						//checkLongName;
						if(tSt.indexOf(targetStr2) == i){
							numTarg2_sens ++;
						}else{
							if(tSt_lc.indexOf(targetStr2_lc) == i){
								numTarg2_sens ++;
							}else{
								String tStrLeft = tSt.substring(i + subTargetStr.length());
								if(targetStr2_partial.startsWith(tStrLeft)){		//if " Technologies" starts with left string.
									partialTar2_sen = true;
									subTargetStr = targetStr2_partial.substring(tStrLeft.length());
									break;
								}
							}
						}
					}else{
						numTarg1_insens ++;
						//checkLongName;
						if(tSt_lc.indexOf(targetStr2.toLowerCase()) == i){
							numTarg2_insens ++;
						}else{
							String tStrLeft = tSt_lc.substring(i + subTargetStr.length());
							if(targetStr2_partial.toLowerCase().startsWith(tStrLeft)){
								partialTar2_insen = true;
								subTargetStr = targetStr2_partial.substring(tStrLeft.length());
								break;
							}
						}
					}
				}
				
				tSt_lc = tSt_lc.substring(i + subTargetStr.length());
				tSt = tSt.substring(i + subTargetStr.length());
				subTargetStr = targetStr1;
			}else{
				if(!subTargetStr.equals(targetStr1)){		//if it's handling the left part of last search. then correct the target string and carry on.
					subTargetStr = targetStr1;
				}else{
					//todo:check the end and set the subTarget if possible.
					int tLengDiff = tSt_lc.length() - targetStr1.length();
					if(tLengDiff > 0){
						tSt_lc = tSt_lc.substring(tLengDiff);
					}
					subTargetStr = checkEndStr(tSt_lc);
					break;
				}
			}
		}
	}
	
	private String checkEndStr(String endStr){
		int times = endStr.length();
		for(int i = 0; i > times; i++){
			String tEndStr = endStr.substring(i);
			if(targetStr1.startsWith(tEndStr)){
				theEndOfLastStrIsOK = true;
				partialTar1_sen = true;
				return targetStr1.substring(tEndStr.length());
			}else if(targetStr1_lc.startsWith(tEndStr.toLowerCase())){
				theEndOfLastStrIsOK = true;
				partialTar1_insen = true;
				return targetStr1.substring(tEndStr.length());
			}
			
			char lastChar = endStr.charAt(endStr.length() - 1);
			theEndOfLastStrIsOK  = '0' > lastChar && 'z' < lastChar;
		}
		return null;
	}
	
	private void processTheTailString(){
		//do the same thing with the string left!
	}

}
