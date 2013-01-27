package it.unibo.ing2.jade.service;

import java.io.IOException;
import java.net.InetSocketAddress;

import it.unibo.ing2.jade.exceptions.NoTucsonAuthenticationException;
import it.unibo.ing2.jade.exceptions.TucsonNodeNotFoundException;
import it.unibo.ing2.jade.operations.TucsonOperationHandler;
import jade.core.Agent;
import jade.core.IMTPException;
import jade.core.ServiceException;
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
	 * Permette di effettuare l'autenticazione per il nodo TuCSoN locale. Equivale a {@link #authenticate(Agent, String, int)} con i parametri
	 * relativi al nodo locale.
	 * @param agent L'agente che richiede l'autenticazione.
	 * @throws TucsonInvalidAgentIdException Se il nome dell'agente non soddisfa i requisiti di TuCSoN.
	 */
	public void authenticate(Agent agent) throws TucsonInvalidAgentIdException;

	/**
	 * Permette di effettuare l'autenticazione per il nodo TuCSoN specificato.
	 * @param agent L'agente che richiede l'autenticazione.
	 * @param netid L'indirizzo IP o nome DNS del nodo TuCSoN.
	 * @param portno Il numero di porta del nodo TuCSoN.
	 * @throws TucsonInvalidAgentIdException Se il nome dell'agente non soddisfa i requisiti di TuCSoN.
	 */
	public void authenticate(Agent agent, String netid, int portno)
			throws TucsonInvalidAgentIdException;

	/**
	 * Permette di effettuare la deautenticazione per il nodo TuCSoN sul quale si era precedentemente autenticati.
	 * @param agent L'agente che richiede la deautenticazione.
	 */
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
	public boolean isTucsonNodeRunning(int port) throws IOException;

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

	/**
	 * Permette di spostare sul nodo TuCSoN indicato tutte le tuple che fanno match con il template specificato e che appartengono ad uno fra i centri di tuple
	 * specificati. Questa operazione comporta, inoltre, l'eliminazione sul nodo TuCSoN locale di tutte le tuple che subiscono lo 
	 * spostamento.
	 * @param destinationNetId L'indirizzo IP o nome DNS del nodo TuCSoN di destinazione.
	 * @param portno Il numero di porta del nodo TuCSoN di destinazione.
	 * @param tupleTemplate Il template delle tuple che si vogliono trasferire. Se &egrave; null vengono trasferite tutte le tuple.
	 * @param tupleCentreNames Un array contenente i nomi di tutti i centri di tuple che si vogliono trasferire.
	 * @throws UnreachableNodeException Se il nodo TuCSoN non &egrave; raggiungibile.
	 * @throws NoTucsonAuthenticationException Se l'agente non ha effettuato precedentemente l'autenticazione.
	 * @throws IllegalArgumentException Se il numero di porta &egrave; invalido.
	 */
	public void doMove(String destinationNetId, int portno,
			String tupleTemplate, String[] tupleCentreNames)
			throws UnreachableNodeException, NoTucsonAuthenticationException,
			IllegalArgumentException;

	/**
	 * Identica a {@link #doMove(String, int, String, String[])} ma permette di identificare un nodo TuCSoN sulla base di un nome
	 * simbolico piuttosto che delle sue informazioni di rete.
	 * @param nodeName Il nome simbolico del nodo TuCSoN.
	 * @throws ServiceException Se si verificano problemi nel {@linkplain TuCSoNService}.
	 * @throws TucsonNodeNotFoundException Se non esiste nessun nodo TuCSoN identificato da <code>nodeName</code>.
	 */
	public void doMove(String nodeName, String tupleTemplate,
			String[] tupleCentreNames) throws UnreachableNodeException,
			NoTucsonAuthenticationException, ServiceException,
			TucsonNodeNotFoundException;

	
	public void doClone(String destinationNetId, int portno,
			String tupleTemplate, String[] tupleCentreNames) throws UnreachableNodeException,
			NoTucsonAuthenticationException, IllegalArgumentException;

	public void doClone(String nodeName, String tupleTemplate,
			String[] tupleCentreNames) throws UnreachableNodeException,
			NoTucsonAuthenticationException, ServiceException,
			TucsonNodeNotFoundException;

	public void addTupleCentreName(String tcName, String netId, int portno)
			throws ServiceException, IllegalArgumentException, IMTPException;
	
	public void removeTupleCentreName(String tcName) throws IMTPException, ServiceException;
	
	public InetSocketAddress findTupleCentre(String tcName) throws ServiceException, IMTPException, TucsonNodeNotFoundException;
	
	public void setMainNode(String netId, int portno) throws TucsonInvalidTupleCentreIdException;
}
