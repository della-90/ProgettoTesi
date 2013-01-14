package it.unibo.ing2.jade.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class TucsonBindingsParser {
	
	protected BufferedReader reader;
	protected static TucsonBindingsParser instance = null;
	
	public TucsonBindingsParser(String filePath) throws FileNotFoundException{
		reader = new BufferedReader(new FileReader(filePath));
	}
	
	public static Map<String, InetSocketAddress> parse(String filePath) throws FileNotFoundException, IOException {
		instance = new TucsonBindingsParser(filePath);
		return instance.parse();
		
	}
	
	private Map<String, InetSocketAddress> parse() throws IOException{
		Map<String, InetSocketAddress> result = new HashMap<>();
		
		while (reader.ready()){
			String line = reader.readLine();
			try {
				String tcName = extractName(line);
				InetSocketAddress addr = extractAddress(line);
				
				result.put(tcName, addr);
				System.out.println("[TucsonBindingParser] Aggiunto nodo "+tcName+" con indirizzo "+addr);
			} catch (Exception e) {
				if (e instanceof IOException){
					throw (IOException) e;
				} else {
					//TODO: better handling of this exception?
					System.err.println("Errore nel parsing di "+line+". Errore: "+e.getMessage());
				}
			}
		}
		
		return result;
	}
	
	public String extractName(String line) throws Exception{
		String result = null;
		result = line.substring(0, line.indexOf("@"));
		return result;
	}
	
	public InetSocketAddress extractAddress(String line) throws Exception{
		InetSocketAddress result = null;
		int portno;
		String ip;
		String socketAddress = line.substring(line.indexOf("@")+1);
		try {
			ip = socketAddress.substring(0, socketAddress.indexOf(":"));
			String port = socketAddress.substring(socketAddress.indexOf(":")+1);
			portno = Integer.parseInt(port);
		} catch (StringIndexOutOfBoundsException ex){
			//Il numero di porta non è specificato! Uso quello di default
			portno = 20504;
			
			//Ottengo l'ip
			ip = socketAddress.substring(0);
		}
		result = new InetSocketAddress(ip, portno);
		return result;
	}

}
