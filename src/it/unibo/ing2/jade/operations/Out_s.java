package it.unibo.ing2.jade.operations;

import alice.logictuple.LogicTuple;
import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;

public class Out_s extends TucsonSpecificationAction {

	public Out_s(TucsonTupleCentreId tcid, LogicTuple event, LogicTuple guards,
			LogicTuple reaction) {
		super(tcid, event, guards, reaction);
	}
	
	@Override
	protected ITucsonOperation executeSynch(EnhancedSynchACC acc, Long timeout)
			throws Exception {
		return acc.out_s(tcid, event, guards, reaction, timeout);
	}

	@Override
	protected ITucsonOperation executeAsynch(EnhancedAsynchACC acc,
			TucsonOperationCompletionListener listener) throws Exception {
		return acc.out_s(tcid, event, guards, reaction, listener);
	}

}
