package it.unibo.ing2.jade.service;

import it.unibo.ing2.jade.exceptions.NoTucsonAuthenticationException;
import it.unibo.ing2.jade.operations.TucsonOperationHandler;
import jade.core.Agent;
import jade.core.ServiceHelper;
import alice.logictuple.LogicTuple;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.service.TucsonNodeService;

/**
 * 
 * @author Nicola
 * 
 */
public interface TuCSoNHelper extends ServiceHelper {

	public final int TUCSON_PORT = 20504;
	
	/**
	 * Permette di ottenere un {@link alice.tucson.api.EnhancedACC} EnhancedACC
	 * (attualmente l'ACC pi&ugrave; avanzato).
	 * 
	 * @param agent
	 *            L'agente che richiede l'ACC.
	 * @param netid
	 *            L'ID del nodo TuCSoN (indirizzo IP oppure nome DNS)
	 * @param portno
	 *            Il numero di porta del nodo TuCSoN
	 * @return L'EnhancedACC associato al nodo TuCSoN specificato
	 * @throws TucsonInvalidAgentIdException
	 *             Se l'<code>aid</code> specificato non &egrave; ammissibile.
	 */
	// public EnhancedACC obtainAcc(Agent agent, String netid, int portno)
	// throws TucsonInvalidAgentIdException;

	/**
	 * Permette di ottenere un {@link alice.tucson.api.EnhancedACC} EnhancedACC
	 * (attualmente l'ACC pi&ugrave; avanzato). Equivale a
	 * {@link #obtainAcc(Agent, String, int)} con <code>netid</code> e
	 * <code>portno</code> di default (localhost:20504)
	 * 
	 * @param agent
	 *            L'agente che richiede l'ACC.
	 * @return L'EnhancedACC associato al nodo TuCSoN specificato
	 * @throws TucsonInvalidAgentIdException
	 *             Se l'<code>aid</code> specificato non &egrave; ammissibile.
	 */
	// public EnhancedACC obtainAcc(Agent agent) throws
	// TucsonInvalidAgentIdException;

	public void authenticate(Agent agent) throws TucsonInvalidAgentIdException;

	public void authenticate(Agent agent, String netid, int portno)
			throws TucsonInvalidAgentIdException;

	public void deauthenticate(Agent agent);

	/**
	 * Permette di ottenere il {@link alice.tucson.api.TucsonTupleCentreId}
	 * TucsonTupleCentreId relativo al <code>tupleCentreName</code>,
	 * <code>netid</code> e <code>portno</code> specificati
	 * 
	 * @param tupleCentreName
	 *            Il nome del tuple centre
	 * @param netid
	 *            L'indirizzo IP o nome DNS del nodo che ospita il tuple centre
	 * @param portno
	 *            Il numero di porta del tuple centre (default 20504)
	 * @return Il TucsonTupleCentreId relativo ai parametri specificati
	 * @throws TucsonInvalidTupleCentreIdException
	 *             Se il tuple centre non &egrave; valido.
	 */
	public TucsonTupleCentreId getTupleCentreId(String tupleCentreName,
			String netid, int portno)
			throws TucsonInvalidTupleCentreIdException;

	/**
	 * Permette di avviare un {@link TucsonNodeService} TucsonNodeService
	 * sull'host alla porta specificata.
	 * 
	 * @param port
	 *            La porta del TucsonNodeService.
	 * @exception
	 */
	public void startTucsonNode(int port) throws Exception;

	/**
	 * Permette di fermare un {@link TucsonNodeService} TucsonNodeService
	 * sull'host.
	 * 
	 * @param port
	 *            La porta del TucsonNodeService
	 */
	public void stopTucsonNode(int port);

	/**
	 * Permette di controllare se &egrave; attivo un {@link TucsonNodeService}
	 * TucsonNodeService alla porta indicata.
	 * 
	 * @param port
	 *            La porta del TucsonNodeService.
	 * @return True se il nodo &egrave; attivo, false altrimenti.
	 */
	public boolean isTucsonNodeRunning(int port);

	/**
	 * Permette di ottenere il {@link TucsonOperationHandler} tramite il quale
	 * &egrave; possibile interagire con TuCSoN.
	 * 
	 * @param agent
	 *            L'agente che richiede il TucsonOperationHandler.
	 * @return Il TucsonOperationHandler che permette l'interazione con TuCSoN.
	 * @throws NoTucsonAuthenticationException
	 *             Se l'agente <code>agent</code> non ha ottenuto un ACC.
	 * @see #obtainAcc(Agent, String, int)
	 */
	public TucsonOperationHandler getOperationHandler(Agent agent)
			throws NoTucsonAuthenticationException;

	/*
	 * FIXME Metodo di debug
	 */
	public void foo();

	/*
	 * FIXME Metodo di debug
	 */
	public void foo2();

	public void doMove(String destinationNetId, String tupleTemplate, String[] tupleCentreNames)
			throws UnreachableNodeException, NoTucsonAuthenticationException;

	public void doClone(TucsonTupleCentreId destination, String[] tupleCentreNames)
			throws UnreachableNodeException, NoTucsonAuthenticationException;

}
