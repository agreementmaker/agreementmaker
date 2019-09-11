package am.extension.feedback.ui;

import java.util.ArrayList;

import javax.swing.JRadioButton;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import am.Utility;

public class CandidatesTableModel extends AbstractTableModel {
		

	private static final long serialVersionUID = 1L;
	
	private static final Logger sLog = Logger.getLogger(CandidatesTableModel.class);
	
	public final static int C1_RADIO = 0;
	public final static int C2_SOURCE = C1_RADIO+1;
	public final static int C3_TARGET =C2_SOURCE+1;
	public final static int C4_SIMILARITY = C3_TARGET+1;
	public final static int C5_GROUP = C4_SIMILARITY+1;
	public final static int C6_TYPE = C5_GROUP+1;
	public final static int C7_CONCEPT = C6_TYPE+1;
	
	public String[] columnNames = {	
									"Selection",
	                                "Source Concept",
	                                "Target Concept",
	                                "Similarity",
	                                "Group",
	                                "Mapping Type",
	                                "Candidate Concept",
                                };
        
	public ArrayList<CandidatesTableRow> data;
	
	public final Object[] defaultValues = {
        		new JRadioButton(), 
        		"0123456789012345678912345",
        		"0123456789012345678912345",
        		 "100%",
        		 "Group 99",
        		 "Property",
        		 "0123456789012345678912345"
                };

        public CandidatesTableModel(ArrayList<CandidatesTableRow> rows) {
        	super();
        	data = rows;
	    }

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
        	else{
            	try {
            		CandidatesTableRow a = data.get(row);
                	if(col == C1_RADIO){
                		return a.radio;
                	}
                	else if(col == C2_SOURCE)
                		return a.getMapping().getEntity1().toString();
                	else if(col == C3_TARGET)
                		return a.getMapping().getEntity2().toString();
                	else if(col == C4_SIMILARITY) 
                		return Utility.getNoDecimalPercentFromDouble(a.getMapping().getSimilarity());
                	else if(col == C5_GROUP)
                		return a.getGroupString();
                	else if(col == C6_TYPE)
                		return a.getTypeString();
                	else if(col == C7_CONCEPT)
                		return a.candidateConcept.getNode().toString();
                	else return null;
            	}
            	catch(Exception e) {
            		sLog.error("There is a development error in the table data management, the Exception was caught to keep the system running, check the error", e);
            		return null;
            	}
        	}
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class<?> getColumnClass(int col) {
            Object o = getValueAt(0, col);
            if(o != null)
             return o.getClass();
            else{
            	return defaultValues[col].getClass();
            	/*
            	if(col == C1_RADIO)
            		return new JRadioButton().getClass();
            	else if(col == C2_SOURCE)
            		return defaultValues[C2_SOURCE].getClass();
            	else if(col == C3_TARGET)
            		return defaultValues[C3_TARGET].getClass();
            	else if(col == C4_SIMILARITY) 
            		return defaultValues[C4_SIMILARITY].getClass();
            	else if(col == C5_GROUP)
            		return defaultValues[C5_GROUP].getClass();
            	else return null;
            	*/
            }
        }

        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
        public boolean isCellEditable(int row, int col) {
        	if(col == C1_RADIO)
        		return true;
        	else return false;
        }

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public void setValueAt(Object value, int row, int col) {
        	if(!(data == null || data.size() == 0 || value == null)) {
        		/*
                    System.out.println("Setting value at " + row + "," + col
                                       + " to " + value
                                       + " (an instance of "
                                       + value.getClass() + ")");
                 */
        		if(col == C1_RADIO){
        			data.get(row).radio.setSelected(((JRadioButton)value).isSelected());
        			fireTableRowsUpdated(0, getRowCount()-1);
        		}
        	}
        }
        
/*		private void printDebugData() {
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
        }*/
		

    }
