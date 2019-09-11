package am.utility.messagesending;

/**
 * A quick and simple implementation of a {@link Message} to be used
 * with {@link MessageDispatch}.
 * 
 * @author Cosmin Stroe
 *
 * @param <E> The type of object the message contains (the payload of this message).
 */
public class SimpleMessage<E> implements Message<E> {

	private String key;
	private E payload;
	
	public SimpleMessage(String key, E payload) {
		this.key = key;
		this.payload = payload;
	}
	
	@Override
	public String getKey() {
		return key;
	}

	@Override
	public E getPayload() {
		return payload;
	}

}
