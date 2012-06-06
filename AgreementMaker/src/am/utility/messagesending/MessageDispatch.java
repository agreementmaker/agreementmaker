package am.utility.messagesending;

public interface MessageDispatch<E> {

	/**
	 * Send a message via this dispatch.
	 * @param message Message to be sent.
	 */
	public void publish(Message<E> message);
	
	/**
	 * Register a consumer with this dispatch.  The consumer
	 * will receive all messages published to this dispatch.
	 * If the MessageConsumer is registered already to one 
	 * or more specific keys, this registration will take precedence
	 * over the others.  The consumer will receive one message
	 * for each publish, even if they are registered multiple 
	 * times (once for the wildcard and one .
	 */
	public void addConsumer(MessageConsumer<E> consumer);
	
	/**
	 * Register a consumer with this dispatch.  The consumer
	 * will receive only messages which match the messageKey.
	 * If the MessageConsumer is registered to all keys using
	 * the {@link #addConsumer(MessageConsumer)} method, that
	 * registration will take precedence.  The MessageConsumer
	 * will not receive a Message more than once.
	 * 
	 * @see {@link #addConsumer(MessageConsumer)}
	 */
	public void addConsumer(MessageConsumer<E> consumer, String messageKey);
	
	/**
	 * Unregister a consumer from this dispatch so that they no
	 * longer receive any messages.  All registrations of the 
	 * MessageConsumer will be removed, even if they are registered
	 * multiple times to different keys.
	 */
	public void removeConsumer(MessageConsumer<E> consumer);
}
