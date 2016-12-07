package com.theone.sns.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zhangwenhai on 2014/9/8.
 */
public class MD5Util {
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'A', 'B', 'C', 'D', 'E', 'F' };

	private static final char[] LITTLEHEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'a', 'b', 'c', 'd', 'e', 'f' };
	private static final String MD5_KEY = "2f9*p#omg";

	public static String toHexString(byte[] b) {
		return toHexString(b, true);
	}

	public static String toHexString(byte[] b, boolean bBig) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			if (bBig) {
				sb.append(HEX_DIGITS[((b[i] & 0xF0) >>> 4)]);
				sb.append(HEX_DIGITS[(b[i] & 0xF)]);
			} else {
				sb.append(LITTLEHEX_DIGITS[((b[i] & 0xF0) >>> 4)]);
				sb.append(LITTLEHEX_DIGITS[(b[i] & 0xF)]);
			}
		}
		return sb.toString();
	}

	public static String md5Phone(String phone) {
		if (phone != null) {
			return md5(phone + "2f9*p#omg");
		}
		return "";
	}

	public static byte[] md5Bytes(String text) throws Exception {
		if ((text == null) || ("".equals(text))) {
			return new byte[0];
		}

		MessageDigest msgDigest = null;
		try {
			msgDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("System doesn't support MD5 algorithm.");
		}
		byte[] bytes = (byte[]) null;
		synchronized (msgDigest) {
			msgDigest.update(text.getBytes());
			bytes = msgDigest.digest();
		}
		return bytes;
	}

	public static String md5str(byte[] rawdata, boolean big) {
		if (rawdata == null) {
			return "";
		}
		MessageDigest msgDigest = null;
		try {
			msgDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("System doesn't support MD5 algorithm.");
		}
		byte[] bytes = (byte[]) null;
		synchronized (msgDigest) {
			msgDigest.update(rawdata);
			bytes = msgDigest.digest();
		}
		return toHexString(bytes, big);
	}

	public static String md5(String text, boolean isReturnRaw) {
		if ((text == null) || ("".equals(text))) {
			return text;
		}

		byte[] bytes = (byte[]) null;
		try {
			bytes = md5Bytes(text);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		if (isReturnRaw) {
			return new String(bytes);
		}

		String md5Str = new String();

		for (int i = 0; i < bytes.length; i++) {
			byte tb = bytes[i];

			char tmpChar = (char) (tb >>> 4 & 0xF);
			char high;
			if (tmpChar >= '\n')
				high = (char) ('a' + tmpChar - 10);
			else {
				high = (char) ('0' + tmpChar);
			}
			md5Str = md5Str + high;

			tmpChar = (char) (tb & 0xF);
			char low;
			if (tmpChar >= '\n')
				low = (char) ('a' + tmpChar - 10);
			else {
				low = (char) ('0' + tmpChar);
			}
			md5Str = md5Str + low;
		}

		return md5Str;
	}

	public static String md5(String text) {
		return md5(text, false);
	}

	public static String md5sum(String filename) {
		byte[] buffer = new byte[1024];
		int numRead = 0;
		try {
			InputStream fis = new FileInputStream(filename);
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			if (md5 == null)
				return "";
			byte[] md5dist = (byte[]) null;
			synchronized (md5) {
				while ((numRead = fis.read(buffer)) > 0) {
					md5.update(buffer, 0, numRead);
				}
				fis.close();
				md5dist = md5.digest();
			}
			return toHexString(md5dist);
		} catch (Exception e) {
			System.out.println("error");
		}
		return null;
	}
}
