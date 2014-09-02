import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;


public class JoinTest extends Thread {  
    
	static String a = "abcde";
	public JoinTest(){
//		Byte b = Byte.parseByte("128");
//		Character c = 'c';
//		Short s = 32767;
//		Boolean bb = Boolean.parseBoolean("true");
//		Long l = 1234L;
		
	}
	
	public void run(){
		System.out.println("avbbb");
	}
	
    public static void main(String[] args) throws Exception {
    	ArrayList<Integer> a = new ArrayList(Integer.MAX_VALUE/10000);
    	Vector<Integer> v = new Vector(Integer.MAX_VALUE/10000);
    	Integer[] ints = new Integer[Integer.MAX_VALUE/500];
    	for(int i = Integer.MAX_VALUE/10000; i > 0; i-- ){
    		a.add(Integer.valueOf(i));
    	}
    	for(int i = Integer.MAX_VALUE/10000; i > 0; i-- ){
    		v.add(Integer.valueOf(i));
    	}
    	long startTime = Calendar.getInstance().getTimeInMillis();
    	System.out.println("start" );

    	for(int i = Integer.MAX_VALUE/10000 - 3; i > 0; i-- ){
    		v.remove(1);
    	}
    	System.out.println("time cost:" + (Calendar.getInstance().getTimeInMillis() - startTime)/1000 + "secs");
    	
//    	System.out.println("==================");
//
//    	startTime = Calendar.getInstance().getTimeInMillis();
//    	System.out.println("start" );
//    	for(int i = Integer.MAX_VALUE/500; i > 0; i-- ){
//    		hm.put(Integer.valueOf(i), Integer.valueOf(i));
//    	}
//    	System.out.println("time cost:" + (Calendar.getInstance().getTimeInMillis() - startTime)/1000 + "secs");
    }
    
    
}  
