package server;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import connection.ConnectionException;
import connection.ServerConnectionInterface;
import packet.Packet;
import packet.PacketConvertor;
import packet.Payload;
import user.SendRecord;
import user.User;
import user.UserConnection;
import util.Utf8Convertor;

/**
 * Example server thread.
 * @author William Phillips (ScruffyCoder@users.noreply.github.com)
 */
public class ServerThread implements Runnable
{
	private static final long CYCLES_PER_SECOND = 30;
	private static final long MILLIS_PER_SECOND = 1000;
	private static final long NANOS_PER_SECOND = 1000000000;
	
	private static final long NANOS_PER_CYCLE = NANOS_PER_SECOND / CYCLES_PER_SECOND;
	private static final long NANOS_PER_MILLI = NANOS_PER_SECOND / MILLIS_PER_SECOND;
	private static final long MINIMUM_SLEEP = NANOS_PER_MILLI;
	
	private static final int PROTOCOL_VERSION = 0;
	
	private static final int DISCONNECT = 0;
	private static final int INVALID_PROTOCOL_VERSION = 1;
	private static final int USERNAME_TAKEN = 2;
	
	private ServerConnectionInterface connection;
	private volatile boolean running;
	private HashMap<Object,User> users;
	
	/**
	 * Create a new server thread.
	 * @param connection Server connection interface.
	 */
	public ServerThread(ServerConnectionInterface connection)
	{
		this.connection = connection;
		this.running = true;
		this.users = new HashMap<>();
	}
	
	/**
	 * Stop the server thread.
	 */
	public void stop()
	{
		running = false;
	}

	@Override
	public void run()
	{
		ByteBuffer buffer = ByteBuffer.allocate(512);
		
		System.out.println("Running...");
		
		try
		{
			int cycleCount = 0;
			long difference = 0;
			
			while (running)
			{
				long startTime = System.nanoTime();
				
				Object key;
				Packet packet;
				do
				{
					key = connection.receive(buffer);
					if (key != null)
					{
						buffer.flip();
						
						packet = PacketConvertor.getPacket(buffer);
						buffer.clear();
						if (packet != null)
						{
							handlePacket(buffer, key, packet);
						}
					}
				}
				while (key != null);
				
				long runTime = System.nanoTime() - startTime;
				
				long sleepGoal = NANOS_PER_CYCLE - runTime + difference;
				if (sleepGoal >= MINIMUM_SLEEP)
				{
					long sleepStartTime = System.nanoTime();
					try
					{
						Thread.sleep(sleepGoal / NANOS_PER_MILLI, (int)(sleepGoal % NANOS_PER_MILLI));
					}
					catch (InterruptedException e) {}
					
					long sleepTime = System.nanoTime() - sleepStartTime;
					
					difference = sleepGoal - sleepTime;
				}
				else
				{
					difference = sleepGoal;
				}
				
				cycleCount++;
			}
		}
		catch (ConnectionException e)
		{
			e.printStackTrace();
		}
		finally
		{
			connection.close();
		}
		
		System.out.println("Stopped.");
	}
	
