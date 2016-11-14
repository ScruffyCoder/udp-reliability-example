package util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Converts between UTF-8 byte arrays and UTF-8 strings.
 * @author William Phillips (ScruffyCoder@users.noreply.github.com)
 */
public class Utf8Convertor
{
	public static boolean isValid(String string, int maxLength)
	{	
		return isValid(string.getBytes(StandardCharsets.UTF_8), maxLength);
	}
	
	public static boolean isValid(byte[] stringBytes, int maxLength)
	{	
		return (stringBytes.length <= maxLength);
	}
	
	public static String getUtf8(ByteBuffer buffer, int maxLength)
	{
		if (buffer.remaining() >= 2)
		{
			int stringLength = buffer.getShort() & 0xFFFF;
			
			if (stringLength <= buffer.remaining() && stringLength <= maxLength)
			{
				byte[] stringBytes = new byte[stringLength];
				buffer.get(stringBytes);
				return new String(stringBytes, StandardCharsets.UTF_8);
			}
		}
		
		return null;
	}
	
	public static boolean putUtf8(ByteBuffer buffer, String string, int maxLength)
	{	
		return putUtf8(buffer, string.getBytes(StandardCharsets.UTF_8), maxLength);
	}
	
	public static boolean putUtf8(ByteBuffer buffer, byte[] stringBytes, int maxLength)
	{
		if (isValid(stringBytes, maxLength))
		{
			buffer.putShort((short)stringBytes.length);
			buffer.put(stringBytes);
			return true;
		}
		
		return false;
	}
}
