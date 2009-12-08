import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import pkgxsi0.*;

public class Xsi0 extends JApplet
{
	static final String[] listaAlgoritmi = {"Genetic", "Random", "Uman"};
	static final String[] listaSimboluri = {"X", "0"};
	Image imgX;
	Image img0;
	JTextField txtLinii = new JTextField("10"), 
			txtColoane = new JTextField("10");

	// listele cu algoritmi
	JComboBox cmbAlgoritm1 = new JComboBox(listaAlgoritmi);
	JComboBox cmbAlgoritm2 = new JComboBox(listaAlgoritmi);

	// alegerea simbolului cu care se incepe
	JComboBox cmbSimbolStart = new JComboBox(listaSimboluri);


	private void eroare(String cod_eroare, String mesaj)
	{ 
		JOptionPane.showMessageDialog (null,
				mesaj, cod_eroare, JOptionPane.ERROR_MESSAGE);
	}


	public void init()
	{
		// incarcam imaginile
		try {
			imgX = getImage(new URL(getCodeBase(), 
						"imagini/imgX.gif"));
			img0 = getImage(new URL(getCodeBase(), 
						"imagini/img0.gif"));
		}
		catch (MalformedURLException e)	{
			System.err.println("exceptia: " + e);
			System.exit(1);
		}

		// punem optiunile
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(5,2));
		p.add(new JLabel("Nr de linii", JLabel.CENTER));
		p.add(txtLinii);
		p.add(new JLabel("Nr de coloane", JLabel.CENTER));
		p.add(txtColoane);
		p.add(new JLabel("Algoritmul pt primul jucator", 
					JLabel.CENTER));
		p.add(cmbAlgoritm1);
		p.add(new JLabel("Simbolul cu care se incepe", JLabel.CENTER));
		p.add(cmbSimbolStart);
		p.add(new JLabel("Algoritmul pt al doilea jucator", 
					JLabel.CENTER));
		p.add(cmbAlgoritm2);

		// punem "despartitorul"
		Container cp = getContentPane();
		cp.add(p, BorderLayout.NORTH);

		// punem butonul
		JButton b = new JButton("JOACA!");
		b.addActionListener(new BL());
		cp.add(b, BorderLayout.SOUTH);
	}


	public void start()
	{
		if ((imgX == null) || (img0 == null)) {
			eroare("Poze", "n-am poze!");
			System.exit(1);
		}
	}


	AlgoritmJoc aflaAlg(JComboBox cmbAlgoritmi, int l, int c)
	{
		String strAlg = "" + cmbAlgoritmi.getSelectedItem();
		// ce algoritm a fost ales ?
		System.err.println("A fost selectat <"+strAlg+">");
		if (strAlg.equals("Uman"))
			return null;
		if (strAlg.equals("Random"))
			return (AlgoritmJoc) (new AlgoritmRandom(l, c));
		if (strAlg.equals("Genetic"))
			return (AlgoritmJoc) (new AlgoritmGenetic(l, c));

		System.err.println("ceva ciudat la combo box!");
		return null; // anti-warning
	}


	class BL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int nrlinii, nrcoloane;

			try {
				nrlinii = Integer.parseInt(txtLinii.getText());
				nrcoloane = Integer.parseInt(txtColoane.getText());
			}
			catch (NumberFormatException exp) {
				eroare(exp.toString(), "da nr bai ...");
				return;
			}

			AlgoritmJoc algXX = null, algOO = null;
			String strSimbolStart = "" + cmbSimbolStart.getSelectedItem();
			int simbol_start;

			// cu ce simbol se incepe ?
			if (strSimbolStart.equals("X"))	{
				simbol_start = TablaDeJoc.XX;
				algXX = aflaAlg(cmbAlgoritm1, nrlinii, 
						nrcoloane);
				// al doilea jucator
				algOO = aflaAlg(cmbAlgoritm2, nrlinii, 
						nrcoloane);
			}
			else {	// se incepe cu "0"
				simbol_start = TablaDeJoc.OO;
				algOO = aflaAlg(cmbAlgoritm1, nrlinii, 
						nrcoloane);
				// al doilea jucator
				algXX = aflaAlg(cmbAlgoritm2, nrlinii, 
						nrcoloane);
			}

			// am aflat toate optiunile; dam drumul la joc
			try {
				TablaDeJoc tabeladejoc = new TablaDeJoc(
						nrlinii, nrcoloane, algXX, 
						algOO, imgX, img0, 
						simbol_start);
				tabeladejoc.setVisible(true);
			}
			catch (IllegalArgumentException exp) {
				eroare("Err Optiuni", "Optiuni gresite");
			}
		}
	}
}
