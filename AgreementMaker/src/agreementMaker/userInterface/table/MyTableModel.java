package agreementMaker.userInterface.table;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import agreementMaker.Utility;
import agreementMaker.application.Core;
import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.userInterface.UI;
import java.awt.Color;

public class MyTableModel extends AbstractTableModel {
		
	public final static String NONE = "N/A";
	public final static String ANY = "ANY";
	
	public final static int CELLUPDATE = 0;
	public final static int ROWUPDATE = 1;
	public final static int ALLROWUPDATE = 2;
	
	public final static int INDEX = 0;
	public final static int NAME = INDEX+1;
	public final static int SHOWHIDE =NAME+1;
	public final static int THRESHOLD = SHOWHIDE+1;
	public final static int SRELATIONS = THRESHOLD+1;
	public final static int TRELATIONS = SRELATIONS+1;
	public final static int INPUTMATCHERS  = TRELATIONS+1;
	public final static int MODIFIED = INPUTMATCHERS+1;
	public final static int ALIGNCLASSES = MODIFIED+1;
	public final static int ALIGNPROPERTIES  = ALIGNCLASSES+1;
	public final static int PERFORMANCE = ALIGNPROPERTIES+1;
	public final static int FOUND = PERFORMANCE+1;
	public final static int CORRECT = FOUND+1;
	public final static int REFERENCE = CORRECT+1;
	public final static int PRECISION = REFERENCE+1;
	public final static int RECALL = PRECISION+1;
	public final static int FMEASURE = RECALL+1;	
	public final static int COLOR = FMEASURE+1;	
	
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
					                                        "Performance(ms)",
					                                        "Found",
					                                        "Correct",
					                                        "Reference",
					                                        "Precision",
					                                        "Recall",
					                                        "F-Measure",
					                                        "Color"
					                                        };
        
	public ArrayList<AbstractMatcher> data =  Core.getInstance().getMatcherInstances();
	
	public final Object[] defaultValues = {
        		new Integer(99), 
        		"0123456789012345678912345",
        		Boolean.TRUE,
                "100%",
                "ANY",
                "ANY",
                "0123456789012345678912345",
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.TRUE,
                new Long(999999),
                new Integer(999999), 
                new Integer(999999), 
                new Integer(999999), 
                "100%",
                "100%",
                "100%",
                Color.pink
                };

        public MyTableModel() {
        	super();
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
        	try {
        		AbstractMatcher a = data.get(row);
            	if(col == INDEX)
            		return a.getIndex();
            	else if(col == NAME)
            		return a.getName().getMatcherName();
            	else if(col == SHOWHIDE)
            		return a.isShown();
            	else if(col == THRESHOLD) 
            		return Utility.getNoFloatPercentFromDouble(a.getThreshold());
            	else if(col == SRELATIONS)
            		return Utility.getStringFromNumRelInt(a.getMaxSourceAlign());
            	else if(col == TRELATIONS)
            		return Utility.getStringFromNumRelInt(a.getMaxTargetAlign());
            	else if(col == INPUTMATCHERS) {
            		if(a.getInputMatchers()!= null && a.getInputMatchers().size() >0)
            			return a.getInputMatchers().get(0).getName().getMatcherName();
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
            	else if(col == PERFORMANCE ) {
            		if(a.getExecutionTime() == 0)
            			return NONE;
            		else return a.getExecutionTime();
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
            		else return  Utility.getNoFloatPercentFromDouble(a.getRefEvaluation().getRecall());
            	else if(col == PRECISION )
            		if(!a.isRefEvaluated())
            			return NONE;
            		else return  Utility.getNoFloatPercentFromDouble(a.getRefEvaluation().getPrecision());
            	else if(col == FMEASURE )
            		if(!a.isRefEvaluated())
            			return NONE;
            		else return Utility.getNoFloatPercentFromDouble(a.getRefEvaluation().getFmeasure());
            	else if(col == COLOR )
            		return a.getColor();
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
        	else if(col == COLOR)
        		return true;
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
                 

                int whatToUpdate = setValueOnMatcher(value, row, col);
                if(whatToUpdate == CELLUPDATE)
                	fireTableCellUpdated(row, col);
                else if(whatToUpdate == ROWUPDATE)
                	fireTableRowsUpdated(row, row);
                else if(whatToUpdate == ALLROWUPDATE)
                	fireTableRowsUpdated(0, getRowCount()-1);
        	}

        }

        public int setValueOnMatcher(Object value, int row, int col) {
        	int update = CELLUPDATE;
        	try {
        		Core core = Core.getInstance();
        		UI ui = core.getUI();
        		AbstractMatcher a = data.get(row);
            	if(col == SHOWHIDE) {
            		a.setShown((Boolean)value);
            		ui.redisplayCanvas();
            		update = CELLUPDATE;
            	}

            	else if(col == THRESHOLD) {
            			a.setThreshold(Utility.getDoubleFromPercent((String)value));
            			core.selectAndUpdateMatchers(a);
            			update = ALLROWUPDATE;
            			ui.redisplayCanvas();
            			
            	}
            	else if(col == SRELATIONS) {
            		a.setMaxSourceAlign(Utility.getIntFromNumRelString((String)value));
            		core.selectAndUpdateMatchers(a);
            		update = ALLROWUPDATE;
            		ui.redisplayCanvas();
            	}
            	else if(col == TRELATIONS) {
            		a.setMaxTargetAlign(Utility.getIntFromNumRelString((String)value));
            		core.selectAndUpdateMatchers(a);
            		update = ALLROWUPDATE;
            		ui.redisplayCanvas();
            	}
            	else if(col == ALIGNCLASSES) {
            		a.setAlignClass((Boolean)value);
            		core.matchAndUpdateMatchers(a);
            		update = ALLROWUPDATE;
            		ui.redisplayCanvas();
            	}
            	else if(col == ALIGNPROPERTIES) {
            		a.setAlignProp((Boolean)value);
            		core.matchAndUpdateMatchers(a);
            		update = ALLROWUPDATE;
            		ui.redisplayCanvas();
            	}
            	else if(col == COLOR) {
            		a.setColor((Color)value);
            		update = ROWUPDATE;
            		ui.redisplayCanvas();
            	}
        	}
        	catch(Exception e) {
        		e.printStackTrace();
        		System.out.println("There is a development error in the table data management, the Exception get catched to keep the system running, check the error");
        	}
        	return update;
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
