package am.app.mappingEngine;



import java.util.ArrayList;
import java.util.Iterator;

import am.app.feedback.CandidateConcept.ontology;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Node;


public class AlignmentSet<E extends Alignment>
{
    protected ArrayList<E> collection = null;

    public AlignmentSet()
    {
        collection = new ArrayList<E>();
    }

    public void addAlignment(E alignment)
    {
        if( alignment != null ) collection.add(alignment);
    }
    
    public void addAll(AlignmentSet<E> a)
    {
    	if(a != null) {
    		for(int i= 0; i<a.size();i++) {
    			E alignment = a.getAlignment(i);
    			collection.add(alignment);
    		}
    	}
        
    }

    // adds all the alignments in the set a, but checking for duplicates, making sure it doesn't add duplicate alignments
    public void addAllNoDuplicate(AlignmentSet<E> a)
    {
    	if(a != null) {
    		for(int i= 0; i<a.size();i++) {
    			E alignment = a.getAlignment(i);
    			if( !contains( alignment ) ) addAlignment(alignment);
    		}
    	}
        
    }    
    
    public E getAlignment(int index)
    {
        if (index >= 0 && index < size()) {
            return collection.get(index);
        } else {
            System.err.println("getAlignmentError: Index is out of bound.");
            return null;
        }
    }

    public double getSimilarity(Node left, Node right)
    {
        E align = contains(left, right);
        if (align == null) {
            return 0;
        } else {
            return align.getSimilarity();
        }
    }

    public void setSimilarity(Node left, Node right, double sim)
    {
        E align = contains(left, right);
        if (align == null) {
            System.err.println("setSimilarityError: Cannot find such alignment.");
        } else {
            align.setSimilarity(sim);
        }
    }

    public boolean removeAlignment(int index)
    {
        if (index >= 0 && index < size()) {
            collection.remove(index);
            return true;
        } else {
            System.err.println("removeAlignmentError: Index is out of bound.");
            return false;
        }
    }

    public boolean removeAlignment(Node left, Node right)
    {
        for (int i = 0, n = size(); i < n; i++) {
            Alignment align = (Alignment) collection.get(i);
            if (align.getEntity1().equals(left) && align.getEntity2().equals(right)) {
                collection.remove(i);
                return true;
            }
        }
        System.err.println("removeAlignmentError: Cannot find such alignment.");
        return false;
    }

    public E contains(Node left, Node right)
    {
        for (int i = 0, n = size(); i < n; i++) {
            E align = collection.get(i);
            if (align.getEntity1().equals(left) && align.getEntity2().equals(right)) {
                return align;
            }
        }
        return null;
    }
    
    public E contains(Node nod, ontology o)
    {
        for (int i = 0, n = size(); i < n; i++) {
            E align = collection.get(i);
            if(o == ontology.source){
            	if(align.getEntity1().equals(nod))
            		return align;
            }
            else{
            	if(align.getEntity2().equals(nod))
            		return align;
            }
        }
        return null;
    }
    
    
    public boolean contains( E alignment ) {
        for (int i = 0, n = size(); i < n; i++) {
            E align = collection.get(i);
            Node left = alignment.getEntity1();
            Node right = alignment.getEntity2();
            if (align.getEntity1().equals(left) && align.getEntity2().equals(right)) {
                return true;
            }
        }
        return false;
    }
    

    public AlignmentSet<E> cut(double threshold)
    {
        for (int i = 0; i < size(); i++) {
            E align = collection.get(i);
            if (align.getSimilarity() <= threshold) {
                removeAlignment(i);
                i--;
            }
        }
        return this;
    }

    public int size()
    {
        return collection.size();
    }

    public int size(double threshold)
    {
        int count = 0;
        for (int i = 0, n = size(); i < n; i++) {
            E align = collection.get(i);
            if (align.getSimilarity() > threshold) {
                count++;
            }
        }
        return count;
    }

    public void show()
    {
        for (int i = 0, n = size(); i < n; i++) {
            E align = collection.get(i);
            System.out.println("entity1=" + align.getEntity1().toString());
            System.out.println("entity2=" + align.getEntity2().toString());
            System.out.println("similarity=" + align.getSimilarity());
            System.out.println("relation=" + align.getRelation() + "\n");
        }
    }

	
    public String getStringList() {
		String result = "";
		E a;
		for(int i = 0; i < collection.size(); i++) {
			a = collection.get(i);
			result += a.getString();
			if(i == collection.size()-1)
				result+= "\n";
		}
		return result;
	}
    
    public Iterator<E> iterator() {
    	return collection.iterator();
    }
}
