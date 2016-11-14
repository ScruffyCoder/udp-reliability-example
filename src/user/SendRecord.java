package user;
import packet.Payload;

/**
 * Record of payload being sent to the remote host.
 * @author William Phillips (ScruffyCoder@users.noreply.github.com)
 */
public class SendRecord
{
	private int packetId;
	private long sendTime;
	private Payload payload;
	
	public SendRecord(int packetId, long sendTime, Payload payload)
	{
		this.packetId = packetId;
		this.sendTime = sendTime;
		this.payload = payload;
	}

	public int getPacketId()
	{
		return packetId;
	}

	public void setPacketId(int packetId)
	{
		this.packetId = packetId;
	}

	public long getSendTime()
	{
		return sendTime;
	}

	public void setSendTime(long sendTime)
	{
		this.sendTime = sendTime;
	}

	public Payload getPayload()
	{
		return payload;
	}
}
