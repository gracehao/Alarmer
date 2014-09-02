package org.cas.tool;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;


public class TaoUtil {
	
	static String logPath = "c:/temp/log.txt";
	
	public static void log(String pLogContent) {
		
		File file = new File(logPath);
		if (file.exists()) {
			file = file.getAbsoluteFile();
		} else {
			new File(file.getParent()).mkdirs();
			file = file.getAbsoluteFile();
		}

		try {
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(file.length());
			
//			for(int i = 0; i < Integer.MAX_VALUE/1000; i++){
//				raf.writeBytes(i + "-");
//				raf.writeShort(3);
//			}
			raf.writeUTF(pLogContent);
			raf.close();
		} catch (java.io.IOException e) {
			System.out.println("unexpected error! " + e);
		}
	}
	
	public static void generateHugeBinaryFile(String path){
		File file = new File(path);
		if (file.exists()) {
			file = file.getAbsoluteFile();
		} else {
			new File(file.getParent()).mkdirs();
			file = file.getAbsoluteFile();
		}
		
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "rw");	//generate the file on disk.
			raf.seek(file.length());
			
			for(int i = 0; i < Integer.MAX_VALUE/1000; i++){
				raf.writeBytes(i + "-");
				raf.writeShort(3);
			}
		} catch (java.io.IOException e) {
			System.out.println("unexpected error! " + e);
		} finally{
			try{
				raf.close();
			}catch(IOException ee){
				System.out.println("unexpected error! " + ee);
			}
		}
	}
	
}
