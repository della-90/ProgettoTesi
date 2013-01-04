package it.unibo.ing2.jade.service;

import it.unibo.ing2.jade.coordination.TucsonAccManager;
import it.unibo.ing2.jade.coordination.TucsonNodeUtility;
import it.unibo.ing2.jade.exceptions.NoTucsonAuthenticationException;
import it.unibo.ing2.jade.operations.TucsonOperationHandler;
import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.BaseService;
import jade.core.ContainerID;
import jade.core.GenericCommand;
import jade.core.HorizontalCommand;
import jade.core.IMTPException;
import jade.core.MainContainer;
import jade.core.Node;
import jade.core.Profile;
import jade.core.ProfileException;
import jade.core.Service;
import jade.core.ServiceException;
import jade.core.ServiceHelper;
import jade.core.Sink;
import jade.core.VerticalCommand;

import java.util.LinkedHashMap;
import java.util.Map;

import alice.tucson.api.EnhancedACC;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonMetaACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonGenericException;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;

public class TuCSoNService extends BaseService {

	public static final String NAME = "it.unibo.ing2.jade.service.TuCSoN";
	private TuCSoNHelper mHelper = new TuCSoNHelperImpl();
	private TucsonAccManager mAccManager = TucsonAccManager.getInstance();
	private Map<String, TucsonTupleCentreId> mTupleCentres = new LinkedHashMap<>();
	private AgentContainer myContainer;
	
	//The local slice for this service
	private ServiceComponent localSlice = new ServiceComponent();
	private CommandSourceSink sourceSink = new CommandSourceSink();
	private CommandTargetSink targetSink = new CommandTargetSink();

	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public void init(AgentContainer ac, Profile p) throws ProfileException {
		super.init(ac, p);
		myContainer = ac;
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
	public void boot(Profile p) throws ServiceException {
		super.boot(p);
		
		if (p.isMain()){
			System.out.println("Main container! Running TuCSoNSliceImpl");	
		}
		
		try {
			TucsonTupleCentreId tcid = new TucsonTupleCentreId("provaa");
			mTupleCentres.put("prova", tcid);
		} catch (TucsonInvalidTupleCentreIdException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Sink getCommandSink(boolean direction) {
		if (direction == Sink.COMMAND_SOURCE){
			return sourceSink;
		}
		return targetSink;
	}
	
	@Override
	public Class getHorizontalInterface() {
		return TuCSoNSlice.class;
	}

	private class TuCSoNHelperImpl implements TuCSoNHelper {

		@Override
		public void init(Agent arg0) {
		}
		
		@Override
		public void foo() {
			try {
				//Ottengo lo slice principale
				TuCSoNSlice mainSlice = (TuCSoNSlice) getSlice(MAIN_SLICE);
				//ed eseguo su esso l'operazione
				TucsonTupleCentreId tcid = mainSlice.findTupleCentre("prova");
				System.out.println("[foo] found: "+tcid);
			} catch (ServiceException e) {
				e.printStackTrace();
			} catch (IMTPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private EnhancedACC obtainAcc(Agent agent, String netid, int portno) throws TucsonInvalidAgentIdException {
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
		public void authenticate(Agent agent, String netid, int portno) throws TucsonInvalidAgentIdException {
			EnhancedACC acc = obtainAcc(agent, netid, portno);
			mAccManager.addAcc(agent, acc);
		}
		
		@Override
		public void deauthenticate(Agent agent) {
			mAccManager.removeAcc(agent);
		}

		private EnhancedACC obtainAcc(Agent agent) throws TucsonInvalidAgentIdException {
			TucsonAgentId taid = new TucsonAgentId(agent.getLocalName());
			return TucsonMetaACC.getContext(taid);
		}

		@Override
		public TucsonTupleCentreId getTupleCentreId(String tupleCentreName, String netid, int portno) throws TucsonInvalidTupleCentreIdException {
			TucsonTupleCentreId tcid = new TucsonTupleCentreId(tupleCentreName, netid, new Integer(portno).toString());
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
		public TucsonOperationHandler getOperationHandler(Agent agent) throws NoTucsonAuthenticationException {
			if (!mAccManager.hasAcc(agent)){
				throw new NoTucsonAuthenticationException("The agent does not hold an ACC");
			}
			
			return new TucsonOperationHandler(mAccManager.getAcc(agent));
		}

	}
	
	private class ServiceComponent implements Service.Slice {

		@Override
		public Node getNode() throws ServiceException {
			try {
				return TuCSoNService.this.getLocalNode();
			} catch (IMTPException e) {
				//Should never happen; this is a local call
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
			switch (cmdName) {
			case TuCSoNSlice.H_FINDTUPLECENTRE:
				try {
				String tsName = (String) cmd.getParam(0);
				TucsonTupleCentreId tcid = mTupleCentres.get(tsName);
				cmd.setReturnValue(tcid);
				} catch (Exception e) {
					// TODO: handle exception
				}
				break;
			default:
				break;
			}
			
			return result;
		}
		
	}
	
	private class CommandSourceSink implements Sink {

		@Override
		public void consume(VerticalCommand cmd) {
			String cmdName = cmd.getName();
//			if (cmdName.equals(TuCSoNSlice.H_FINDTUPLECENTRE)){
				System.out.println(cmd.getName()+" on CommandSourceSink");
//			}
		}
		
	}
	
	private class CommandTargetSink implements Sink {

		@Override
		public void consume(VerticalCommand cmd) {
			String cmdName = cmd.getName();
//			if (cmdName.equals(TuCSoNSlice.H_FINDTUPLECENTRE)){
				System.out.println(cmd.getName()+" on CommandTargetSink");
//			}
		}
		
	}

}
