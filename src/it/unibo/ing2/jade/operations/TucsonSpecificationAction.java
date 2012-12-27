package it.unibo.ing2.jade.operations;

import alice.logictuple.LogicTuple;
import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;

public abstract class TucsonSpecificationAction extends TucsonAction {
	
	protected LogicTuple event, guards, reaction;
	
	public TucsonSpecificationAction(TucsonTupleCentreId tcid, LogicTuple event, LogicTuple guards, LogicTuple reaction) {
		super(tcid);
		this.event = event;
		this.guards = guards;
		this.reaction = reaction;
	}

}
