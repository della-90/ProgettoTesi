package it.unibo.ing2.jade.operations;

import jade.core.GenericCommand;
import it.unibo.ing2.jade.service.TuCSoNService;
import it.unibo.ing2.jade.service.TuCSoNSlice;
import alice.tucson.api.EnhancedACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;

public class TucsonOperationHandler {
	
	private EnhancedACC acc;
	private TuCSoNService service;
	
	public TucsonOperationHandler(EnhancedACC acc, TuCSoNService service){
		this.acc = acc;
		this.service = service;
	}
	
	public ITucsonOperation executeSynch(TucsonAction action, Long timeout) throws Exception{
		if (action instanceof TucsonSpecificationAction){
			//controlla permessi
			System.out.println("Specification action");
		}
		
		GenericCommand cmd = new GenericCommand(TuCSoNSlice.EXECUTE_SYNCH, TuCSoNService.NAME, null);
		cmd.addParam(action);
		cmd.addParam(acc);
		cmd.addParam(timeout);
		ITucsonOperation result = (ITucsonOperation) service.submit(cmd);
		return result;
	}
	
	public ITucsonOperation executeAsynch(TucsonAction action, TucsonOperationCompletionListener listener) throws Exception {
		return action.executeAsynch(acc, listener);
	}

}
