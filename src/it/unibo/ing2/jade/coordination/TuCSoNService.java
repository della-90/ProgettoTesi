package it.unibo.ing2.jade.coordination;

import it.unibo.ing2.jade.exceptions.NoTucsonAuthenticationException;
import it.unibo.ing2.jade.operations.TucsonOperationHandler;
import jade.core.Agent;
import jade.core.BaseService;
import jade.core.ServiceException;
import jade.core.ServiceHelper;
import alice.tucson.api.EnhancedACC;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonMetaACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonGenericException;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;

public class TuCSoNService extends BaseService {

	public static final String NAME = "it.unibo.ing2.jade.coordination.TuCSoN";
	private TuCSoNHelper mHelper = new TuCSoNHelperImpl();
	private TucsonAccManager mAccManager = TucsonAccManager.getInstance();

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public ServiceHelper getHelper(Agent a) throws ServiceException {
		return mHelper;
	}

	private class TuCSoNHelperImpl implements TuCSoNHelper {

		@Override
		public void init(Agent arg0) {

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

}
