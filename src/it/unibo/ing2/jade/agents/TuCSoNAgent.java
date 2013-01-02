package it.unibo.ing2.jade.agents;

import it.unibo.ing2.jade.operations.In;
import it.unibo.ing2.jade.operations.Out;
import it.unibo.ing2.jade.operations.Out_s;
import it.unibo.ing2.jade.operations.TucsonOperationHandler;
import it.unibo.ing2.jade.service.TuCSoNHelper;
import it.unibo.ing2.jade.service.TuCSoNService;
import jade.core.Agent;
import jade.core.ServiceException;
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
//		addBehaviour(new TuCSoNInitBehaviour());
		addBehaviour(new Prova());
	}
	
	private class Prova extends OneShotBehaviour {
		
		@Override
		public void action() {
			try {
				TuCSoNHelper helper = (TuCSoNHelper) getHelper(TuCSoNService.NAME);
				helper.foo();
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			
		}
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
				Out_s out_s = new Out_s(tcid, null, null, null);
				mOperationHandler.executeSynch(out_s, null);
				
				//Fine! rilascio ACC
				helper.deauthenticate(myAgent);
				
				//Termino nodo tucson
				helper.stopTucsonNode(20504);
			} catch (Exception ex){
				ex.printStackTrace();
			}

		}
	}

}
