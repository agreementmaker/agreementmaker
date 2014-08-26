package am.matcher.lod.LinkedOpenData;

import java.io.File;

import am.app.Core;

/**
 * Contains the paths for various LOD reference alignments.
 * These paths are relative to the AM_ROOT folder, and should be retrieved with {@link #getPath()}.
 */
public enum LODReferences {
	MUSIC_BBC			("LOD/LOD-Reference-Alignment/music_bbcprogram.txt"),
	MUSIC_DBPEDIA		("LOD/LOD-Reference-Alignment/music_dbpedia.txt"),
	SIOC_FOAF			("LOD/LOD-Reference-Alignment/sioc_foaf.txt"),
	GEONAMES_DBPEDIA	("LOD/LOD-Reference-Alignment/geonames_DBpedia.txt"),
	SWC_AKT				("LOD/LOD-Reference-Alignment/swc_akt.txt"),
	SWC_DBPEDIA			("LOD/LOD-Reference-Alignment/swc_dbpedia.txt"),
	FOAF_DBPEDIA		("LOD/LOD-Reference-Alignment/foaf_dbpedia.txt"),
	
	MUSIC_BBC_EQ		("LOD/LOD-Reference-Alignment/Equality/music_bbcprogram.txt"),
	MUSIC_DBPEDIA_EQ	("LOD/LOD-Reference-Alignment/Equality/music_dbpedia.txt"),
	SIOC_FOAF_EQ		("LOD/LOD-Reference-Alignment/Equality/sioc_foaf.txt"),
	GEONAMES_DBPEDIA_EQ	("LOD/LOD-Reference-Alignment/Equality/geonames_DBpedia.txt"),
	SWC_AKT_EQ			("LOD/LOD-Reference-Alignment/Equality/swc_akt.txt"),
	SWC_DBPEDIA_EQ		("LOD/LOD-Reference-Alignment/Equality/swc_dbpedia.txt"),
	FOAF_DBPEDIA_EQ		("LOD/LOD-Reference-Alignment/Equality/foaf_dbpedia.txt"),
	
	SIOC_FOAF_FIXED		("LOD/ReferenceAlignmentFixed/sioc_foaf.txt");
	
	private String ref;
	
	private LODReferences(String ref) {
		this.ref = ref;
	}
	
	public String getPath() {
		String root = Core.getInstance().getRoot();
		if( root != null )
			return root + File.separator + ref;
		else
			return ref;
	}
}
