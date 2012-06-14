package am.utility;

import java.util.Set;
import java.util.Map.Entry;

/**
 * <p>
 * An interface meant to be implemented by any object that can have metadata
 * attached to it, and would like to make it available to others. This is
 * basically an interface to a Key-Value store. A common method of implementing
 * this is to use {@link java.util.Properties}.
 * </p>
 * 
 * <p>
 * NOTE: This interface assumes Java String for keys and values.
 * </p>
 * 
 * @author Cosmin Stroe
 * 
 */
public interface HasMetadata {

	/**
	 * Retrieve the metadata associated with a key.
	 * 
	 * @param key
	 *            A string representing the key.
	 * @return The string value associated with this key.
	 */
	public String getMetadata(String key);

	/**
	 * Set the metadata for a specific key.
	 * 
	 * @param key
	 *            A string representing the key.
	 * @param value
	 */
	public void setMetadata(String key, String value);

	/**
	 * @return Returns the entry set of Key-Value entries. Even though the
	 *         generic parameters are &lt;Object,Object&gt; the objects inside
	 *         are just Strings.
	 */
	public Set<Entry<Object, Object>> getMetadataEntries();
}
