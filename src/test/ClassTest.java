import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalAttribute;

public class ClassTest extends parent{
	public ClassTest(){
		a++;
	}
	public static void main(String[] args) {
		int i = -1;
		i = -1 << 1;
		System.out.println(Integer.toHexString(i));
		i = -1 << 1;
		System.out.println(Integer.toHexString(i));
		System.out.println(i);
//		i = -1111 >> 2;
//		System.out.println(i);
//		i = -16 >>> 1;
//		System.out.println(i);
//		i = -16 >> 32;
//		System.out.println(i);
	}
}

class parent{
	static int a = 0;
	public parent(){
		a++;
	}
}

