package it.unibo.ing2.jade.operations;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jade.core.GenericCommand;
import it.unibo.ing2.jade.service.TuCSoNService;
import it.unibo.ing2.jade.service.TuCSoNSlice;
import alice.tucson.api.EnhancedACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;

public class TucsonOperationHandler {

	private EnhancedACC acc;
	private TuCSoNService service;
	private List<TucsonTupleCentreId> mVisitedTupleCentres;

	public TucsonOperationHandler(EnhancedACC acc, TuCSoNService service) {
		this.acc = acc;
		this.service = service;
		mVisitedTupleCentres = new LinkedList<>();
	}

	public ITucsonOperation executeSynch(TucsonAction action, Long timeout)
			throws Exception {
		if (action instanceof TucsonSpecificationAction) {
			// controlla permessi
			System.out.println("Specification action");
		}

		GenericCommand cmd = new GenericCommand(TuCSoNSlice.EXECUTE_SYNCH,
				TuCSoNService.NAME, null);
		cmd.addParam(action);
		cmd.addParam(acc);
		cmd.addParam(timeout);
		Object result = service.submit(cmd);

		// Controllo la presenza di eccezioni
		if (result instanceof Exception) {
			throw (Exception) result;
		}
		
		//Aggiungo il Tuple Centre all'elenco di tuple centres visitati
		addTupleCentre(action);
		//altrimenti restituisco il risultato
		return (ITucsonOperation) result;
	}

	public ITucsonOperation executeAsynch(TucsonAction action,
			TucsonOperationCompletionListener listener) throws Exception {
		if (action instanceof TucsonSpecificationAction){
			System.out.println("Specification action");
		}
		
		GenericCommand cmd = new GenericCommand(TuCSoNSlice.EXECUTE_ASYNCH, TuCSoNService.NAME, null);
		cmd.addParam(action);
		cmd.addParam(acc);
		cmd.addParam(listener);
		Object result = service.submit(cmd);
		
		//Controllo la presenza di eccezioni
		if (result instanceof Exception){
			throw (Exception) result;
		}
		
		addTupleCentre(action);
		return (ITucsonOperation) result;
	}

	
	private void addTupleCentre(TucsonAction action){
		TucsonTupleCentreId tcid = action.getTcid();
		if (!mVisitedTupleCentres.contains(tcid)){
			mVisitedTupleCentres.add(tcid);
		}
	}
	
	public TucsonTupleCentreId[] getVisitedTupleCentres(){
		TucsonTupleCentreId[] array = null;
		mVisitedTupleCentres.toArray(array);
		return array;
	}
}
