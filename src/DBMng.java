import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;


public class DBMng {
	
	private Connection connection;
	private TableModelListaTagli tmListaTagli;
	private Operatore defOp;
	private static File file_txt;
	private String file_name;
	private Logger logger;
	
	private static String sqlBarcode = "SELECT BF.NUMERO_BOLLA ,"
			+ "	BF.CODICE_PARTE, BF.DESCRIZIONE,  BF.CODICE_FASE, " 
			+ " BF.DESCRIZIONE_OPERAZIONE,"
			+ "	M.CODICE_PARTE, M.DESCRIZIONE,"
			+ "	F.QUANTITA_FASE, OFC.QUANTITA_BUONA,"
			+ "	OFC.STATO , P.CUST_VAL03" +
			" FROM ORDINI_FASI F, ORDINI_FASI_CONS OFC,"
			+ "	MOVIMENTI M ,BOLLE_FASI BF, PARTI P"
			+ "	WHERE OFC.CODICE_ORDINE(+) = F.CODICE_ORDINE"
			+ "	AND OFC.NUMERO_FASE(+)     = F.NUMERO_FASE"
			+ "	AND M.CODICE_ORDINE(+)     = F.CODICE_ORDINE"
			+ "	AND M.CODICE_FASE(+)       = F.CODICE_FASE"
			+ "	AND BF.CODICE_ORDINE       = F.CODICE_ORDINE"
			+ "	AND BF.CODICE_FASE         = F.CODICE_FASE" +
			" AND P.CODICE_PARTE = BF.CODICE_PARTE";
	
	private static String sqlOperatore = "SELECT OP.MATRICOLA_OPERATORE, OP.COGNOME_NOME FROM OPERATORI OP ORDER BY 2";
	
	private static String sqlMateriale = "SELECT * FROM PARTI P";

	/*
	 * SELECT O.CODICE_PARTE, O.DESCRIZIONE, M.CODICE_PARTE, M.DESCRIZIONE,F.QUANTITA_FASE ,  OFC.QUANTITA_BUONA
FROM ORDINI_FASI F,    ODL O,    ORDINI_FASI_CONS OFC    ,    MOVIMENTI M    
WHERE O.CODICE_ORDINE       = F.CODICE_ORDINE  AND OFC.CODICE_ORDINE(+)    = F.CODICE_ORDINE
AND OFC.NUMERO_FASE(+)       = F.NUMERO_FASE   AND M.CODICE_ORDINE(+) = F.CODICE_ORDINE  AND M.CODICE_FASE(+) = F.CODICE_FASE
	*/
 	public static void main(String[] args)
	 {
	   //new DBMng().doTest();
	 }
	
	public void connect() throws ClassNotFoundException, SQLException{
		 Class.forName("oracle.jdbc.driver.OracleDriver");
        connection = DriverManager.getConnection("jdbc:oracle:thin:@win2008-sql:1521:NIC","nicim","nicim");
   }

	public void disconnect() throws SQLException {
		connection.close();
	}
	
	private void test() throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		
		stmt = connection.createStatement();


