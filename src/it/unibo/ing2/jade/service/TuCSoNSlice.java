package it.unibo.ing2.jade.service;

import jade.core.IMTPException;
import jade.core.Service.Slice;
import jade.core.ServiceException;

public interface TuCSoNSlice extends Slice {
	
	/*
	 * I seguenti sono i nomi dei comandi orizzontali 
	 */
	public static final String H_FINDTUCSONNODE = "1";
	public static final String H_ADDTUPLECENTRE = "2";
	public static final String H_REMOVETUPLECENTRE = "3";
//	public static final String H_EXECUTE_SYNCH = "2";
//	public static final String H_EXECUTE_ASYNCH = "3";
	
	/*
	 * I seguenti sono i nomi dei comandi verticali
	 */
//	public static final String FIND_TUPLE_CENTRE = "Find-Tuple-Centre";
	public static final String EXECUTE_SYNCH = "Execute-Synch";
	public static final String EXECUTE_ASYNCH = "Execute-Asynch";
	
	/*
	 * I seguenti sono i metodi associati ai comandi orizzontali
	 */
	public Object findTupleCentre(String tupleCentreName) throws IMTPException, ServiceException;
	
	public void addTupleCentre(String tupleCentreName, String netId, int portno) throws IMTPException, ServiceException, IllegalArgumentException;
	
	public void removeTupleCentre(String tupleCentreName) throws IMTPException, ServiceException;
	
	//I seguenti non sono comandi orizzontali!!
//	public void executeSynch(TucsonAction action, Long timeout) throws ServiceException, IMTPException;
//	public void executeAsynch(TucsonAction action, TucsonOperationCompletionListener listener) throws ServiceException, IMTPException;
	

}
