
public class Taglio {
	Integer ind;
	String barcode;
	String codArt;
	String desArt;
	String codMat;
	String desMat;
	Integer qtaODL;
	Integer qtaLav;
	Integer qta;
	Float misura;
	Boolean parziale;

	public Taglio(Integer ind) {
		super();
		this.ind = ind;
	}
	
	public Taglio(Integer ind, String arcode, String codArt, String desArt,
			String codMat, String desMat, Integer qtaODL, Integer qtaLav,
			Integer qta, Float misura, Boolean parziale) {
		super();
		this.ind = ind;
		this.barcode = arcode;
		this.codArt = codArt;
		this.desArt = desArt;
		this.codMat = codMat;
		this.desMat = desMat;
		this.qtaODL = qtaODL;
		this.qtaLav = qtaLav;
		this.qta = qta;
		this.misura = misura;
		this.parziale = parziale;
	}

	public Taglio(Taglio taglio) {
		this.ind = taglio.getInd();
		this.barcode = taglio.getBarcode();
		this.codArt = taglio.getCodArt();
		this.desArt = taglio.getDesArt();
		this.codMat = taglio.getCodMat();
		this.desMat = taglio.getDesMat();
		this.qtaODL = taglio.getQtaODL();
		this.qtaLav = taglio.getQtaLav();
		this.qta = taglio.getQta();
		this.misura = taglio.getMisura();
		this.parziale = taglio.getParziale();
	}

	public Integer getInd() {
		return ind;
	}

	public String getBarcode() {
		return barcode;
	}

	public String getCodArt() {
		return codArt;
	}

	public String getDesArt() {
		return desArt;
	}

	public String getCodMat() {
		return codMat;
	}

	public String getDesMat() {
		return desMat;
	}

	public Integer getQtaODL() {
		return qtaODL;
	}

	public Integer getQtaLav() {
		return qtaLav;
	}

	public Integer getQta() {
		return qta;
	}

	public Float getMisura() {
		return misura;
	}

	public Boolean getParziale() {
		return parziale;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public void setQta(Integer qta) {
		this.qta = qta;
	}

	public void setMisura(Float misura) {
		this.misura = misura;
	}

	public void setParziale(Boolean parziale) {
		this.parziale = parziale;
		
	}


	

	
	
}
