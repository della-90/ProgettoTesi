package it.unibo.ing2.jade.operations;

import alice.logictuple.LogicTuple;
import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;

public class Out extends TucsonOrdinaryAction {

	public Out(TucsonTupleCentreId tcid, LogicTuple tuple) {
		super(tcid, tuple);
	}

	@Override
	protected ITucsonOperation executeSynch(EnhancedSynchACC acc, Long timeout) throws Exception{
		return acc.out(tcid, tuple, timeout);
	}

	@Override
	protected ITucsonOperation executeAsynch(EnhancedAsynchACC acc,
			TucsonOperationCompletionListener listener) throws Exception {
		return acc.out(tcid, tuple, listener);
	}

}
