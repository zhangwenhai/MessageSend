/*
 * 文件名: StringUtil.java
 * 描    述: 字符串操作工具类
 * 创建人: zhouyujun
 */
package com.theone.sns.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.Adler32;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.theone.sns.R;
import com.theone.sns.common.FusionConfig;

/**
 * 字符串操作的工具类<BR>
 * 
 * @author zhouyujun
 */

public abstract class StringUtil {

	private static final String TAG = "StringUtil";

	/**
	 * 单位为一万
	 */
	private static final double TEN_THOUSAND = 10000d;

	/**
	 * 单位为一亿
	 */
	private static final double HUNDRED_MILLION = 100000000d;

	/**
	 * 邮箱验证的正则表达式
	 */
	private static Pattern emailPattern = Pattern
			.compile("^([\\w-\\.]+)@((\\[[0-9]{1,3}"
					+ "\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))"
					+ "([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");

	/**
	 * 判断str1和str2是否相同
	 *
	 * @param str1
	 *            str1
	 * @param str2
	 *            str2
	 * @return true or false
	 */
	public static boolean equals(String str1, String str2) {
		return str1 == str2 || str1 != null && str1.equals(str2);
	}

	/**
	 * 判断str1和str2是否相同(不区分大小写)
	 *
	 * @param str1
	 *            str1
	 * @param str2
	 *            str2
	 * @return true or false
	 */
	public static boolean equalsIgnoreCase(String str1, String str2) {
		return str1 != null && str1.equalsIgnoreCase(str2);
	}

	/**
	 *
	 * 判断字符串str1是否包含字符串str2
	 *
	 * @param str1
	 *            源字符串
	 * @param str2
	 *            指定字符串
	 * @return true源字符串包含指定字符串，false源字符串不包含指定字符串
	 */
	public static boolean contains(String str1, String str2) {
		return str1 != null && str1.contains(str2);
	}

	/**
	 *
	 * 判断字符串是否为空，为空则返回一个空值，不为空则返回原字符串
	 *
	 * @param str
	 *            待判断字符串
	 * @return 判断后的字符串
	 */
	public static String getString(String str) {
		return str == null ? "" : str;
	}

	/**
	 * 验证字符串是否符合email格式
	 *
	 * @param email
	 *            需要验证的字符串
	 * @return 验证其是否符合email格式，符合则返回true,不符合则返回false
	 */
	public static boolean isEmail(String email) {
		// 通过正则表达式验证email是否合法
		return email != null && emailPattern.matcher(email).matches();
	}

	/**
	 * 验证字符串是否为数字
	 *
	 * @param str
	 *            需要验证的字符串
	 * @return 不是数字返回false，是数字就返回true
	 */
	public static boolean isNumeric(String str) {
		return !TextUtils.isEmpty(str) && str.matches("[0-9]*");
	}

	/**
	 * 验证字符串是否符合手机号格式
	 *
	 * @param str
	 *            需要验证的字符串
	 * @return 不是手机号返回false，是手机号就返回true
	 */
	public static boolean isMobile(String str) {
		return str != null
				&& str.matches("(\\+86|86|0086)?(13[0-9]|15[0-35-9]|14[57]|18[02356789])\\d{8}");
	}

	/**
	 * 替换字符串中特殊字符
	 *
	 * @param strData
	 *            源字符串
	 * @return 替换了特殊字符后的字符串，如果strData为NULL，则返回空字符串
	 */
	public static String encodeString(String strData) {
		if (strData == null) {
			return "";
		}
		return strData.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;").replaceAll("'", "&apos;")
				.replaceAll("\"", "&quot;");
	}

	/**
	 * 还原字符串中特殊字符
	 *
	 * @param strData
	 *            strData
	 * @return 还原后的字符串
	 */
	public static String decodeString(String strData) {
		if (strData == null) {
			return "";
		}
		return strData.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
				.replaceAll("&apos;", "'").replaceAll("&quot;", "\"")
				.replaceAll("&amp;", "&");
	}

	/**
	 *
	 * 去掉url中多余的斜杠
	 *
	 * @param url
	 *            字符串
	 * @return 去掉多余斜杠的字符串
	 */
	public static String fixUrl(String url) {
		if (TextUtils.isEmpty(url)) {
			return url;
		}

		StringBuffer stringBuffer = new StringBuffer(url);
		for (int i = stringBuffer.indexOf("//", stringBuffer.indexOf("//") + 2); i != -1; i = stringBuffer
				.indexOf("//", i + 1)) {
			stringBuffer.deleteCharAt(i);
		}
		return stringBuffer.toString();
	}

