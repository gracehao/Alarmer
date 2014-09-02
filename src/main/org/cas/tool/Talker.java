package org.cas.tool;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class Talker {
	/* ��������������ָ���ı�ʾ�������ַ� */
	public static void main(String args[]) {
//		String a = "˹���¿ɿ��ɿ���";
//		char[] b = a.toCharArray();
//		for(int i = 0; i < b.length; i++)
//			System.out.println((int)b[i]);
		System.out.println((char)37085);
		System.out.println((char)(37085 >>> 8) & 0xFF);
		System.out.println((char)(37085 >>> 0) & 0xFF);
		char[] tProdName = "1��v".toCharArray();	//��ȡ��Ʒ��        					
		for(int j = 0; j < tProdName.length; j++)
			System.out.println((int)tProdName[j]);
//		Talker player = new Talker();
//		if (args.length > 0)
//			player.sayPhoneWord(args[0]);
//		
//		Talker f = new Talker();
//		String a[] = {"/", "TestServlet", "/test", "/TestServlet2" };
//		System.out.println(f.getHandler(a, "/"));
	}
//	public String getHandler(String[] config, String requestUri)
//	  {
//		  String result ="";
//		  String regx = "^"+requestUri;
//			Pattern pa = Pattern.compile(regx);
//			for(String temp:config)
//			{
//				Matcher macher = pa.matcher(temp);
//				if(macher.find()&&(temp.length()>result.length()))
//				{
//					result=temp;
//				}
//			}
//			return result==""?"v3KuXGE":result;  
//	  }

	/*
	 * sayPhoneWord()�����ȿ���ͨ�������main()�������ã�Ҳ������Java������ֱ�ӵ��á�
	 * �ӱ����Ͽ���sayPhoneWord()�����Ƚϸ��ӣ���ʵ������ˡ�ʵ���ϣ���򵥵ر������е��ʵ�����Ԫ�أ��������ַ�������Ԫ���ԡ�|���ָ�����
	 * ͨ��һ���������ͨ��һ��Ԫ��һ��Ԫ�صز��ų�����Ϊ������������ȻһЩ���Ұ�ÿһ��������Ľ�β����һ��������Ŀ�ͷ�ϲ���������
	 * 
	 * ����ָ���������ַ�
	 */
	public void sayPhoneWord(String word) {
		byte[] previousSound = null; // Ϊ��һ�����������ģ��byte����
		StringTokenizer st = new StringTokenizer(word, "|", false); // �������ַ�ָ�ɵ���������
		while (st.hasMoreTokens()) {
			String thisPhoneFile = st.nextToken(); // Ϊ���ع�����Ӧ���ļ�����
			thisPhoneFile = "/allophones/" + thisPhoneFile + ".au";
			byte[] thisSound = getSound(thisPhoneFile); // �������ļ���ȡ���

			if (previousSound != null) {
				int mergeCount = 0; // �����ܵĻ�����ǰһ�����غ͵�ǰ���غϲ�
				if (previousSound.length >= 500 && thisSound.length >= 500)
					mergeCount = 500;
				for (int i = 0; i < mergeCount; i++) {
					previousSound[previousSound.length - mergeCount + i] = (byte) ((previousSound[previousSound.length
							- mergeCount + i] + thisSound[i]) / 2);
				}
				playSound(previousSound); // ����ǰһ������
				byte[] newSound = new byte[thisSound.length - mergeCount]; // �Ѿ���ض̵ĵ�ǰ������Ϊǰһ������

				for (int ii = 0; ii < newSound.length; ii++)
					newSound[ii] = thisSound[ii + mergeCount];
				previousSound = newSound;
			} else
				previousSound = thisSound;
		}
		playSound(previousSound); // �������һ�����أ���������ͨ��
		drain();
	}

	/*
	 * ��sayPhoneWord()�ĺ��棬����Կ��������playSound()���������������һ�����أ���Ȼ�����drain()��������ͨ����
	 * ������playSound()�Ĵ��룺
	 * 
	 * �÷�������һ��������
	 */
	private void playSound(byte[] data) {
		if (data.length > 0)
			line.write(data, 0, data.length);
	}

	/*
	 * ������drain()�Ĵ��룺 �÷�����������ͨ��
	 */
	private void drain() {
		if (line != null)
			line.drain();
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
	}

	/*
	 * ���ڻع�ͷ����sayPhoneWord()�����ﻹ��һ����������û�з�������getSound()������
	 * getSound()������һ��au�ļ����ֽ���ݵ���ʽ����Ԥ��¼�Ƶ�������Ҫ�˽��ȡ��ݡ�ת����Ƶ��ʽ��
	 * ��ʼ����������У�SouceDataLine���Լ������ֽ���ݵ���ϸ��̣���ο���������е�ע�ͣ�
	 * 
	 * �÷������ļ���ȡһ�����أ�������ת����byte����
	 */
	private byte[] getSound(String fileName) {
		try {
			URL url = Talker.class.getResource(fileName);
			AudioInputStream stream = AudioSystem.getAudioInputStream(url);
			AudioFormat format = stream.getFormat();

			if ((format.getEncoding() == AudioFormat.Encoding.ULAW)
					|| (format.getEncoding() == AudioFormat.Encoding.ALAW)) { // ��һ��ALAW/ULAW����ת����PCM�Ա�ط�
				AudioFormat tmpFormat = new AudioFormat(
						AudioFormat.Encoding.PCM_SIGNED,
						format.getSampleRate(),
						format.getSampleSizeInBits() * 2, format.getChannels(),
						format.getFrameSize() * 2, format.getFrameRate(), true);

				stream = AudioSystem.getAudioInputStream(tmpFormat, stream);
				format = tmpFormat;
			}

			DataLine.Info info = new DataLine.Info(Clip.class, format,
					((int) stream.getFrameLength() * format.getFrameSize()));

			if (line == null) {
				DataLine.Info outInfo = new DataLine.Info(SourceDataLine.class,
						format);// ����߻�û��ʵ�� �Ƿ��ܹ��ҵ����ʵ���������ͣ�
				if (!AudioSystem.isLineSupported(outInfo)) {
					System.out.println("��֧��ƥ��" + outInfo + "�������");
					throw new Exception("��֧��ƥ��" + outInfo + "�������");
				}
				line = (SourceDataLine) AudioSystem.getLine(outInfo); // �������
				line.open(format, 50000);
				line.start();
			}

			int frameSizeInBytes = format.getFrameSize();
			int bufferLengthInFrames = line.getBufferSize() / 8;
			int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
			byte[] data = new byte[bufferLengthInBytes];

			int numBytesRead = 0; // ��ȡ�ֽ���ݣ�������
			if ((numBytesRead = stream.read(data)) != -1) {
				int numBytesRmaining = numBytesRead;
			}

			byte[] newData = new byte[numBytesRead]; // ���ֽ�����и�ɺ��ʵĴ�С
			for (int i = 0; i < newData.length; i++)
				newData[i] = data[i];
			return newData;
		} catch (Exception e) {
			return new byte[0];
		}
	}

	private SourceDataLine line = null;
}