		rs = stmt.executeQuery(sqlBarcode);
        int i=0;
		while(rs.next() || i == 100) {
            System.out.print(rs.getString(1) + "\t");
            System.out.print(rs.getString(3) + "\t");
            System.out.println(rs.getString(5));
            i++;
        }
        rs.close();
        stmt.close();
	}
	
	private void doTest() {
		try {
			connect();
			test();
			disconnect();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

	}

	public DefaultComboBoxModel<Operatore> getCbModelOperatore() throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		stmt = connection.createStatement();
		rs = stmt.executeQuery(sqlOperatore);
		final DefaultComboBoxModel<Operatore> cbmOperatore = new DefaultComboBoxModel<Operatore>();
		while(rs.next()) {
			Operatore op = new Operatore(rs.getString(1),rs.getString(2));
			if(rs.getString(1).equals("OP048"))
				defOp = op;
            System.out.print(rs.getString(1) + "\t");
            System.out.println(rs.getString(2));
            cbmOperatore.addElement(op);
        }
        rs.close();
        stmt.close();
        
		return cbmOperatore;
		
		
	}

	public DBMng() {
		super();
		readIni();
		setLogger();
		
		
	}

	private void readIni() {
		Properties ini;
		ini = new Properties();
		//dafault settings
		//server="win2008-sql";
		//db="NUOVAOMEC";
		file_name = "c:/NicimLisinger.txt";
		
		try {
			ini.load(new FileInputStream("NicimLisinger.ini"));
			file_name = ini.getProperty("OutputFile");
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Errore, file ini non trovato", JOptionPane.INFORMATION_MESSAGE);
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Errore in lettura file ini", JOptionPane.INFORMATION_MESSAGE);
			e.printStackTrace();
		}		
	}
	
	private void setLogger() {
		logger = Logger.getLogger("Log");  
        FileHandler fh;  
          
        try {  
              
            // This block configure the logger with handler and formatter  
        	DateFormat dateFormat = new SimpleDateFormat("ddMMyy_HHmmss");
        	Date date = new Date();
        	
            fh = new FileHandler("./log/NicimLisinger_"+ dateFormat.format(date)  +".log",true);  
            logger.addHandler(fh);  
            //logger.setLevel(Level.ALL);  
            LogFormatter formatter = new LogFormatter();  
            fh.setFormatter(formatter);  
              
        } catch (SecurityException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
          

	}
	
	public Operatore getDefOp() {
		return defOp;
	}

	public TableModelListaTagli getTbModel() {
		tmListaTagli = new TableModelListaTagli(initListaTagli(), this);
		
		return tmListaTagli;
	}
	
	private ArrayList<Taglio> initListaTagli() {
		
		ArrayList<Taglio> listaTagli = new ArrayList<Taglio>();
		for (int i=0 ; i < 8; i++){
			listaTagli.add(new Taglio(i+1));
		}
		
		return listaTagli;
			
	}

	public Taglio getBarcodeData(String barcode, int row) throws SQLException, barcodeExc {
		String sql = sqlBarcode + " AND BF.NUMERO_BOLLA='" + barcode + "'";

		Statement stmt = null;
		ResultSet rs = null;
		
		stmt = connection.createStatement();

		rs = stmt.executeQuery(sql);
		
		//1 BF.NUMERO_BOLLA ,"
		//2	BF.CODICE_PARTE, 
		//3 BF.DESCRIZIONE,  
		//4 BF.CODICE_FASE, " 
		//5 " BF.DESCRIZIONE_OPERAZIONE,"
		//6 "	M.CODICE_PARTE, 
		//7 M.DESCRIZIONE,"
		//8 "	F.QUANTITA_FASE, 
		//9 OFC.QUANTITA_BUONA,"
		//10	OFC.STATO 
		//11 P.CUST_VAL03
		
		if(rs.next()){
			if(rs.getString(10) != null && rs.getString(10).equals("F"))
				throw new barcodeExc("Fase già Finita");

			
			if(rs.getInt(4) != 11)
				throw new barcodeExc("Il barcode non si rifarisce a una fase di Taglio Tubo");

			String bolla = rs.getString(1);
			String codArt=rs.getString(2);
			String descArt= rs.getString(3);
			String codMat =rs.getString(6);
			String descMat =rs.getString(7);
			int qtaOdl =rs.getInt(8);
			int qtaLav =rs.getInt(9);
			int qta = qtaOdl - qtaLav; 
			float misura = rs.getFloat(11);
			return new Taglio(row+1,bolla,codArt,descArt,codMat,descMat,qtaOdl,qtaLav,qta,misura,false);
		}
		else{
			throw new barcodeExc("Barcode Non Valido");
		}
	}

	public boolean controllaLista(List<Taglio> list) throws barcodeExc {
		String prevMateriale = null;
		String materiale = null;
		boolean flagMateriale = false;
		for (int i=0; i<8; i++){
			Taglio t = list.get(i);
			if(t.getBarcode() == null) continue;
			materiale = t.getCodMat();

			if(materiale != null){
				if (prevMateriale !=null && !materiale.equals(prevMateriale)){
					flagMateriale = true;
				}
				prevMateriale = materiale;
			}

			if(t.getQta() == null || t.getQta() == 0)
				throw new barcodeExc("Riga "+ (i+1) +": campo 'Quantità' obbligatorio");
			if(t.getMisura() == null || t.getMisura() == 0)
				throw new barcodeExc("Riga "+ (i+1) +": campo 'Misura' obbligatorio");
		}
		
		if(flagMateriale){
			System.out.println("materiale incoerente");
			return false;
		}
		
		 return true;
		
		
		
		
	}

	public void generaLista(List<Taglio> list, Operatore operatore, String codMateriale, String lungMateriale) throws IOException {
		//char[] row = new char[71];
		char[] row = new char[108];
		//Character c = ' ';

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS");
    	Date date = new Date();
		String log = extracted(dateFormat, date)+ " - ";
		log+= "Inizia Generazione Lista";
		logger.info(log);

		//aggiungo i due decimali
		lungMateriale+="00";

		file_txt = new File("file_name.tmp");
		
		for (int i=0; i<8; i++){
			Arrays.fill(row, ' ');
			Taglio t = list.get(i);
			if(t.getBarcode() == null) continue;
						
			System.arraycopy(operatore.getMatricola().toCharArray(), 0, row, 0, operatore.getMatricola().length());
			System.arraycopy(t.getBarcode().toCharArray(), 0, row, 15, t.getBarcode().length());
			
			String qta = t.getQta().toString() + "00";
			qta = qta.replace(",","").replace(".","");
			System.arraycopy(qta.toCharArray(), 0, row, 50-qta.length(), qta.length());
			
			
			String misura =String.format("%.2f", t.getMisura()); 
			misura = misura.replace(",","").replace(".","");
			System.arraycopy(misura.toCharArray(), 0, row, 70-misura.length(), misura.length());
			
			
			if(!t.getBarcode().equals("LIBERO") && t.getParziale()) row[70]='S';
			else row[70]='F';
			
			System.arraycopy(codMateriale.toCharArray(), 0, row, 72, codMateriale.length());
			
			System.arraycopy(lungMateriale.toCharArray(), 0, row, 107-lungMateriale.length(), lungMateriale.length());

			
			System.out.println(row);
			writeToFile(new String(row));
			logger.info(new String(row));
		}
		logger.info("Fine generazione");

		file_txt.renameTo(new File(file_name));
		logger.info("File di scambio: " + file_name + " disponibile");

		date = new Date();
		log = extracted(dateFormat, date)+ " - ";
		log+= "Fine Processo";
		logger.info(log);
		logger.info(" ----------------- ");
		logger.info(" ");
		
	}
	
	private String extracted(DateFormat dateFormat, Date date) {
		String log = dateFormat.format(date)+ " - ";
		return log;
	}
	
	public boolean fileExist(){
		File f = new File(file_name);
		return (f.exists() && !f.isDirectory());
	}
	
    public static void writeToFile(String text) throws IOException {
    	
        BufferedWriter bw = new BufferedWriter(new FileWriter(file_txt, true));
        bw.write(text);
        bw.newLine();
        bw.close();
}

	public void deleteExistingFile() {
		if(fileExist()){
			new File(file_name).delete();
		}
		
	}

	public String[] getMatDesc(String codMat) throws SQLException, barcodeExc {
		String sql = sqlMateriale + " WHERE P.CODICE_PARTE = '" + codMat + "'";

		Statement stmt = null;
		ResultSet rs = null;
		
		stmt = connection.createStatement();

		rs = stmt.executeQuery(sql);
		
		//1 BF.NUMERO_BOLLA ,"
		//2	BF.CODICE_PARTE, 
		//3 BF.DESCRIZIONE,  
		//4 BF.CODICE_FASE, " 
		//5 " BF.DESCRIZIONE_OPERAZIONE,"
		//6 "	M.CODICE_PARTE, 
		//7 M.DESCRIZIONE,"
		//8 "	F.QUANTITA_FASE, 
		//9 OFC.QUANTITA_BUONA,"
		//10	OFC.STATO 
		//11 P.CUST_VAL03
		
		if(rs.next()){
			String descMat = rs.getString(5);
			String lugnhMat = rs.getString("CUST_VAL05");
			String[] param={descMat,lugnhMat};
			return param;
		}
		else{
			throw new barcodeExc("Codice Materiale Non Trovato");
		}
	}
	
}
