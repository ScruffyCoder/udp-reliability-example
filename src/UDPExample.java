import java.nio.ByteBuffer;
import java.util.Scanner;

import client.UDPClientConnection;
import connection.ConnectionException;
import packet.Packet;
import packet.PacketConvertor;
import packet.Payload;
import server.ServerThread;
import server.UDPServerConnection;
import util.Utf8Convertor;

/**
 * Example server/client.
 * @author William Phillips (ScruffyCoder@users.noreply.github.com)
 */
public class UDPExample
{
	public static void main(String[] args)
	{
		Scanner keyboard = new Scanner(System.in);
		
		System.out.println("SERVER - Run Server");
		System.out.println("CLIENT - Run Client");
		System.out.println("EXIT - Exit");
		
		
		String command;
		try
		{
			do
			{
				System.out.println();
				System.out.print("Command>");
				command = keyboard.nextLine().toUpperCase();
				
				switch (command)
				{
					case "SERVER":
					{
						server(keyboard);
						break;
					}
					case "CLIENT":
					{
						client(keyboard);
						break;
					}
					default:
					{
						System.out.println("Unknown command.");
					}
				}
			}
			while (!command.equals("EXIT"));
		}
		catch (ConnectionException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Example server.
	 * @param keyboard Keyboard scanner.
	 * @throws ConnectionException Thrown when a fatal connection exception occurs.
	 */
	private static void server(Scanner keyboard) throws ConnectionException
	{
		UDPServerConnection serverConnection = null;
		try
		{
			System.out.print("Port Number>");
			int portNumber = Integer.parseInt(keyboard.nextLine());
			
			serverConnection = new UDPServerConnection(portNumber);
			ServerThread server = new ServerThread(serverConnection);
			server.run();
		}
		finally
		{
			if (serverConnection != null)
			{
				serverConnection.close();
			}
		}
	}
	
	/**
	 * Example client.
	 * @param keyboard Keyboard scanner.
	 * @throws ConnectionException Thrown when a fatal connection exception occurs.
	 */
	private static void client(Scanner keyboard) throws ConnectionException
	{
		UDPClientConnection clientConnection = null;
		try
		{
			System.out.print("Username>");
			String userName = keyboard.nextLine();
			System.out.print("Hostname>");
			String hostname = keyboard.nextLine();
			System.out.print("Port Number>");
			int portNumber = Integer.parseInt(keyboard.nextLine());
			
			clientConnection = new UDPClientConnection(hostname, portNumber);
			ByteBuffer buffer = ByteBuffer.allocate(512);
			int packetId = 0;
			
			// Send connection request.
			buffer.putShort((short)0); // Protocol Version
			Utf8Convertor.putUtf8(buffer, userName, 32); // Username
			buffer.flip();
			Packet newPacket = new Packet(packetId++, 0, 0, new Payload(1, buffer, buffer.remaining()));
			buffer.clear();
			PacketConvertor.putPacket(buffer, newPacket);
			buffer.flip();
			clientConnection.send(buffer);
			buffer.clear();
			
			String message;
			do
			{
				System.out.print("Message>");
				message = keyboard.nextLine();
				
				if (!message.equals(""))
				{
					// Send message.
					Utf8Convertor.putUtf8(buffer, message, 256); // Message
					buffer.flip();
					newPacket = new Packet(packetId++, 0, 0, new Payload(2, buffer, buffer.remaining()));
					buffer.clear();
					PacketConvertor.putPacket(buffer, newPacket);
					buffer.flip();
					clientConnection.send(buffer);
					buffer.clear();
					
					try
					{
						Thread.sleep(1000);
					}
					catch (InterruptedException e) {}
					
					while (clientConnection.receive(buffer))
					{
						buffer.clear();
					}
				}
			}
			while (!message.equals(""));
			
			// Disconnect.
			buffer.putInt(0); // Error code
			buffer.flip();
			newPacket = new Packet(packetId++, 0, 0, new Payload(0, buffer, buffer.remaining()));
			buffer.clear();
			PacketConvertor.putPacket(buffer, newPacket);
			buffer.flip();
			clientConnection.send(buffer);
			buffer.clear();
		}
		finally
		{
			if (clientConnection != null)
			{
				clientConnection.close();
			}
		}
	}
}
