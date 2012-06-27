package am.app.mappingEngine.IterativeInstanceStructuralMatcher;

import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.DefaultMatcherParameters;

public class IterativeInstanceStructuralParametersPanel extends AbstractMatcherParametersPanel{

	private static final long serialVersionUID = 1L;
	
	private JPanel thresholdsPanel;
	
	private JCheckBox individuals;
	
	private JCheckBox rangeDomainBox;
	private JCheckBox superclassBox;
	private JCheckBox propertyValuesBox;
	private JCheckBox propertyUsageBox;
	
	private JTextField rangeDomainText;
	private JTextField superclassText;
	private JTextField propertyValuesText;
	private JTextField propertyUsageText;
	
	private IterativeInstanceStructuralParameters parameters;
	
	private String errorMessage = "Error: not a number";
	
	public IterativeInstanceStructuralParametersPanel() {
		super();
		initComponents();	
		addComponents();
		
		parameters = new IterativeInstanceStructuralParameters();
	}
	
	
	private void addComponents() {
		GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup( layout.createParallelGroup()
				.addComponent(individuals)
				.addGap(20)
				.addComponent(thresholdsPanel)
				
		);
		
		layout.setVerticalGroup( layout.createSequentialGroup()
				.addComponent(individuals)
				.addGap(20)
				.addComponent(thresholdsPanel)
		);
		
		setLayout(layout);
	}


	private void initComponents() {
		individuals = new JCheckBox("considerIndividuals");
		individuals.setSelected(true);
		
		thresholdsPanel = new JPanel();
		
		thresholdsPanel.setBorder(new TitledBorder("Thresholds:"));
		
		GroupLayout layout = new GroupLayout(thresholdsPanel);
		thresholdsPanel.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		rangeDomainBox = new JCheckBox("range/domain");
		rangeDomainBox.setSelected(true);
		rangeDomainText = new JTextField("0.9");
		rangeDomainText.setPreferredSize(new Dimension(50, rangeDomainText.getPreferredSize().height));
		
		superclassBox = new JCheckBox("superclasses and restrictions");
		superclassBox.setSelected(true);
		superclassText = new JTextField("0.6");
		
		propertyUsageBox = new JCheckBox("property usage");
		propertyUsageBox.setSelected(true);
		propertyUsageText = new JTextField("0.5");
		
		propertyValuesBox = new JCheckBox("property values");
		propertyValuesBox.setSelected(true);
		propertyValuesText = new JTextField("0.6");
		
		
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		
		hGroup.addGroup(layout.createParallelGroup().
	            addComponent(rangeDomainBox).addComponent(superclassBox).addComponent(propertyValuesBox).addComponent(propertyUsageBox));
		hGroup.addGroup(layout.createParallelGroup().
	            addComponent(rangeDomainText).addComponent(superclassText).addComponent(propertyValuesText).addComponent(propertyUsageText));
		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
				addComponent(rangeDomainBox).addComponent(rangeDomainText));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
				addComponent(superclassBox).addComponent(superclassText));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
				addComponent(propertyValuesBox).addComponent(propertyValuesText));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
				addComponent(propertyUsageBox).addComponent(propertyUsageText));
		layout.setVerticalGroup(vGroup); 
	}

	@Override
	public String checkParameters() {
		parameters.setConsiderIndividuals(individuals.isSelected());
		
		parameters.setUsePropertyUsage(propertyUsageBox.isSelected());
		parameters.setUsePropertyValues(propertyValuesBox.isSelected());
		parameters.setUseRangeDomain(rangeDomainBox.isSelected());
		parameters.setUseSuperclasses(superclassBox.isSelected());
		
		double curr = 0;
		
		try{
			curr = Double.parseDouble(rangeDomainText.getText());
		}
		catch(NumberFormatException e){
			return e.getMessage();			
		}
		parameters.setRangeDomainThreshold(curr);
		
		try{
			curr = Double.parseDouble(superclassText.getText());
		}
		catch(NumberFormatException e){
			return e.getMessage();			
		}
		parameters.setSuperclassThreshold(curr);
		
		try{
			curr = Double.parseDouble(propertyUsageText.getText());
		}
		catch(NumberFormatException e){
			return e.getMessage();			
		}
		parameters.setPropertyUsageThreshold(curr);
		
		try{
			curr = Double.parseDouble(propertyValuesText.getText());
		}
		catch(NumberFormatException e){
			return e.getMessage();			
		}
		parameters.setPropertyValuesThreshold(curr);
				
		return null;
	}
	
	@Override
	public DefaultMatcherParameters getParameters() {
		return parameters;
	}
}
