package pkgxsi0;

import java.util.*;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;

/**
 * Aceasta clasa implementeaza un algoritm genetic de joc.
 *
 * <p>
 * <b>Avantajul</b> unui algoritm genetic este timpul de rulare, insa in general
 * acestia sunt folositi pentru jocuri "single-player", si nu multi-player,
 * asa cum este X si 0.
 * <p>
 * <b>Dezavantajul</b> unui algoritm genetic consta in faptul ca nu gaseste
 * solutia optima, ci una aproximativa si ca depinde (foarte) mult de 
 * generatorul de numere aleatoare. Din pacate generatorul de numere aleatoare
 * din Java scoate numere <i>pseudoaleatoare</i>, si deci nu este prea grozav. 
 * Aceasta parte ar putea fi imbunatatita prin folosirea unui generator mai 
 * bun asa cum este SecureRandom, insa din acesta este cam lent. Nu duce
 * probabil la o utilizare mai mare a procesorului, insa are nevoie de 
 * extragarea unor esantioane lucru care necesita un timp fix.
 * Alt dezavantaj consta in faptul ca fiind un algoritm probabilistic,
 * pot exista situatii in care algoritmul nu se apara, desi este evident 
 * ca ar trebui s-o faca. El poate fi corectat prin modificarea metodelor
 * de evaluare a pozitiilor sau prin combinarea lui cu altii algoritmi care
 * sa caute intr-un mod mai bun, pozitiile/configuratiile critice.
 * 
 * <p>
 * De mentionat ar fi ca acest algoritm a reusit sa bata de cateva ori
 * minimax-ul.
 * 
 * <p>
 * <b>Descrierea algoritmului</b>
 * <p>
 * Gena e formata dintr-o pozitie si valoarea acelei pozitii.
 * Cromozomul e format din mai multe pozitii, adica mutari, si reprezinta, deci
 * seria de mutari necesare pentru a castiga. Un cromozom nu contine doar
 * mutarile unui singur jucator, ci mutarile <i>ambilor</i> jucatori, pentru
 * ca avem de-a face cu o competitie.
 * 
 * <p>
 * Pentru acest algoritm avem mai intai nevoie de niste clase auxiliare, si 
 * anume clasele <code>Gena</code>, <code>Cromozom</code> si 
 * <code>Populatie</code>. 
 * O gena reprezina o mutare, iar cromozonul o serie de mutari (intrucat 
 * un cromozom are mai multe gene); acestea sunt mutari sunt facute succesiv 
 * de ambii jucatori, adica un cromozom nu contine doar mutarile unui singur
 * jucator, ci mutarile pentru <i>ambii</i> jucatori.
 * 
 * <p>
 * <b>Nota:</b>
 *
 * Algoritmul este adaptiv, in sensul ca pentru o tabla de joc mai mare,
 * se utilizeaza mai multe resurse de calcul, pentru a juca in continuare
 * la fel de bine. Adapatarea se face in mod liniar.
 *  
 * @author Cristian Ciupitu 342 C2
 */
public class AlgoritmGenetic extends AlgoritmAvansat {
	/**
	 * nr de gene dintr-un cromozom
	 */
	protected int Ng;
	/**
	 * nr de cromozomi (populatia)
	 */
	protected int Np;
	/**
	 * nr de generatii
	 */
	protected int Ge;
	/**
	 * nr de pasi prezisi
	 */
	protected int Npasi;
	/**
	 * probabilitate controla pt incrucisare
	 */
	protected float Pc;
	public static final float default_Pc = 0.90123f;
	/**
	 * factorul de incrucisare (cat procente din al doilea cromozom
	 * se duc in cromozomul rezultat din imperechere)
	 */
	protected float Tx;
	public static final float default_Tx = 0.5f;
	/**
	 * acesti factori stabilesc o proportionalitate intre dimensiunile 
	 * tablei si parametrii "genetici" de mai sus
	 */
	protected double factor_Ng, factor_Np, factor_Ge;
	public static final double default_factor_Ng = 0.0536,
	    default_factor_Np = 0.0565, default_factor_Ge = 0.0457;
	/**
	 * generatorul de numere aleatoare
	 */
	// protected SecureRandom randGenerator;
	protected Random randGenerator;

