package am.extension.userfeedback.preset;

import java.io.File;
import java.io.Serializable;

import am.app.Core;

/**
 * Represents a matching task definition (a name and the ontologies to be matched).
 * 
 * @author <a href="http://cstroe.com">Cosmin Stroe</a>
 *
 */
public class MatchingTaskPreset implements Comparable<MatchingTaskPreset>, Serializable {

	private static final long serialVersionUID = -8532157957978948285L;
	
	private static final String AM_ROOT = "AM_ROOT/";
	
	private String name;
	private String sourceOnt;
	private String targetOnt;
	
	private boolean hasReference = false;
	private String reference;
	
	/**
	 * NOTE: We replace the AM_ROOT path with the string {@link #AM_ROOT}. This
	 * is changed back when the get methods are called. This is to allow
	 * transfering of these settings across computers.
	 * 
	 * @param sourceOnt
	 *            Path to the source ontology file. Expecting OWL in RDF/XML. If
	 *            the path is relative, it will be relative to AM_ROOT.
	 * @param targetOnt
	 *            Path to the target ontology file. Expecting OWL in RDF/XML. If
	 *            the path is relative, it will be relative to AM_ROOT.
	 * @param reference
	 *            Path to the reference alignment file. Expecting OAEI RDF
	 *            format. If the path is relative, it will be relative to
	 *            AM_ROOT.
	 */	
	public MatchingTaskPreset(String name, String sourceOnt, String targetOnt, String reference) {
		this.name = name;
		
		String root = Core.getInstance().getRoot();
		
		if( sourceOnt.startsWith(root) ) {
			sourceOnt = sourceOnt.replaceFirst(root, AM_ROOT);
		} else if( !sourceOnt.startsWith(File.separator) ) {
			sourceOnt = AM_ROOT + sourceOnt;
		}
		
		if( targetOnt.startsWith(root) ) {
			targetOnt = targetOnt.replaceFirst(root, AM_ROOT);
		} else if( !targetOnt.startsWith(File.separator) ) {
			targetOnt = AM_ROOT + targetOnt;
		}
		
		this.sourceOnt = sourceOnt;
		this.targetOnt = targetOnt;
		
		// set the reference
		if( reference != null ) {
			this.hasReference = true;
			
			if( reference.startsWith(root) ) {
				reference = reference.replaceFirst(root, AM_ROOT);
			} else if( !reference.startsWith(File.separator) ) {
				reference = AM_ROOT + reference;
			}
			
			this.reference = reference;
		}
	}
	
	/** Cloning constructor */
	public MatchingTaskPreset(MatchingTaskPreset p) {
		// TODO: Use reflection to do this.
		this.name = p.name;
		this.sourceOnt = p.sourceOnt;
		this.targetOnt = p.targetOnt;
		this.hasReference = p.hasReference;
		this.reference = p.reference;  
	}
	
	/**
	 * @return The name of this matching task.
	 */
	public String getName() { return name; }
	
	/**
	 * @return The path of the source ontology that we will use for this
	 *         experiment. If the path is relative, it will be relative to the
	 *         AM_ROOT.  Otherwise, the path must be absolute.
	 */
	public String getSourceOntology() { 
		if( sourceOnt.startsWith(AM_ROOT) ) {
			String root = Core.getInstance().getRoot();
			return sourceOnt.replaceFirst(AM_ROOT, root);
		}
		return sourceOnt; 
	}

	/**
	 * @return The path of the target ontology that we will use for this
	 *         experiment. If the path is relative, it will be relative to the
	 *         AM_ROOT.  Otherwise, the path must be absolute.
	 */
	public String getTargetOntology() {
		if( targetOnt.startsWith(AM_ROOT) ) {
			String root = Core.getInstance().getRoot();
			return targetOnt.replaceFirst(AM_ROOT, root);
		}
		return targetOnt;
	}
	
	/**
	 * @return true if there was a reference file specified for this matching task
	 */
	public boolean hasReference() {
		return hasReference;
	}
	
	/**
	 * @return The path to the reference alignment for this matching task.
	 */
	public String getReference() {
		if( reference.startsWith(AM_ROOT) ) {
			String root = Core.getInstance().getRoot();
			return reference.replaceFirst(AM_ROOT, root);
		}
		return reference;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int compareTo(MatchingTaskPreset o) {
		return getName().compareTo(o.getName());
	}
	
	@Override
	public MatchingTaskPreset clone() throws CloneNotSupportedException {
		return new MatchingTaskPreset(this);
	}
}
