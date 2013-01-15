package it.unibo.ing2.jade.coordination;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import alice.tucson.api.exceptions.TucsonGenericException;
import alice.tucson.service.TucsonNodeService;

public class TucsonNodeUtility {

	private static TucsonNodeService tns;

	// TODO nella versione attuale non è ancora implementato questo metodo
	public static boolean isTucsonNodeRunning(int port) throws IOException {
		boolean isInstalled = TucsonNodeService.isInstalled(port);
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

	public synchronized static void startTucsonNode(int port)
			throws TucsonGenericException {
		try {
			if (isTucsonNodeRunning(port)) {
				return;
			}
		} catch (IOException e) {
			System.err.println("[TucsonNodeUtility]: "+e);
			e.printStackTrace();
		}

		tns = new TucsonNodeService(port);
		tns.install();
	}

	public synchronized static void stopTucsonNode(int port) {
		try {
			if (!isTucsonNodeRunning(port)) {
				return;
			}
		} catch (IOException e) {
			System.err.println("[TucsonNodeUtility]: "+e);
			e.printStackTrace();
		}
		tns.shutdown();
	}

}
