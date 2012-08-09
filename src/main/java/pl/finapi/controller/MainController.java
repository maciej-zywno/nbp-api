package pl.finapi.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pl.finapi.ExchangeRateRepository;

@Controller
public class MainController {

	private final Logger log = Logger.getLogger(this.getClass());
	private final ExchangeRateRepository exchangeRateRepository;

	@Autowired
	public MainController(ExchangeRateRepository exchangeRateRepository) {
		this.exchangeRateRepository = exchangeRateRepository;
	}

	@RequestMapping(value = "/")
	public void processRequest(@RequestParam(value = "waluta") String currencyCode, @RequestParam(value = "data") String date,
			HttpServletResponse response) throws IOException {
		try {
			String rateAsString = exchangeRateRepository.findRate(currencyCode, date);
			String tableName = exchangeRateRepository.findTableName(date);
			response.getWriter().write(rateAsString + "," + tableName);
		} catch (RuntimeException e) {
			response.getWriter().write("Nie znaleziono kursu dla waluty '" + currencyCode + "' z dnia '" + date + "'. ");
			response.getWriter().write("Wymagany format: 'http://finapi.pl/?waluta=EUR&data=2011-12-26'.");
			e.printStackTrace(response.getWriter());
			log.error(logMessage(currencyCode, date), e);
		}
		response.getWriter().flush();
	}

	private String logMessage(String currencyCode, String date) {
		return "currency='" + currencyCode + "', date='" + date + "'";
	}

}
