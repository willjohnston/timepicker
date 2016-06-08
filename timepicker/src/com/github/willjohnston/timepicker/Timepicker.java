package com.github.willjohnston.timepicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Timepicker {
	private static final String RELATIVE_REGEX = "(([+\\-]\\d+)([hdwmy]))?(@([hdmy]|w([0-7])?))?(([+\\-]\\d+)([hdwmy]))?";
	private static final Pattern patternRelative = Pattern.compile(RELATIVE_REGEX);

	private static final Map<String, TimeZone> TZ_MAP = new HashMap<String, TimeZone>();
	static {
		TZ_MAP.put("NY", TimeZone.getTimeZone("America/New_York"));
		TZ_MAP.put("UTC", TimeZone.getTimeZone("UTC"));
		TZ_MAP.put("LN", TimeZone.getTimeZone("Europe/London"));
		TZ_MAP.put("BD", TimeZone.getTimeZone("Europe/Budapest"));
		TZ_MAP.put("IN", TimeZone.getTimeZone("Asia/Kolkata"));
		TZ_MAP.put("SG", TimeZone.getTimeZone("Asia/Singapore"));
		TZ_MAP.put("SH", TimeZone.getTimeZone("Asia/Shanghai"));
		TZ_MAP.put("HK", TimeZone.getTimeZone("Asia/Hong_Kong"));
		TZ_MAP.put("TK", TimeZone.getTimeZone("Asia/Tokyo"));
		TZ_MAP.put("SY", TimeZone.getTimeZone("Australia/Sydney"));
	}

	private long now;

	public Timepicker() {
		this.now = 0;
	}
	
	public Timepicker(long now) {
		this.now = now;
	}

	public Timepicker(Date now) {
		this.now = now.getTime();
	}

	private long getNow() {
		return now > 0 ? now : System.currentTimeMillis();
	}

	public Date parse(String input) {
		return parse(input, TimeZone.getDefault().getRawOffset()/60000);
	}

	public Date parse(String input, long offsetMinutes) {
		// first get the timezone, either directly from the input or calculated from offsetMinutes
		TimeZone tz;
		Pattern tzPattern = Pattern.compile("(.*),(\\S+)");
		Matcher tzMatcher = tzPattern.matcher(input);
		if (tzMatcher.matches()) {
			input = tzMatcher.group(1);
			String tzString = tzMatcher.group(2);

			tz = TZ_MAP.get(tzString);

			if (tz == null) {
				tz = TimeZone.getTimeZone(tzString);
			}

			if ("GMT".equals(tz.getID()) && !"GMT".equals(tzString)) {
				throw new IllegalArgumentException("unknown timezone: " + tzString);				
			}
		} else {
			tz = offsetMinToTimeZone(offsetMinutes);			
		}

		// create the calendar
		Calendar cal = Calendar.getInstance(tz);
		cal.setTimeInMillis(getNow());
		
		Matcher relative = patternRelative.matcher(input);
		if (relative.matches()) {
			
			if (relative.group(2) != null) {
				// pre-adjust the time
				int preAdjustAmount = Integer.parseInt(relative.group(2));
				int preAdjustUnit = getAdjustmentUnit(relative.group(3));
				cal.add(preAdjustUnit, preAdjustAmount);
			}

			if (relative.group(5) != null) {
				if (relative.group(5).startsWith("w")) {
					int dow = relative.group(6) == null ? 0 : Integer.parseInt(relative.group(6));
					cal.set(Calendar.DAY_OF_WEEK, dow + 1);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					if (cal.getTime().getTime() > getNow()) {
						cal.add(Calendar.DATE, -7);
					}
				} else {
					// roll down the units
					switch (relative.group(5)) {
					case "y":
						cal.set(Calendar.MONTH, 0);
					case "m":
						cal.set(Calendar.DATE, 1);
					case "d":
						cal.set(Calendar.HOUR_OF_DAY, 0);
					case "h":
						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.SECOND, 0);
						cal.set(Calendar.MILLISECOND, 0);
						break;
					default:
						throw new IllegalArgumentException("unknown time adjustment unit: " + relative.group(5));
					}
				}
			}

			if (relative.group(8) != null) {
				// post-adjust the time
				int postAdjustAmount = Integer.parseInt(relative.group(8));
				int postAdjustUnit = getAdjustmentUnit(relative.group(9));
				cal.add(postAdjustUnit, postAdjustAmount);
			}

			Date ret = cal.getTime();
			System.out.println(input + "\t" + ret + " " + ret.getTime());
			return ret;

		} else if (input.matches("\\d{4}-\\d{2}-\\d{2}:\\d{2}:\\d{2}")) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
				sdf.setTimeZone(tz);
				return sdf.parse(input);
			} catch (ParseException e) {
				throw new IllegalArgumentException(e);
			}
		} else if (input.matches("\\d{4}-\\d{2}-\\d{2}")) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				sdf.setTimeZone(tz);
				return sdf.parse(input);
			} catch (ParseException e) {
				throw new IllegalArgumentException(e);
			}
		} else {
			throw new IllegalArgumentException(input);
		}
	}
	
	public static TimeZone offsetMinToTimeZone(long offsetMin) {
		// look for TZ id's that match offset
		String[] idList = TimeZone.getAvailableIDs((int)(offsetMin * 60 * 1000));
		for (String id : idList) {
			// if some found, look for one that is found in TZ_MAP
			//   - use it
			for (TimeZone tz : TZ_MAP.values()) {
				if (id.equals(tz.getID())) {
					return tz;
				}
			}
		}

		//   - otherwise use the one that starts with Etc
		for (String id : idList) {
			if (id.matches("^Etc.+")) {
				return TimeZone.getTimeZone(id);
			}
		}
		
		//   - otherwise the first in the list
		if (idList.length > 0) {
			return TimeZone.getTimeZone(idList[0]);
		}

		String gmtFormat = String.format("GMT%s%02d%02d", (offsetMin < 0 ? "-" : "+"), Math.abs(offsetMin/60), Math.abs(offsetMin%60));
		TimeZone tz = TimeZone.getTimeZone(gmtFormat);
		if (tz != null) return tz;
		
		throw new IllegalArgumentException("can't find or construct TimeZone for offset: " + offsetMin);
	}

	private int getAdjustmentUnit(String unit) {
		switch (unit) {
		case "y":
			return Calendar.YEAR;
		case "m":
			return Calendar.MONTH;
		case "w":
			return Calendar.WEEK_OF_YEAR;
		case "d":
			return Calendar.DATE;
		case "h":
			return Calendar.HOUR;
		default:
			throw new IllegalArgumentException("unknown time adjustment unit: " + unit);
		}
	}
}
