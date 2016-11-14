package client;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import connection.ClientConnectionInterface;
import connection.ConnectionException;

/**
 * Client connection over UDP.
 * @author William Phillips (ScruffyCoder@users.noreply.github.com)
 */
public class UDPClientConnection implements ClientConnectionInterface
{
	private DatagramChannel channel;
	
	/**
	 * Create a client connection over UDP.
	 * @param hostname Remote hostname.
	 * @param port Remote host port.
	 * @throws ConnectionException Thrown when a fatal connection exception occurs.
	 */
	public UDPClientConnection(String hostname, int port) throws ConnectionException
	{
		try
		{
			channel = DatagramChannel.open();
			channel.configureBlocking(false);
			channel.connect(new InetSocketAddress(hostname, port));
		}
		catch (IOException e)
		{
			throw new ConnectionException(e.getClass().getName() + ": " + e.getMessage());
		}
	}
	
	@Override
	public void close()
	{
		try
		{
			channel.close();
		}
		catch (IOException e) {}
	}

	@Override
	public boolean receive(ByteBuffer data) throws ConnectionException
	{
		try
		{
			return (channel.read(data) != 0);
		}
		catch (IOException e)
		{
			throw new ConnectionException(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	@Override
	public boolean send(ByteBuffer data) throws ConnectionException
	{
		try
		{
			int count = channel.write(data);
			
			return (count != 0);
		}
		catch (IOException e)
		{
			throw new ConnectionException(e.getClass().getName() + ": " + e.getMessage());
		}
	}
}
