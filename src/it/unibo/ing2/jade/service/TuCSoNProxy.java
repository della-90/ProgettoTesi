package it.unibo.ing2.jade.service;

import jade.core.GenericCommand;
import jade.core.IMTPException;
import jade.core.Node;
import jade.core.ServiceException;
import jade.core.SliceProxy;
import alice.tucson.api.TucsonTupleCentreId;

public class TuCSoNProxy extends SliceProxy implements TuCSoNSlice {
	
	@Override
	public TucsonTupleCentreId findTupleCentre(String tupleCentreName) throws IMTPException, ServiceException {

		//Creo il comando orizzontale
		GenericCommand cmd = new GenericCommand(TuCSoNSlice.H_FINDTUPLECENTRE, TuCSoNService.NAME, null);
		cmd.addParam(tupleCentreName);
		Node node = getNode();
		Object result = node.accept(cmd);
	
		//restituisco il risultato
		return (TucsonTupleCentreId)result;
	}

}
