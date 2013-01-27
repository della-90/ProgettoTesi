package it.unibo.ing2.jade.service;

import it.unibo.ing2.jade.coordination.TucsonAccManager;
import it.unibo.ing2.jade.coordination.TucsonNodeUtility;
import it.unibo.ing2.jade.exceptions.NoTucsonAuthenticationException;
import it.unibo.ing2.jade.exceptions.TucsonNodeNotFoundException;
import it.unibo.ing2.jade.operations.Out;
import it.unibo.ing2.jade.operations.TucsonAction;
import it.unibo.ing2.jade.operations.TucsonOperationHandler;
import it.unibo.ing2.jade.operations.TucsonOrdinaryAction;
import it.unibo.ing2.jade.operations.TucsonSpecificationAction;
import jade.core.AID;
import jade.core.Agent;
import jade.core.BaseService;
import jade.core.Filter;
import jade.core.GenericCommand;
import jade.core.HorizontalCommand;
import jade.core.IMTPException;
import jade.core.Node;
import jade.core.Profile;
import jade.core.Service;
import jade.core.ServiceException;
import jade.core.ServiceHelper;
import jade.core.Sink;
import jade.core.VerticalCommand;
import jade.core.behaviours.TickerBehaviour;
import jade.tools.sniffer.MMCanvas;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Templates;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.tucson.api.EnhancedACC;
import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonMetaACC;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonGenericException;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.service.TucsonNodeService;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

@SuppressWarnings("unused")
public class TuCSoNService extends BaseService {

	/*
	 * Il nome del servizio
	 */
	public static final String NAME = "it.unibo.ing2.jade.service.TuCSoN";

	/*
	 * L'helper del servizio
	 */
	private TuCSoNHelper mHelper = new TuCSoNHelperImpl();

	/*
	 * Gestisce gli acc posseduti dagli agenti
	 */
	private TucsonAccManager mAccManager = TucsonAccManager.getInstance();

	/*
	 * Gestisce il mapping Agente-OperationHandler
	 */
	private Map<AID, TucsonOperationHandler> mOperationHandlers = new HashMap<AID, TucsonOperationHandler>();
	
	/*
	 * Gestisce il mapping nodeName-InetAddress
	 */
	private Map<String, InetSocketAddress> mTucsonNodes = new HashMap<>();

	/**
	 * Parametro booleano di avvio che permette di specificare se attivare o
	 * meno un nodo TuCSoN sull'host. Di default è FALSE
	 */
	public static final String BOOT_TUCSON_NODE = "boot_tucson_node";

	/**
	 * Parametro di tipo Stringa che consente di specificare il nome di un file
	 * all'interno del quale &egrave; possibile specificare tutte le associazioni
	 * tupleCentreName-NetId
	 */
	public static final String TUCSON_NODE_MAPPINGS = "tucson_node_mappings";
	/*
	 * Il tuple centre relativo alla mobilità di TuCSoN
	 */
	private TucsonTupleCentreId mobilityTC;

	/*
	 * L'insieme dei comandi verticali che il servizio è in grado di soddisfare
	 * autonomamente
	 */
	private static final String[] OWNED_COMMANDS = { TuCSoNSlice.EXECUTE_SYNCH,
			TuCSoNSlice.EXECUTE_ASYNCH
	// TuCSoNSlice.FIND_TUPLE_CENTRE
	};

	// The local slice for this service
	private ServiceComponent localSlice = new ServiceComponent();

	// The source and target sinks
	private Sink sourceSink = new CommandSourceSink();
	private Sink targetSink = new CommandTargetSink(); // Al momento non è
														// utilizzato

	// The outgoing filter for the service
	private IncomingPrimitiveFilter inFilter = new IncomingPrimitiveFilter();

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public ServiceHelper getHelper(Agent a) throws ServiceException {
		return mHelper;
	}

	@Override
	public Slice getLocalSlice() {
		return localSlice;
	}

	@Override
	public String[] getOwnedCommands() {
		return OWNED_COMMANDS;
	}

