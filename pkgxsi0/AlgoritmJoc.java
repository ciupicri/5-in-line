package pkgxsi0;

/**
 * Prototipul unui algoritm generic (general).
 * <p>
 * Prin conventie noi jucam cu X, iar celalalt cu 0.
 *
 * @author Cristian Ciupitu 342 C2
 */
public abstract class AlgoritmJoc {
	/** 
	 * Constante care descriu stadiul partidei.
	 */
	static final int NETERMINATA = 0, REMIZA = 1, XXCASTIGATOR = 2,
			 OOCASTIGATOR = 3;

	static final int GOL = 0, XX = 1, OO = 2;
	protected int linii, coloane;
	protected int nr_mutari;
	protected int[][] tabla;


	public AlgoritmJoc(int linii, int coloane) {
		this.linii = linii;
		this.coloane = coloane;
		nr_mutari = 0;
		tabla = new int[linii][coloane];
	}

	
	/**
	 * O metoda care ne spune daca am ajuns la o configuratie 
	 * castigatoare dupa ce am facut mutarea <code>mutare</code>.
	 * <p>
	 * In aceasta procedura ne bazam pe faptul ca ultima mutare
	 * poate fi castigatoare, mai exact pe faptul ca ultimul
	 * simbol pus pe tabla poate face parte din configuratia 
	 * castigatoare (5 in linie) si cautam in jurul acestei 
	 * pozitii in cele 6 directii.
	 *
	 * @return stadiul partidei; s-a terminat (cum?) sau nu.
	 */ 
	public static int stadiuPartida(int[][]tabla,
					int nr_mutari, Pozitie mutare) {
		int i, j;
		int s;
		int linii = tabla.length, coloane = tabla[0].length;

		/*
		 *  O posibila optimizare. Pt a termina o partida e nevoie
		 * de cel putin 9 mutari, dar nu are sens pentru ca oricum
		 * partidele se joaca pana la capat, deci in cele din urma
		 * utilizatorul va simti o penalizare.
		 */
		i = mutare.linie;
		j = mutare.coloana;
		s = tabla[i][j];	// simbolul cu care s-a mutat

		int rez = NETERMINATA;
		if (s == XX)
			rez = XXCASTIGATOR;
		else if (s == OO)
			rez = OOCASTIGATOR;

		/*
		 * k = cate piesa in linie avem;
		 * k = 1 .. 5
		 * Cand ajungem la a 5-a piesa ne oprim
		 *
		 * Pe aceeasi directie piesele (k) se cumuleaza
		 * (k nu e reinitilizat)
		 */

		/*
		 * AXA OX 
		 */
		int k = 1;
		/* <- */
		j--;		// mutare.coloana - 1
		for (; (j >= 0) && (k < 5); k++, j--)
			if (tabla[i][j] != s)
				break;
		/* -> */
		j = mutare.coloana + 1;
		for (; (j < coloane) && (k < 5); k++, j++)
			if (tabla[i][j] != s)
				break;
		if (k == 5)	// s-au strans 5 simboluri in linie
			return rez;

		/*
		 * AXA OY 
		 */
		k = 1;
		/* v */
		j = mutare.coloana;
		i++;		// i = mutare.linie + 1
		for (; (i < linii) && (k < 5); k++, i++)
			if (tabla[i][j] != s)
				break;
		/* ^ */
		i = mutare.linie - 1;
		for (; (i >= 0) && (k < 5); k++, i--)
			if (tabla[i][j] != s)
				break;
		if (k == 5)	// s-au strans 5 simboluri in linie
			return rez;

		/*
		 * AXA \
		 */
		k = 1;
		/* \v */
		i = mutare.linie + 1;
		j++;		// j = mutare.coloana + 1
		for (; (i < linii) && (j < coloane) && (k < 5);
		     k++, i++, j++)
			if (tabla[i][j] != s)
				break;
		/* ^\ */
		i = mutare.linie - 1;
		j = mutare.coloana - 1;
		for (; (i >= 0) && (j >= 0) && (k < 5); k++, i--, j--)
			if (tabla[i][j] != s)
				break;
		if (k == 5)	// s-au strans 5 simboluri in linie
			return rez;

		/*
		 * AXA / 
		 */
		k = 1;
		/* /^ */
		i = mutare.linie - 1;
		j = mutare.coloana + 1;
		for (; (i >= 0) && (j < coloane) && (k < 5); k++, i--, j++)
			if (tabla[i][j] != s)
				break;
		/* v/ */
		i = mutare.linie + 1;
		j = mutare.coloana - 1;
		for (; (i < linii) && (j >= 0) && (k < 5); k++, i++, j--)
			if (tabla[i][j] != s)
				break;
		if (k == 5)	// s-au strans 5 simboluri in linie
			return rez;

		if (nr_mutari == (linii * coloane))
			return REMIZA;

		return NETERMINATA;
	}

	
	/**
	 * Aceasta metoda va fi implimentata de diversii algoritmi de joc.
	 */
	protected abstract Pozitie _joaca(Pozitie mutare);
	

	/**
	 * Metoda wrapper pentru a nu scrie aceleasi "prostii"
	 * in fiecare algoritm
	 *
	 * @param       mutare  pozitia pe care a jucat celalalt jucator.
	 *
	 * @return      pozitia pe care muta algoritmul (calculatorul).
	 */
	public Pozitie joaca(Pozitie mutare) {
		// executam mutarea adversarului 
		if (mutare != null) {
			tabla[mutare.linie][mutare.coloana] = OO;
			nr_mutari++;
		}
		// aflam unde trebuie sa mutam
		Pozitie rez = _joaca(mutare);

		// executam mutarea noastra
		tabla[rez.linie][rez.coloana] = XX;
		nr_mutari++;

		return rez;
	}
}
