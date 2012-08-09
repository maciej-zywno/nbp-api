package pl.finapi;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.finapi.downloader.ExchangeRateFilesDownloader;
import pl.finapi.model.Day;
import pl.finapi.xml.parser.TableXmlsParser;

@Component
public class DownloaderThread {

	private final long period = 4 * 60 * 60 * 1000; // every 4 hours

	private final ExchangeRateFilesDownloader downloader;
	private final TableXmlsParser tableXmlsParser;
	private final ExchangeRateRepository repository;

	@Autowired
	public DownloaderThread(ExchangeRateFilesDownloader downloader, TableXmlsParser tableXmlsParser, ExchangeRateRepository repository) {
		this.downloader = downloader;
		this.tableXmlsParser = tableXmlsParser;
		this.repository = repository;
	}

	@SuppressWarnings("unused")
	// invoked by spring at startup time
	private void schedule() {
		TimerTask task = new TimerTask() {
			private volatile boolean firstRun = true;

			@Override
			public void run() {
				try {
					if (firstRun) {
						firstRun = false;
						Pair<Map<Day, Map<String, Double>>, Map<Day, String>> mapsPair = tableXmlsParser.parse(Constants.tableAFilesDir);
						repository.updateDayToExchangeRateByCurrencyMap(mapsPair.getLeft());
						repository.updateDayToTableNameMap(mapsPair.getRight());
					}
					downloader.downloadFiles();
					Pair<Map<Day, Map<String, Double>>, Map<Day, String>> freshMapsPair = tableXmlsParser.parse(Constants.tableAFilesDir);
					repository.updateDayToExchangeRateByCurrencyMap(freshMapsPair.getLeft());
					repository.updateDayToTableNameMap(freshMapsPair.getRight());
				} catch (RuntimeException e) {
					// nothing
				}
			}
		};
		// at startup time initialize with data already stored on disk
		new Timer().scheduleAtFixedRate(task, 0, period);
	}

}
