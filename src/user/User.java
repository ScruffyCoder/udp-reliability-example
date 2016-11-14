package user;

/**
 * Connected user.
 * @author William Phillips (ScruffyCoder@users.noreply.github.com)
 */
public class User
{
	private Object key;
	private String userName;
	private Object data;
	
	public User(Object key, String userName, Object data)
	{
		this.key = key;
		this.userName = userName;
		this.data = data;
	}

	public Object getKey()
	{
		return key;
	}
	
	public String getUserName()
	{
		return userName;
	}
	
	public Object getData()
	{
		return data;
	}

	public void setData(Object data)
	{
		this.data = data;
	}
}
