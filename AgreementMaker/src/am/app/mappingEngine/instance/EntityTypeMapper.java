package am.app.mappingEngine.instance;

import java.util.Arrays;
import java.util.List;

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

	private static List<String> ignoreGateTypes = Arrays.asList(new String[]{
			"Token", "SpaceToken", "Split", "Sentence", "Lookup", "FirstPerson", "Identifier", "Temp", "QuotedText", "PleonasticIt"
	});

	public enum EntityType {
		UNKNOWN, PERSON, ORGANIZATION, LOCATION, COUNTRY, STATE_OR_PROVINCE, DATE, CITY, JOBTITLE, TITLE, MONEY, ADDRESS, NUMBER;
	};

	public static EntityType getEnumEntityType(String typeString) {
		if (ignoreGateTypes.contains(typeString))
			return EntityType.UNKNOWN;
		for (EntityType entityType : EntityType.values()) {
			if (matches(typeString, entityType)) return entityType;
		}

		log.warn("The entity type: '" + typeString + "' could not be mapped. UNKNOWN type assigned.");

		return EntityType.UNKNOWN;
	}

	private static boolean matches(String typeString, EntityType typeEnum){

		//GATE Type
		if (typeString.equalsIgnoreCase(typeEnum.name())) return true;

		//NIST Ontology Type
		if (typeString.contains("#")) {
			String typeSubstring = StringUtils.substringAfter(typeString, "#");
			if (typeSubstring.toString().equalsIgnoreCase(typeEnum.name()))
				return true;
		}
		if (typeString.contains("#Literal") && typeEnum == EntityType.UNKNOWN) return true;
		if (typeString.contains("#integer") && typeEnum == EntityType.NUMBER) return true;

		//wiki Nist type
		if (typeEnum.name().substring(0,3).equalsIgnoreCase(typeString)) return true;

		//hardcoded exceptions
		if (typeString.equals("UKN") && typeEnum == EntityType.UNKNOWN) return true;
		if (typeString.equals("GPE") && typeEnum == EntityType.LOCATION) return true;

		return false;
	}
}