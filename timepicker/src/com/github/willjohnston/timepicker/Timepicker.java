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
	private static final String RELATIVE_REGEX = "(([+\\-]\\d+)([hdwmy]))?(@([hdmy]|w([0-7])?))?(([+\\-]\\d+)([hdwmy]))?(/(NY|LN|BD|IN|SG|HK|SH|SE|TK|SY|UTC))?";
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
		TZ_MAP.put("SE", TimeZone.getTimeZone("Asia/Seoul"));
		TZ_MAP.put("TK", TimeZone.getTimeZone("Japan"));
		TZ_MAP.put("SY", TimeZone.getTimeZone("Australia/Sydney"));
	}

	// don't forget case-insensitive

	private long now;

	public Timepicker() {
		this.now = 0;

		// for (TimeZone tz : TZ_MAP.values()) {
		// System.out.println(tz.getID());
		// }
		// for (String id : TimeZone.getAvailableIDs(540 * 60 * 1000)) {
		// System.out.println(id);
		// }

	}

	// relative times use the provided "now" - if that's not provided, use the
	// parse time
	public Timepicker(long now) {
		this.now = now;
	}

	public Timepicker(Date now) {
		this.now = now.getTime();
	}

	public Date parse(String input) {
		return parse(input, 0);
	}

	public int getAdjustmentUnit(String unit) {
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

	private long getNow() {
		return now > 0 ? now : System.currentTimeMillis();
	}

	public Date parse(String input, long defaultOffset) {
		Matcher relative = patternRelative.matcher(input);
		if (relative.matches()) {
			String msg = String.format("MATCH [%s]: ", input);
			msg += String.format(" tz [%s]", relative.group(11));
			msg += String.format(" pre [%s,%s]", relative.group(2), relative.group(3));
			msg += String.format(" roll [%s|%s]", relative.group(5), relative.group(6));
			msg += String.format(" post [%s,%s]", relative.group(8), relative.group(9));
			// System.out.println(msg);

			// create the calendar, set it to now or the provided "now"
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(getNow());

			// set the TZ
			String tzString = relative.group(11);
			if (tzString == null) {
				// for now, we just use current TZ, but will need to lookup a TZ
				// using defaultOffset and comparing to TZ_MAP values
			} else if (TZ_MAP.containsKey(tzString)) {
				TimeZone tz = TZ_MAP.get(tzString);
				cal.setTimeZone(tz);
			} else {
				throw new IllegalArgumentException("unknown timezone: " + tzString);
			}

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
				return sdf.parse(input);
			} catch (ParseException e) {
				throw new IllegalArgumentException(e);
			}
		} else if (input.matches("\\d{4}-\\d{2}-\\d{2}")) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				return sdf.parse(input);
			} catch (ParseException e) {
				throw new IllegalArgumentException(e);
			}
		} else {
			throw new IllegalArgumentException(input);
		}
	}
}
