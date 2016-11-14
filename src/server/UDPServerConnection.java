package server;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import connection.ConnectionException;
import connection.ServerConnectionInterface;

/**
 * Server connection over UDP.
 * @author William Phillips (ScruffyCoder@users.noreply.github.com)
 */
public class UDPServerConnection implements ServerConnectionInterface
{
	private DatagramChannel channel;
	
	public UDPServerConnection(int port) throws ConnectionException
	{
		try
		{
			channel = DatagramChannel.open();
			channel.configureBlocking(false);
			channel.bind(new InetSocketAddress(port));
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
	public Object receive(ByteBuffer data) throws ConnectionException
	{
		try
		{
			return channel.receive(data);
		}
		catch (IOException e)
		{
			throw new ConnectionException(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	@Override
	public boolean send(Object key, ByteBuffer data) throws ConnectionException
	{
		try
		{
			int count = channel.send(data, (SocketAddress)key);
			
			return (count != 0);
		}
		catch (IOException e)
		{
			throw new ConnectionException(e.getClass().getName() + ": " + e.getMessage());
		}
	}
}
