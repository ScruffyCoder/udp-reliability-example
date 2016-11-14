package packet;

/**
 * Packet to be sent/received over the connection.
 * @author William Phillips (ScruffyCoder@users.noreply.github.com)
 */
public class Packet
{
	/**
	 * Packet id.
	 */
	public final int id;
	
	/**
	 * Last received packet id from the remote host.
	 */
	public final int lastReceivedId;
	
	/**
	 * Last received packet bit array from the remote host. (lastReceivedId - [1,33])
	 */
	public final int lastReceivedBits;
	
	/**
	 * Packet payload.
	 */
	public final Payload payload;
	
	/**
	 * Create a new packet.
	 * @param packetId Packet id.
	 * @param lastReceivedId Last received packet id from the remote host.
	 * @param lastReceivedBits Last received packet bit array from the remote host. (lastReceivedId - [1,33])
	 * @param payload Packet payload.
	 */
	public Packet(int packetId, int lastReceivedId, int lastReceivedBits, Payload payload)
	{
		this.id = packetId;
		this.lastReceivedId = lastReceivedId;
		this.lastReceivedBits = lastReceivedBits;
		this.payload = payload;
	}
}