	protected int gene_furate; /** nr de gene luate din alt cromozom */


	/**
	 * Clasa care implementeaza gena. 
	 *
	 * O gena reprezinta o mutare (pozitia in care se muta) 
	 * si valorea (calitatea) acelei mutari.
	 */
	protected class Gena implements Comparable {
		int linie, coloana;
		int valoare;

		
		public Gena() {
			super();
		}

		/**
		 * Constructor de copiere.
		 */
		public Gena(Gena g) {
			super();
			linie = g.linie;
			coloana = g.coloana;
		}

		
		/**
		 * @see Comparable
		 */
		public int compareTo(Object alta) {
			if (valoare > ((Gena) alta).valoare)
				return 1;
			if (valoare < ((Gena) alta).valoare)
				return -1;
			return 0;
		}
		
		/**
		 * @see Comparable
		 */ 
		public boolean equals(Object alta) {
			return (linie == ((Gena) alta).linie) &&
			    (coloana == ((Gena) alta).coloana);
		} 
		
		public String toString() {
			return "(" + linie + ", " + coloana + ", " +
			    valoare + ")";
		}


		/**
		 * Genereaza aleator valorile pentru linie si coloana
		 * astfel incat gena (mutarea) sa nu intre in conflict
		 * cu situatia de pe tabla.
		 */
		public void genereaza_aleator() {
			// System.err.
			//   println("Info: Gena.genereaza_aleator()");

			// reinitializam "samanta" pentru entropie mai mare
			// randGenerator.setSeed(System.currentTimeMillis());
			do {
				linie = randGenerator.nextInt(linii);
				coloana = randGenerator.nextInt(coloane);
			} while (tabla[linie][coloana] != GOL);
		}


		/**
		 * Intoarce pozitia reprezentata de gena.
		 *
		 * @return pozitia reprezentata de gena.
		 */
		public Pozitie getPozitie() {
			return new Pozitie(linie, coloana);
		}


		/**
		 * Initializeaza proprietatea valoare corespunzator.
		 */
		public void eval() {
			// System.err.println("Info: Gena.eval() " + this);
			valoare = poz_eval(linie, coloana);
		}
	}			// sfarsitul clasei Gena



	/**
	 * Clasa care implementeaza cromozomul.
	 */
	protected class Cromozom implements Comparable {
		ArrayList gene;
		int valoare;

		
		/**
		 * Constructor pentru generarea unui cromozom 
		 * cu <code>nr_gene</code> gene.
		 * 
		 * @param	nr_gene		nr de gene din cromozom
		 */ 
		public Cromozom(int nr_gene) {
			gene = new ArrayList(nr_gene);
			valoare = 0;
		}

		/**
		 * Constructor pentru generarea unui cromozom 
		 * cu <code>Ng</code> gene.
		 */
		public Cromozom() {
			this(Ng);
		}

		/**
		 * Constructor pentru obtinerea unui cromozom din 
		 * incrucisarea celor doi cromozomi 
		 * <code>crz1</code> si <code>crz2</code>
		 * (cromozomul rezultat este si reparat).
		 */
		public Cromozom(Cromozom crz1, Cromozom crz2) {
			this(Ng + gene_furate);

			int j;
			for (j = 0; j < Ng; j++)
				this.gene.
				    add(new Gena((Gena) crz1.gene.get(j)
					));
			for (j = 0; j < gene_furate; j++)
				this.gene.
				    add(new
					Gena((Gena) crz2.gene.
					     get(randGenerator.
						 nextInt(Ng))));
			this.repara();
		}


		/**
		 * @see Comparable
		 */
		public int compareTo(Object altul) {
			if (valoare > ((Cromozom) altul).valoare)
				return 1;
			if (valoare < ((Cromozom) altul).valoare)
				return -1;
			return 0;
		}
		
		
		/**
		 * Umple cu gene aleatoare cromozomul.
		 * <p>
		 * Ar putea fi optimizat pentru a genera gene de langa
		 * anumite pozitii (de ex: langa adversar, langa noi, 
		 * de un anumit punctaj) cu o probabilitate mai mare.
		 */
		public void genereaza_aleator() {
			Gena g;

			/* System.err.
			   println("Info: Cromozom.genereaza_aleator()"); */
			for (int i = 0; i < Ng; i++) {
				do {
					g = new Gena();
					g.genereaza_aleator();
				}
				while (gene.contains(g));
				gene.add(g);
			}
		}


