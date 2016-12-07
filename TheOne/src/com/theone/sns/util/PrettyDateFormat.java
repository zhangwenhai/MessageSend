package com.theone.sns.util;

import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class PrettyDateFormat extends SimpleDateFormat {

	private static final long serialVersionUID = 1L;

	private static final String TAG = "PrettyDateFormat";

	private static final String ISO_8601_PARSE_STRING = "yyyy-MM-dd'T'HH:mm:ss";

	private static final String GET_ISO_8601_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	private Pattern pattern = Pattern.compile("('*)(#{1,2}|@)");

	private final static int[] dayArr = new int[] { 20, 19, 21, 20, 21, 22, 23,
			23, 23, 24, 23, 22 };

	private final static String[] constellationArr = new String[] { "摩羯座",
			"水瓶座", "双鱼座", "牡羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座",
			"天蝎座", "射手座", "摩羯座" };

	private FormatType formatType = FormatType.DEAFULT;

	private SimpleDateFormat simpleDateFormat;

	private enum FormatType {
		DEAFULT, TIME, DAY
	};

	public static String formatISO8601Time(String time) {

		if (TextUtils.isEmpty(time)) {

			return "";
		}

		try {

			SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_PARSE_STRING);

			sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));

			return new PrettyDateFormat("@", "yyyy-MM-dd").format(sdf
					.parse(time));

		} catch (ParseException e) {

			Log.e(TAG, "PrettyDateFormat ParseException");
		}

		return "";
	}

	public static String getISO8601Time() {

		try {

			SimpleDateFormat sdf = new SimpleDateFormat(GET_ISO_8601_STRING);

			sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));

			return sdf.format(new Date());

		} catch (Exception e) {

			Log.e(TAG, "getISO8601Time exception");
		}

		return "";
	}

	public static String formatISO8601TimeForChat(String time) {

		if (TextUtils.isEmpty(time)) {

			return "";
		}

		SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");

		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

		String nowMonth = sdf2.format(new Date());

		try {

			SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_PARSE_STRING);

			sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));

			Date date = sdf.parse(time);

			String chatTime = sdf1.format(date);

			String chatMonth = sdf2.format(date);

			if (nowMonth.equals(chatMonth)) {

				return chatTime;

			} else {

				return chatMonth;
			}

		} catch (ParseException e) {

			Log.e(TAG, "formatISO8601TimeForChat ParseException");
		}

		return "";
	}

	public static boolean isShowTimeForChat(String time1, String time2) {

		if (TextUtils.isEmpty(time1) || TextUtils.isEmpty(time2)) {

			return true;
		}

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd:HH");
		SimpleDateFormat minsdf = new SimpleDateFormat("mm");

		try {

			SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_PARSE_STRING);
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
			Date date1 = sdf.parse(time1);
			Date date2 = sdf.parse(time2);

			String str1 = sdf1.format(date1);
			String str2 = sdf1.format(date2);

			if (!str1.equals(str2)) {

				return true;

			} else {

				int min1 = Integer.valueOf(minsdf.format(date1));

				int min2 = Integer.valueOf(minsdf.format(date2));

				if (Math.abs(min1 - min2) >= 5) {

					return true;
				}
			}

		} catch (Exception e) {

			Log.e(TAG, "isShowTimeForChat ParseException");
		}

		return false;
	}

	public static String getBirthday(String birthdayTime) {

		if (TextUtils.isEmpty(birthdayTime)) {

			return "";
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {

			Date birthdayDate = new SimpleDateFormat(ISO_8601_PARSE_STRING)
					.parse(birthdayTime);

			return sdf.format(birthdayDate);

		} catch (ParseException e) {

			Log.e(TAG, "PrettyDateFormat ParseException");
		}

		return "";
	}

	public static long getTimeMillis(String time) {

		long timeMillis = -1L;

		if (TextUtils.isEmpty(time)) {

			return timeMillis;
		}

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmssSSS");

		try {

			SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_PARSE_STRING);

			sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));

			Date date = sdf.parse(time);

			timeMillis = Long.parseLong(sdf1.format(date));

		} catch (Exception e) {

			Log.e(TAG, "getTimeMillis error: " + time);
		}

		return timeMillis;
	}

	/**
	 * 根据月和日获取星座
	 */
	public static String getConstellation(int month, int day) {

		return day < dayArr[month - 1] ? constellationArr[month - 1]
				: constellationArr[month];
	}

	/**
	 * format 中的@表示[XXX秒前,XXX分钟前,XXX小时前(最多是23小时前)]
	 * <p>
	 * format中的#表示[空字串(表示今天),昨天,前天]
	 * <p>
	 * format中的##表示[今天,昨天,前天]
	 *
	 * @param format
	 *            和SimpleDateFormat中的格式设置基本上是一样的,只是多的@格 式 #格式和##格式
	 * @param fullFormat
	 *            和 SimpleDateFormat中的格式设置是一样的
	 */
	public PrettyDateFormat(String format, String fullFormat) {

		super(fullFormat);

		Matcher m = pattern.matcher(format);

		while (m.find()) {

			if (m.group(1).length() % 2 == 0) {
				if ("@".equals(m.group(2))) {

					if (formatType == FormatType.DAY) {

						throw new IllegalArgumentException("#和@模式字符不能同时使用.");
					}

					formatType = FormatType.TIME;

				} else {

					if (formatType == FormatType.TIME) {

						throw new IllegalArgumentException("#和@模式字符不能同时使用.");
					}

					formatType = FormatType.DAY;
				}
			}
		}

		this.simpleDateFormat = new SimpleDateFormat(format.replace("'", "''"));
	}

	public StringBuffer format(Date date, StringBuffer toAppendTo,
			FieldPosition pos) {

		if (formatType == FormatType.DEAFULT) {

			return super.format(date, toAppendTo, pos);
		}

		long curTime = System.currentTimeMillis();

		long diffDay = 0L;

		long diffSecond = 0L;

		if (formatType == FormatType.TIME) {

			diffSecond = (curTime - date.getTime()) / 1000L;

			if (diffSecond < 0 || diffSecond >= 86400) {

				return super.format(date, toAppendTo, pos);
			}
		}

		if (formatType == FormatType.DAY) {

			Calendar curDate = new GregorianCalendar();

			curDate.setTime(new Date(curTime));

			curDate.set(Calendar.HOUR_OF_DAY, 23);

			curDate.set(Calendar.MINUTE, 59);

			curDate.set(Calendar.SECOND, 59);

			curDate.set(Calendar.MILLISECOND, 999);

			diffDay = (curDate.getTimeInMillis() - date.getTime()) / 86400000L;

			if (diffDay < 0 || diffDay > 2) {

				return super.format(date, toAppendTo, pos);
			}
		}

		StringBuffer sb = new StringBuffer();

		Matcher m = pattern.matcher(simpleDateFormat.format(date));

		if (m.find()) {

			String group2 = m.group(2);

			String replacement = "";

			while (true) {

				if ("@".equals(group2)) {

					if (diffSecond < 60) {

						replacement = diffSecond == 0 ? "1秒前" : diffSecond
								+ "秒前";

					} else if (diffSecond < 3600) {

						replacement = diffSecond / 60 + "分钟前";

					} else if (diffSecond < 86400) {

						replacement = diffSecond / 3600 + "小时前";
					}

				} else {

					if (diffDay == 0) {

						replacement = group2.length() == 2 ? "今天" : "";

					} else if (diffDay == 1) {

						replacement = "昨天";

					} else {

						replacement = "前天";
					}
				}

				m.appendReplacement(sb, replacement);

				if (!m.find()) {

					break;
				}
			}

			m.appendTail(sb);
		}

		return toAppendTo.append(sb.toString());
	}

	@Override
	public Object parseObject(String source, ParsePosition pos) {
		throw new UnsupportedOperationException("暂时还不支持的操作");
	}
}