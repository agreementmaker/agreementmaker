package am.ui.controlpanel.table;

import java.awt.Color;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.MatchingTaskChangeEvent;
import am.app.mappingEngine.MatchingTaskChangeEvent.EventType;
import am.app.mappingEngine.qualityEvaluation.QualityEvaluationData;
import am.ui.UI;
import am.ui.UICore;

public class MatchersControlPanelTableModel extends AbstractTableModel {
		
	private final Logger log = LogManager.getLogger(MatchersControlPanelTableModel.class);
	private static final long serialVersionUID = 1L;
	public final static String NONE = "N/A";
	public final static String ANY = "ANY";
	
	public final static int CELLUPDATE = 0;
	public final static int ROWUPDATE = 1;
	public final static int ALLROWUPDATE = 2;
	
	public final static int COLOR = 0;
	public final static int INDEX = COLOR+1;
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
	public final static int CQUAL = FMEASURE+1;
	public final static int PQUAL = CQUAL+1;	
	
	public String[] columnNames = {							"Color",
															"Index",
					                                        "Name",
					                                        "Show/Hide",
					                                        "Threshold",
					                                        "S-Relations",
					                                        "T-Relations",
					                                        "Input Matchers",
					                                        "Modified",
					                                        "Align Class",
					                                        "Align Prop",
					                                        "Performance(ms)",
					                                        "Found",
					                                        "Correct",
					                                        "Reference",
					                                        "Precision",
					                                        " Recall ",
					                                        "F-Measure",
					                                        "Class Quality",
					                                        "Prop Quality"
					                                        };
        
	public List<MatchingTask> data =  Core.getInstance().getMatchingTasks();
	
	public final Object[] defaultValues = {
        		new Integer(99), 
        		"0123456789012345678912345",
        		Boolean.TRUE,
                "100%",
                "ANY",
                "ANY",
                "012345678901",
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.TRUE,
                new Long(999999),
                new Integer(999999), 
                new Integer(999999), 
                new Integer(999999), 
                "100.00%",
                "100.00%",
                "100.00%",
                "100.00%",
                "100.00%",
                Color.pink
                };

