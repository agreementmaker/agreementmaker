package am.userInterface;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import am.GlobalStaticVariables;
import am.app.Core;

public class AboutDialog extends JDialog implements ActionListener {
		
	private static final long serialVersionUID = -1107442375488915232L;

	private final static byte am[]={ // don't touch
	    -119,80,78,71,13,10,26,10,0,0,0,13,73,72,68,82,0,0,0,61,0,0,0,21,1,3,0,0,0,-49,83,116,-15,0,0,0,1,115,82,
	    71,66,0,-82,-50,28,-23,0,0,0,6,80,76,84,69,-1,-1,-1,0,0,0,85,-62,-45,126,0,0,0,1,98,75,71,68,0,-120,5,29,72,0,0,
	    0,9,112,72,89,115,0,0,46,35,0,0,46,35,1,120,-91,63,118,0,0,0,7,116,73,77,69,7,-40,11,26,9,45,18,-90,-113,-94,32,0,0,
	    0,29,116,69,88,116,67,111,109,109,101,110,116,0,67,114,101,97,116,101,100,32,119,105,116,104,32,84,104,101,32,71,73,77,80,-17,100,37,110,0,
	    0,0,89,73,68,65,84,8,-41,99,96,-64,10,-28,-1,127,80,0,18,64,112,64,-2,1,-125,-16,-1,127,32,65,5,6,1,-120,44,54,-122,4,-126,97,
	    0,99,-28,49,-80,67,24,-121,25,120,30,-128,25,7,25,36,10,-64,-116,102,6,3,9,16,-125,-79,-99,-63,-128,3,-52,-24,103,48,-80,0,50,56,24,-7,
	    25,12,42,112,48,-6,97,-116,54,48,3,0,76,57,24,0,37,7,126,100,0,0,0,0,73,69,78,68,-82,66,96,-126
		  };
	
	//private JDialog frameAbout;
	private JButton close, cartoon;
	private JLabel image_label;
	private ImageIcon label_icon;
	
	private JLabel AgreementMaker;
	private BufferedImage image;

	private JDialog isytcctssoaa;
	private JButton OkBackToWork;
	
