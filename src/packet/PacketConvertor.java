package packet;

import java.nio.ByteBuffer;

/**
 * Converts between packet objects and packet data buffers.
 * @author William Phillips (ScruffyCoder@users.noreply.github.com)
 */
public class PacketConvertor
{
	private static final int PROTOCOL_ID = 0;
	
	/**
	 * Convert packet data buffer into a packet object.
	 * @param buffer Packet data buffer.
	 * @return Packet object, null if not a valid packet.
	 */
	public static Packet getPacket(ByteBuffer buffer)
	{
		if (buffer.remaining() >= 20 && buffer.getInt() == PROTOCOL_ID)
		{
			int packetId = buffer.getInt();
			int lastReceivedPacketId = buffer.getInt();
			int lastReceivedPacketBits = buffer.getInt();
			int payloadId = buffer.getShort() & 0xFFFF;
			int payloadSize = buffer.getShort() & 0xFFFF;
			
			if (payloadSize <= buffer.remaining())
			{
				return new Packet(packetId, lastReceivedPacketId,
						lastReceivedPacketBits, new Payload(payloadId, buffer, payloadSize));
			}
		}
		
		return null;
	}
	
	/**
	 * Convert packet object into a packet data buffer.
	 * @param buffer Buffer to put packet data.
	 * @param packet Packet object.
	 */
	public static void putPacket(ByteBuffer buffer, Packet packet)
	{
		buffer.putInt(PROTOCOL_ID);
		buffer.putInt(packet.id);
		buffer.putInt(packet.lastReceivedId);
		buffer.putInt(packet.lastReceivedBits);
		buffer.putShort((short)packet.payload.id);
		buffer.putShort((short)packet.payload.getDataLength());
		buffer.put(packet.payload.getData());
	}
}
