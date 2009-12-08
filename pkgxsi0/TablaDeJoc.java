package pkgxsi0;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;

/**
 * Aceasta clasa implementeaza partea grafica si nu numai a unei table de joc.
 *
 * @author Cristian Ciupitu 342 C2
 */
public class TablaDeJoc extends JDialog {
	// constante
	public static final int XX = AlgoritmJoc.XX, OO = AlgoritmJoc.OO;
	private static final int GOL = AlgoritmJoc.GOL, 
		SELECTATA = XX + OO + GOL + 1;	// sa fim sigur ca-i diferita
	public static final int MAX_LINII = 30, MAX_COLOANE = 30;
	private static int dim_celula_ox = 32, dim_celula_oy = 32;
	public static final Paint
	    paint_fond = (Paint) Color.white,
	    paint_margine = (Paint) Color.black,
	    paint_selectie = (Paint) new Color(97, 253, 101);
	//
	private int mutare;	/** simbolul cu care joaca jucatorul curent */
	private int nr_mutari;	/** nr de mutari efectuate */
	private int linii, coloane; /** cate linii si cate coloane sunt */
	private boolean se_muta; /** semafor care indica ca are loc o mutare */
	private AlgoritmJoc algXX, algOO;	/** algoritmii de joc */
	private Celula[][] celule;		/** celulele de pe ecran */
	private int[][] tabla;		/** tabla folosita de algoritmi */
	private Image imgXX, imgOO;	/** imaginile pt simboluri 	*/


	/**
	 * Afiseaza un mesaj.
	 * <p>
	 * Este folosita pentru a anunta sfarsitul partidei.
	 */
	private void anunta(String titlu, String mesaj) {
		JOptionPane.showMessageDialog(null,
					      mesaj, titlu,
					      JOptionPane.ERROR_MESSAGE);
	}
	
	
	/**
	 * Pune simbolul curent in pozitia <code>pozitie</code>.
	 *
	 * @return daca partida s-a terminat sau nu
	 */ 
	private void muta(Pozitie pozitie) {
		/* momentan din cauza "elegantei" OOP avem informatie 
		   redundanda, si anume simbolul aflat intr-o celula */
		
		// errare humane est :-)
		// pt doar de verificare a corectitudinii algoritmului ...
		assert(tabla[pozitie.linie][pozitie.coloana] != GOL);

		tabla[pozitie.linie][pozitie.coloana] = mutare;
		celule[pozitie.linie][pozitie.coloana].simbol = mutare;
		celule[pozitie.linie][pozitie.coloana].repaint();
		nr_mutari++;

		/* s-a ajuns la sfarsitul partidei ? */
		int stadiu =
		    AlgoritmJoc.stadiuPartida(tabla, nr_mutari, pozitie);
		if (stadiu == AlgoritmJoc.REMIZA) {
			anunta("Sfarsitul partidei de " + nr_mutari +
			       " mutari", "REMIZA");
			se_muta = false;	// ca sa se termine thread-ul
			dispose();	// inchidem fereastra
		} else if (stadiu == AlgoritmJoc.XXCASTIGATOR) {
			anunta("Sfarsitul partidei de " + nr_mutari +
			       " mutari", "X castigator!");
			se_muta = false;	// ca sa se termine thread-ul
			dispose();	// inchidem fereastra
		} else if (stadiu == AlgoritmJoc.OOCASTIGATOR) {
			anunta("Sfarsitul partidei de " + nr_mutari +
			       " mutari", "O castigator!");
			se_muta = false;	// ca sa se termine thread-ul
			dispose();	// inchidem fereastra
		}
	}

	
	/**
	 * Executa urmatoarea mutare, dupa ce s-a mutat in pozitia pozitie.
	 */
	private void urmatoareaMutare(Pozitie pozitie) {
		// punem calculatorul sa joace
		// daca e om nu se face nimic 
		if ((mutare == XX) && (algXX != null)) {
			muta(algXX.joaca(pozitie));
			mutare = OO;
		} else if ((mutare == OO) && (algOO != null)) {
			muta(algOO.joaca(pozitie));
			mutare = XX;
		}
	}

	
	/**
	 * O celula din tabla.
	 */
	class Celula extends JPanel implements ImageObserver {
		public static final int SELECTATA = 3;
		int simbol = GOL;	// simbolul aflat in celula
		Pozitie pozitie;	// in ce pozitie se afla celula

		public Celula(int linie, int coloana) {
			this.pozitie = new Pozitie(linie, coloana);
			// doar daca cel putin unul din algoritmi e uman
			// are sens sa punem un mouse listener
			if ((algXX == null) || (algOO == null))
				addMouseListener(new ML());
		}
		
		
		/**
		 * Deseneaza celula.
		 */ 		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D) g;
			int x2 = getSize().width - 1;
			int y2 = getSize().height - 1;

