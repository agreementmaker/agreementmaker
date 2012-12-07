/**
 * <p>This package has the interfaces and classes for supporting 
 * message sending between plain old java objects (POJO).</p>
 * 
 * <p>Ideally we would use Java Message Service (JMS) for this, but
 * its somewhat more complicated to learn, and it seemed faster design a simple
 * messaging system from scratch for our purposes.</p>
 * 
 * <p>{@link am.utility.messagesending.MessageDispatch} allows consumers to register
 * themselves to the dispatch, either as wildcard listeners or listeners to specific keys.  
 * Anyone can send a {@link am.utility.messagesending.Message} via the dispatch.</p>
 * 
 * <ul>
 * <li> Use {@link am.utility.messagesending.SimpleMessage} for a simple implementation of a message.</li>
 * <li> Use {@link am.utility.messagesending.MessageDispatchSupport} 
 *      for an implementation of a {@link am.utility.messagesending.MessageDispatch}</li>
 * </ul>
 * 
 * @author Cosmin Stroe <cstroe@gmail.com>
 */
package am.utility.messagesending;