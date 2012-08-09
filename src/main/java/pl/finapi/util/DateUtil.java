package pl.finapi.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Component;

import pl.finapi.model.Day;

@Component
public class DateUtil {

	private final DateTimeZone dateTimeZone = DateTimeZone.forID("Europe/Warsaw");
	// 2003-01-02
	private final DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

	public Day toDay(String dateAsString) throws ParseException {
		return toDay(dateFormatter.parse(dateAsString));
	}

	private Day toDay(Date date) {
		DateTime dateTime = new DateTime(date, dateTimeZone);
		return new Day(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
	}

}
