package pl.finapi.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import pl.finapi.ExchangeRateRepository;

@Controller
public class HealthController {

	private final ExchangeRateRepository exchangeRateRepository;

	@Autowired
	public HealthController(ExchangeRateRepository exchangeRateRepository) {
		this.exchangeRateRepository = exchangeRateRepository;
	}

	@RequestMapping(value = "/health")
	public void processRequest(HttpServletResponse response) throws IOException {
		response.getWriter().write("<html><body>");
		response.getWriter().write(exchangeRateRepository.asString());
		response.getWriter().write("</body></html>");
		response.getWriter().flush();
	}

}
