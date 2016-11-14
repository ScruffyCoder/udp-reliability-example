package connection;
import java.nio.ByteBuffer;

/**
 * Server connection interface.
 * @author William Phillips (ScruffyCoder@users.noreply.github.com)
 */
public interface ServerConnectionInterface
{
	/**
	 * Disconnect the connection.
	 */
	public void close();
	
	/**
	 * Receive packet from the remote host.
	 * @param data ByteBuffer to store the packet data.
	 * @return Key of the remote host, null if no packet was received.
	 * @throws ConnectionException Thrown when a fatal connection exception occurs.
	 */
	public Object receive(ByteBuffer data) throws ConnectionException;
	
	/**
	 * Send packet to the remote host.
	 * @param data ByteBuffer of packet data to send.
	 * @return True if packet data was sent, false if sending packet data failed.
	 * @throws ConnectionException Thrown when a fatal connection exception occurs.
	 */
	public boolean send(Object key, ByteBuffer data) throws ConnectionException;
}
