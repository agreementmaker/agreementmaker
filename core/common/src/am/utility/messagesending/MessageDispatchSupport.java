package am.utility.messagesending;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class implements a MessageDispatch.
 * All operations are thread safe because we just maintain
 * everything in ConcurrentHashMap data structures.
 * 
 * @author Cosmin Stroe <cstroe@gmail.com>
 *
 * @param <E> The type of objects the Messages will contain.
 */
public class MessageDispatchSupport<E> implements MessageDispatch<E> {

	private Set<MessageConsumer<E>> wildcardConsumers;
	private Map<String,Set<MessageConsumer<E>>> keyConsumersMap; 
	private Map<MessageConsumer<E>,Set<String>> consumerKeysMap;
	
	public MessageDispatchSupport() {
		wildcardConsumers = Collections.newSetFromMap(new ConcurrentHashMap<MessageConsumer<E>,Boolean>());
		keyConsumersMap = new ConcurrentHashMap<String,Set<MessageConsumer<E>>>();
		consumerKeysMap = new ConcurrentHashMap<MessageConsumer<E>,Set<String>>();
	}
	
	@Override
	public void publish(Message<E> message) {
				
		// first send the message to the wildcard consumers
		for( MessageConsumer<E> consumer : wildcardConsumers ) {
			consumer.consume(message);
		}
		
		// next send the message to the key consumers, 
		// but only if they are not already in the wildcard consumers
		String messageKey = message.getKey();
		Set<MessageConsumer<E>> keyConsumers = keyConsumersMap.get(messageKey);
		
		if( keyConsumers == null ) return; // noone is registered for this key
		
		for( MessageConsumer<E> consumer : keyConsumers ) {
			if( wildcardConsumers.contains(consumer) ) continue; // don't send the message twice
			consumer.consume(message);
		}
		
	}

	@Override
	public void addConsumer(MessageConsumer<E> consumer) {
		wildcardConsumers.add(consumer);
	}

	@Override
	public void addConsumer(MessageConsumer<E> consumer, String messageKey) {
		
		// update the keyConsumersMap
		if( keyConsumersMap.containsKey(messageKey) ) {
			Set<MessageConsumer<E>> existingSet = keyConsumersMap.get(messageKey);
			existingSet.add(consumer);
		}
		else { // create a new set to add to the map
			Set<MessageConsumer<E>> newSet = 
					Collections.newSetFromMap(new ConcurrentHashMap<MessageConsumer<E>,Boolean>());
			newSet.add(consumer);
			keyConsumersMap.put(messageKey, newSet);
		}
		
		// now update the consumerKeysMap
		if( consumerKeysMap.containsKey(consumer) ) {
			Set<String> existingKeys = consumerKeysMap.get(consumer);
			existingKeys.add(messageKey);
		}
		else { // create a new set to add to the map
			Set<String> newKeySet = 
					Collections.newSetFromMap(new ConcurrentHashMap<String,Boolean>());
			newKeySet.add(messageKey);
			consumerKeysMap.put(consumer, newKeySet);
		}
	}

	@Override
	public void removeConsumer(MessageConsumer<E> consumer) {
		wildcardConsumers.remove(consumer);
		
		if( !consumerKeysMap.containsKey(consumer) ) return;
		
		Set<String> keys = consumerKeysMap.get(consumer);
		consumerKeysMap.remove(consumer);
		
		for( String key : keys ) {
			Set<MessageConsumer<E>> consumers = keyConsumersMap.get(key);
			consumers.remove(consumer);
		}	
	}

}
