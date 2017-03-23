import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;


public class TableModelListaTagli extends AbstractTableModel {

	private List<Taglio> dati = new ArrayList<Taglio>();
	
	private String[] columnNames = {
			"Ind",
            "Barcode",
            "Cod. Articolo",
            "Descr. Articolo",
            "Cod. Materiale",
            "Descr. Materiale",
            "QtaODL",
            "QtaLav",
            "Qta",
            "Misura",
            "Parziale"
	};
	
	private DBMng dbMng;
	
	private boolean bcValidate = false;
	
	public List<Taglio> getDati() {
		return dati;
	}

	private Taglio exTaglio;

	private String materiale;
	
	public Taglio getExTaglio() {
		return exTaglio;
	}

	public TableModelListaTagli(List<Taglio> dati, DBMng dbMng) {
		super();
		this.dati = dati;
		this.dbMng = dbMng;
	}

	@Override
	public int getRowCount() {
		return dati.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
    	switch(columnIndex){
        case 0:
        	//return dati.get(rowIndex).getInd();
        	return rowIndex+1;
        case 1:
        	return dati.get(rowIndex).getBarcode();
        case 2:
        	return dati.get(rowIndex).getCodArt();
        case 3:
        	return dati.get(rowIndex).getDesArt();
        case 4:
        	return dati.get(rowIndex).getCodMat();
        case 5:
        	return dati.get(rowIndex).getDesMat();
        case 6:
        	return dati.get(rowIndex).getQtaODL();
        case 7:
        	return dati.get(rowIndex).getQtaLav();
        case 8:
        	return dati.get(rowIndex).getQta();
        case 9:
        	return dati.get(rowIndex).getMisura();
        case 10:
        	return dati.get(rowIndex).getParziale();

        default :
        
        }
    	return "";
	}
	
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Class getColumnClass(int col) {
    	switch(col){
    	case 0:
    	case 6:
    	case 7:
    	case 8:
    		return new Integer(0).getClass();
    	case 1:
    	case 2:
    	case 3:
    	case 4:
    	case 5:
    		return new String("").getClass();
    	case 9:
    		return new Float(0).getClass();
    	case 10:
    		return new Boolean(false).getClass();
    	}
    	
    	
		return null;
    	
//        return new String("").getClass();
    	//return getValueAt(0, c).getClass();
    
        
    }

  
    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
    	switch(col){
        case 0:
        	return false; // dati.get(rowIndex).getInd();
        case 1:
        	return true; //dati.get(row).getBarcode();
        case 2:
        	return false; //dati.get(rowIndex).getCodArt();
        case 3:
        	return false; //dati.get(rowIndex).getDesArt();
        case 4:
        	return false; //dati.get(rowIndex).getCodMat();
        case 5:
        	return false; //dati.get(rowIndex).getDesMat();
        case 6:
        	return false; //dati.get(rowIndex).getQtaODL();
        case 7:
        	return false; //dati.get(rowIndex).getQtaLav();
        case 8:
        	if(getValueAt(row,1)==null)return false;
        	return true; //dati.get(row).getQta();
        case 9:
        	if(getValueAt(row,1)==null)return false;
        	return true; //dati.get(row).getMisura();
        case 10:
        	if(getValueAt(row,1)==null)return false;
        	return true; //parziale

        default :
    	
    	}
		return false;
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
    	
    	//prima salvo la riga
    	exTaglio = new Taglio(dati.get(row));
    	
    	switch(col){
        /*case 0:
        	return;// dati.get(rowIndex).getInd();
        case 1:
        	dati.get(row).setBarcode((String)value);
        case 2:
        	return; //dati.get(rowIndex).getCodArt();
        case 3:
        	return; //dati.get(rowIndex).getDesArt();
        case 4:
        	return; //dati.get(rowIndex).getCodMat();
        case 5:
        	return; //dati.get(rowIndex).getDesMat();
        case 6:
        	return; //dati.get(rowIndex).getQtaODL();
        case 7:
        	return; //dati.get(rowIndex).getQtaLav();
        case 8:
        	dati.get(row).getQta();
        case 9:
        	dati.get(row).getMisura();
        case 10:
        	dati.get(row).getTipo();*/

        case 1:
        	try {
        		bcValidate = false;
        		String corValue = correctValue((String)value, dati.get(row).getBarcode());
        		bcValidate = setBarcodeAt(corValue, row, col);
				
			} catch (SQLException | barcodeExc e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, e.getMessage());
				e.printStackTrace();
			}
        	
