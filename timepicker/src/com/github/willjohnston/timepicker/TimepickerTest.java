package com.github.willjohnston.timepicker;

import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;

import org.junit.Test;

public class TimepickerTest {

	@Test
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
		
		tp.parse("@d/UTC");
		tp.parse("@d/NY");
		tp.parse("@d/LN");
		tp.parse("@d/BD");
		tp.parse("@d/IN");
		tp.parse("@d/SG");
		tp.parse("@d/HK");
		tp.parse("@d/SH");
		tp.parse("@d/SE");
		tp.parse("@d/TK");
		tp.parse("@d/SY");

		tp.parse("@d+1h/UTC");
		tp.parse("@d-1h/UTC");

		tp.parse("+1w@w5+18h/NY");
		tp.parse("+1w@w1+8h/TK");

		
		System.out.println(tp.parse("1973-09-11:04:56"));
		System.out.println(tp.parse("1973-09-11"));

//		fail("Not yet implemented");
	}

}
