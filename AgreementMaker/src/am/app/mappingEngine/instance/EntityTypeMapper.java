package am.app.mappingEngine.instance;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
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
			if (isMatch(typeString, entityType)) return entityType;
		}

		log.warn("The entity type: '" + typeString + "' could not be mapped. UNKNOWN type assigned.");
		
		return EntityType.UNKNOWN;
	}

	private static boolean isMatch(String typeString, EntityType typeEnum){

		//GATE Type
		if (typeString.equalsIgnoreCase(typeEnum.name())) return true;

		//NIST Ontology Type
		if (typeString.indexOf("#")>0) {
			String typeSubstring = StringUtils.substringAfter(typeString, "#");
			if (typeSubstring.toString().equalsIgnoreCase(typeEnum.name()))
				return true;
		}

		//wiki Nist type
		if (typeEnum.name().substring(0,3).equalsIgnoreCase(typeString)) return true;
		if( typeString.equals("UKN") && typeEnum == EntityType.UNKNOWN ) return true;
		if( typeString.equals("GPE") && typeEnum == EntityType.LOCATION ) return true;
		

		return false;
	}
}