		/**
		 * Repara cromozomul.
		 * <p>
		 * Un cromozom e prost daca genele se repeta (aceleasi mutari)
		 * sau daca se afla in pozitii deja ocupate.
		 */
		private void repara() {
			Gena g;

			// System.err.println("Info: Cromozom.repara()");
			// e pe o pozitie deja ocupata ?
			g = (Gena) gene.get(0);
			while (tabla[g.linie][g.coloana] != GOL)
				g.genereaza_aleator();

			int poz;
			for (int i = 1; i < Ng; i++) {
				g = (Gena) gene.get(i);
				while (((tabla[g.linie][g.coloana] != GOL)
					|| ((poz = gene.indexOf(g)) >= 0)
					&& (poz < i)))
					g.genereaza_aleator();
			}
		}


		/**
		 * Afla valorea unui cromozom. 
		 * <p>
		 * Are complexitatea O(Ng).
		 */
		public void eval() {
			valoare = 0;
			Iterator i = gene.iterator();
			while (i.hasNext())
				valoare += ((Gena) i.next()).valoare;
		}


		/**
		 * Face mutatii prin sortare si trunchiere.
		 * <p>
		 * Pune gena cea mai valoaroasa pe primul loc; reevaluaza
		 * celelalte gene, ca si cum ar fi facut mutarea 
		 * corespunzatoare primei gene si pune pe al doilea loc
		 * pe cea mai valoaroasa din ele; procesul continua pt
		 * Npasi pasi. 
		 * <p> 
		 * Apoi se truncheaza cromozomul la Ng gene, 
		 */
		public void sorteaza_si_trunchiaza() {
			int i, j, index_max;
			Gena g_max, g;
			Object[]gene_tmp;

			// sortam inceputul cromozomului
			int s = XX;	// simbolul curent 
			gene_tmp = gene.toArray();
			for (i = 0; i < Npasi; i++) {
				(g_max = (Gena) gene_tmp[i]).eval();
				index_max = i;
				for (j = gene.size() - 1; j > i; j--) {
					(g = (Gena) gene_tmp[j]).eval();
					if (g.valoare > g_max.valoare) {
						g_max = g;
						index_max = i;
					}
				}
				// punem gena la inceput
				gene_tmp[index_max] = gene_tmp[i];
				gene_tmp[i] = g_max;
				// executam mutarea ...
				tabla[g_max.linie][g_max.coloana] = s;
				s = (s == XX) ? OO : XX;
			}

			// sortam si restul de gene, insa mai sumar (mai prost)
			Arrays.sort(gene_tmp, Npasi, gene_tmp.length);

			// bagam in cromozom cel mai bune gene
			// si facem si undo la mutari 
			gene.clear();
			for (i = 0; i < Npasi; i++) {
				g = (Gena) gene_tmp[i];
				tabla[g.linie][g.coloana] = GOL;
				gene.add(g);
			}

			// restul de gene sunt bagate in ordine inversa 
			// pt c-au fost sortate crescator, 
			// iar noi vrem sa le bagam descrescator
			int l = gene_tmp.length - (Ng - Npasi);
			for (i = gene_tmp.length - 1; i >= l; i--)
				gene.add((Gena) gene_tmp[i]);
		}
	}			// sfarsitul clasei Cromozom


	/**
	 * Clasa care implementeaza populatia de cromozomi.
	 */
	protected class Populatie {
		private int Np;
		private Cromozom[] cromozomi;