        public MatchersControlPanelTableModel() {
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
        		MatchingTask a = data.get(row);
            	if(col == INDEX)
            		return a.getID();
            	else if(col == NAME)
            		return a.matchingAlgorithm.getName();
            	else if(col == SHOWHIDE)
            		return UICore.getInstance().getVisData(a).isShown;
            	else if(col == THRESHOLD) 
            		return Utility.getNoDecimalPercentFromDouble(a.selectionParameters.threshold);
            	else if(col == SRELATIONS)
            		return Utility.getStringFromNumRelInt(a.selectionParameters.maxSourceAlign);
            	else if(col == TRELATIONS)
            		return Utility.getStringFromNumRelInt(a.selectionParameters.maxTargetAlign);
            	else if(col == INPUTMATCHERS) {
            		if(a.matcherParameters.inputResults != null && a.matcherParameters.inputResults.size() >0)
            			return a.matcherParameters.inputResults.get(0).matchingAlgorithm.getName();
            		else return NONE;
            	}
            	else if( col == MODIFIED){
            		return a.matcherResult.isModifiedByUser();
            	}else if(col == ALIGNCLASSES)
            		return a.selectionParameters.alignClasses;
            	else if (col == ALIGNPROPERTIES)
            		return a.selectionParameters.alignProperties;
            	else if(col == FOUND ) {
            		return a.selectionResult.getTotalNumberAlignments();
            	}
            	else if(col == PERFORMANCE ) {
            		if(a.matcherResult == null || a.matcherResult.getExecutionTime() == 0)
            			return NONE;
            		else return a.matcherResult.getExecutionTime();
            	}
            	else if (col == CORRECT ){
            		if(!a.selectionResult.isRefEvaluated())
            			return NONE;
            		else return a.selectionResult.refEvalData.getCorrect();
            	}else if(col == REFERENCE ){
            		if(a.selectionResult == null || !a.selectionResult.isRefEvaluated())
            			return NONE;
            		else return a.selectionResult.refEvalData.getExist();
            	}else if(col == RECALL)
            		if(a.selectionResult == null || !a.selectionResult.isRefEvaluated())
            			return NONE;
            		else return  Utility.getOneDecimalPercentFromDouble(a.selectionResult.refEvalData.getRecall());
            	else if(col == PRECISION )
            		if(a.selectionResult == null || !a.selectionResult.isRefEvaluated())
            			return NONE;
            		else return  Utility.getOneDecimalPercentFromDouble(a.selectionResult.refEvalData.getPrecision());
            	else if(col == FMEASURE )
            		if(a.selectionResult == null || !a.selectionResult.isRefEvaluated())
            			return NONE;
            		else return Utility.getOneDecimalPercentFromDouble(a.selectionResult.refEvalData.getFmeasure());
            	else if(col == CQUAL ) {
            		if(a.selectionResult == null || !a.selectionResult.isQualEvaluated())
            			return NONE;
            		else {
            			QualityEvaluationData q = a.selectionResult.qualEvalData;
            			if(!q.isLocal()) {
            				return Utility.getOneDecimalPercentFromDouble(q.getGlobalClassMeasure());
            			}
            			else return NONE;
            		}
            	}
            	else if(col == PQUAL ) {
            		if(a.selectionResult == null || !a.selectionResult.isQualEvaluated())
            			return NONE;
            		else {
            			QualityEvaluationData q = a.selectionResult.qualEvalData;
            			if(!q.isLocal()) {
            				return Utility.getOneDecimalPercentFromDouble(q.getGlobalPropMeasure());
            			}
            			else return NONE;
            		}
            	}
            			
            	else if(col == COLOR )
            		return UICore.getInstance().getVisData(a).color;
            	else return NONE;
        	}
        	catch(Exception e) {
        		e.printStackTrace();
        		System.out.println("There is a development error in the table data management, the Exception get catched to keep the system running, check the error");
        		return null;
        	}
        }

        public List<MatchingTask> getData() { return data; }
        
        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class<? extends Object> getColumnClass(int c) {
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
        		/*
                    System.out.println("Setting value at " + row + "," + col
                                       + " to " + value
                                       + " (an instance of "
                                       + value.getClass() + ")");
                 */

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
        		UI ui = UICore.getUI();
        		MatchingTask a = data.get(row);
            	if(col == SHOWHIDE) {
            		UICore.getInstance().getVisData(a).isShown = ((Boolean)value).booleanValue();
            		Core.getInstance().fireEvent(new MatchingTaskChangeEvent(a, EventType.MATCHER_VISIBILITY_CHANGED));
            		ui.redisplayCanvas();
            	}

            	else if(col == THRESHOLD) {
        			a.selectionParameters.threshold = Utility.getDoubleFromPercent((String)value);
        			//core.selectAndUpdateMatchers(a);
        			update = ALLROWUPDATE;
        			ui.redisplayCanvas();
            			
            	}
            	else if(col == SRELATIONS) {
            		a.selectionParameters.maxSourceAlign = Utility.getIntFromNumRelString((String)value);
            		//core.selectAndUpdateMatchers(a);
            		update = ALLROWUPDATE;
            		ui.redisplayCanvas();
            	}
            	else if(col == TRELATIONS) {
            		a.selectionParameters.maxTargetAlign = Utility.getIntFromNumRelString((String)value);
            		//core.selectAndUpdateMatchers(a);
            		update = ALLROWUPDATE;
            		ui.redisplayCanvas();
            	}
            	else if(col == ALIGNCLASSES) {
            		a.selectionParameters.alignClasses = (Boolean)value;
            		//core.matchAndUpdateMatchers(a);
            		update = ALLROWUPDATE;
            		ui.redisplayCanvas();
            	}
            	else if(col == ALIGNPROPERTIES) {
            		a.selectionParameters.alignProperties = (Boolean)value;
            		//core.matchAndUpdateMatchers(a);
            		update = ALLROWUPDATE;
            		ui.redisplayCanvas();
            	}
            	else if(col == COLOR) {
            		UICore.getInstance().getVisData(a).color = (Color)value;
            		update = ROWUPDATE;
            		ui.redisplayCanvas();
            	}
        	}
        	catch(Exception e) {
        		e.printStackTrace();
        		System.out.println("There is a development error in the table data management, the Exception got caught to keep the system running, check the error");
        	}
        	return update;
		}

		/*private void printDebugData() {
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
