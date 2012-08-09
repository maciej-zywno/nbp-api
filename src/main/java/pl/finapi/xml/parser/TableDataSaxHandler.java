package pl.finapi.xml.parser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pl.finapi.model.Day;
import pl.finapi.model.Item;
import pl.finapi.model.TableData;
import pl.finapi.util.DateUtil;

@Component
public class TableDataSaxHandler extends DefaultHandler {


	/** START OF INTERNAL STATE FIELDS */

	private Day day;
	private String tableName;
	private final List<Item> items = new ArrayList<>();

	private boolean isTableName = false;
	private boolean isDate = false;
	private boolean isCountry = false;
	private boolean isCurrencySymbol = false;
	private boolean isMultiplier = false;
	private boolean isCurrencyCode = false;
	private boolean isAverageRate = false;

	// item state
	private String country;
	private String currencySymbol;
	private int multiplier;
	private String currencyCode;
	private double averageRate;

	private final DateUtil dateUtil;

	@Autowired
	public TableDataSaxHandler(DateUtil dateUtil) {
		this.dateUtil = dateUtil;
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("numer_tabeli")) {
			isTableName = true;
		}
		if (qName.equalsIgnoreCase("data_publikacji")) {
			isDate = true;
		}
		if (qName.equalsIgnoreCase("nazwa_kraju")) {
			isCountry = true;
		}
		if (qName.equalsIgnoreCase("symbol_waluty")) {
			isCurrencySymbol = true;
		}
		if (qName.equalsIgnoreCase("przelicznik")) {
			isMultiplier = true;
		}
		if (qName.equalsIgnoreCase("kod_waluty")) {
			isCurrencyCode = true;
		}
		if (qName.equalsIgnoreCase("kurs_sredni")) {
			isAverageRate = true;
		}
	}

	public void characters(char ch[], int start, int length) throws SAXException {
		if (isTableName) {
			tableName = new String(ch, start, length);
			isTableName = false;
		}
		if (isDate) {
			try {
				day = dateUtil.toDay(new String(ch, start, length));
			} catch (ParseException e) {
				// nothing
			}
			isDate = false;
		}
		if (isCountry) {
			country = new String(ch, start, length);
			isCountry = false;
		}
		if (isCurrencySymbol) {
			currencySymbol = new String(ch, start, length);
			isCurrencySymbol = false;
		}
		if (isMultiplier) {
			multiplier = Integer.parseInt(new String(ch, start, length));
			isMultiplier = false;
		}
		if (isCurrencyCode) {
			currencyCode = new String(ch, start, length);
			isCurrencyCode = false;
		}
		if (isAverageRate) {
			averageRate = Double.parseDouble(new String(ch, start, length).replace(',', '.'));
			isAverageRate = false;
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("pozycja")) {
			items.add(endItem());
		}
	}

	private Item endItem() {
		return new Item(country, currencySymbol, multiplier, currencyCode, averageRate);
	}

	public TableData getTableData() {
		return new TableData(day, tableName, items);
	}
}