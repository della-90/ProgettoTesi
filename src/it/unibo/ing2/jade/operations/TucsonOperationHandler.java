package it.unibo.ing2.jade.operations;

import it.unibo.ing2.jade.service.TuCSoNSlice;
import alice.tucson.api.EnhancedACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;

public class TucsonOperationHandler {
	
	private EnhancedACC acc;
	private TuCSoNSlice localSlice;
	
	public TucsonOperationHandler(EnhancedACC acc, TuCSoNSlice localSlice){
		this.acc = acc;
		this.localSlice = localSlice;
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