	/**
	 * This is the part of the program where the REAL fun stuff happens.
	 */
	public AboutDialog(Frame parent) {
		super(parent, true);
		/** Main window */
		setTitle("About AgreementMaker...");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		
		
		/***************** TITLE ******************/
	
		
		
		AgreementMaker = new JLabel("<html><h1>AgreementMaker" + " " + GlobalStaticVariables.AgreementMakerVersion + "</h1></html>");
		
		JPanel title = new JPanel(new FlowLayout(FlowLayout.CENTER));
		title.add(AgreementMaker);		
		
		
		
		/********* MIDDLE *****************/
		
		image_label = new JLabel();
		label_icon = new ImageIcon("images"+File.separator+"splash.png");
		//image_label.setSize(600,400);
		image_label.setIcon(label_icon);

		
		JPanel credits = new JPanel();
		credits.setLayout(new BoxLayout(credits, BoxLayout.Y_AXIS));
		
		ArrayList<Component> l = new ArrayList<Component>();
				//l.add( new JLabel("Advances in Information Systems Laboratory") ); 
		//l.add( new JLabel("University of Illinois at Chicago") );          

		l.add( new JLabel("Professor Isabel F. Cruz") );					// 0
		l.add( Box.createVerticalStrut(10) );
		l.add( new JLabel("AgreementMaker v0.23 ( April 14th 2011 )"));   // 1
		l.add( new JLabel("Joe Lozar"));   // 1
		l.add( Box.createVerticalStrut(10) );
		l.add( new JLabel("AgreementMaker v0.22 ( Dec 25th 2010 )"));   // 1
		l.add( new JLabel("Michele Caci, Matteo Palmonari, Federico Caimi")); // 2
		l.add( Box.createVerticalStrut(10) );
		l.add( new JLabel("AgreementMaker v0.21 ( Aug 19th 2010 )"));  // 3
		l.add( Box.createVerticalStrut(10) );
		l.add( new JLabel("AgreementMaker v0.2 ( fall 2008 - 2009):") );  // 4
		l.add( new JLabel("Flavio Palandri Antonelli, Cosmin Stroe, Ula\u0219 Kele\u0219.") );  // Ulas Keles  // 5
		l.add( Box.createVerticalStrut(10) );
		l.add( new JLabel("AgreementMaker v0.1 (2001 - 2008):") );   // 6
		l.add( new JLabel("Afsheen Rajendran, Anjli Chaudhry, Nalin Makar,") );  // 7
		l.add( new JLabel("Sarang Kapadia, Sujan Bathala, William Sunna.") ); //8
		
		for(int i=0; i< l.size(); i++ ) { 
			if( l.get(i) instanceof JLabel ) {
				((JLabel)l.get(i)).setAlignmentX(Component.CENTER_ALIGNMENT);
			}
		}

		int index = 0;
		l.get(index).setFont(new Font("Helvetica", Font.PLAIN,  22)); index++;
		index++;
		l.get(index).setFont(new Font("Helvetica", Font.BOLD,  14)); index++;
		l.get(index).setFont(new Font("Helvetica", Font.PLAIN,  14)); index++;
		index++;
		l.get(index).setFont(new Font("Helvetica", Font.BOLD,  14)); index++;
		l.get(index).setFont(new Font("Helvetica", Font.PLAIN,  14)); index++;
		index++;
		l.get(index).setFont(new Font("Helvetica", Font.BOLD,   14)); index++;
		index++;
		l.get(index).setFont(new Font("Helvetica", Font.BOLD,   14)); index++;
		l.get(index).setFont(new Font("Helvetica", Font.PLAIN,   14)); index++;
		index++;
		l.get(index).setFont(new Font("Helvetica", Font.BOLD,  14)); index++;
		l.get(index).setFont(new Font("Helvetica", Font.PLAIN,   14)); index++;
		l.get(index).setFont(new Font("Helvetica", Font.PLAIN,  14)); index++;
		
		credits.add(Box.createVerticalStrut(10));

		for( int i=0; i<l.size(); i++) { credits.add(l.get(i)); }

		credits.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		

		
		JPanel middle = new JPanel(new BorderLayout());
		middle.add(image_label, BorderLayout.NORTH);
		middle.add(credits, BorderLayout.CENTER);
		
		/*********** BOTTOM *************/
		
		close = new JButton("Close");
		close.addActionListener(this);
		JPanel bottom = new JPanel( new FlowLayout(FlowLayout.CENTER));
		
		image = new BufferedImage(61, 21, BufferedImage.TYPE_BYTE_BINARY);
		
		try {
			image = ImageIO.read(new ByteArrayInputStream(am));
		} catch (IOException e) {
			e.printStackTrace();
		}	
		cartoon = new JButton(new ImageIcon(image));
		cartoon.addActionListener(this);
		
		close.setPreferredSize(cartoon.getPreferredSize());
		
		bottom.add(close);
		bottom.add(cartoon);
		
		BorderLayout layout = new BorderLayout(5,5);
		setLayout(layout);
		setSize(620, 500);
		
		

		
		//frameAbout.add(title, BorderLayout.NORTH);
		add(middle, BorderLayout.CENTER);
		
		add(bottom, BorderLayout.SOUTH);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
	}

	// make the escape key work
	@Override
	protected JRootPane createRootPane() {
		JRootPane rootPane = new JRootPane();
		KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
		Action actionListener = new AbstractAction() {
			private static final long serialVersionUID = 1774539460694983567L;
			public void actionPerformed(ActionEvent actionEvent) {
				close.doClick();
			}
		};
		InputMap inputMap = rootPane
		.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(stroke, "ESCAPE");
		rootPane.getActionMap().put("ESCAPE", actionListener);

		return rootPane;
	}
	
	public void actionPerformed(ActionEvent arg0) {
		
		
		Object o = arg0.getSource();
		
		if( o.equals(close) ) {
			setVisible(false);
		} else if( o.equals(cartoon)) {

			
			isytcctssoaa = new JDialog();
			isytcctssoaa.setTitle(":)");
			isytcctssoaa.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			
			JPanel threeAm = new JPanel();
			threeAm.setLayout(new BorderLayout(10,10));
		
			JLabel am = new JLabel();
			ImageIcon drawing = new ImageIcon("images/agreementMaker.png");
			am.setIcon(drawing);
			
			threeAm.add(am, BorderLayout.CENTER);
			
			JPanel three45Am = new JPanel(new FlowLayout(FlowLayout.CENTER));
			OkBackToWork = new JButton("Ok");
			OkBackToWork.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			three45Am.add(OkBackToWork);
			threeAm.add(three45Am, BorderLayout.SOUTH);
			OkBackToWork.addActionListener(this);
			
			isytcctssoaa.add(threeAm);
			isytcctssoaa.setModal(true);
			isytcctssoaa.pack();
			isytcctssoaa.setLocationRelativeTo(null);
			
			
			isytcctssoaa.setVisible(true); // the end for tonight.
			
			
		} else if( o.equals(OkBackToWork)) {
			isytcctssoaa.dispose();
		}
	}
	
	
}
