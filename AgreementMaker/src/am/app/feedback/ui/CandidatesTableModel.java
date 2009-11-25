package am.app.feedback.ui;

import java.util.ArrayList;

import javax.swing.JRadioButton;
import javax.swing.table.AbstractTableModel;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.qualityEvaluation.QualityEvaluationData;
import am.app.ontology.Node;
import am.userInterface.UI;

import java.awt.Color;

public class CandidatesTableModel extends AbstractTableModel {
		

	private static final long serialVersionUID = 1L;
	
	public final static int C1_RADIO = 0;
	public final static int C2_SOURCE = C1_RADIO+1;
	public final static int C3_TARGET =C2_SOURCE+1;
	public final static int C4_SIMILARITY = C3_TARGET+1;
	public final static int C5_GROUP = C4_SIMILARITY+1;
	public final static int C6_TYPE = C5_GROUP+1;
	
	public String[] columnNames = {	
									"Selection",
	                                "Source Concept",
	                                "Target Concept",
	                                "Similarity",
	                                "Group",
	                                "Mapping Type",
                                };
        
	public ArrayList<CandidatesTableRow> data;
	
	public final Object[] defaultValues = {
        		new JRadioButton(), 
        		"0123456789012345678912345",
        		"0123456789012345678912345",
        		 "100%",
        		 "Group 99",
        		 "Property"
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
                		return a.mapping.getEntity1().getCandidateString();
                	else if(col == C3_TARGET)
                		return a.mapping.getEntity2().getCandidateString();
                	else if(col == C4_SIMILARITY) 
                		return Utility.getNoDecimalPercentFromDouble(a.mapping.getSimilarity());
                	else if(col == C5_GROUP)
                		return "Group "+a.group;
                	else if(col == C6_TYPE)
                		return a.type;
                	else return null;
            	}
            	catch(Exception e) {
            		e.printStackTrace();
            		System.out.println("There is a development error in the table data management, the Exception get catched to keep the system running, check the error");
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
        public Class getColumnClass(int col) {
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
