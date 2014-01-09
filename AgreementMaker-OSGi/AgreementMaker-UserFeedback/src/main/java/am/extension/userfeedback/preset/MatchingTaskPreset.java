package am.extension.userfeedback.preset;

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
	
	private static final String AM_ROOT = "AM_ROOT";
	
	private String name;
	private String sourceOnt;
	private String targetOnt;
	
	private boolean hasReference = false;
	private String reference;
	
	/**
	 * NOTE: We replace the AM_ROOT path with the string {@link #AM_ROOT}. This is
	 * changed back when the get methods are called. This is to allow
	 * transfering of these settings across computers.
	 */
	public MatchingTaskPreset(String name, String sourceOnt, String targetOnt) {
		this.name = name;
		
		String root = Core.getInstance().getRoot();
		
		if( sourceOnt.startsWith(root) ) {
			sourceOnt = sourceOnt.replaceFirst(root, AM_ROOT);
		}
		
		if( targetOnt.startsWith(root) ) {
			targetOnt = targetOnt.replaceFirst(root, AM_ROOT);
		}
		
		this.sourceOnt = sourceOnt;
		this.targetOnt = targetOnt;
	}
	
	public MatchingTaskPreset(String name, String sourceOnt, String targetOnt, String reference) {
		this.name = name;
		
		String root = Core.getInstance().getRoot();
		
		if( sourceOnt.startsWith(root) ) {
			sourceOnt = sourceOnt.replaceFirst(root, AM_ROOT);
		}
		
		if( targetOnt.startsWith(root) ) {
			targetOnt = targetOnt.replaceFirst(root, AM_ROOT);
		}
		
		this.sourceOnt = sourceOnt;
		this.targetOnt = targetOnt;
		
		// set the reference
		this.hasReference = true;
		
		if( reference.startsWith(root) ) {
			reference = reference.replaceFirst(root, AM_ROOT);
		}
		
		this.reference = reference;
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
	public boolean equals(Object obj) {
		if( obj instanceof MatchingTaskPreset ) {
			return ((MatchingTaskPreset) obj).getName().equals(name);
		}
		return false;
	}

	@Override
	public int compareTo(MatchingTaskPreset o) {
		return getName().compareTo(o.getName());
	}
}
