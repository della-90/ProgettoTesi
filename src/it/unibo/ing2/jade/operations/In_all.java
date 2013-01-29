package it.unibo.ing2.jade.operations;

import alice.logictuple.LogicTuple;
import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;

public class In_all extends TucsonOrdinaryAction {

	public In_all(TucsonTupleCentreId tcid, LogicTuple tuple) {
		super(tcid, tuple);
	}

	@Override
	public ITucsonOperation executeSynch(EnhancedSynchACC acc, Long timeout)
			throws Exception {
		return acc.in_all(tcid, tuple, timeout);
	}

	@Override
	public ITucsonOperation executeAsynch(EnhancedAsynchACC acc,
			TucsonOperationCompletionListener listener) throws Exception {
		return acc.in_all(tcid, tuple, listener);
	}

}
