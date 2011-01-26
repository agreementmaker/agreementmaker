package am.app.ontology.profiling.manual;

import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import com.hp.hpl.jena.rdf.model.Property;

import am.app.ontology.profiling.OntologyProfilerPanel;
import am.app.ontology.profiling.OntologyProfilerParameters;

/**
 * This class represents the matching panel for the manual ontology profiling algorithm.
 * 
 * @author Cosmin Stroe @date January 26, 2011
 *
 */
public class ManualProfilerMatchingPanel extends OntologyProfilerPanel {

	private static final long serialVersionUID = 1776629568738791879L;

	JCheckBox sourceClassLocalName, targetClassLocalName, sourcePropertyLocalName, targetPropertyLocalName;
	JCheckBox[] sourceClassList, targetClassList, sourcePropertyList, targetPropertyList;
	
	List<Property> sC, tC, sP, tP;
	
	public ManualProfilerMatchingPanel(List<Property> sourceClass, List<Property> targetClass,
									   List<Property> sourceProperty, List<Property> targetProperty ) {
	
		sC = sourceClass;
		tC = targetClass;
		sP = sourceProperty;
		tP = targetProperty;
		
		initializeComponents();
		
		initializeLayout();
		
		
		
	}
	
	private void initializeComponents() {
		
		// TODO: Most likely would be better to do a list instead of checkboxes. - cosmin
		
		sourceClassLocalName = new JCheckBox("Localname"); sourceClassLocalName.setSelected(true);
		targetClassLocalName = new JCheckBox("Localname"); targetClassLocalName.setSelected(true);
		
		sourcePropertyLocalName = new JCheckBox("Localname"); sourcePropertyLocalName.setSelected(true);
		targetPropertyLocalName = new JCheckBox("Localname"); targetPropertyLocalName.setSelected(true);
		
		//source classes
		sourceClassList = new JCheckBox[ sC.size() ];
		for( int i = 0; i < sC.size(); i++ ) {
			sourceClassList[i] = new JCheckBox(sC.get(i).getLocalName());
			sourceClassList[i].setSelected(true);
		}
		
		//target classes
		targetClassList = new JCheckBox[ tC.size() ];
		for( int i = 0; i < tC.size(); i++ ) {
			targetClassList[i] = new JCheckBox(tC.get(i).getLocalName());
			targetClassList[i].setSelected(true);
		}
		
		// source properties
		sourcePropertyList = new JCheckBox[ sP.size() ];
		for( int i = 0; i < sP.size(); i++ ) {
			sourcePropertyList[i] = new JCheckBox(sP.get(i).getLocalName());
			sourcePropertyList[i].setSelected(true);
		}
		
		// target properties
		targetPropertyList = new JCheckBox[ tP.size() ];
		for( int i = 0; i < tP.size(); i++ ) {
			targetPropertyList[i] = new JCheckBox(tP.get(i).getLocalName());
			targetPropertyList[i].setSelected(true);
		}
		
	}