			g2.setPaint(paint_margine);	// desenam marginea
			g.drawRect(0, 0, x2, y2);
			if (simbol == GOL)
				return;
			if (simbol == SELECTATA) {
				// umplem celula selectata 
				g2.setPaint(paint_selectie);
				g2.fillRect(1, 1, x2 - 1, y2 - 1);
				return;
			}
			if (simbol == XX) {
				g.drawImage(imgXX, 1, 1, x2 - 1, y2 - 1,
					    this);
				return;
			}
			if (simbol == OO) {
				g.drawImage(imgOO, 1, 1, x2 - 1, y2 - 1,
					    this);
				return;
			}
		}

		class ML extends MouseAdapter {
			public void mousePressed(MouseEvent e) {
				if (se_muta)
					return;
				// celula e deja ocupata ?
				// pt a face click trebuie selectata mai intai
				if (simbol != SELECTATA) {
					System.err.
					    println
					    ("celula e deja ocupata");
					return;
				}
				se_muta = true;
				muta(pozitie);	// mazgalim celula
				// trecem la urmatorul simbol (jucator)
				mutare = ((mutare == XX) ? OO : XX);
				// punem adversarul sa mute                     
				urmatoareaMutare(pozitie);
				se_muta = false;
			}

			public void mouseEntered(MouseEvent e) {
				// doar o celula goala poate fi selectata
				if (simbol != GOL)
					return;
				simbol = SELECTATA;
				repaint();
			}

			public void mouseExited(MouseEvent e) {
				// doar daca a fost selectata o deselectam
				if (simbol != SELECTATA)
					return;
				simbol = GOL;
				repaint();
			}
		}
	}

	
	/**
	 * Fir de executie care se ocupa de jocul Calculator vs Calculator.
	 */
	private class CalcVsCalc extends Thread {
		TablaDeJoc fereastra_parinte;
		
		public CalcVsCalc(TablaDeJoc fereastra_parinte) {
			// scriem cod cat mai elegant si fiabil
			if ((algXX == null) || (algOO == null))
				throw new UnsupportedOperationException();
			 this.fereastra_parinte = fereastra_parinte;
		} 
		
		public void run() {
			Pozitie poz = null;
			se_muta = true;
			while (se_muta) {
				if (mutare == OO) {
					muta(poz = algOO.joaca(poz));
					mutare = XX;
				} else {	// if (mutare == XX)
					muta(poz = algXX.joaca(poz));
					mutare = OO;
				}
				try {
					sleep(400); // o mica pauza (in ms)
				}
				catch(InterruptedException e) {
					System.err.println(e);
				}
			}
		}
	}


	/**
	 * @param	linii		nr de linii ale tablei.
	 * @param	coloane		nr de coloane ale tablei.
	 * @param	algXX		algoritmul pt cel care joaca cu X.
	 * @param	algOO		algoritmul pt cel care joaca cu 0.
	 * @param	imgXX		imaginea pentru simbolul X.
	 * @param	imgOO		imaginea pentru simbolul 0.
	 * @param	prima_mutare	simbolul care incepe primul.
	 */
	public TablaDeJoc(int linii, int coloane, AlgoritmJoc algXX,
			  AlgoritmJoc algOO, Image imgXX, Image imgOO,
			  int prima_mutare) throws IllegalArgumentException
	{
		// validarea parametrilor
		if ((linii < 5) || (linii > MAX_LINII) || (coloane < 5) ||
		    (coloane > MAX_COLOANE) ||
		    ((prima_mutare != XX) && (prima_mutare != OO)))
			throw new IllegalArgumentException();

		 this.linii = linii;
		 this.coloane = coloane;
		 this.algXX = algXX;
		 this.algOO = algOO;
		 this.imgXX = imgXX;
		 this.imgOO = imgOO;
		 this.mutare = prima_mutare;
		 dim_celula_ox = imgXX.getWidth(this) + 2;
		 dim_celula_ox = imgXX.getHeight(this) + 2;

		Container cp = getContentPane();
		 cp.setLayout(new GridLayout(linii, coloane));
		 celule = new Celula[linii][coloane];
		 tabla = new int[linii][coloane];

		int i, j;
		for (i = 0; i < linii; i++)
			for (j = 0; j < coloane; j++) {
				tabla[i][j] = GOL;
				cp.add(celule[i][j] = new Celula(i, j));
			}
		    setSize(linii * dim_celula_ox,
			    coloane * dim_celula_oy);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		try {
			CalcVsCalc cvsc = new CalcVsCalc(this);
			System.err.println("Dau drumul la thread!");
			cvsc.start();
		}
		catch (UnsupportedOperationException e) {
			// avem cel putin un jucator uman
			// oare incepe calculatorul primul ?
			if (((mutare == XX) && (algXX != null)) ||
			    ((mutare == OO) && (algOO != null)))
				urmatoareaMutare(null);
		}
	}
}