		/**
		 *  Initializeaza populatia de <code>Np</code>cromozomi.
		 */
		public Populatie(int Np) {
			cromozomi = new Cromozom[Np];

			for (int i = 0; i < cromozomi.length; i++) {
				cromozomi[i] = new Cromozom();
				cromozomi[i].genereaza_aleator();
			}
		}

		
		/**
		 * Evalueaza toti cromozomii. 
		 */ 
		private void eval() {
			for (int i = 0; i < cromozomi.length; i++)
				cromozomi[i].eval();
		}


		/**
		 * Calculeaza diviziunile necesare selectiei. 
		 */
		private double[] calc_diviziuni() {
			double[] q = new double[cromozomi.length];
			int i;

			this.eval();
			// aflarea diviziunilor necesare pt selectie
			int S = 0;	// suma valorilor tuturor cromozomilor
			for (i = 0; i < cromozomi.length; i++) {
				S += cromozomi[i].valoare;
			}

			double S_aux = S;	// S converted to a double
			q[0] = cromozomi[0].valoare / S_aux;
			for (i = 1; i < cromozomi.length; i++) {
				q[i] =
				    q[i - 1] +
				    cromozomi[i].valoare / S_aux;
			}

			return q;
		}


		/**
		 * Selecteaza un cromozom pt incrucisare.
		 */
		private Cromozom alege_cromozom(double[]q) {
			double q_rand = randGenerator.nextDouble();

			for (int i = 1; i < cromozomi.length; i++)
				if ((q[i - 1] < q_rand)
				    && (q_rand <= q[i]))
					return cromozomi[i];
			// cazul cand q_rand < q[0]
			return cromozomi[0];
		}


		/**
		 * Incruciseaza cromozomii in felul urmator:
		 * <ul>
		 * 	<li>alege semi-aleator cei mai buni cromozomi</li>
		 * 	<li>din ei se aleg in mod aleator perechi, care
		 * 		sunt incrucisate mai apoi</li>
		 *	<li>cromozomii "copii" vor inlocui "parintii"
		 * </ul>
		 */
		public void incruciseaza() {
			int i;
			double[] q;

			q = calc_diviziuni();

			// ii selectam pe cei mai tari
			Cromozom[]cromozomi_tmp = new
			    Cromozom[cromozomi.length];
			for (i = 0; i < cromozomi.length; i++) {
				cromozomi_tmp[i] = alege_cromozom(q);
			}

			// incrucisarea propriu-zisa
			int nr_crmz = 0; // cromozomii selectati pt incrucisare
			int[] i_crmz = new int[2];
			/*
			 * daca cromozomul este ales si a fost selectata
			 * inainte si perechea sa, atunci ii incrucisam 
			 * si ii inlocuim cu copiii lor
			 */
			for (i = 0; i < cromozomi.length; i++) {
				if (randGenerator.nextFloat() < Pc) {
					i_crmz[nr_crmz] = i;
					nr_crmz++;
					if (nr_crmz == 2) {
						cromozomi[i_crmz[0]] = new
						    Cromozom(cromozomi_tmp
							     [i_crmz[0]],
							     cromozomi_tmp
							     [i_crmz[1]]);
						cromozomi[i_crmz[1]] =
						    new
						    Cromozom(cromozomi_tmp
							     [i_crmz[0]],
							     cromozomi_tmp
							     [i_crmz[1]]);
						nr_crmz = 0;
					}
				} else
					cromozomi[i] = cromozomi_tmp[i];
			}
			if (nr_crmz == 1) {	// fara pereche
				cromozomi[i_crmz[0]] =
				    cromozomi_tmp[i_crmz[0]];
			}
		}


		/**
		 * Efectueaza niste mutatii asupra populatiei de cromozomi. 
		 * <p>
		 * Aceste mutatii constau in sortarea genelor cromozomilor
		 * si trunchierea lor.
		 */
		public void fa_mutatii() {
			// System.err.println("fa_mutatii()");
			for (int i = 0; i < cromozomi.length; i++) {
				cromozomi[i].sorteaza_si_trunchiaza();
			}
		}


