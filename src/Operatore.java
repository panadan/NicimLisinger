
public class Operatore {
	String matricola;
	String descrizione;
	
	public Operatore(String matricola, String descrizione) {
		super();
		this.matricola = matricola;
		this.descrizione = descrizione;
	}

	public String getMatricola() {
		return matricola;
	}

	@Override
	public String toString() {
		return matricola + " - " + descrizione;
	}
	
	
}
