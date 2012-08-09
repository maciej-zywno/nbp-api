package pl.finapi.model;

import java.util.List;

public class TableData {

	private final Day day;
	private final String tableName;
	private final List<Item> items;

	public TableData(Day day, String tableName, List<Item> items) {
		this.day = day;
		this.tableName = tableName;
		this.items = items;
	}

	public Day getDay() {
		return day;
	}

	public String getTableName() {
		return tableName;
	}

	public List<Item> getItems() {
		return items;
	}
}
