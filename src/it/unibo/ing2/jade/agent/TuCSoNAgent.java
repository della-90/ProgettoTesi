package it.unibo.ing2.jade.agent;

import it.unibo.ing2.jade.coordination.TuCSoNHelper;
import it.unibo.ing2.jade.coordination.TuCSoNService;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.OneShotBehaviour;
import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

public class TuCSoNAgent extends Agent {
	
	@Override
	protected void setup() {
		super.setup();
		System.out.println("Agent "+getLocalName()+" started!");
		addBehaviour(new TuCSoNInitBehaviour());
	}
	
	private class TuCSoNInitBehaviour extends OneShotBehaviour {
			
		@Override
		public void action() {
			try {
				TuCSoNHelper helper = (TuCSoNHelper) getHelper(TuCSoNService.NAME);
				System.out.println("isTucsonNodeRunning? "+helper.isTucsonNodeRunning(20504));
				
				if (!helper.isTucsonNodeRunning(20504)){
					return;
				}
				SynchACC acc = helper.obtainAcc(myAgent.getLocalName());
				TucsonTupleCentreId tcid = helper.getTupleCentreId("tuple_centre", "localhost", 20504);
				LogicTuple tuple = LogicTuple.parse("msg(X)");
				ITucsonOperation result = acc.in(tcid, tuple, null);
				System.out.println("Is operation completed? "+result.isOperationCompleted());
				if (result.isOperationCompleted()){
					LogicTuple r = result.getLogicTupleResult();
					System.out.println("Arity = "+r.getArity());
					System.out.println("Argument 0 = "+r.getArg(0));
				}
				acc.exit();
			} catch (ServiceException e) {
				System.err.println("Error on retrieving service: "+e.getMessage());
			} catch (TucsonInvalidAgentIdException e) {
				System.err.println("Error on agent id: "+e.getMessage());
			} catch (TucsonInvalidTupleCentreIdException e) {
				System.err.println("Error on tuple centre id: "+e.getMessage());
			} catch (InvalidLogicTupleException e) {
				System.err.println("Error on parsing logic tuple: "+e.getMessage());
			} catch (TucsonOperationNotPossibleException e) {
				
				e.printStackTrace();
			} catch (UnreachableNodeException e) {
				
				e.printStackTrace();
			} catch (OperationTimeOutException e) {
				
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
