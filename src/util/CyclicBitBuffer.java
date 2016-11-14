package util;
/**
 * Cyclic bit buffer. Used to track history in a bit array format.
 * @author William Phillips (ScruffyCoder@users.noreply.github.com)
 */
public class CyclicBitBuffer
{
	private int newest, position;
	private byte[] history;
	private int historySize;
	private boolean firstReceived;
	
	public CyclicBitBuffer(int historySize)
	{
		if (historySize <= 0)
		{
			throw new IllegalArgumentException("History size must be a non-zero, positive integer.");
		}
		
		int historyBytes = (historySize / 8);
		if ((historySize & 0x07) != 0)
		{
			historySize++;
			historyBytes++;
		}
		
		this.newest = 0;
		this.position = 0;
		this.history = new byte[historyBytes];
		this.historySize = historySize;
		this.firstReceived = false;
	}
	
	public boolean addNew(int id)
	{
		if (!firstReceived)
		{
			newest = id;
			firstReceived = true;
			return true;
		}
		
		int distance = newest - id;
		
		if (distance < 0 && distance >= (historySize * -1))
		{
			setBit(-1, true);
			for (int i = 0; i > (distance + 1); i--)
			{
				setBit(i - 2, false);
			}
			
			newest = id;
			position += distance;
			if (position < 0)
			{
				position += historySize;
			}
			
			return true;
		}
		else if (distance > 0 && distance <= historySize)
		{
			if (!getBit(distance - 1))
			{
				setBit(distance - 1, true);
				return true;
			}
		}
		
		return false;
	}
	
	public byte[] get(int historySize)
	{
		int historyBytes = (historySize / 8);
		if ((historySize & 0x07) != 0)
		{
			historySize++;
			historyBytes++;
		}
		
		byte[] output = new byte[historyBytes + 4];
		output[0] = (byte)(newest >>> 24);
		output[1] = (byte)((newest >>> 16) & 0xFF);
		output[2] = (byte)((newest >>> 8) & 0xFF);
		output[3] = (byte)(newest & 0xFF);
		
		for (int i = 0; i < historySize; i++)
		{
			int byteOffset = (i / 8) + 4;
			int bitOffset = i % 8;
			
			if (getBit(i))
			{
				output[byteOffset] |= (0x80 >>> bitOffset);
			}
		}
		
		return output;
	}
	
	private boolean getBit(int offset)
	{
		offset += position;
		if (offset < 0)
		{
			offset += historySize;
		}
		else if (offset >= historySize)
		{
			offset -= historySize;
		}
		
		int byteOffset = offset / 8;
		int bitOffset = offset % 8;
		
		return ((history[byteOffset] & (0x80 >>> bitOffset)) != 0);
	}
	
	private void setBit(int offset, boolean newValue)
	{
		offset += position;
		if (offset < 0)
		{
			offset += historySize;
		}
		else if (offset >= historySize)
		{
			offset -= historySize;
		}
		
		int byteOffset = offset / 8;
		int bitOffset = offset % 8;
		
		if (newValue)
		{
			history[byteOffset] |= (0x80 >>> bitOffset);
		}
		else
		{
			history[byteOffset] &= 0xFF ^ (0x80 >>> bitOffset);
		}
	}
}