	/**
	 *
	 * 按照一个汉字两个字节的方法计算字数
	 *
	 * @param string
	 *            String
	 * @return 返回字符串's count
	 */
	public static int count2BytesChar(String string) {
		int count = 0;
		if (string != null) {
			for (char c : string.toCharArray()) {
				count++;
				if (isChinese(c)) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * 判断字符串中是否包含中文 <BR>
	 * [功能详细描述] [added by zhouyujun]
	 *
	 * @param str
	 *            检索的字符串
	 * @return 是否包含中文
	 */
	public static boolean hasChinese(String str) {
		boolean hasChinese = false;
		if (str != null) {
			for (char c : str.toCharArray()) {
				if (isChinese(c)) {
					hasChinese = true;
					break;
				}
			}
		}
		return hasChinese;
	}

	/**
	 *
	 * 截取字符串，一个汉字按两个字符来截取<BR>
	 * [功能详细描述] [added by zhouyujun]
	 *
	 * @param src
	 *            源字符串
	 * @param charLength
	 *            字符长度
	 * @return 截取后符合长度的字符串
	 */
	public static String subString(String src, int charLength) {
		if (src != null) {
			int i = 0;
			for (char c : src.toCharArray()) {
				i++;
				charLength--;
				if (isChinese(c)) {
					charLength--;
				}
				if (charLength <= 0) {
					if (charLength < 0) {
						i--;
					}
					break;
				}
			}
			return src.substring(0, i);
		}
		return src;
	}

	/**
	 *
	 * 判断参数c是否为中文<BR>
	 * [功能详细描述] [added by zhouyujun]
	 *
	 * @param c
	 *            char
	 * @return 是中文字符返回true，反之false
	 */
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;

	}

	/**
	 *
	 * 检测密码强度
	 *
	 * @param password
	 *            密码
	 * @return 密码强度（1：低 2：中 3：高）
	 */
	public static int checkStrong(String password) {
		boolean num = false;
		boolean lowerCase = false;
		boolean upperCase = false;
		boolean other = false;

		int threeMode = 0;
		int fourMode = 0;

		for (int i = 0; i < password.length(); i++) {
			// 单个字符是数字
			if (password.codePointAt(i) >= 48 && password.codePointAt(i) <= 57) {
				num = true;
			}
			// 单个字符是小写字母
			else if (password.codePointAt(i) >= 97
					&& password.codePointAt(i) <= 122) {
				lowerCase = true;
			}
			// 单个字符是大写字母
			else if (password.codePointAt(i) >= 65
					&& password.codePointAt(i) <= 90) {
				upperCase = true;
			}
			// 特殊字符
			else {
				other = true;
			}
		}

		if (num) {
			threeMode++;
			fourMode++;
		}

		if (lowerCase) {
			threeMode++;
			fourMode++;
		}

		if (upperCase) {
			threeMode++;
			fourMode++;
		}

		if (other) {
			fourMode++;
		}

		// 数字、大写字母、小写字母只有一个，密码强度低
		if (threeMode == 1 && !other) {
			return 1;
		}
		// 四种格式有其中两个，密码强度中
		else if (fourMode == 2) {
			return 2;
		}
		// 四种格式有三个或者四个，密码强度高
		else if (fourMode >= 3) {
			return 3;
		}
		// 正常情况下不会出现该判断
		else {
			return 0;
		}
	}

	/**
	 *
	 * 返回一个制定长度范围内的随机字符串
	 *
	 * @param min
	 *            范围下限
	 * @param max
	 *            范围上限
	 * @return 字符串
	 */
	public static String createRandomString(int min, int max) {
		StringBuffer strB = new StringBuffer();
		Random random = new Random();
		int lenght = min;
		if (max > min) {
			lenght += random.nextInt(max - min + 1);
		}
		for (int i = 0; i < lenght; i++) {
			strB.append((char) (97 + random.nextInt(26)));
		}
		return strB.toString();
	}

	/**
	 *
	 * [用于获取字符串中字符的个数]<BR>
	 * [功能详细描述]
	 *
	 * @param content
	 *            文本内容
	 * @return 返回字符的个数
	 */
	public static int getStringLeng(String content) {
		return (int) Math.ceil(count2BytesChar(content) / 2.0);
	}

	/**
	 * 根据业务拼装电话号码<BR>
	 *
	 * @param number
	 *            电话号码
	 * @return 拼装后的电话号码
	 */
	public static String fixPortalPhoneNumber(String number) {
		if (TextUtils.isEmpty(number)) {
			return number;
		}

		String retPhoneNumber = number.trim();

		// 确定是否是手机号码，然后将前缀去除，只保留纯号码
		if (isMobile(retPhoneNumber)) {
			if (retPhoneNumber.startsWith("+86")) {
				retPhoneNumber = retPhoneNumber.substring(3);
			} else if (retPhoneNumber.startsWith("86")) {
				retPhoneNumber = retPhoneNumber.substring(2);
			} else if (retPhoneNumber.startsWith("0086")) {
				retPhoneNumber = retPhoneNumber.substring(4);
			}
		}

		return retPhoneNumber;
	}

	/**
	 * 根据业务拼装电话号码<BR>
	 *
	 * @param number
	 *            电话号码
	 * @return 拼装后的电话号码
	 */
	public static String fixPortalPhoneNumber(String number, String contryCode) {
		if (TextUtils.isEmpty(number)) {
			return number;
		}

		String retPhoneNumber = number.trim();

		// 将前缀去除，只保留纯号码
		// 如果是中国的电话号码，需要做如下处理
		if ("+86".equals(contryCode)) {
			if (retPhoneNumber.startsWith("+86")) {
				retPhoneNumber = retPhoneNumber.substring(3);
			} else if (retPhoneNumber.startsWith("86")) {
				retPhoneNumber = retPhoneNumber.substring(2);
			} else if (retPhoneNumber.startsWith("0086")) {
				retPhoneNumber = retPhoneNumber.substring(4);
			}
		}
		// 如果是其他国家电话号码，需要得到除国家码之外的电话号码
		else if (retPhoneNumber.startsWith(contryCode)) {
			retPhoneNumber = retPhoneNumber.substring(contryCode.length());
		}

		return retPhoneNumber;
	}

	/**
	 *
	 * 生成唯一的字符串对象<BR>
	 *
	 * @return 唯一的字符串
	 */
	public static String generateUniqueID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 *
	 * 计算string的Adler32校验和
	 *
	 * @param str
	 *            待计算字符串
	 * @return Adler32校验和
	 */
	public static long adler32Value(String str) {
		if (TextUtils.isEmpty(str)) {
			return 0;
		}

		byte[] buf = str.getBytes();

		Adler32 adler32 = new Adler32();
		adler32.reset();
		adler32.update(buf, 0, buf.length);

		return adler32.getValue();
	}

	/**
	 * 获取业务请求序列号
	 *
	 * @return 业务请求序列号
	 */
	public static String getRequestSerial() {
		return System.currentTimeMillis() + "";
	}

	/**
	 * 将字符串数组装换成字符串中间“，”隔开
	 *
	 * @return 生成的字符串
	 */
	public static String getStringFromList(String[] st) {
		if (null == st) {
			Log.e(TAG, st + " is null!");
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < st.length; i++) {
			sb.append(st[i]).append(",");
		}

		return sb.toString();
	}

	/**
	 *
	 * 格式化统计的数量,超过一万的数据用万做单位，超过亿的数据用亿做单位，
	 *
	 * @param context
	 *            上下文
	 * @param number
	 *            统计的数量
	 * @return 格式化后的数量
	 */
	public static String formatNumber(Context context, String number) {
		if (!TextUtils.isEmpty(number)) {
			long numberFormat = Long.valueOf(number.trim());

			if (numberFormat < 0) {
				Log.w(TAG, "The number should not be less than 0.");
				return "0";
			} else if (numberFormat < TEN_THOUSAND) {
				return String.valueOf(numberFormat);
			}

			// 设置单位为万
			else if (numberFormat < HUNDRED_MILLION) {

				return context.getResources().getString(
						R.string.common_number_unit_wan,
						numberFormat / TEN_THOUSAND);
			}
			// 设置单位为亿
			else {
				return context.getResources().getString(
						R.string.common_number_unit_yi,
						numberFormat / HUNDRED_MILLION);
			}
		} else {
			Log.e(TAG, "The number is wrong.");
			return "0";
		}

	}

	/**
	 * 功能：判断一个字符串是否包含特殊字符
	 *
	 * @param string
	 *            要判断的字符串
	 * @return true 提供的参数string包含特殊字符
	 * @return false 提供的参数string不包含特殊字符
	 */
	public static boolean isConSpeCharacters(String string) {
		if (TextUtils.isEmpty(string)) {
			return false;
		}

		if (string.indexOf("*") != -1 || string.indexOf("&") != -1
				|| string.indexOf("/") != -1 || string.indexOf("\\") != -1
				|| string.indexOf("\"") != -1 || string.indexOf(":") != -1) {
			// 如果包含特殊字符
			return true;
		} else {
			return false;
		}
	}

	public static String listToString(List<String> strList) {

		String str = "";

		if (null == strList || strList.isEmpty()) {

			return str;
		}

		for (String s : strList) {

			str += (s + FusionConfig.DB_COLUMN_SEPARATE);
		}

		if (str.endsWith(FusionConfig.DB_COLUMN_SEPARATE)) {

			str = str.substring(0, str.length() - 1);
		}

		return str;
	}

	public static List<String> StringToList(String str) {

		if (TextUtils.isEmpty(str)) {

			return new ArrayList<String>();
		}

		String[] strs = str.split(FusionConfig.DB_COLUMN_SEPARATE);

		if (null == strs || strs.length == 0) {

			return new ArrayList<String>();
		}

		return new ArrayList<String>(Arrays.asList(strs));
	}

	public static String getDistance(double longitude1, double latitude1,
			double longitude2, double latitude2) {

		double Lat1 = rad(latitude1);

		double Lat2 = rad(latitude2);

		double a = Lat1 - Lat2;

		double b = rad(longitude1) - rad(longitude2);

		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(Lat1) * Math.cos(Lat2)
				* Math.pow(Math.sin(b / 2), 2)));

		s = s * 6378137.0;

		s = Math.round(s * 10000) / 10000;

		return String.format("%.1f", s / 1000);
	}

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

}
