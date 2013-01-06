package it.unibo.ing2.jade.service;

import it.unibo.ing2.jade.coordination.TucsonAccManager;
import it.unibo.ing2.jade.coordination.TucsonNodeUtility;
import it.unibo.ing2.jade.exceptions.NoTucsonAuthenticationException;
import it.unibo.ing2.jade.operations.Out;
import it.unibo.ing2.jade.operations.TucsonAction;
import it.unibo.ing2.jade.operations.TucsonOperationHandler;
import it.unibo.ing2.jade.operations.TucsonOrdinaryAction;
import it.unibo.ing2.jade.operations.TucsonSpecificationAction;
import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.BaseService;
import jade.core.ContainerID;
import jade.core.Filter;
import jade.core.GenericCommand;
import jade.core.HorizontalCommand;
import jade.core.IMTPException;
import jade.core.MainContainer;
import jade.core.Node;
import jade.core.NotFoundException;
import jade.core.Profile;
import jade.core.ProfileException;
import jade.core.Service;
import jade.core.ServiceException;
import jade.core.ServiceHelper;
import jade.core.Sink;
import jade.core.VerticalCommand;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

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
	 * Contiene l'elenco di tutti i nomi associati ai tuple centres
	 */
	private Map<String, TucsonTupleCentreId> mTupleCentres = new LinkedHashMap<>();

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

		/*
		 * Questo è il punto adatto per interpretare parametri aggiuntivi del
		 * servizio
		 */

		// FIXME Questo è stato introdotto per debug
		try {
			TucsonTupleCentreId tcid = new TucsonTupleCentreId("provaa");
			mTupleCentres.put("prova", tcid);
		} catch (TucsonInvalidTupleCentreIdException e) {
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

	/*
	 * L'implementazione del TuCSoNHelper
	 */
	private class TuCSoNHelperImpl implements TuCSoNHelper {

		@Override
		public void init(Agent arg0) {
		}

		@Override
		public void foo() {
			try {
				// Ottengo lo slice principale
				TuCSoNSlice mainSlice = (TuCSoNSlice) getSlice(MAIN_SLICE);
				// ed eseguo su esso l'operazione
				TucsonTupleCentreId tcid = mainSlice.findTupleCentre("prova");
				System.out.println("[foo] found: " + tcid);
			} catch (ServiceException e) {
				e.printStackTrace();
			} catch (IMTPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void foo2() {
			try {
				/*
				 * Ottengo lo slice locale; l'operazione non deve essere
				 * eseguita per forza sul main slice
				 */
				TuCSoNSlice thisSlice = (TuCSoNSlice) getSlice(getLocalNode()
						.getName());
				TucsonOrdinaryAction action = new Out(null, null);
				// thisSlice.executeSynch(action, null);

				GenericCommand cmd = new GenericCommand(
						TuCSoNSlice.EXECUTE_SYNCH, TuCSoNService.NAME, null);
				cmd.addParam(action);
				cmd.addParam(null);
				submit(cmd);
			} catch (ServiceException e) {
				e.printStackTrace();
			} catch (IMTPException e) {
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
			EnhancedACC acc = obtainAcc(agent);
			mAccManager.addAcc(agent, acc);
		}

		@Override
		public void authenticate(Agent agent, String netid, int portno)
				throws TucsonInvalidAgentIdException {
			EnhancedACC acc = obtainAcc(agent, netid, portno);
			mAccManager.addAcc(agent, acc);
		}

		@Override
		public void deauthenticate(Agent agent) {
			mAccManager.removeAcc(agent);
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
		public boolean isTucsonNodeRunning(int port) {
			return TucsonNodeUtility.isTucsonNodeRunning(port);
		}

		@Override
		public TucsonOperationHandler getOperationHandler(Agent agent)
				throws NoTucsonAuthenticationException {
			if (!mAccManager.hasAcc(agent)) {
				throw new NoTucsonAuthenticationException(
						"The agent does not hold an ACC");
			}

			return new TucsonOperationHandler(mAccManager.getAcc(agent),
					TuCSoNService.this);
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
				throw new ServiceException(
						"Unexpected error on retrieving local node");
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
			switch (cmdName) {
			case TuCSoNSlice.H_FINDTUPLECENTRE:
				System.out
						.println("[TuCSoNSlice] called findTupleCentre with arg: "
								+ cmd.getParam(0));
				try {
					String tsName = (String) cmd.getParam(0);
					TucsonTupleCentreId tcid = mTupleCentres.get(tsName);
					cmd.setReturnValue(tcid);
				} catch (Exception e) {
					// TODO: handle exception
				}
				break;

			default:
				System.out.println("[TuCSoNSlice] called " + cmdName);
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
		public void consume(VerticalCommand cmd) {}
	}

}