	private void initializeLayout() {
		
		GroupLayout mainLayout = new GroupLayout(this);
		
		JLabel sourceClassLabel = new JLabel("Source Class Annotations");
		JLabel targetClassLabel = new JLabel("Target Class Annotations");
		JLabel sourcePropertyLabel = new JLabel("Source Property Annotations");
		JLabel targetPropertyLabel = new JLabel("Target Property Annotations");
		
		{ // the horizontal layout
			
			// source class horizontal parallel group
			GroupLayout.ParallelGroup h_SourceClassGroup = mainLayout.createParallelGroup();
			h_SourceClassGroup.addComponent(sourceClassLabel);
			h_SourceClassGroup.addComponent(sourceClassLocalName);
			for( JCheckBox cb : sourceClassList ) h_SourceClassGroup.addComponent(cb);
			
			// target class horizontal parallel group
			GroupLayout.ParallelGroup h_TargetClassGroup = mainLayout.createParallelGroup();
			h_TargetClassGroup.addComponent(targetClassLabel);
			h_TargetClassGroup.addComponent(targetClassLocalName);
			for( JCheckBox cb : targetClassList ) h_TargetClassGroup.addComponent(cb);
			
			// combine the source and target class groups
			GroupLayout.SequentialGroup h_ClassGroup = mainLayout.createSequentialGroup();
			h_ClassGroup.addGap(10);
			h_ClassGroup.addGroup(h_SourceClassGroup);
			h_ClassGroup.addGap(20);
			h_ClassGroup.addGroup(h_TargetClassGroup);
			
			
			// source property horizontal parallel group
			GroupLayout.ParallelGroup h_SourcePropertyGroup = mainLayout.createParallelGroup();
			h_SourcePropertyGroup.addComponent(sourcePropertyLabel);
			h_SourcePropertyGroup.addComponent(sourcePropertyLocalName);
			for( JCheckBox cb : sourcePropertyList ) h_SourcePropertyGroup.addComponent(cb);
			
			// target property horizontal parallel group
			GroupLayout.ParallelGroup h_TargetPropertyGroup = mainLayout.createParallelGroup();
			h_TargetPropertyGroup.addComponent(targetPropertyLabel);
			h_TargetPropertyGroup.addComponent(targetPropertyLocalName);
			for( JCheckBox cb : targetPropertyList ) h_TargetPropertyGroup.addComponent(cb);
			
			// combine the source and target property groups
			GroupLayout.SequentialGroup h_PropertyGroup = mainLayout.createSequentialGroup();
			h_PropertyGroup.addGap(10);
			h_PropertyGroup.addGroup(h_SourcePropertyGroup);
			h_PropertyGroup.addGap(20);
			h_PropertyGroup.addGroup(h_TargetPropertyGroup);
			
			// combine the class and property groups.
			GroupLayout.ParallelGroup h_mainGroup = mainLayout.createParallelGroup();
			h_mainGroup.addGroup(h_ClassGroup);
			h_mainGroup.addGroup(h_PropertyGroup);
			
			mainLayout.setHorizontalGroup(h_mainGroup);
		}
		
		{ // the vertical layout
			
			// source class vertical parallel group
			GroupLayout.SequentialGroup v_SourceClassGroup = mainLayout.createSequentialGroup();
			v_SourceClassGroup.addComponent(sourceClassLabel);
			v_SourceClassGroup.addComponent(sourceClassLocalName);
			for( JCheckBox cb : sourceClassList ) v_SourceClassGroup.addComponent(cb);
			
			// target class horizontal parallel group
			GroupLayout.SequentialGroup v_TargetClassGroup = mainLayout.createSequentialGroup();
			v_TargetClassGroup.addComponent(targetClassLabel);
			v_TargetClassGroup.addComponent(targetClassLocalName);
			for( JCheckBox cb : targetClassList ) v_TargetClassGroup.addComponent(cb);
			
			// combine the source and target class groups
			GroupLayout.ParallelGroup v_ClassGroup = mainLayout.createParallelGroup();
			v_ClassGroup.addGroup(v_SourceClassGroup);
			v_ClassGroup.addGroup(v_TargetClassGroup);
			
			
			// source property horizontal parallel group
			GroupLayout.SequentialGroup v_SourcePropertyGroup = mainLayout.createSequentialGroup();
			v_SourcePropertyGroup.addComponent(sourcePropertyLabel);
			v_SourcePropertyGroup.addComponent(sourcePropertyLocalName);
			for( JCheckBox cb : sourcePropertyList ) v_SourcePropertyGroup.addComponent(cb);
			
			// target property horizontal parallel group
			GroupLayout.SequentialGroup v_TargetPropertyGroup = mainLayout.createSequentialGroup();
			v_TargetPropertyGroup.addComponent(targetPropertyLabel);
			v_TargetPropertyGroup.addComponent(targetPropertyLocalName);
			for( JCheckBox cb : targetPropertyList ) v_TargetPropertyGroup.addComponent(cb);
			
			// combine the source and target property groups
			GroupLayout.ParallelGroup v_PropertyGroup = mainLayout.createParallelGroup();
			v_PropertyGroup.addGroup(v_SourcePropertyGroup);
			v_PropertyGroup.addGroup(v_TargetPropertyGroup);
			
			// combine the class and property groups.
			GroupLayout.SequentialGroup v_mainGroup = mainLayout.createSequentialGroup();
			v_mainGroup.addGap(30);
			v_mainGroup.addGroup(v_ClassGroup);
			v_mainGroup.addGap(30);
			v_mainGroup.addGroup(v_PropertyGroup);
			
			mainLayout.setVerticalGroup(v_mainGroup);
		}
		
		
		setLayout(mainLayout);
		
		
	}
	
	@Override
	public OntologyProfilerParameters getParameters() {

		ManualProfilerMatchingParameters param = new ManualProfilerMatchingParameters();
		
		param.matchSourceClassLocalname = sourceClassLocalName.isSelected();
		param.matchTargetClassLocalname = targetClassLocalName.isSelected();
		
		param.matchSourcePropertyLocalname = sourcePropertyLocalName.isSelected();
		param.matchTargetPropertyLocalname = targetPropertyLocalName.isSelected();
		
		//source classes
		for( int i = 0; i < sC.size(); i++ ) {
			if( sourceClassList[i].isSelected() ) {
				if( param.sourceClassAnnotations == null ) param.sourceClassAnnotations = new ArrayList<Property>();
				param.sourceClassAnnotations.add(sC.get(i));
			}
		}
		
		//target classes
		for( int i = 0; i < tC.size(); i++ ) {
			if( targetClassList[i].isSelected() ) {
				if( param.targetClassAnnotations == null ) param.targetClassAnnotations = new ArrayList<Property>();
				param.targetClassAnnotations.add(tC.get(i));
			}
		}
		
		// source properties
		for( int i = 0; i < sP.size(); i++ ) {
			if( sourcePropertyList[i].isSelected() ) {
				if( param.sourcePropertyAnnotations == null ) param.sourcePropertyAnnotations = new ArrayList<Property>();
				param.sourcePropertyAnnotations.add(sP.get(i));
			}
		}
		
		// target properties
		for( int i = 0; i < tP.size(); i++ ) {
			if( targetPropertyList[i].isSelected() ) {
				if( param.targetPropertyAnnotations == null ) param.targetPropertyAnnotations = new ArrayList<Property>();
				param.targetPropertyAnnotations.add(tP.get(i));
			}
		}
		
		return param;
	}

	
}
