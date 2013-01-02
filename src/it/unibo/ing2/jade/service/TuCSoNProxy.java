package it.unibo.ing2.jade.service;

import jade.core.GenericCommand;
import jade.core.IMTPException;
import jade.core.Node;
import jade.core.Service;
import jade.core.ServiceException;
import jade.core.SliceProxy;
import alice.tucson.api.TucsonTupleCentreId;

public class TuCSoNProxy extends SliceProxy implements TuCSoNSlice {

	@Override
	public TucsonTupleCentreId findTupleCentre(String tupleCentreName)
			throws IMTPException, ServiceException {

		System.out.println("[Proxy] findTupleCentre called");
		GenericCommand cmd = new GenericCommand(TuCSoNSlice.H_FINDTUPLECENTRE,
				TuCSoNService.NAME, null);
		cmd.addParam(tupleCentreName);
		Node node = getNode();
		Object result = node.accept(cmd);
		
		if (result != null && result instanceof Throwable){
			throw new ServiceException(((Throwable) result).getMessage());
		}
		System.out.println("[Proxy] result = "+result);
		return (TucsonTupleCentreId)result;
	}

}
