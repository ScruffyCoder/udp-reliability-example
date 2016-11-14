package packet;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Payload containing data.
 * @author William Phillips (ScruffyCoder@users.noreply.github.com)
 */
public class Payload
{
	/**
	 * Payload id.
	 */
	public final int id;

	private byte[] data;
	
	/**
	 * Create a new payload.
	 * @param id Payload id.
	 * @param buffer Data buffer containing payload data.
	 * @param length Length of the data to write into the buffer.
	 */
	public Payload(int id, ByteBuffer buffer, int length)
	{
		this.id = id;
		this.data = new byte[length];
		buffer.get(data);
	}
	
	/**
	 * Get the byte length of the payload.
	 * @return Count of bytes in payload.
	 */
	public int getDataLength()
	{
		return data.length;
	}
	
	/**
	 * Get the payload data.
	 * @return Payload data as a byte array.
	 */
	public byte[] getData()
	{
		return Arrays.copyOf(data, data.length);
	}
}
