package connection;
import java.nio.ByteBuffer;

/**
 * Client connection interface.
 * @author William Phillips (ScruffyCoder@users.noreply.github.com)
 */
public interface ClientConnectionInterface
{
	/**
	 * Disconnect the connection.
	 */
	public void close();
	
	/**
	 * Receive packet from the remote host.
	 * @param data ByteBuffer to store the packet data.
	 * @return True if packet data was received, false if no packet was received.
	 * @throws ConnectionException Thrown when a fatal connection exception occurs.
	 */
	public boolean receive(ByteBuffer data) throws ConnectionException;
	
	/**
	 * Send packet to the remote host.
	 * @param data ByteBuffer of packet data to send.
	 * @return True if packet data was sent, false if sending packet data failed.
	 * @throws ConnectionException Thrown when a fatal connection exception occurs.
	 */
	public boolean send(ByteBuffer data) throws ConnectionException;
}
