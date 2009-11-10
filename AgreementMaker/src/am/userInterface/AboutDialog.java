package am.userInterface;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import am.GlobalStaticVariables;

public class AboutDialog implements ActionListener {
		
	
  private final static byte am[]={ // don't touch
	    -119,80,78,71,13,10,26,10,0,0,0,13,73,72,68,82,0,0,0,61,0,0,0,21,1,3,0,0,0,-49,83,116,-15,0,0,0,1,115,82,
	    71,66,0,-82,-50,28,-23,0,0,0,6,80,76,84,69,-1,-1,-1,0,0,0,85,-62,-45,126,0,0,0,1,98,75,71,68,0,-120,5,29,72,0,0,
	    0,9,112,72,89,115,0,0,46,35,0,0,46,35,1,120,-91,63,118,0,0,0,7,116,73,77,69,7,-40,11,26,9,45,18,-90,-113,-94,32,0,0,
	    0,29,116,69,88,116,67,111,109,109,101,110,116,0,67,114,101,97,116,101,100,32,119,105,116,104,32,84,104,101,32,71,73,77,80,-17,100,37,110,0,
	    0,0,89,73,68,65,84,8,-41,99,96,-64,10,-28,-1,127,80,0,18,64,112,64,-2,1,-125,-16,-1,127,32,65,5,6,1,-120,44,54,-122,4,-126,97,
	    0,99,-28,49,-80,67,24,-121,25,120,30,-128,25,7,25,36,10,-64,-116,102,6,3,9,16,-125,-79,-99,-63,-128,3,-52,-24,103,48,-80,0,50,56,24,-7,
	    25,12,42,112,48,-6,97,-116,54,48,3,0,76,57,24,0,37,7,126,100,0,0,0,0,73,69,78,68,-82,66,96,-126
		  };
	
	private JDialog frameAbout;
	private JButton close, cartoon;
	private JLabel image_label;
	private ImageIcon label_icon;
	
	private JLabel AgreementMaker;
	private BufferedImage image;

	private JDialog isytcctssoaa;
	private JButton OkBackToWork;
	
	
	public AboutDialog() {
		
		/**
		 * This is the part of the program where the REAL fun stuff happens.
		 */
		
		/** Main window */
		frameAbout = new JDialog();
		frameAbout.setTitle("About Agreement Maker...");
		frameAbout.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		frameAbout.setResizable(false);
		
		
		
		/***************** TITLE ******************/
	
		
		
		AgreementMaker = new JLabel("<html><h1>Agreement Maker" + " " + GlobalStaticVariables.AgreementMakerVersion + "</h1></html>");
		
		JPanel title = new JPanel(new FlowLayout(FlowLayout.CENTER));
		title.add(AgreementMaker);		
		
		
		
		/********* MIDDLE *****************/
		
		image_label = new JLabel();
		label_icon = new ImageIcon("images/advis.png");
		//image_label.setSize(600,400);
		image_label.setIcon(label_icon);

		
		JPanel credits = new JPanel();
		credits.setLayout(new BoxLayout(credits, BoxLayout.Y_AXIS));
		
		JLabel[] l = new JLabel[8];
		
		l[0] = new JLabel("Professor Isabel Cruz");
		l[1] = new JLabel("Advances in Information Systems Laboratory");
		l[2] = new JLabel("University of Illinois at Chicago");

		l[3] = new JLabel("Agreement Maker v0.2 ( fall 2008 - 2009):");
		l[4] = new JLabel("Flavio Palandri Antonelli, Cosmin Stroe, Ula\u0219 Kele\u0219.");
		
		l[5] = new JLabel("Agreement Maker v0.1 (2001 - 2008):");
		l[6] = new JLabel("Afsheen Rajendran, Anjli Chaudhry, Nalin Makar,");
		l[7] = new JLabel("Sarang Kapadia, Sujan Bathala, William Sunna.");
		
		for(int i=0; i<3; i++ ) { l[i].setAlignmentX(Component.CENTER_ALIGNMENT); }
		for(int i=3; i<l.length; i++ ) { l[i].setAlignmentX(Component.CENTER_ALIGNMENT); }

		l[0].setFont(new Font("Helvetica", Font.PLAIN,  22));
		l[1].setFont(new Font("Helvetica", Font.PLAIN,  18));
		l[2].setFont(new Font("Helvetica", Font.PLAIN,  18));
		
		l[3].setFont(new Font("Helvetica", Font.BOLD,   14));
		l[4].setFont(new Font("Helvetica", Font.PLAIN,  14));
		l[5].setFont(new Font("Helvetica", Font.BOLD,   14));
		l[6].setFont(new Font("Helvetica", Font.PLAIN,  14));
		l[7].setFont(new Font("Helvetica", Font.PLAIN,  14));
		
		credits.add(l[0]);
		credits.add(Box.createVerticalStrut(10));
		credits.add(l[1]);
		credits.add(Box.createVerticalStrut(10));
		credits.add(l[2]);
		credits.add(Box.createVerticalStrut(20));
		
		for( int i=3; i<l.length; i++) {
			credits.add(l[i]);
			if( i == 3 || i == 5 ) credits.add(Box.createVerticalStrut(5));
			if( i == 4 ) credits.add(Box.createVerticalStrut(10));
		}

		credits.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		

		
		JPanel middle = new JPanel(new FlowLayout(FlowLayout.CENTER));
		middle.add(image_label);
		middle.add(credits);
		
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
		frameAbout.setLayout(layout);
		frameAbout.setSize(620, 500);
		
		

		
		frameAbout.add(title, BorderLayout.NORTH);
		frameAbout.add(middle, BorderLayout.CENTER);
		
		frameAbout.add(bottom, BorderLayout.SOUTH);
		
		frameAbout.pack();
		frameAbout.setLocationRelativeTo(null);
		frameAbout.setModal(true);
		frameAbout.setVisible(true);
		
		
	}

	public void actionPerformed(ActionEvent arg0) {
		
		
		Object o = arg0.getSource();
		
		if( o.equals(close) ) {
			frameAbout.dispose();
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
