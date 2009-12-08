package pkgxsi0;

/**
 * Prototipul unui algoritm avansat. 
 * <p>
 * Contine diverse functii auxiliare.
 * Pentru simplitate se considera ca se joaca cu X.
 *
 * @author Cristian Ciupitu 342 C2
 */

abstract class AlgoritmAvansat extends AlgoritmJoc {
	/**
	 * ponderile pentru piesele noastre; 
	 * fac algoritmul mai constructiv (ofensiv)
	 */
	// valorile recomandate
	// protected int[] pondereXX = { 0, 4, 16, 100, 5000 };
	// valori experimentale
	// protected int[] pondereXX = { 0, 4, 16, 64, 4096 };
	// protected int[] pondereXX = { 0, 16, 32, 56, 2096 };
	// valori pentru o tactica ceva mai precauta, care contracareaza
	// unele constructii ale adversarului, cu pretul unei constructii
	// proprii mai slabe
	protected int[] pondereXX = { 0, 4, 16, 100, 6234 };
	/**
	 * ponderile pentru piesele adversarului; 
	 * fac algoritmul mai destructiv (defensiv)
	 */
	// protected int[] pondereOO = { 0, 2, 10, 180, 3000 };
	// protected int[] pondereOO = { 0, 6, 24, 120, 720};
	// protected int[] pondereOO = { 0, 4, 24, 64, 3720};
	protected int[] pondereOO = { 0, 2, 10, 80, 4000 };


	public AlgoritmAvansat(int linii, int coloane) {
		super(linii, coloane);
	}


	/**
	 * @return cate celule libere au mai ramas pe tabla de joc.
	 */
	protected int celule_libere() {
		return linii * coloane - nr_mutari;
	}


	/**
	 * Afla un punctaj in functie de cate simboluri <code>s</code> 
	 * sunt aliniate de-a lungul axelor in jurul pozitiei primite 
	 * ca parametru.
	 *
	 * @param	s	simbolul cautat
	 * 
	 * @return 	scorul unei pozitii pentru un anumit simbol.
	 */
	protected int poz_eval_simbol(int linie, int coloana, int s) {
		int i, j, k;
		int[] pondere = (s == XX) ? pondereXX : pondereOO;
		int rez = 0;

		/*
		 * AXA OX 
		 */
		k = 0;
		/* <- */
		i = linie;
		j = coloana - 1;
		for (; (k < 4) && (j >= 0); k++, j--)
			if (tabla[i][j] != s)
				break;
		/* -> */
		// i = linie;
		j = coloana + 1;
		for (; (k < 4) && (j < coloane); k++, j++)
			if (tabla[i][j] != s)
				break;
		rez += pondere[k];


		/*
		 * AXA OY 
		 */
		k = 0;
		/* v */
		// i = linie;
		i++;
		j = coloana;
		for (; (k < 4) && (i < linii); k++, i++)
			if (tabla[i][j] != s)
				break;
		/* ^ */
		i = linie - 1;
		// j = coloana;
		for (; (k < 4) && (i >= 0); k++, i--)
			if (tabla[i][j] != s)
				break;
		rez += pondere[k];


		/*
		 * AXA \
		 */
		k = 0;
		/* ^\ */
		i = linie - 1;
		j--;		// j = coloana - 1
		for (; (k < 4) && (i >= 0) && (j >= 0); k++, i--, j--)
			if (tabla[i][j] != s)
				break;
		/* \v */
		i = linie + 1;
		j = coloana + 1;
		for (; (k < 4) && (i < linii) && (j < coloane);
		     k++, i++, j++)
			if (tabla[i][j] != s)
				break;
		rez += pondere[k];


		/*
		 * AXA /
		 */
		k = 0;
		/* /^ */
		i = linie - 1;
		j = coloana + 1;
		for (; (k < 4) && (i >= 0) && (j < coloane); k++, i--, j++)
			if (tabla[i][j] != s)
				break;
		/* v/ */
		i = linie + 1;
		j = coloana - 1;
		for (; (k < 4) && (i < linii) && (j >= 0); k++, i++, j--)
			if (tabla[i][j] != s)
				break;
		rez += pondere[k];

		return rez;
	}


	/**
	 * O functie euristica de evaluare a unei pozitii de pe tabla de joc.
	 *
	 * @return scorul pozitiei (linie, coloana).
	 */
	protected int poz_eval(int linie, int coloana) {
		return poz_eval_simbol(linie, coloana, XX) +
		    poz_eval_simbol(linie, coloana, OO);
	}
}
