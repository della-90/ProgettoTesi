package it.unibo.ing2.jade.coordination;

import alice.logictuple.LogicTuple;
import alice.tucson.api.EnhancedACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import jade.core.ServiceHelper;

public interface TuCSoNHelper extends ServiceHelper {
	
	/**
	 * Permette di ottenere un {@link alice.tucson.api.EnhancedACC} EnhancedACC (attualmente l'ACC pi&ugrave; avanzato).
	 * @param aid L'ID dell'agente
	 * @param netid L'ID del nodo TuCSoN (indirizzo IP oppure nome DNS)
	 * @param portno Il numero di porta del nodo TuCSoN
	 * @return L'EnhancedACC associato al nodo TuCSoN specificato
	 * @throws TucsonInvalidAgentIdException Se l'<code>aid</code> specificato non &egrave; ammissibile.
	 */
	public EnhancedACC obtainAcc(String aid, String netid, int portno) throws TucsonInvalidAgentIdException;
	
	/**
	 * Permette di ottenere un {@link alice.tucson.api.EnhancedACC} EnhancedACC (attualmente l'ACC pi&ugrave; avanzato).
	 * Equivale a {@link #obtainAcc(String, String, int)} con <code>netid</code> e <code>portno</code> di default (localhost:20504)
 	 * @param aid L'ID dell'agente
	 * @return L'EnhancedACC associato al nodo TuCSoN specificato
	 * @throws TucsonInvalidAgentIdException Se l'<code>aid</code> specificato non &egrave; ammissibile.
	 */
	public EnhancedACC obtainAcc(String aid) throws TucsonInvalidAgentIdException;
	
	/**
	 * Permette di ottenere il {@link alice.tucson.api.TucsonTupleCentreId} TucsonTupleCentreId relativo al <code>tupleCentreName</code>,<code>netid</code> e <code>portno</code>
	 * specificati
	 * @param tupleCentreName Il nome del tuple centre
	 * @param netid L'indirizzo IP o nome DNS del nodo che ospita il tuple centre
	 * @param portno Il numero di porta del tuple centre (default 20504)
	 * @return Il TucsonTupleCentreId relativo ai parametri specificati
	 * @throws TucsonInvalidTupleCentreIdException Se il tuple centre non &egrave; valido.
	 */
	public TucsonTupleCentreId getTupleCentreId(String tupleCentreName, String netid, int portno) throws TucsonInvalidTupleCentreIdException;
	
	public boolean isTucsonNodeRunning();

}
