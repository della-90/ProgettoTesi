package it.unibo.ing2.jade.operations;

import alice.logictuple.LogicTuple;
import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

public abstract class TucsonOrdinaryAction extends TucsonAction {
	
	protected LogicTuple tuple;
	
	public TucsonOrdinaryAction(TucsonTupleCentreId tcid, LogicTuple tuple) {
		super(tcid);
		this.tuple = tuple;
	}

}
