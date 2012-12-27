package it.unibo.ing2.jade.operations;

import alice.tucson.api.EnhancedACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;

public class TucsonOperationHandler {
	
	private EnhancedACC acc;
	
	public TucsonOperationHandler(EnhancedACC acc){
		this.acc = acc;
	}
	
	public ITucsonOperation executeSynch(TucsonAction action, Long timeout) throws Exception{
		if (action instanceof TucsonSpecificationAction){
			//controlla permessi
			System.out.println("Specification action");
		}
		return action.executeSynch(acc, timeout);
	}
	
	public ITucsonOperation executeAsynch(TucsonAction action, TucsonOperationCompletionListener listener) throws Exception {
		return action.executeAsynch(acc, listener);
	}

}
