package pkgxsi0;

import java.util.*;

/**
 * Aceasta clasa implementeaza un algoritm care joaca la plezneala (aleator).
 * 
 * @author Cristian Ciupitu 342 C2
 */
public class AlgoritmRandom extends AlgoritmJoc {
	
	public AlgoritmRandom(int linii, int coloane) {
		super(linii, coloane);
	} 

	
	protected Pozitie _joaca(Pozitie mutare) {
		Random generator = new Random();
		int l, c;
		
		do {
			l = generator.nextInt(linii);
			c = generator.nextInt(coloane);
		} while (tabla[l][c] != GOL);
		
		return new Pozitie(l, c);
	}
}
