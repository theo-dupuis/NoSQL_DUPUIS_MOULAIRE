package tse.info;

import tse.info.Call.Status;

public class Operator {
	private String id;
	private String nom;
	private String prenom;
	private Call call;
	
	
	
	
	public Operator(String id, String nom, String prenom) {
		super();
		this.id = id;
		this.nom = nom;
		this.prenom = prenom;
		this.call = null;
		redisConnection.createOperator(this);
	}

	
	public String toString()
	{
		if (call==null)
			return nom+" "+prenom+" identifiant:"+id;
		else
			return nom+" "+prenom+" identifiant:"+id+"\n en appel avec "+ call.getId();
	}
	
	public boolean takeCall(Call call)
	{
		if (this.call==null && call.getStatus()== Status.NOT_AFFECTED)
		{
			this.call = call;
			call.setOperator(this);
			return true;
		}
		return false;
	}
	
	public boolean takeCall()
	{
		if(this.call==null)
		{
			this.call =redisConnection.getFirstWaitingCall();
			if(this.call == null)
				return false;
			call.setOperator(this);
		}
		return false;
	}
	
	public void setCall(Call call)
	{
		if(call.getOperator()== this && call.getStatus()==Status.ON_GOING)
		{
			this.call = call;
		}
	}
	
	public void endCall()
	{
		if(this.call.getStatus() != Status.FINISHED)
			this.call.finish();
		this.call = null;
	}

	public String getId() {
		return id;
	}

	public String getNom() {
		return nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public Call getCall() {
		return call;
	}
	
	
}
