package it.unibo.ing2.jade.coordination;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import alice.tucson.api.exceptions.TucsonGenericException;
import alice.tucson.service.TucsonNodeService;

public class TucsonNodeUtility {
	
	//TODO nella versione attuale non è ancora implementato questo metodo
	public static boolean isTucsonNodeRunning(int port) {
		boolean isInstalled = TucsonNodeService.isInstalled();
		if (!isInstalled) {
			SocketAddress addr = new InetSocketAddress(port);
			try {
				Socket s = new Socket();
				s.bind(addr);
				s.close();
			} catch (IOException e) {
				isInstalled = true;
			}
		}
		return isInstalled;
	}
	
	public static void startTucsonNode(int port) throws TucsonGenericException{
		if (isTucsonNodeRunning(port)){
			return;
		}
		
		TucsonNodeService tns = new TucsonNodeService(port);
		tns.install();
	}
	
	public static void stopTucsonNode(int port) {
		if (!isTucsonNodeRunning(port)){
			return;
		}
		// TODO nella versione attuale non è ancora implementato questo metodo
//		TucsonNodeService.getInstance().shutdown();
	}

}
