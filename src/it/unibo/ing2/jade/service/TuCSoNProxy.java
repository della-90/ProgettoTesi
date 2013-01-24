package it.unibo.ing2.jade.service;

import it.unibo.ing2.jade.exceptions.TucsonNodeNotFoundException;
import jade.core.GenericCommand;
import jade.core.IMTPException;
import jade.core.Node;
import jade.core.ServiceException;
import jade.core.SliceProxy;

import java.net.InetSocketAddress;

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
	
	@Override
	public void addTupleCentre(String tupleCentreName, String netId, int portno) throws IMTPException,
			ServiceException, IllegalArgumentException {
		System.out.println("[TuCSoNProxy] addTupleCentre: "+tupleCentreName);
		
		
		InetSocketAddress addr = new InetSocketAddress(netId, portno);
		//Creo il cmd orizzontale
		GenericCommand cmd = new GenericCommand(TuCSoNSlice.H_ADDTUPLECENTRE, TuCSoNService.NAME, null);
		cmd.addParam(tupleCentreName);
		cmd.addParam(addr);
		
		Node node = getNode();
		node.accept(cmd);
		
	}
	
	@Override
	public void removeTupleCentre(String tupleCentreName)
			throws IMTPException, ServiceException {
		
		System.out.println("[TuCSoNProxy] addTupleCentre: "+tupleCentreName);

		GenericCommand cmd = new GenericCommand(TuCSoNSlice.H_REMOVETUPLECENTRE, TuCSoNService.NAME, null);
		cmd.addParam(tupleCentreName);
		
		Node node = getNode();
		node.accept(cmd);
		
	}

}
