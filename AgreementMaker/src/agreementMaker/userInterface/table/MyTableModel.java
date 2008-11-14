package agreementMaker.userInterface.table;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import agreementMaker.Utility;
import agreementMaker.application.Core;
import agreementMaker.application.mappingEngine.AbstractMatcher;

public class MyTableModel extends AbstractTableModel {
		
	public final static String NONE = "N/A";
	public final static String ANY = "ANY";
	
	public final static int INDEX = 0;
	public final static int NAME = 1;
	public final static int SHOWHIDE = 2;
	public final static int THRESHOLD = 3;
	public final static int SRELATIONS = 4;
	public final static int TRELATIONS = 5;
	public final static int INPUTMATCHERS  = 6;
	public final static int MODIFIED = 7;
	public final static int ALIGNCLASSES = 8;
	public final static int ALIGNPROPERTIES  = 9;
	public final static int FOUND = 10;
	public final static int CORRECT = 11;
	public final static int REFERENCE = 12;
	public final static int PRECISION = 13;
	public final static int RECALL = 14;
	public final static int FMEASURE = 15;	
	
	public String[] columnNames = {"Index",
					                                        "Name",
					                                        "Show/Hide",
					                                        "Threshold",
					                                        "S-Relations",
					                                        "T-Relations",
					                                        "Input Matchers",
					                                        "Modified",
					                                        "Align Classes",
					                                        "Align Properties",
					                                        "Found",
					                                        "Correct",
					                                        "Reference",
					                                        "Precision",
					                                        "Recall",
					                                        "F-Measure"
					                                        };
        
	public ArrayList<AbstractMatcher> data =  Core.getInstance().getMatcherInstances();

	public final Object[] longValues = {
        		new Integer(99), 
        		"0123456789012345678912345",
        		Boolean.TRUE,
                new Double(100),
                new Integer(10),
                new Integer(10),
                "0123456789012345678912345",
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.TRUE,
                new Integer(999999), 
                new Integer(999999), 
                new Integer(999999), 
                new Double(100),
                new Double(100),
                new Double(100),
                };

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.size();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
        	if(data == null || data.size() == 0) 
        		return null;
        	try {
        		AbstractMatcher a = data.get(row);
            	if(col == INDEX)
            		return a.getIndex();
            	else if(col == NAME)
            		return a.getName();
            	else if(col == SHOWHIDE)
            		return a.isShown();
            	else if(col == THRESHOLD) 
            		return Utility.getPercentFromDouble(a.getThreshold());
            	else if(col == SRELATIONS)
            		return Utility.getStringFromNumRelInt(a.getMaxSourceAlign());
            	else if(col == TRELATIONS)
            		return Utility.getStringFromNumRelInt(a.getMaxTargetAlign());
            	else if(col == INPUTMATCHERS) {
            		if(a.getInputMatchers()!= null && a.getInputMatchers().size() >0)
            			return a.getInputMatchers().get(0).getName();
            		else return NONE;
            	}
            	else if( col == MODIFIED)
            		return a.isModifiedByUser();
            	else if(col == ALIGNCLASSES)
            		return a.isAlignClass();
            	else if (col == ALIGNPROPERTIES)
            		return a.isAlignProp();
            	else if(col == FOUND ) {
            		return a.getTotalNumberAlignments();
            	}
            	else if (col == CORRECT )
            		if(!a.isRefEvaluated())
            			return NONE;
            		else return a.getRefEvaluation().getCorrect();
            	else if(col == REFERENCE )
            		if(!a.isRefEvaluated())
            			return NONE;
            		else return a.getRefEvaluation().getExist();
            	else if(col == RECALL)
            		if(!a.isRefEvaluated())
            			return NONE;
            		else return a.getRefEvaluation().getRecall();
            	else if(col == PRECISION )
            		if(!a.isRefEvaluated())
            			return NONE;
            		else return a.getRefEvaluation().getPrecision();
            	else if(col == FMEASURE )
            		if(!a.isRefEvaluated())
            			return NONE;
            		else return a.getRefEvaluation().getFmeasure();
            	else return NONE;
        	}
        	catch(Exception e) {
        		e.printStackTrace();
        		System.out.println("There is a development error in the table data management, the Exception get catched to keep the system running, check the error");
        		return null;
        	}
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
            Object o = getValueAt(0, c);
            if(o != null)
             return o.getClass();
            else return null;
        }

        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
        public boolean isCellEditable(int row, int col) {
        	if(col == INDEX)
        		return false;
        	else if(col == NAME)
        		return false;
        	else if(col == SHOWHIDE)
        		return true;
        	else if(col == THRESHOLD) 
        		return true;
        	else if(col == SRELATIONS)
        		return true;
        	else if(col == TRELATIONS)
        		return true;
        	else if(col == INPUTMATCHERS)
        		return true;
        	else if( col == MODIFIED)
        		return false;
        	else if(col == ALIGNCLASSES)
        		return true;
        	else if (col == ALIGNPROPERTIES)
        		return true;
        	else if(col == FOUND)
        		return false;
        	else if (col == CORRECT)
        		return false;
        	else if(col == REFERENCE)
        		return false;
        	else if(col == RECALL)
        		return false;
        	else if(col == PRECISION)
        		return false;
        	else if(col == FMEASURE)
        		return false;
        	else return false;
        }

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public void setValueAt(Object value, int row, int col) {
        	if(!(data == null || data.size() == 0 || value == null)) {

                    System.out.println("Setting value at " + row + "," + col
                                       + " to " + value
                                       + " (an instance of "
                                       + value.getClass() + ")");


                setValueOnMatcher(value, row, col);
                fireTableCellUpdated(row, col);
        	}

        }

        public void setValueOnMatcher(Object value, int row, int col) {
        	try {
        		AbstractMatcher a = data.get(row);
            	if(col == SHOWHIDE)
            		a.setShown((Boolean)value);
            	else if(col == THRESHOLD) {
            			a.setThreshold(Utility.getDoubleFromPercent((String)value));
            	}
            	else if(col == SRELATIONS) {
            		a.setMaxSourceAlign(Utility.getIntFromNumRelString((String)value));
            	}
            	else if(col == TRELATIONS) {
            		a.setMaxTargetAlign(Utility.getIntFromNumRelString((String)value));
            	}
            	else if(col == ALIGNCLASSES) {
            		a.setAlignClass((Boolean)value);
            	}
            	else if(col == ALIGNPROPERTIES) {
            		a.setAlignProp((Boolean)value);
            	}
        	}
        	catch(Exception e) {
        		e.printStackTrace();
        		System.out.println("There is a development error in the table data management, the Exception get catched to keep the system running, check the error");
        	}
		}

		private void printDebugData() {
            int numRows = getRowCount();
            int numCols = getColumnCount();

            for (int i=0; i < numRows; i++) {
                System.out.print("    row " + i + ":");
                for (int j=0; j < numCols; j++) {
                    System.out.print("  " + getValueAt(i,j));
                }
                System.out.println();
            }
            System.out.println("--------------------------");
        }
		

    }
