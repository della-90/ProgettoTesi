package it.unibo.ing2.jade.operations;

import alice.logictuple.LogicTuple;
import alice.tucson.api.EnhancedACC;
import alice.tucson.api.TucsonTupleCentreId;

public abstract class TucsonOperation implements ITucsonOperation {
	
	protected TucsonTupleCentreId tcid;
	protected LogicTuple lt;
	
	public TucsonOperation(TucsonTupleCentreId tcid, LogicTuple lt){
		this.tcid = tcid;
		this.lt = lt;
	}

}
