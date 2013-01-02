package it.unibo.ing2.jade.service;

import alice.tucson.api.TucsonTupleCentreId;
import jade.core.IMTPException;
import jade.core.Service.Slice;
import jade.core.ServiceException;

public interface TuCSoNSlice extends Slice {
	
	static final String H_FINDTUPLECENTRE = "1";
	
	public static final String EXECUTE = "execute";
	
	public TucsonTupleCentreId findTupleCentre(String tupleCentreName) throws IMTPException, ServiceException;

}
