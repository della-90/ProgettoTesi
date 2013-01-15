package it.unibo.ing2.jade.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TucsonMappingsParser {

	protected BufferedReader reader;
	protected static TucsonMappingsParser instance = null;

	public TucsonMappingsParser(String filePath) throws FileNotFoundException {
		reader = new BufferedReader(new FileReader(filePath));
	}

	public static Map<String, InetSocketAddress> parse(String filePath)
			throws FileNotFoundException, IOException {
		instance = new TucsonMappingsParser(filePath);
		return instance.parse();

	}

	private Map<String, InetSocketAddress> parse() throws IOException {
		Map<String, InetSocketAddress> result = new HashMap<>();

		while (reader.ready()) {
			String line = reader.readLine();

			if (!checkLine(line)) {
				System.err.println("La riga " + line + " non è corretta.");
				continue;
			}

			String nodeName = extractName(line);
			InetSocketAddress address = extractAddress(line);
			System.out.println("[TucsonBindingdsParser]: aggiunto nodo "+nodeName+" all'indirizzo "+address);
			result.put(nodeName, address);
		}

		return result;
	}

	private boolean checkLine(String line) {
		StringBuilder builder = new StringBuilder("");

		// Il nome del tuple centre (opzionale)
		builder.append("^[a-zA-Z0-9àòùèé]*");

		// Il separatore fra nome e IP (obbligatorio)
		builder.append("@");

		/*
		 * L'indirizzo IP. Ognuno dei 4 byte può essere nella forma: 0XX XX X
		 * 1XX 20X-24X 250-255
		 */
		builder.append("(localhost|(([01]?\\d{1,2}|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d{1,2}|2[0-4]\\d|25[0-5]))");

		// Il separatore fra IP e porta
		builder.append("(:");

		/*
		 * Il numero di porta. Può essere nelle forme Y YY YYY YYYY 1YYYY-5YYYY
		 * 60000-65535
		 */
		builder.append("(6553[0-5]|655[0-2]\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[1-5]\\d{4}|[1-9]\\d{0,3})");

		// Chiudo la parentesi del separatore fra IP e porta
		builder.append(")?");

		Pattern pattern = Pattern.compile(builder.toString());
		Matcher matcher = pattern.matcher(line);

		return matcher.find();
	}

	private String extractName(String line)
			throws StringIndexOutOfBoundsException {
		String result = null;
		StringBuilder builder = new StringBuilder("");

		// Il nome del tuple centre (opzionale)
		builder.append("^[a-zA-Z0-9àòùèé]*");

		Pattern pattern = Pattern.compile(builder.toString());
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			result = matcher.group();
		} else {
			result = "default";
		}
		return result;
	}

	private InetSocketAddress extractAddress(String line) {
		InetSocketAddress result = null;
		int portno;
		String ip;
		StringBuilder builder = new StringBuilder("");

		/*
		 * L'indirizzo IP. Ognuno dei 4 byte può essere nella forma: 0XX XX X
		 * 1XX 20X-24X 250-255
		 */
		builder.append("(localhost|(([01]?\\d{1,2}|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d{1,2}|2[0-4]\\d|25[0-5]))");
		Pattern ipPattern = Pattern.compile(builder.toString());
		Matcher ipMatcher = ipPattern.matcher(line);

		builder = new StringBuilder("");
		// Il separatore fra IP e porta
		builder.append("(:");

		/*
		 * Il numero di porta. Può essere nelle forme Y YY YYY YYYY 1YYYY-5YYYY
		 * 60000-65535
		 */
		builder.append("(6553[0-5]|655[0-2]\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[1-5]\\d{4}|[1-9]\\d{0,3})");

		// Chiudo la parentesi del separatore fra IP e porta
		builder.append(")");

		Pattern portPattern = Pattern.compile(builder.toString());
		Matcher portMatcher = portPattern.matcher(line);

		// Cerco l'IP. C'è di sicuro perché ho controllato prima
		ipMatcher.find();
		int ipEnd = ipMatcher.end();
		ip = ipMatcher.group();

		//Cerco se c'è la porta
		if (portMatcher.find(ipEnd)){
			String sPortno = portMatcher.group();
			portno = Integer.parseInt(sPortno.substring(1));
		} else {
			portno = 20504;
		}

		result = new InetSocketAddress(ip, portno);
		return result;
	}

}