	@Override
	public void boot(Profile p) throws ServiceException {
		super.boot(p);

		try {
			// TODO non è detto che localhost vada bene!! Inoltre magari la
			// porta non è sempre 20504!
			mobilityTC = new TucsonTupleCentreId("default", "localhost",
					"20504");
		} catch (TucsonInvalidTupleCentreIdException e) {
			// Should never be thrown
			e.printStackTrace();
		}

		/*
		 * Questo è il punto adatto per interpretare parametri aggiuntivi del
		 * servizio
		 */
		boolean bootTucsonNode = p.getBooleanProperty(BOOT_TUCSON_NODE, false);
		System.out.println("Boot tucson node? " + bootTucsonNode);

		/*
		 * Se e' il main-container aggiungo i mappings, altrimenti li ignoro
		 */
		if (p.isMain()){
			String filePath = p.getParameter(TUCSON_NODE_MAPPINGS, null);
			if (filePath != null) {
				try {
					Map<String, InetSocketAddress> mappings = TucsonMappingsParser.parse(filePath);
					mTucsonNodes.putAll(mappings);
				} catch (FileNotFoundException e) {
					System.err.println("[TuCSoNService] File "+filePath+" not found!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// Se non è attivo un nodo tucson lo lancio
		if (bootTucsonNode) {

			try {
				//Lancio il nodo
				TucsonNodeUtility.startTucsonNode(20504);
				
				//Inserisco le tuple di specifica
				TucsonAgentId aid = new TucsonAgentId("tucsonService");
				EnhancedSynchACC acc = TucsonMetaACC.getContext(aid);
				System.out.println("[TuCSoNService] TuCSoN node started on port 20504");
				/*
				 * TODO: dato che il TucsonNode è su un altro thread, non c'è garanzia che la seguente
				 * chiamata venga fatta DOPO che il TucsonNode sia effettivamente avviato
				 */
				Thread.sleep(2000);
				
				insertSpecificationTuples(acc);
				//Rilascio ACC
				acc.exit();
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[TuCSoNService] "+e);
				e.printStackTrace();
			} catch (TucsonInvalidAgentIdException e) {
				// Should never be thrown
				System.err.println("[TuCSoNService]: "+e);
				e.printStackTrace();
			} catch (TucsonGenericException e) {
				System.err.println("[TuCSoNService]: Cannot launch TuCSoN node! "+e);
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO: sostituire la Thread.sleep con qualcosa di più appropriato
			}
		}
	}

	@Override
	public void shutdown() {
		System.out.println("[TuCSoNService] Shutting down TuCSoN node");
		TucsonNodeUtility.stopTucsonNode(20504);
		super.shutdown();
	}

	private boolean hasSpecificationTuples(EnhancedSynchACC acc)
			throws TucsonOperationNotPossibleException {
		LogicTuple event, guard, reaction;
		boolean hasTuples = true;
		for (int i=0; i < SpecificationTuples.events.length; i++) {
			event = SpecificationTuples.events[i];
			guard = SpecificationTuples.guards[i];
			reaction = SpecificationTuples.reactions[i];
			
			try {
				ITucsonOperation op = acc.no_s(mobilityTC, event, guard, reaction, null);
				if (op.isResultSuccess()){
					hasTuples = false;
					break;
				}
			} catch (UnreachableNodeException e) {
				//Should never be thrown
				e.printStackTrace();
			} catch (OperationTimeOutException e) {
				//Should never be thrown
				e.printStackTrace();
			}
		}
		return hasTuples;

	}

	private void insertSpecificationTuples(EnhancedSynchACC acc)
			throws TucsonOperationNotPossibleException {
		LogicTuple event, guards, reaction;
		try {
			
			for (int i=0; i<SpecificationTuples.events.length; i++){
				event = SpecificationTuples.events[i];
				guards = SpecificationTuples.guards[i];
				reaction = SpecificationTuples.reactions[i];
				
				acc.out_s(mobilityTC, event, guards, reaction, null);
			}
//			event = LogicTuple.parse("out(wanna_move(Destination, TupleCentreName, Template))");
//			guards = LogicTuple.parse("(from_agent, completion)");
//			reaction = LogicTuple.parse("in(wanna_move(Destination, TupleCentreName, Template)), rd_all(Template, TupleList), Destination ? out_all(TupleList)");
//			ITucsonOperation op = acc.out_s(mobilityTC, event, guards,reaction, null);
//			
//			event = LogicTuple.parse("out(move_tuples(Destination, N))");
//			guards = LogicTuple.parse("completion, success");
//			reaction = LogicTuple
//					.parse("( N>0, in(move_tuples(Destination, N)), in(Tuple), Destination ? out(Tuple), N2 is N-1, out(move_tuples(Destination, N2)) )");
//			acc.out_s(mobilityTC, event, guards, reaction, null);

//			event = LogicTuple.parse("out(move_tuples(Destination, 0))");
//			guards = LogicTuple.parse("completion, success");
//			reaction = LogicTuple.parse("in(move_tuples(Destination, 0))");
//			acc.out_s(mobilityTC, event, guards, reaction, null);
		} catch (OperationTimeOutException e) {
			// Should never be thrown
			System.err.println("[TuCSoNService]: "+e);
			e.printStackTrace();
		} catch (UnreachableNodeException e) {
			/*
			 * Il tuple centre mobilityTC si trova su un nodo locale, per cui non può
			 * essere irraggiungibile
			 */
			System.err.println("[TuCSoNService]: Errore nell'inserimento delle tuple di specifica nel nodo locale! "+e);
			e.printStackTrace();
		}

	}

	@Override
	public Sink getCommandSink(boolean direction) {
		if (direction == Sink.COMMAND_SOURCE) {
			return sourceSink;
		} else {
			return targetSink;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getHorizontalInterface() {
		return TuCSoNSlice.class;
	}

	@Override
	public Filter getCommandFilter(boolean direction) {
		if (direction == Filter.OUTGOING) {
			return null;
		} else {
			return inFilter;
		}
	}

	/*
	 * Un semplice filtro per le operazioni in ingresso (quelle eseguite dal
	 * CommandSourceSink)
	 */
	private class IncomingPrimitiveFilter extends Filter {

		@Override
		protected boolean accept(VerticalCommand cmd) {
			// System.out.println("[InFilter] cmd = "+cmd.getName());
			String cmdName = cmd.getName();
			if (cmdName.equals(TuCSoNSlice.EXECUTE_SYNCH)
					|| cmdName.equals(TuCSoNSlice.EXECUTE_ASYNCH)) {
				Object action = cmd.getParam(0);
				if (action instanceof TucsonSpecificationAction) {
					System.out
							.println("[IncomingPrimitiveFilter] Richiesta un'operazione di specifica");
				} else if (action instanceof TucsonOrdinaryAction) {
					System.out
							.println("[IncomingPrimitiveFilter] Richiesta un'operazione ordinaria");
				}
				// Per il momento non blocco alcuna operazione
				return true;
			} else {
				return true;
			}
		}

	}

	private void doMove(Agent agent, String tupleTemplate,
			String destinationNetId, int portno, String[] tupleCentreNames)
			throws NoTucsonAuthenticationException, UnreachableNodeException {

		EnhancedSynchACC acc = mAccManager.getAcc(agent);
		if (acc == null) {
			throw new NoTucsonAuthenticationException(
					"Authentication required for moving");
		}

		// Controllo tupleTemplate
		if (tupleTemplate == null || tupleTemplate.trim().length() == 0) {
			tupleTemplate = "X";
		}

		for (int i = 0; i < tupleCentreNames.length; i++) {
			try {
				 String tupleBody = new String("wanna_move(" +
						 tupleCentreNames[i] +" @ "+destinationNetId+":"+portno+","+
						 tupleCentreNames[i] +","+
						 tupleTemplate +
				 ")");
				LogicTuple tuple = LogicTuple.parse(tupleBody);

				// Eseguo la tupla direttamente dalle API TuCSoN
				System.out.println("[TuCSoNService] Doing out of tuple: "
						+ tuple+" on TC "+mobilityTC);
				acc.out(mobilityTC, tuple, null);

			} catch (InvalidLogicTupleException | OperationTimeOutException e) {
				// Should be never thrown
				System.err.println("[TuCSoNService]: "+e);
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[TuCSoNService]: "+e);
				e.printStackTrace();
			}
		}

	}
	
	private void doClone(Agent agent, String tupleTemplate, String destinationNetId, int portno,
			String[] tupleCentreNames) throws NoTucsonAuthenticationException, UnreachableNodeException {
		
		EnhancedSynchACC acc = mAccManager.getAcc(agent);
		if (acc == null) {
			throw new NoTucsonAuthenticationException("Authentication required for cloning");
		}
		
		//Controllo tupleTemplate
		if (tupleTemplate == null || tupleTemplate.trim().length() == 0){
			tupleTemplate = "X";
		}
		
		for (int i=0; i<tupleCentreNames.length; i++) {
			try {
				String tupleBody = new String(
						"wanna_clone(" +
						tupleCentreNames[i] +"@"+destinationNetId+":"+portno+","+
						tupleCentreNames[i] +","+
						tupleTemplate + ")"
						);
				
				LogicTuple tuple = LogicTuple.parse(tupleBody);
				
				System.out.println("[TuCSoNService] Doing out of tuple: "
						+ tuple);
				acc.out(mobilityTC, tuple, null);
				
			} catch (InvalidLogicTupleException | OperationTimeOutException e) {
				// Should be never thrown
				System.err.println("[TuCSoNService]: "+e);
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[TuCSoNService]: "+e);
				e.printStackTrace();
			}
		}
	}
	
	private void addTupleCentreName(String tcName, String netId, int portno) throws ServiceException, IMTPException, IllegalArgumentException{
		TuCSoNSlice mainSlice = (TuCSoNSlice) getSlice(MAIN_SLICE);
		mainSlice.addTupleCentre(tcName, netId, portno);
	}
	
	private void removeTupleCentreName(String tcName) throws IMTPException, ServiceException {
		TuCSoNSlice mainSlice = (TuCSoNSlice) getSlice(MAIN_SLICE);
		mainSlice.removeTupleCentre(tcName);
	}
	
	private InetSocketAddress findTupleCentre(String tcName) throws ServiceException, IMTPException, TucsonNodeNotFoundException {
		TuCSoNSlice mainSlice = (TuCSoNSlice) getSlice(MAIN_SLICE);
		Object result = mainSlice.findTupleCentre(tcName);
		
		if (result instanceof Throwable) {
			throw (TucsonNodeNotFoundException) result;
		}
		return (InetSocketAddress) result;
	}

	/*
	 * L'implementazione del TuCSoNHelper
	 */
	private class TuCSoNHelperImpl implements TuCSoNHelper {

		private Agent myAgent;

		@Override
		public void init(Agent agent) {
			this.myAgent = agent;
		}
		
		@Override
		public void addTupleCentreName(String tcName, String netId, int portno)
				throws ServiceException, IllegalArgumentException, IMTPException {
			TuCSoNService.this.addTupleCentreName(tcName, netId, portno);
		}
		
		@Override
		public void removeTupleCentreName(String tcName) throws IMTPException, ServiceException {
			TuCSoNService.this.removeTupleCentreName(tcName);
		}
		
		@Override
		public InetSocketAddress findTupleCentre(String tcName) throws ServiceException, IMTPException, TucsonNodeNotFoundException {
			return TuCSoNService.this.findTupleCentre(tcName);
		}

		@Override
		public void doMove(String destinationNetId, int portno, String tupleTemplate,
				String[] tupleCentreNames) throws UnreachableNodeException,
				NoTucsonAuthenticationException, IllegalArgumentException {
			if (portno<= 0 || portno>65535){
				throw new IllegalArgumentException("Port number +"+portno+" is not valid");
			}
			
			TuCSoNService.this.doMove(this.myAgent, tupleTemplate,
					destinationNetId, portno, tupleCentreNames);
		}
		
		@Override
		public void setMainNode(String netId, int portno) throws TucsonInvalidTupleCentreIdException {
			mobilityTC = new TucsonTupleCentreId("default", netId, ""+portno);
		}
		
		@Override
		public void doMove(String nodeName, String tupleTemplate,
				String[] tupleCentreNames) throws UnreachableNodeException,
				NoTucsonAuthenticationException, ServiceException, TucsonNodeNotFoundException {

			TuCSoNSlice mainSlice = (TuCSoNSlice) getSlice(MAIN_SLICE);
			System.out.println("[TuCSoNService] Main-Slice = "+mainSlice);
			try {
				Object result = mainSlice.findTupleCentre(nodeName);
				if (result instanceof Throwable){
					throw (TucsonNodeNotFoundException) result;
				}
				
				InetSocketAddress addr = (InetSocketAddress) result;
				String ip = addr.getAddress().getHostAddress();
				int portno = addr.getPort();
				this.doMove(ip, portno, tupleTemplate, tupleCentreNames);
			} catch (IMTPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		@Override
		public void doClone(String destinationNetId, int portno,
				String tupleTemplate, String[] tupleCentreNames)
				throws UnreachableNodeException,
				NoTucsonAuthenticationException, IllegalArgumentException {
			
			if (portno<=0 || portno>65535){
				throw new IllegalArgumentException("Port number +"+portno+" is not valid");
			}
			
			TuCSoNService.this.doClone(this.myAgent, tupleTemplate, destinationNetId, portno, tupleCentreNames);
			
		}
		
		@Override
		public void doClone(String nodeName, String tupleTemplate,
				String[] tupleCentreNames) throws UnreachableNodeException,
				NoTucsonAuthenticationException, ServiceException,
				TucsonNodeNotFoundException {
			
			TuCSoNSlice mainSlice = (TuCSoNSlice) getSlice(MAIN_SLICE);
			try {
				Object result = mainSlice.findTupleCentre(nodeName);
				if (result instanceof Throwable){
					throw (TucsonNodeNotFoundException) result;
				}
				
				InetSocketAddress addr = (InetSocketAddress) result;
				String ip = addr.getAddress().getHostAddress();
				int portno = addr.getPort();
				this.doClone(ip, portno, tupleTemplate, tupleCentreNames);
			} catch (IMTPException e){
				//TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		private EnhancedACC obtainAcc(Agent agent, String netid, int portno)
				throws TucsonInvalidAgentIdException {
			TucsonAgentId taid = new TucsonAgentId(agent.getLocalName());
			EnhancedACC acc = TucsonMetaACC.getContext(taid, netid, portno);
			return acc;
		}

		@Override
		public void authenticate(Agent agent)
				throws TucsonInvalidAgentIdException {
			if (mAccManager.hasAcc(agent)){
				return;
			}
			EnhancedACC acc = obtainAcc(agent);
			mAccManager.addAcc(agent, acc);
			System.out.println("[TuCSoNHelper] Ottenuto acc "+acc+" per l'agente "+agent.getLocalName());
		}

		@Override
		public void authenticate(Agent agent, String netid, int portno)
				throws TucsonInvalidAgentIdException {
			if (mAccManager.hasAcc(agent)){
				System.out.println("[TuCSoNHelper] L'agente possiede già un ACC");
				return;
			}
			EnhancedACC acc = obtainAcc(agent, netid, portno);
			mAccManager.addAcc(agent, acc);
		}

		@Override
		public void deauthenticate(Agent agent) {
			//Esco dall'acc
			EnhancedACC acc = mAccManager.getAcc(agent);
			if (acc!=null){
				try {
					acc.exit();
				} catch (TucsonOperationNotPossibleException e) {
					e.printStackTrace();
				}
			}
			//Rimuovo l'ACC
			mAccManager.removeAcc(agent);
			
			//Rimuovo eventuali TucsonOperationHandler
			mOperationHandlers.remove(myAgent.getAID());
		}

		private EnhancedACC obtainAcc(Agent agent)
				throws TucsonInvalidAgentIdException {
			TucsonAgentId taid = new TucsonAgentId(agent.getLocalName());
			return TucsonMetaACC.getContext(taid);
		}

		@Override
		public TucsonTupleCentreId getTupleCentreId(String tupleCentreName,
				String netid, int portno)
				throws TucsonInvalidTupleCentreIdException {
			TucsonTupleCentreId tcid = new TucsonTupleCentreId(tupleCentreName,
					netid, new Integer(portno).toString());
			return tcid;
		}

		@Override
		public void startTucsonNode(int port) throws TucsonGenericException {
			TucsonNodeUtility.startTucsonNode(port);

		}

		@Override
		public void stopTucsonNode(int port) {
			TucsonNodeUtility.stopTucsonNode(port);
		}

		@Override
		public boolean isTucsonNodeRunning(int port) throws IOException {
			return TucsonNodeUtility.isTucsonNodeRunning(port);
		}

		@Override
		public TucsonOperationHandler getOperationHandler(Agent agent)
				throws NoTucsonAuthenticationException {
			if (!mAccManager.hasAcc(agent)) {
				throw new NoTucsonAuthenticationException(
						"The agent does not hold an ACC");
			}

			// Controllo se esiste già un OperationHandler per l'agente,
			// altrimenti lo creo
			TucsonOperationHandler operationHandler = mOperationHandlers.get(agent.getAID());
			if (operationHandler == null) {
				operationHandler = new TucsonOperationHandler(mAccManager.getAcc(agent), TuCSoNService.this);
				mOperationHandlers.put(agent.getAID(), operationHandler);
			}
			return operationHandler;
		}

	}

	/**
	 * Classe interna il cui compito e' quello di ricevere e servire i comandi
	 * orizzontali
	 * 
	 * @author Nicola
	 * 
	 */
	@SuppressWarnings("serial")
	private class ServiceComponent implements Service.Slice {

		@Override
		public Node getNode() throws ServiceException {
			try {
				return TuCSoNService.this.getLocalNode();
			} catch (IMTPException e) {
				// Should never happen; this is a local call
				throw new ServiceException("Unexpected error on retrieving local node");
			}
		}

		@Override
		public Service getService() {
			return TuCSoNService.this;
		}

		@Override
		public VerticalCommand serve(HorizontalCommand cmd) {
			VerticalCommand result = null;
			String cmdName = cmd.getName();
			
			String nodeName = null;
			InetSocketAddress addr = null;
			switch (cmdName) {
			case TuCSoNSlice.H_FINDTUCSONNODE:
				
				nodeName = (String) cmd.getParam(0);
				addr = mTucsonNodes.get(nodeName);
				
				cmd.setReturnValue(addr);
				break; 
				
			case TuCSoNSlice.H_ADDTUPLECENTRE:
				
				nodeName = (String) cmd.getParam(0);
				addr = (InetSocketAddress) cmd.getParam(1);
				
				InetSocketAddress previous = TuCSoNService.this.mTucsonNodes.put(nodeName, addr);
				if (previous == null){
					System.out.println("[TuCSoNService] Nodo TuCSoN "+nodeName+" aggiunto");
				} else {
					System.out.println("[TuCSoNService] Il nodo "+nodeName+" ha sovrascritto il precedente: "+previous.getAddress().getHostAddress()+":"+previous.getPort());
				}
				break;
				
			case TuCSoNSlice.H_REMOVETUPLECENTRE:
				
				nodeName = (String) cmd.getParam(0);
				
				previous = TuCSoNService.this.mTucsonNodes.remove(nodeName);
				if (previous == null){
					System.out.println("[TuCSoNService] Nodo "+nodeName+" non presente");
				} else {
					System.out.println("[TuCSoNService] Nodo "+nodeName+" rimosso");
				}
				break;
			}

			return result;
		}

	}

	/*
	 * Classe interna che ha il compito di eseguire i comandi verticali
	 * "interni", ovvero quelli gestiti direttamente dal servizio (quelli
	 * inclusi nell'elenco OWNED_COMMANDS)
	 */
	private class CommandSourceSink implements Sink {

		@Override
		public void consume(VerticalCommand cmd) {
			String cmdName = cmd.getName();
			if (cmdName.equals(TuCSoNSlice.EXECUTE_SYNCH)) {
				TucsonAction action = (TucsonAction) cmd.getParam(0);
				EnhancedSynchACC acc = (EnhancedSynchACC) cmd.getParam(1);
				Long timeout = (Long) cmd.getParam(2);

				try {
					ITucsonOperation result = action.executeSynch(acc, timeout);
					cmd.setReturnValue(result);
				} catch (Exception e) {
					cmd.setReturnValue(e);
				}

			} else if (cmdName.equals(TuCSoNSlice.EXECUTE_ASYNCH)) {
				TucsonAction action = (TucsonAction) cmd.getParam(0);
				EnhancedAsynchACC acc = (EnhancedAsynchACC) cmd.getParam(1);
				TucsonOperationCompletionListener listener = (TucsonOperationCompletionListener) cmd
						.getParam(2);

				try {
					ITucsonOperation result = action.executeAsynch(acc,
							listener);
					cmd.setReturnValue(result);
				} catch (Exception e) {
					cmd.setReturnValue(e);
				}
			}
		}

	}

	/*
	 * Classe interna che ha il compito di eseguire i comandi verticali generati
	 * dal ServiceComponent (al momento nessuno)
	 */
	private class CommandTargetSink implements Sink {
		@Override
		public void consume(VerticalCommand cmd) {
		}
	}

}
