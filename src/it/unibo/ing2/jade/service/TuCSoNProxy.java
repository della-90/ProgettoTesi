package it.unibo.ing2.jade.service;

import it.unibo.ing2.jade.exceptions.TucsonNodeNotFoundException;

import java.net.InetSocketAddress;

import jade.core.GenericCommand;
import jade.core.IMTPException;
import jade.core.Node;
import jade.core.ServiceException;
import jade.core.SliceProxy;
import alice.tucson.api.TucsonTupleCentreId;

public class TuCSoNProxy extends SliceProxy implements TuCSoNSlice {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Object findTupleCentre(String tupleCentreName)
			throws IMTPException, ServiceException {
		System.out.println("[TuCSoNProxy] findTupleCentre");
		// Creo il comando orizzontale
		GenericCommand cmd = new GenericCommand(TuCSoNSlice.H_FINDTUCSONNODE,
				TuCSoNService.NAME, null);
		cmd.addParam(tupleCentreName);
		Node node = getNode();
		Object result = node.accept(cmd);

		if (result == null) {
			result = new TucsonNodeNotFoundException("No tucson node with name "+tupleCentreName);
		}
		
		// restituisco il risultato
		return result;
	}

//	@Override
//	public void executeSynch(TucsonAction action, Long timeout) throws ServiceException, IMTPException {
//		System.out.println("[TuCSoNProxy] executeSynch");
//		
//		GenericCommand cmd = new GenericCommand(TuCSoNSlice.H_EXECUTE_SYNCH, TuCSoNService.NAME, null);
//		cmd.addParam(action);
//		cmd.addParam(timeout);
//		getNode().accept(cmd);
//	}
//
//	@Override
//	public void executeAsynch(TucsonAction action,
//			TucsonOperationCompletionListener listener) throws IMTPException, ServiceException {
//		System.out.println("[TuCSoNProxy] executeAsynch");
//		GenericCommand cmd = new GenericCommand(TuCSoNSlice.H_EXECUTE_ASYNCH, TuCSoNService.NAME, null);
//		cmd.addParam(action);
//		cmd.addParam(listener);
//		getNode().accept(cmd);
//		
//	}

}
