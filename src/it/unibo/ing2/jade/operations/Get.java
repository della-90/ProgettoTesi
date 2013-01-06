package it.unibo.ing2.jade.operations;

import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;

public class Get extends TucsonOrdinaryAction {

	public Get(TucsonTupleCentreId tcid) {
		super(tcid, null);
	}

	@Override
	public ITucsonOperation executeSynch(EnhancedSynchACC acc, Long timeout)
			throws Exception {
		return acc.get(tcid, timeout);
	}

	@Override
	public ITucsonOperation executeAsynch(EnhancedAsynchACC acc,
			TucsonOperationCompletionListener listener) throws Exception {
		return acc.get(tcid, listener);
	}

}
