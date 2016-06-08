package com.github.willjohnston.timepicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

public class TimepickerTest {

//	@Test
	public void testOffset() {
		Timepicker tp = new Timepicker(new Date());

		for (int i = (-12 * 60); i < (12 * 60) ; i += 60) {
			tp.parse("@d", i);
			System.out.println(String.format("%d -> %s\n", i, tp.offsetMinToTimeZone(i).getID()));
		}
		
		
//		tp.parse("+1h");
//		tp.parse("-1h");		
	}

//	@Test
	public void getIds() {
		for (String id : TimeZone.getAvailableIDs()) {
			System.out.println(id + " -> " + TimeZone.getTimeZone(id));
		}		
	}

	@Test
	public void getIds2() {
		Timepicker tp = new Timepicker(new Date());
		tp.parse("@d",0);
		tp.parse("@d,GMT",0);
		tp.parse("@d,LN",0);
		tp.parse("@d,UTC",0);

		tp.parse("@d,Japan",0);
		tp.parse("@d,TK",0);

		tp.parse("@d,IST",0);
		tp.parse("@d,IN",0);

		tp.parse("@d,America/New_York",0);
		tp.parse("@d,EST",0);
		tp.parse("@d,NY",0);

	}
	
//	@Test
	public void testParse() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		
		Timepicker tp = new Timepicker();

		tp.parse("");
		tp.parse("+1h");
		tp.parse("-1h");
		tp.parse("+1d");
		tp.parse("-1d");
		tp.parse("+1w");
		tp.parse("-1w");
		tp.parse("+1m");
		tp.parse("-1m");
		tp.parse("+1y");
		tp.parse("-1y");

		tp.parse("@h");
		tp.parse("@d");
		tp.parse("@w");
		tp.parse("@m");
		tp.parse("@y");

		tp.parse("@w0");
		tp.parse("@w1");
		tp.parse("@w2");
		tp.parse("@w3");
		tp.parse("@w4");
		tp.parse("@w5");
		tp.parse("@w6");
		tp.parse("@w7");

		tp.parse("@d+1h");
		tp.parse("@d-1h");
		tp.parse("@d+1d");
		tp.parse("@d-1d");
		tp.parse("@d+1w");
		tp.parse("@d-1w");
		tp.parse("@d+1m");
		tp.parse("@d-1m");
		tp.parse("@d+1y");
		tp.parse("@d-1y");

		tp.parse("@d+1h");
		tp.parse("@d-1h");
		tp.parse("@d+1d");
		tp.parse("@d-1d");
		tp.parse("@d+1w");
		tp.parse("@d-1w");
		tp.parse("@d+1m");
		tp.parse("@d-1m");
		tp.parse("@d+1y");
		tp.parse("@d-1y");
		
		tp.parse("@d,UTC");
		tp.parse("@d,NY");
		tp.parse("@d,LN");
		tp.parse("@d,BD");
		tp.parse("@d,IN");
		tp.parse("@d,SG");
		tp.parse("@d,HK");
		tp.parse("@d,SH");
		tp.parse("@d,TK");
		tp.parse("@d,SY");

		tp.parse("@d+1h,UTC");
		tp.parse("@d-1h,UTC");

		tp.parse("+1w@w5+18h,NY");
		tp.parse("+1w@w1+8h,TK");

		// should implement /TZ for these...
		System.out.println(tp.parse("1973-09-11:04:56"));
		System.out.println(tp.parse("1973-09-11:04:56,PST"));
		System.out.println(tp.parse("1973-09-11"));

//		fail("Not yet implemented");
	}

}