	/**
	 * Logic to handle packets.
	 * @param buffer ByteBuffer for temporary storage. Should be clear before returning.
	 * @param key Key for remote host.
	 * @param packet Packet data.
	 * @throws ConnectionException Thrown when a fatal connection exception occurs.
	 */
	private void handlePacket(ByteBuffer buffer, Object key, Packet packet) throws ConnectionException
	{
		User user = users.get(key);
		UserConnection userConnection = null;
		if (user != null)
		{
			userConnection = (UserConnection)user.getData();
			
			if (!userConnection.isNewlyReceived(packet.id))
			{
				// Duplicate Packet
				return;
			}
			else
			{
				userConnection.getNewlyConfirmedSendRecords(packet.lastReceivedId, packet.lastReceivedBits);
			}
		}
		
		switch (packet.payload.id)
		{
			case 0:
			{
				// Connection Terminated
				if (user != null)
				{
					users.remove(key);
					
					ByteBuffer payloadBuffer = getPayloadBuffer(packet.payload);
					int errorCode = payloadBuffer.getInt();
					
					String errorString;
					switch (errorCode)
					{
						case DISCONNECT: errorString = "User disconnected."; break;
						default: errorString = "Unknown cause.";
					}
					
					System.out.println("<SYSTEM> " + user.getUserName() + " disconnected. (Cause: " + errorString + ")");
				}
				break;
			}
			case 1:
			{
				// New User Request
				if (user == null)
				{
					ByteBuffer payloadBuffer = getPayloadBuffer(packet.payload);
					int protocolVersion = payloadBuffer.getShort() & 0xFFFF;
					String userName = Utf8Convertor.getUtf8(payloadBuffer, 32);

					if (protocolVersion != PROTOCOL_VERSION)
					{
						// Invalid Protocol Version
						buffer.putInt(INVALID_PROTOCOL_VERSION);
						buffer.flip();
						sendPacket(key, 0, packet.id, 0, 0, buffer);
						
						System.out.println("<SYSTEM> User failed to connect. (Cause: Invalid protocol version.)");
					}
					else if (userName != null && getUserByUserName(userName) != null)
					{
						// Username Taken
						buffer.putInt(USERNAME_TAKEN);
						buffer.flip();
						sendPacket(key, 0, packet.id, 0, 0, buffer);
						
						System.out.println("<SYSTEM> User failed to connect. (Cause: Username taken.)");
					}
					else if (userName != null)
					{
						// Add user
						userConnection = new UserConnection();
						userConnection.isNewlyReceived(packet.id);
						user = new User(key, userName, userConnection);
						users.put(key, user);
						
						System.out.println("<SYSTEM> " + user.getUserName() + " connected.");
					}
				}
				break;
			}
			case 2:
			{
				// User message
				if (user != null)
				{
					ByteBuffer payloadBuffer = getPayloadBuffer(packet.payload);
					String message = Utf8Convertor.getUtf8(payloadBuffer, 256);
					
					if (message != null)
					{
						message = "<" + user.getUserName() + "> " + message;
						Utf8Convertor.putUtf8(buffer, message, 356);
						buffer.flip();
						Payload newPayload = new Payload(2, buffer, buffer.remaining());
						buffer.flip();
						
						UserConnection sendUserConnection;
						for (User sendUser : users.values())
						{
							sendUserConnection = (UserConnection)sendUser.getData();
							int packetId = sendUserConnection.addSendRecord(newPayload);
							int[] receiveHistory = sendUserConnection.getReceiveHistory();
							sendPacket(sendUser.getKey(), packetId, receiveHistory[0], receiveHistory[1], 2, buffer);
							buffer.flip();
						}
						
						System.out.println(message);
					}
				}
				break;
			}
		}
		
		buffer.clear();
	}
	
	private void sendPacket(Object key, int packetId, int lastReceivedId, int lastReceivedBits, int payloadId, ByteBuffer payloadBuffer) throws ConnectionException
	{
		Payload newPayload = new Payload(payloadId, payloadBuffer, payloadBuffer.remaining());
		Packet newPacket = new Packet(packetId, lastReceivedId, lastReceivedBits, newPayload);
		payloadBuffer.clear();
		PacketConvertor.putPacket(payloadBuffer, newPacket);
		payloadBuffer.flip();
		connection.send(key, payloadBuffer);
	}
	
	private ByteBuffer getPayloadBuffer(Payload payload)
	{
		ByteBuffer buffer = ByteBuffer.allocate(payload.getDataLength());
		buffer.put(payload.getData());
		buffer.flip();
		
		return buffer;
	}
	
	private User getUserByUserName(String userName)
	{
		String searchString = userName.toUpperCase(Locale.ENGLISH);
		
		for (User user : users.values())
		{
			if (user.getUserName().toUpperCase(Locale.ENGLISH).equals(searchString))
			{
				return user;
			}
		}
		
		return null;
	}
}
