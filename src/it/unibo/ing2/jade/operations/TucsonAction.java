package it.unibo.ing2.jade.operations;

import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;

public abstract class TucsonAction {
	
	protected TucsonTupleCentreId tcid;

	public TucsonAction(TucsonTupleCentreId tcid){
		this.tcid = tcid;
	}
	
	public abstract ITucsonOperation executeSynch(EnhancedSynchACC acc, Long timeout) throws Exception;
	public abstract ITucsonOperation executeAsynch(EnhancedAsynchACC acc, TucsonOperationCompletionListener listener) throws Exception;
}
