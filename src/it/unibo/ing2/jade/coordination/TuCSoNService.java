package it.unibo.ing2.jade.coordination;

import alice.tucson.api.EnhancedACC;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonMetaACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import jade.core.Agent;
import jade.core.BaseService;
import jade.core.ServiceException;
import jade.core.ServiceHelper;

public class TuCSoNService extends BaseService {

	public static final String NAME = "it.unibo.ing2.jade.coordination.TuCSoN";
	private TuCSoNHelper mHelper = new TuCSoNHelperImpl();
	
	
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
		public EnhancedACC obtainAcc(String aid, String netid, int portno) throws TucsonInvalidAgentIdException {
			TucsonAgentId taid = new TucsonAgentId(aid);
			EnhancedACC acc = TucsonMetaACC.getContext(taid, netid, portno);
			return acc;
		}

		@Override
		public EnhancedACC obtainAcc(String aid) throws TucsonInvalidAgentIdException {
			TucsonAgentId taid = new TucsonAgentId(aid);
			EnhancedACC acc = TucsonMetaACC.getContext(taid);
			return acc;
		}

		@Override
		public TucsonTupleCentreId getTupleCentreId(String tupleCentreName,
				String netid, int portno) throws TucsonInvalidTupleCentreIdException {
			TucsonTupleCentreId tcid = new TucsonTupleCentreId(tupleCentreName, netid, new Integer(portno).toString());
			return tcid;
		}
		
	}

}
