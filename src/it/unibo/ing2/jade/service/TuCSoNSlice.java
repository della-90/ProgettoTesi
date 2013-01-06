package it.unibo.ing2.jade.service;

import it.unibo.ing2.jade.operations.TucsonAction;
import alice.logictuple.LogicTuple;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;
import jade.core.IMTPException;
import jade.core.Service.Slice;
import jade.core.ServiceException;

public interface TuCSoNSlice extends Slice {
	
	public static final String H_FINDTUPLECENTRE = "1";
	public static final String H_EXECUTE_SYNCH = "2";
	public static final String H_EXECUTE_ASYNCH = "3";
	
	public static final String FIND_TUPLE_CENTRE = "Find-Tuple-Centre";
	public static final String EXECUTE_SYNCH = "Execute-Synch";
	public static final String EXECUTE_ASYNCH = "Execute-Asynch";
	
	
	public TucsonTupleCentreId findTupleCentre(String tupleCentreName) throws IMTPException, ServiceException;
	public void executeSynch(TucsonAction action, Long timeout) throws ServiceException, IMTPException;
	public void executeAsynch(TucsonAction action, TucsonOperationCompletionListener listener) throws ServiceException, IMTPException;
	

}
