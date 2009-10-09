package am.app.mappingEngine;

import am.app.ontology.*;

import java.util.ArrayList;


public class AlignmentSet
{
    private ArrayList<Alignment> collection = null;

    public AlignmentSet()
    {
        collection = new ArrayList<Alignment>();
    }

    public void addAlignment(Alignment alignment)
    {
        if( alignment != null ) collection.add(alignment);
    }
    
    public void addAll(AlignmentSet a)
    {
    	if(a != null) {
    		for(int i= 0; i<a.size();i++) {
    			Alignment alignment = a.getAlignment(i);
    			collection.add(alignment);
    		}
    	}
        
    }

    public Alignment getAlignment(int index)
    {
        if (index >= 0 && index < size()) {
            return (Alignment) collection.get(index);
        } else {
            System.err.println("getAlignmentError: Index is out of bound.");
            return null;
        }
    }

    public double getSimilarity(Node left, Node right)
    {
        Alignment align = contains(left, right);
        if (align == null) {
            return 0;
        } else {
            return align.getSimilarity();
        }
    }

    public void setSimilarity(Node left, Node right, double sim)
    {
        Alignment align = contains(left, right);
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

    public Alignment contains(Node left, Node right)
    {
        for (int i = 0, n = size(); i < n; i++) {
            Alignment align = (Alignment) collection.get(i);
            if (align.getEntity1().equals(left) && align.getEntity2().equals(right)) {
                return align;
            }
        }
        return null;
    }

    public AlignmentSet cut(double threshold)
    {
        for (int i = 0; i < size(); i++) {
            Alignment align = (Alignment) collection.get(i);
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
            Alignment align = (Alignment) collection.get(i);
            if (align.getSimilarity() > threshold) {
                count++;
            }
        }
        return count;
    }

    public void show()
    {
        for (int i = 0, n = size(); i < n; i++) {
            Alignment align = (Alignment) collection.get(i);
            System.out.println("entity1=" + align.getEntity1().toString());
            System.out.println("entity2=" + align.getEntity2().toString());
            System.out.println("similarity=" + align.getSimilarity());
            System.out.println("relation=" + align.getRelation() + "\n");
        }
    }

	
    public String getStringList() {
		String result = "";
		Alignment a;
		for(int i = 0; i < collection.size(); i++) {
			a = collection.get(i);
			result += a.getString();
			if(i == collection.size()-1)
				result+= "\n";
		}
		return result;
	}
}