		/**
		 * Afla cromozomul cel mai tare.
		 */
		public Cromozom getMax() {
			/* 
			 * evaluam cromozomii si apoi aflam cromozomul
			 * cel mai tare 				
			 */
			this.eval();			
			Cromozom crz_max = cromozomi[0];
			for (int i = 1; i < cromozomi.length; i++)
				if (cromozomi[i].valoare >
				    crz_max.valoare)
					crz_max = cromozomi[i];

			return crz_max;
		}
	}			// sfarsitul clasei Populatie



	public AlgoritmGenetic(int linii, int coloane) {
		super(linii, coloane);

		factor_Np = default_factor_Np;
		factor_Ng = default_factor_Ng;
		factor_Ge = default_factor_Ge;
		Pc = default_Pc;
		Tx = default_Tx;

		Np = Math.max(6,
			      (int) Math.round(linii * coloane *
					       factor_Np));
		Ng = Math.max(5,
			      (int) Math.round(linii * coloane *
					       factor_Ng));
		Ge = Math.max(13, (int) Math.round(linii * coloane *
						   factor_Ge));
		gene_furate = Math.round(Tx * Ng);
		Npasi = Math.min(5, Ng);

		/* try {
		   randGenerator = SecureRandom.getInstance("SHA1PRNG");
		} 
		catch (NoSuchAlgorithmException e) {
		   System.err.println("EROARE LA INITIALIZARE SecRND");
		   System.exit(1);
		} */
		randGenerator = new Random();
	}



	/**
	 * Afla pozitia pe care trebuie sa mute calculatorul.
	 * 
	 * Daca e prima mutare, mutam la mijloc (neinteresant).
	 *
	 * Daca a mutat deja omul: vedem mai intai cate celule libere mai sunt
	 * pentru a mai "regla" (schimba) din parametrii in mod corespunzator.
	 * Dupa aceste ajustari, putem incepe algoritmul genetic efectiv.
	 * 
	 * Pe scurt acest algoritm consta din urmatorii pasi: 
	 * <ul>
	 * 	<li>initializarea populatiei de cromozomi</li>
	 * 	<li>incrucisarea perechilor</li>
	 *	<li>efectuarea unor mutatii asupra cromozomilor rezultati</li>
	 * 	<li>repetarea ultimilor 2 pasi timp de mai multe generatii</li>
	 *	<li>luarea unei decizii in functie de cel mai bun cromozom</li>
	 * </ul>
	 *
	 * @return pozitia pe care muta algoritmul genetic.
	 */
	protected Pozitie _joaca(Pozitie mutare) {
		// e prima mutare ?
		if (mutare == null) {
			// incepem de la mijloc 
			return new Pozitie(linii / 2, coloane / 2);
		}
		
		// s-a facut prima mutare
		
		/* 
		 * trebuie umblat la Npasi & alti parametrii
		 * in functie de cate mutari posibile au mai ramas
		 */
		int celule_libere = celule_libere();
		if (celule_libere == 1) {	// exista doar o posibilitate
			// e ineficient din pacate, but time is money
			Gena g = new Gena();
			g.genereaza_aleator();
			return g.getPozitie();
		} else if ((celule_libere < (Ng * (1 + Tx))) &&
			   (celule_libere > 1)) {
			System.err.
			    println("refac parametrii genetici pentru " +
				    celule_libere + " celule libere!");
			Ng = (int) Math.floor(celule_libere / (1 + Tx));
			gene_furate = (int) Math.floor(Tx * Ng);
			Ge = (int) Math.round(celule_libere * factor_Ge);
			Npasi = Ng;
		}
		System.err.println("Info: Ng = " + Ng + ", Np = " + Np +
				   ", Ge = " + Ge + "; Npasi = " + Npasi);
		
		// reinitializam "samanta" pentru o entropie mai mare
		randGenerator.setSeed(System.currentTimeMillis());
		Populatie populatie = new Populatie(Ng);
		long start, end;
		start = System.currentTimeMillis();
		for (int i = 0; i < Ge; i++) {
			// System.err.println("Info: generatia " + i);
			populatie.incruciseaza();
			populatie.fa_mutatii();
		}
		end = System.currentTimeMillis();
		System.err.println("\t = > " + (end - start) +
				   " milisecunde");

		return ((Gena) populatie.getMax().gene.get(0)).getPozitie();
	}
}
