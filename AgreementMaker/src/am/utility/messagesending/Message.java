package am.utility.messagesending;

/**
 * A message to be send via the {@link MessageDispatch}.
 * A message has a key and a payload.  MessageConsumers can
 * listen for all messages or messages with a certain key.
 * 
 * @author Cosmin Stroe
 *
 * @param <E> The payload object type.
 */
public interface Message<E> {
	
	public String getKey();
	
	public E getPayload();
}
