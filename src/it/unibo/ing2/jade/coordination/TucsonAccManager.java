package it.unibo.ing2.jade.coordination;

import jade.core.AID;
import jade.core.Agent;

import java.util.LinkedHashMap;
import java.util.Map;

import alice.tucson.api.EnhancedACC;

public class TucsonAccManager {
	
	protected Map<AID, EnhancedACC> mAccs;
	protected static TucsonAccManager instance;
	
	protected TucsonAccManager(){
		mAccs = new LinkedHashMap<>();
	}
	
	public static TucsonAccManager getInstance(){
		if (instance == null){
			instance = new TucsonAccManager();
		}
		return instance;
	}
	
	public void addAcc(Agent agent, EnhancedACC acc){
		mAccs.put(agent.getAID(), acc);
	}
	
	public void removeAcc(Agent agent){
		mAccs.remove(agent.getAID());
	}
	
	public EnhancedACC getAcc(Agent agent){
		return mAccs.get(agent.getAID());
	}
	
	public boolean hasAcc(Agent agent){
		return mAccs.containsKey(agent.getAID());
	}

}
