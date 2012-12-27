package it.unibo.ing2.jade.agents;

import it.unibo.ing2.jade.coordination.TuCSoNHelper;
import it.unibo.ing2.jade.coordination.TuCSoNService;
import it.unibo.ing2.jade.operations.In;
import it.unibo.ing2.jade.operations.Out;
import it.unibo.ing2.jade.operations.TucsonOperationHandler;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import alice.logictuple.LogicTuple;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tuplecentre.core.TupleCentreOperation;

public class TuCSoNAgent extends Agent {
	
	@Override
	protected void setup() {
		super.setup();
		System.out.println("Agent "+getLocalName()+" started!");
		addBehaviour(new TuCSoNInitBehaviour());
	}
	
	private class TuCSoNInitBehaviour extends OneShotBehaviour {
		
		private TucsonOperationHandler mOperationHandler;
			
		@Override
		public void action() {
			try {
				System.out.println("Hello, i am "+myAgent.getName());
				TuCSoNHelper helper = (TuCSoNHelper) getHelper(TuCSoNService.NAME);
				System.out.println("isTucsonNodeRunning? "+helper.isTucsonNodeRunning(20504));
				
				if (!helper.isTucsonNodeRunning(20504)){
					System.out.println("No running TuCSoN instance. Launch a new one!");
					helper.startTucsonNode(20504);
				}
				
				//Ottengo ACC
				System.out.println("Obtaining ACC...");
				helper.authenticate(myAgent);
				System.out.println("ACC obtained!");
				
				//Creo operazione
				TucsonTupleCentreId tcid = helper.getTupleCentreId("default", "localhost" ,20504);
				LogicTuple tuple = LogicTuple.parse("msg('Hello, World')");
				Out out = new Out(tcid, tuple);
				mOperationHandler = helper.getOperationHandler(myAgent);
				ITucsonOperation result = mOperationHandler.executeSynch(out, null);
				
				System.out.println("Result = "+result.isOperationCompleted());
				tuple = LogicTuple.parse("msg(X)");
				In in = new In(tcid, tuple);
				
				TucsonOperationCompletionListener listener = new TucsonOperationCompletionListener() {
					
					@Override
					public void operationCompleted(TupleCentreOperation arg0) {
						
					}
					
					@Override
					public void operationCompleted(ITucsonOperation arg0) {
//						TuCSoNInitBehaviour.this.restart();
						System.out.println("Op completed");
					}
				};
				result = mOperationHandler.executeSynch(in,null);
				if (!result.isOperationCompleted()){
					block();
				} else {
					System.out.println(result.getLogicTupleResult());
				}
				
				//Fine! rilascio ACC
				helper.deauthenticate(myAgent);
			} catch (Exception ex){
				ex.printStackTrace();
			}

		}
	}

}
