package it.unibo.ing2.jade.operations;

import jade.core.IMTPException;
import jade.core.ServiceException;
import it.unibo.ing2.jade.exceptions.TupleCentreNotFoundException;
import it.unibo.ing2.jade.service.TuCSoNService;
import it.unibo.ing2.jade.service.TuCSoNSlice;
import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;

public abstract class TucsonAction {
	
	protected TucsonTupleCentreId tcid;
	protected String tupleCentreName;

	public TucsonAction(TucsonTupleCentreId tcid){
		this.tcid = tcid;
	}
	
	public TucsonAction(String tupleCentreName, TuCSoNService service) throws ServiceException, IMTPException, TupleCentreNotFoundException {
		this.tupleCentreName = tupleCentreName;
		
		TuCSoNSlice mainSlice = (TuCSoNSlice) service.getSlice(TuCSoNService.MAIN_SLICE);
		TucsonTupleCentreId tcid = mainSlice.findTupleCentre(tupleCentreName);
		if (tcid == null){
			throw new TupleCentreNotFoundException("Tuple centre not found");
		}
		
		this.tcid = tcid;
	}
	
	//FIXME: in teoria questi due metodi non dovrebbero essere pubblici! O forse si?
	public abstract ITucsonOperation executeSynch(EnhancedSynchACC acc, Long timeout) throws Exception;
	public abstract ITucsonOperation executeAsynch(EnhancedAsynchACC acc, TucsonOperationCompletionListener listener) throws Exception;
}
