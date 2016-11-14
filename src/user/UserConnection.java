package user;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import packet.Payload;
import util.CyclicBitBuffer;

/**
 * Connection attached to a user.
 * @author William Phillips (ScruffyCoder@users.noreply.github.com)
 */
public class UserConnection
{
	private int nextSendId;
	private CyclicBitBuffer receiveBuffer;
	private HashMap<Integer,SendRecord> sendRecords;
	
	public UserConnection()
	{
		nextSendId = 0;
		receiveBuffer = new CyclicBitBuffer(128);
		sendRecords = new HashMap<>();
	}

	public int addSendRecord(Payload payload)
	{
		int packetId = nextSendId++;
		long sendTime = System.nanoTime();
		sendRecords.put(packetId, new SendRecord(packetId, sendTime, payload));
		
		return packetId;
	}
	
	public List<SendRecord> getNewlyConfirmedSendRecords(int remoteLastReceivedId, int remoteLastReceivedBits)
	{
		ArrayList<SendRecord> matches = new ArrayList<>();
		
		SendRecord match = sendRecords.remove(remoteLastReceivedId);
		if (match != null)
		{
			matches.add(match);
		}
		for (int i = 0; i < 32; i++)
		{
			int bitFlag = Integer.MIN_VALUE >>> i;
			if ((remoteLastReceivedBits & bitFlag) != 0)
			{
				match = sendRecords.remove(remoteLastReceivedId - (i + 1));
				if (match != null)
				{
					matches.add(match);
				}
			}
		}
		
		if (matches.size() == 0)
		{
			return null;
		}
		else
		{
			return matches;
		}
	}
	
	public boolean isNewlyReceived(int id)
	{
		return receiveBuffer.addNew(id);
	}
	
	public int[] getReceiveHistory()
	{
		int[] history = new int[2];
		byte[] historyBuffer = receiveBuffer.get(32);
		
		history[0] = (historyBuffer[0] << 24) | (historyBuffer[1] << 16) | (historyBuffer[2] << 8) | historyBuffer[3];
		history[1] = (historyBuffer[4] << 24) | (historyBuffer[5] << 16) | (historyBuffer[6] << 8) | historyBuffer[7];
		
		return history;
	}
}
