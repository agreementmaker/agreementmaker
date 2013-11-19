package am.utility.messagesending;

/**
 * The consumer of a message.  The consumer receives
 * messages from the {@link MessageDispatch}.
 * 
 * @author Cosmin Stroe <cstroe@gmail.com>
 *
 * @param <E> The type of object this consumer can receive.
 */
public interface MessageConsumer<E> {

	public void consume(Message<E> message);
	
}
