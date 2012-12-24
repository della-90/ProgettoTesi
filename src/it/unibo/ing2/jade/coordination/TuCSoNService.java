package it.unibo.ing2.jade.coordination;

import it.unibo.ing2.jade.exceptions.NoTucsonAuthenticationException;
import it.unibo.ing2.jade.operations.TucsonOperationHandler;
import jade.core.AID;
import jade.core.Agent;
import jade.core.BaseService;
import jade.core.ServiceException;
import jade.core.ServiceHelper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import java.util.HashMap;
import java.util.Map;
import alice.tucson.api.EnhancedACC;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonMetaACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonGenericException;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.service.TucsonNodeService;

public class TuCSoNService extends BaseService {

	public static final String NAME = "it.unibo.ing2.jade.coordination.TuCSoN";
	private final int TUCSON_PORT = 20504;
	private TuCSoNHelper mHelper = new TuCSoNHelperImpl();
	private Map<AID, EnhancedACC> mAgentACCs = new HashMap<>();

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

		@Override
		public EnhancedACC obtainAcc(Agent agent, String netid, int portno)
				throws TucsonInvalidAgentIdException {
			int endIndex = agent.getName().indexOf(":");
			String agentName = agent.getLocalName();
 			TucsonAgentId taid = new TucsonAgentId(agentName);
			EnhancedACC acc = TucsonMetaACC.getContext(taid, netid, portno);
			return acc;
		}

		@Override
		public EnhancedACC obtainAcc(Agent agent) throws TucsonInvalidAgentIdException {
			int beginIndex = agent.getName().indexOf("@");
			int endIndex = agent.getName().indexOf(":");
			return obtainAcc(agent, agent.getName().substring(beginIndex, endIndex) , TUCSON_PORT);
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
		public TucsonOperationHandler getOperationHandler(Agent agent) throws NoTucsonAuthenticationException {
			if (!mAgentACCs.containsKey(agent.getAID())){
				throw new NoTucsonAuthenticationException("The agent does not hold an ACC");
			}
			
			return new TucsonOperationHandler(mAgentACCs.get(agent.getAID()));
		}

	}

}
