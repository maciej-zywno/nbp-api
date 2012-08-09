package pl.finapi.xml.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import pl.finapi.model.Day;
import pl.finapi.model.Item;
import pl.finapi.model.TableData;
import pl.finapi.util.DateUtil;

@Component
public class TableXmlsParser {

	private final DateUtil dateUtil;

	@Autowired
	public TableXmlsParser(DateUtil dateUtil) {
		this.dateUtil = dateUtil;
	}

	public Pair<Map<Day, Map<String, Double>>, Map<Day, String>> parse(File tableFileDir) {
		Map<Day, Map<String, Double>> dayToExchangeRateByCurrency = new TreeMap<>();
		Map<Day, String> dayToTableName = new TreeMap<>();

		for (File file : tableFileDir.listFiles()) {
			TableData tableData = parseOneTableFile(file);
			Map<String, Double> oneDayMap = new HashMap<>();
			for (Item item : tableData.getItems()) {
				oneDayMap.put(item.getCurrencyCode(), item.getAverageRate());
			}
			dayToExchangeRateByCurrency.put(tableData.getDay(), oneDayMap);
			dayToTableName.put(tableData.getDay(), tableData.getTableName());
		}
		return new ImmutablePair<Map<Day, Map<String, Double>>, Map<Day, String>>(dayToExchangeRateByCurrency, dayToTableName);
	}

	public static void main(String[] args) {
		DateUtil util = new DateUtil();
		TableXmlsParser parser = new TableXmlsParser(util);
		print(parser.parseOneTableFile(new File("C:/dev/finapi/a/a020z020129.xml")));
		print(parser.parseOneTableFile(new File("C:/dev/finapi/a/a052z120314.xml")));
		Pair<Map<Day, Map<String, Double>>, Map<Day, String>> pair = parser.parse(new File("C:/dev/finapi/a"));
		Map<Day, Map<String, Double>> left = pair.getLeft();
		System.out.println(left.containsKey(new Day(2012, 3, 14)));
	}

	private static void print(TableData tableData) {
		System.out.println(tableData.getDay());
		for (Item entry : tableData.getItems()) {
			System.out.print(entry.getCurrencyCode() + ",");
		}
		System.out.println();
	}

	private TableData parseOneTableFile(File file) {
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			TableDataSaxHandler handler = new TableDataSaxHandler(dateUtil);
			InputSource inputSource = new InputSource(new FileReader(file));
			reader.setContentHandler(handler);
			reader.parse(inputSource);

			return handler.getTableData();
		} catch (SAXException | IOException e) {
			throw new RuntimeException(e);
		}
	}

}