        	fireTableRowsUpdated(row, row);
        	//dati.get(row).setBarcode((String)value);
        	break;
        case 8:
        	//if(value != null && !value.toString().isEmpty())
        	dati.get(row).setQta((Integer)value);
        	fireTableCellUpdated(row, col);
        	break;
        case 9:
        	dati.get(row).setMisura((Float)value);
        	fireTableCellUpdated(row, col);
        	break;
        case 10:
        	dati.get(row).setParziale((Boolean)value);
        	fireTableCellUpdated(row, col);
        	break;
        default :
        	
    	}

        
    }

    
    /**
     * Questo metodo fa in modo che se si spara un barcode su un esistente l'esitende venga cancellato evitando un errore certo
     * @param newValue è il valore immesso nella cella che potrebbe essere la somma del vecchio+il nuovo barcode
     * @param currentValue è il valore presente prima dell'immisione
     * @return
     */
    private String correctValue(String newValue, String currentValue) {
		String value;
    	if(currentValue != null && //se esiste un valore
    			newValue.length() > currentValue.length() && //se il nuovo valore è più lungo dell'attuale  
    			currentValue.equals(newValue.substring(0, currentValue.length()))) //se il nuovo valore inizia con il valore attuale
    		value = newValue.substring(currentValue.length()); //allora cancello la prima parte per tenere solo il nuovo barcode
    	else value = newValue;
    	
		return value;
	}

	public boolean isBcValidate() {
		return bcValidate;
	}

	private boolean setBarcodeAt(String barcode, int row, int col) throws SQLException, barcodeExc {
		if(barcode.equals("LIBERO")){
			//dati.get(row).setBarcode(barcode);
			Taglio taglio = new Taglio(0);
			taglio.setBarcode(barcode);
			dati.set(row,taglio);
			return true;
		}
		if (barcodeEsistente(barcode,row)) return false;
		Taglio t = dbMng.getBarcodeData(barcode, row);
		dati.set(row, t);
		if (t.getCodMat() != null && !t.getCodMat().isEmpty())
			materiale = t.getCodMat();
		return true;
		
	}

	private boolean barcodeEsistente(String barcode, int row) {
		for (int i = 0; i< dati.size(); i++){
			
			if (i != row && dati.get(i).getBarcode() != null &&  dati.get(i).getBarcode().equals(barcode)){
				JOptionPane.showMessageDialog(null, "Il Barcode "+ barcode +" è già in lista");
				return true;  
			}
		
		}
		return false;
	}

	public void spostaSu(int row){
    	if(row == 0) return;
    	Taglio taglio = new Taglio(dati.get(row));
    	dati.set(row, dati.get(row-1));
    	dati.set(row-1, taglio);
    	
    	fireTableDataChanged();
    }

    public void spostaGiu(int row){
    	if(row == 7) return;
    	Taglio taglio = new Taglio(dati.get(row));
    	dati.set(row, dati.get(row+1));
    	dati.set(row+1, taglio);
    	
    	
    	fireTableDataChanged();
    }

	public void setTaglioAt(int row, Taglio taglio) {
		dati.set(row, taglio);
		fireTableRowsUpdated(row, row);
	}

	public void remove(int row) {
		dati.remove(row);
		dati.add(7, new Taglio(0));
		fireTableDataChanged();
	}

	public void clearLista() {
		for (int i = 0; i<8 ; i++){
			dati.set(i, new Taglio(0));
			
		}
		fireTableDataChanged();
	}

	public boolean isEmpty() {
		return dati.get(0).getBarcode().isEmpty();
	}


    
}
