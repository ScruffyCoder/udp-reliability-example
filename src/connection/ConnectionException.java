package connection;

/**
 * Connection exception, for fatal connection issues.
 * @author William Phillips (ScruffyCoder@users.noreply.github.com)
 */
public class ConnectionException extends Exception
{
	/**
	 * Create a new Connection Exception.
	 * @param message Error message.
	 */
	public ConnectionException(String message)
	{
		super(message);
	}
}
