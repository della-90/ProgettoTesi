package it.unibo.ing2.jade.agents;

import java.util.Arrays;

import it.unibo.ing2.jade.exceptions.NoTucsonAuthenticationException;
import it.unibo.ing2.jade.operations.In;
import it.unibo.ing2.jade.operations.Out;
import it.unibo.ing2.jade.operations.Out_s;
import it.unibo.ing2.jade.operations.TucsonAction;
import it.unibo.ing2.jade.operations.TucsonOperationHandler;
import it.unibo.ing2.jade.service.TuCSoNHelper;
import it.unibo.ing2.jade.service.TuCSoNService;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.OneShotBehaviour;
import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tuplecentre.core.TupleCentreOperation;

public class TuCSoNAgent extends Agent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
				
				//Mi autentico
				helper.authenticate(myAgent);
				//Ottengo l'handler per effettuare le operazioni
				TucsonOperationHandler handler = helper.getOperationHandler(myAgent);
				//Creo la tupla
				LogicTuple tuple = LogicTuple.parse("msg(helloworld)");
				//Scelgo il TC di destinazione della tupla
				TucsonTupleCentreId tcid = new TucsonTupleCentreId("default","localhost","20504");
				//Scelgo l'operazione
				TucsonAction action = new Out(tcid, tuple);
				//La eseguo
				handler.executeSynch(action, null);
				
				tuple = LogicTuple.parse("mess(himan)");
				action = new Out(tcid, tuple);
				handler.executeSynch(action, null);
				
				String[] tupleCentreNames = handler.getVisitedTupleCentreNames();
				System.out.println("Visited tuple centres: "+Arrays.toString(tupleCentreNames));
				helper.doMove("tempo", "msg(X)", tupleCentreNames);
				helper.doClone("prova", "mess(X)", tupleCentreNames);
				
			} catch (ServiceException e) {
				e.printStackTrace();
			} catch (NoTucsonAuthenticationException e) {
				e.printStackTrace();
			} catch (TucsonInvalidTupleCentreIdException e) {
				e.printStackTrace();
			} catch (InvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (Exception e) {
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
