package am.app.mappingEngine.instance;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 
 * This
 * 
 * @author Iman Mirrezaei
 * @author Daniele Alfarone
 *
 */
public class EntityTypeMapper {

	private static Logger log = Logger.getLogger(EntityTypeMapper.class);

	public enum EntityType {
		UNKNOWN, PERSON, ORGANIZATION, LOCATION, DATE, CITY;	
	};

	public static EntityType getEnumEntityType(String typeString) {
		for (EntityType entityType : EntityType.values()) {
			if (findMatchingType(typeString, entityType.toString())) return entityType;
		}

		log.warn("The entity type: '"+typeString +"' could not be mapped. UNKNOWN type assigned.");
		
		return EntityType.UNKNOWN;
	}

	private static boolean findMatchingType(String typeString, String typeEnum){

		//GATE Type
		if (typeString.equalsIgnoreCase(typeEnum)) return true;

		//NIST Ontology Type
		if (typeString.indexOf("#")>0) {
			String typeSubstring = StringUtils.substringAfter(typeString, "#");
			if (typeSubstring.toString().equalsIgnoreCase(typeEnum))
				return true;
		}

		//wiki Nist type
		if (typeEnum.substring(0,3).equalsIgnoreCase(typeString)) return true;

		return false;
	}
}
