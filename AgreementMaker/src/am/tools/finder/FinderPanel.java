package am.tools.finder;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import am.tools.finder.DBPediaFinder.QueryType;

import com.hp.hpl.jena.ontology.OntClass;

public class FinderPanel extends JPanel implements ActionListener {
	JTextField searchField;
	JButton search;
	JTextField resourceField;
	JComboBox queryBox;
	JButton query;
	JTextArea textArea;
	JScrollPane textScroll;
	JEditorPane htmlArea;
	JScrollPane htmlScroll;
	JTabbedPane pane;
	
	Finder finder;
		
	public FinderPanel(Finder finder){
		super();
		this.finder = finder;
		initComponents();
		addComponents();
		finder.initialize();
	}
	
	private void initComponents() {
		searchField = new JTextField();
		search = new JButton("Search");
		search.addActionListener(this);
		resourceField = new JTextField();
		queryBox = new JComboBox(QueryType.values());
		query = new JButton("Query");
		query.addActionListener(this);
		textArea = new JTextArea();
		textScroll = new JScrollPane(textArea);
		htmlArea = new JEditorPane();
		htmlArea.setContentType("text/html");
		htmlScroll = new JScrollPane(htmlArea);
		pane = new JTabbedPane();
		pane.add(textScroll,"Text");
		pane.add(htmlScroll,"Html");
		
	}
	
	private void addComponents() {
		GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup( layout.createParallelGroup()
				.addGroup(  layout.createSequentialGroup()
						.addComponent(searchField)
						.addComponent(search)
				)
				.addGroup(  layout.createSequentialGroup()
						.addComponent(resourceField)
						.addComponent(queryBox)
						.addComponent(query)
				)
				.addComponent(pane)
		);
		
		layout.setVerticalGroup( layout.createSequentialGroup()
				.addGroup(  layout.createParallelGroup(Alignment.LEADING,false)
						.addComponent(searchField)
						.addComponent(search)
				)
				.addGroup(  layout.createParallelGroup(Alignment.LEADING,false)
						.addComponent(resourceField)
						.addComponent(queryBox)
						.addComponent(query)
				)
				.addComponent(pane)		
		);
		setLayout(layout);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==search){
			List<OntClass> list = finder.search(searchField.getText());
			for (OntClass ontClass : list) {
				textArea.append(ontClass.getLocalName() + "\t" + ontClass.getURI() + "\n");
			}
		}
		else if(e.getSource()==query){
			String query = finder.getQuery(resourceField.getText(), (QueryType)queryBox.getSelectedItem());
			String result = finder.executeQuery(query);
			textArea.append(result + "\n");
			htmlArea.setText(result);
		}
	}
}
