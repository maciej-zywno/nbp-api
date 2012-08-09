package pl.finapi.downloader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import pl.finapi.Constants;

@Component
public class ExchangeRateFilesDownloader {

	private final String baseUrl = "http://www.nbp.pl/kursy/xml";
	private final String fileNamesUrl = baseUrl + "/dir.txt";
	private final File fileNamesFile = new File(Constants.rootDir, "fileNames.txt");

	public void downloadFiles() {
		copyUrlToFile(fileNamesFile, newUrl(fileNamesUrl));
		makeDirs();
		List<String> existingFileNames = sum(Constants.tableAFilesDir.list(), Constants.tableBFilesDir.list(),
				Constants.tableCFilesDir.list(), Constants.tableHFilesDir.list());
		List<String> allRemoteFilesNames = readLines(fileNamesFile);
		List<String> missingFilesNames = filterMissing(existingFileNames, allRemoteFilesNames);
		for (String fileName : missingFilesNames) {
			File destinationFile = new File(getDirFor(fileName), withXmlExtension(fileName));
			System.out.println("will store " + destinationFile);
			copyUrlToFile(destinationFile, buildTableFileUrl(fileName));
		}
	}

	private List<String> filterMissing(List<String> existingFileNames, List<String> allRemoteFilesNames) {
		List<String> missingFilesNames = new ArrayList<>();
		for (String fileName : allRemoteFilesNames) {
			if (!existingFileNames.contains(fileName + ".xml")) {
				missingFilesNames.add(fileName);
			}
		}
		return missingFilesNames;
	}

	private List<String> sum(String[]... arrays) {
		List<String> sum = new ArrayList<>();
		for (String[] array : arrays) {
			List<String> list = Arrays.asList(array);
			sum.addAll(list);
		}
		return sum;
	}

	private File getDirFor(String fileName) {
		switch (fileName.charAt(0)) {
		case 'a':
			return Constants.tableAFilesDir;
		case 'b':
			return Constants.tableBFilesDir;
		case 'c':
			return Constants.tableCFilesDir;
		case 'h':
			return Constants.tableHFilesDir;
		default:
			throw new RuntimeException("unsupported file name '" + fileName + "'");
		}
	}

	private String withXmlExtension(String fileName) {
		return fileName + ".xml";
	}

	private List<String> readLines(File file) {
		try {
			return FileUtils.readLines(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private URL buildTableFileUrl(String fileName) {
		return newUrl(baseUrl + "/" + fileName + ".xml");
	}

	private URL newUrl(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private void copyUrlToFile(File destinationFile, URL url) {
		try {
			FileUtils.copyURLToFile(url, destinationFile);
		} catch (IOException e) {
			System.out.println("Could not download " + e.getMessage());
			// nothing
		}
	}

	private void makeDirs() {
		Constants.tableAFilesDir.mkdir();
		Constants.tableBFilesDir.mkdir();
		Constants.tableCFilesDir.mkdir();
		Constants.tableHFilesDir.mkdir();
	}
}
