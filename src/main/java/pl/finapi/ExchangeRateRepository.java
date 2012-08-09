package pl.finapi;

import java.text.ParseException;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.finapi.model.Day;
import pl.finapi.util.DateUtil;

@Component
public class ExchangeRateRepository {

	private final DateUtil dateUtil;

	private volatile Map<Day, Map<String, Double>> dayToExchangeRateByCurrencyMap;
	private volatile Map<Day, String> dayToTableNameMap;

	@Autowired
	public ExchangeRateRepository(DateUtil dateUtil) {
		this.dateUtil = dateUtil;
	}

	public String findRate(String currencyCode, String date) {
		throwIfNoDataYet();
		Day day = parseDay(date);
		Map<String, Double> mapForDay = dayToExchangeRateByCurrencyMap.get(day);
		Double rate = mapForDay.get(currencyCode);
		String rateAsString = Double.toString(rate);
		return rateAsString;
	}

	public String findTableName(String date) {
		throwIfNoDataYet();
		Day day = parseDay(date);
		String tableName = dayToTableNameMap.get(day);
		return tableName;
	}

	public void updateDayToExchangeRateByCurrencyMap(Map<Day, Map<String, Double>> map) {
		this.dayToExchangeRateByCurrencyMap = map;
	}

	public void updateDayToTableNameMap(Map<Day, String> map) {
		this.dayToTableNameMap = map;
	}

	private void throwIfNoDataYet() {
		if (dayToExchangeRateByCurrencyMap == null) {
			throw new RuntimeException("no data yet");
		}
	}

	private Day parseDay(String dateAsString) {
		try {
			return dateUtil.toDay(dateAsString);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public String asString() {
		StringBuffer buffer = new StringBuffer();
		for (Entry<Day, Map<String, Double>> entry : dayToExchangeRateByCurrencyMap.entrySet()) {
			buffer.append(entry.getKey());
			for (Entry<String, Double> currencyAndRate : entry.getValue().entrySet()) {
				buffer.append(asString(currencyAndRate));
			}
			buffer.append("<br/>");
			buffer.append("<br/>");
		}
		return buffer.toString();
	}

	private String asString(Entry<String, Double> currencyAndRate) {
		return currencyAndRate.getKey() + "=" + currencyAndRate.getValue();
	}

}
