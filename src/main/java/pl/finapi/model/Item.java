package pl.finapi.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Item {

	private final String country;
	private final String currencySymbol;
	private final Integer multiplier;
	private final String currencyCode;
	private final double averageRate;

	public Item(String country, String currencySymbol, Integer multiplier, String currencyCode, double averageRate) {
		this.country = country;
		this.currencySymbol = currencySymbol;
		this.multiplier = multiplier;
		this.currencyCode = currencyCode;
		this.averageRate = averageRate;
	}

	public String getCountry() {
		return country;
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public Integer getMultiplier() {
		return multiplier;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public double getAverageRate() {
		return averageRate;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
