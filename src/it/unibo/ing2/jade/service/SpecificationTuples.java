package it.unibo.ing2.jade.service;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;

public final class SpecificationTuples {

	public static LogicTuple[] events;
	public static LogicTuple[] guards;
	public static LogicTuple[] reactions;

	static {
		try {
			events = new LogicTuple[] { LogicTuple
					.parse("out(wanna_move(Destination, TupleCentreName, Template))") };
			guards = new LogicTuple[] { LogicTuple
					.parse("(from_agent, completion)") };
			
			//TODO: modificare la out_all con il "ciclo" in Prolog
			reactions = new LogicTuple[] { LogicTuple
					.parse("in(wanna_move(Destination, TupleCentreName, Template)), rd_all(Template, TupleList), Destination ? out_all(TupleList)") };
			
			assert (events.length==guards.length)&&(events.length==reactions.length) : "Errore nelle tuple di specifica";
		} catch (InvalidLogicTupleException e) {
			// Should be never thrown
		}
	}
}
