package tse.info;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;


import redis.clients.jedis.Jedis;
import tse.info.Call.Status;

public class redisConnection {
	static private Jedis jedis = new Jedis("localhost");
	
	static public void createOperator(Operator operator)
	{
		jedis.hset("operator:"+operator.getId(), "id", operator.getId());
		jedis.hset("operator:"+operator.getId(), "nom", operator.getNom());
		jedis.hset("operator:"+operator.getId(), "prenom", operator.getPrenom());
	}
	
	static public void createCall(Call call)
	{
		jedis.hset("call:"+call.getId(), "id", call.getId().toString());
		jedis.hset("call:"+call.getId(), "status", call.getStatus().name());
		jedis.hset("call:"+call.getId(), "number", call.getNumber());
		jedis.hset("call:"+call.getId(), "duration", Integer.toString(call.getDuration()));
		jedis.hset("call:"+call.getId(), "description", call.getDescription());
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss");
		jedis.hset("call:"+call.getId(), "timestamp", df.format(call.getTimestamp()));
		
		if(call.getOperator()==null)
			jedis.hset("call:"+call.getId(), "operator", "");
		else
			jedis.hset("call:"+call.getId(), "operator", call.getOperator().getId());
		
		if (call.getStatus() == Status.ON_GOING)
		{
			jedis.sadd("on_going_calls", call.getId().toString());
			jedis.set("operator_call:"+call.getOperator().getId(), call.getId().toString());
		}
		else
		{
			if(call.getStatus() == Status.NOT_AFFECTED)
			{
				jedis.zadd("waiting_calls", call.getTimestamp().getTime(), call.getId().toString());
			}
		}
	}
	
	static public void updateDescription(Call call)
	{
		jedis.hset("call:"+call.getId(), "description", call.getDescription());
	}
	
	static public void updateStatus(Call call)
	{
		jedis.hset("call:"+call.getId(), "status", call.getStatus().name());
		
		switch(call.getStatus())
		{
		case FINISHED:
			jedis.hset("call:"+call.getId(), "duration", Integer.toString(call.getDuration()));
			jedis.hset("call:"+call.getId(), "operator", call.getOperator().getId());
			jedis.del("operator_call:"+call.getOperator().getId());
			jedis.srem("on_going_calls", call.getId().toString());
			break;
		case ON_GOING:
			jedis.hset("call:"+call.getId(), "operator", call.getOperator().getId());
			jedis.set("operator_call:"+call.getOperator().getId(), call.getId().toString());
			jedis.zrem("waiting_calls", call.getId().toString());
			jedis.sadd("on_going_calls", call.getId().toString());
			break;
		case MISSED:
			jedis.zrem("waiting_calls", call.getId().toString());
			break;
		default:
			break;
		}
		updateDescription(call);
	}
	
	static public Set<String> getWaitingCalls()
	{
		return jedis.zrange("waiting_calls", 0, -1);
	}
	
	static public Call getFirstWaitingCall()
	{
		Set<String> waitings = jedis.zrange("waiting_calls", 0, 0);
		if(waitings.isEmpty())
			return null;
		return getCall(UUID.fromString(jedis.zrange("waiting_calls", 0, 0).iterator().next()));
	}
	
	static public Set<String> getOnGoingCalls()
	{
		return jedis.smembers("on_going_calls");
	}
	
	static public Call getCallByOperator(Operator operator)
	{
		return getCall(UUID.fromString(jedis.get("operator_call:"+ operator.getId())));
	}
	
	static public Operator getOperator(String id)
	{
		if (jedis.exists("operator:"+id))
		{
			String nom = jedis.hget("operator:"+id, "nom");
			String prenom = jedis.hget("operator:"+id, "prenom");
			
			Operator operator = new Operator(id, nom, prenom);

			App.operators.put(operator.getId(), operator);
			if (jedis.exists("operator_call:"+id))
			{
				if (App.calls.containsKey(jedis.get("operator_call:"+id)))
				{
					operator.setCall((App.calls.get(jedis.get("operator_call:"+id))));
				}
				else
					operator.setCall(getCall(UUID.fromString(jedis.get("operator_call:"+id))));
			}
			return operator;
		}
		return null;
	}
	
	static public List<Operator> getOperators()
	{
		Set<String> operatorsId = jedis.keys("operator:*");
		List<Operator> operators = new ArrayList<Operator>();
		for(String id : operatorsId)
		{
			 operators.add(getOperator(id));
		}
		return operators;
	}
	
	static public Call getCall(UUID id)
	{
		
		if (jedis.exists("call:"+id))
		{
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss");
			Date timestamp = null;
			
			try {
				timestamp = df.parse(jedis.hget("call:"+id, "timestamp"));
			} catch (ParseException e) {
				System.out.println(jedis.hget("call:"+id, "timestamp"));
				System.out.println("wrong timestamp format. Format must be yyyy/MM/dd-hh:mm:ss");
			}
			String number = jedis.hget("call:"+id, "number");
			Status status = Status.valueOf(jedis.hget("call:"+id, "status"));
			
			
			int duration = Integer.parseInt(jedis.hget("call:"+id, "duration"));
			Operator operator;
			if (App.operators.containsKey(jedis.hget("call:"+id, "operator")))
			{
				operator = App.operators.get(jedis.hget("call:"+id, "operator"));
			}
			else
			{
				operator = getOperator(jedis.hget("call:"+id, "operator"));
			}
			String description = jedis.hget("call:"+id, "description");
			Call call = new Call(id, timestamp, number, status, duration, operator, description);
			App.calls.put(call.getId(), call);
			return call;
		}
		return null;
	}
	
	static public List<Call> getCalls()
	{
		Set<String> callsId = jedis.keys("call:*");
		List<Call> calls = new ArrayList<Call>();
		for(String id : callsId)
		{
			 id = id.split(":")[1];
			 calls.add(getCall(UUID.fromString(id)));
		}
		return calls;
	}
}
