package tse.info;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Call {
	

	enum Status {NOT_AFFECTED, MISSED, ON_GOING, FINISHED};
	
	private UUID id;
	private Date timestamp;
	private String number;
	private Status status;
	private int duration;
	private Operator operator;
	private String description;
	
	
	
	public Call(String number) {
		super();
		this.id = UUID.randomUUID();
		this.timestamp = new Date();
		this.number = number;
		this.duration = 0;
		this.status = Status.NOT_AFFECTED;
		this.operator = null;
		this.description = "";
		redisConnection.createCall(this);
	}
	
	
	
	public Call(UUID id, Date timestamp, String number, Status status, int duration, Operator operator,
			String description) {
		super();
		this.id = id;
		this.timestamp = timestamp;
		this.number = number;
		this.status = status;
		this.duration = duration;
		this.operator = operator;
		this.description = description;
	}



	public void setOperator(Operator operator)
	{
		this.operator = operator;
		status = Status.ON_GOING;
		redisConnection.updateStatus(this);
	}
	
	public void finish()
	{
		if (status == Status.NOT_AFFECTED)
		{
			status = Status.MISSED;
			return;
		}
		
		status = Status.FINISHED;
		duration = (int) (new Date().getTime() - timestamp.getTime()) /1000;
		operator.endCall();
		redisConnection.updateStatus(this);
	}
	
	public void setDescription(String description)
	{
		this.description = description;
		redisConnection.updateDescription(this);
	}

	public UUID getId() {
		return id;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getNumber() {
		return number;
	}

	public Status getStatus() {
		return status;
	}

	public int getDuration() {
		return duration;
	}

	public Operator getOperator() {
		return operator;
	}

	public String getDescription() {
		return description;
	}
	
	public String toString()
	{
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss");
		StringBuilder sb = new StringBuilder();
		sb = sb.append("id: "+ id);
		sb = sb.append("\nnumber: "+number);
		sb = sb.append("\ntimestamp: "+df.format(timestamp));
		sb = sb.append("\nstatus:" + status.name());
		
		if (operator!=null)
			sb = sb.append("\noperator: " + operator.getId());
		sb = sb.append("\ndescription: "+ description);
		return sb.toString();
	}
	
}
