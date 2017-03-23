import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JScrollPane;
import java.awt.GridBagConstraints;
import javax.swing.JTable;
import java.awt.Insets;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JFormattedTextField;
import javax.swing.JEditorPane;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ListSelectionModel;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import javax.swing.JTextArea;
import java.awt.FlowLayout;


public class NicimLisinger {

	private JFrame frmNicim;
	private JTable table;
	private TableModelListaTagli tmListaTagli;
	private JComboBox<Operatore> cbOperatore;
	private DBMng dbMng;
	private JFormattedTextField ftfLungMat;
	private JTextField tfCodMat;
	private JTextField tfDescMat;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
			        UIManager.setLookAndFeel(
        UIManager.getSystemLookAndFeelClassName());
					NicimLisinger window = new NicimLisinger();

					
					window.frmNicim.setVisible(true);
					window.setInitFocus();
					
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		});
	}

	protected void setInitFocus() {
		cbOperatore.setSelectedItem(dbMng.getDefOp());
		table.changeSelection(0, 1, false, false);
		table.requestFocus();	
		
	}

	/**
	 * Create the application. x
	 */
	public NicimLisinger() {
		initialize();
		
		try {
			myInit();
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Errore Inizializzazione", "", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Impossibile connettersi al Database", "", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	private void myInit() throws ClassNotFoundException, SQLException {
		dbMng = new DBMng();
		dbMng.connect();
		cbOperatore.setModel(dbMng.getCbModelOperatore());
		tmListaTagli = dbMng.getTbModel();
		table.setModel(tmListaTagli);
		setTableListener();
		setTableColumnSize();

		
	}

	private void setTableColumnSize() {
		TableColumn column = null;
	    for (int i = 0; i < table.getModel().getColumnCount(); i++) {
	        column = table.getColumnModel().getColumn(i);
	        if (i == 0) {
	            column.setPreferredWidth(35); //sport column is bigger
	        } else if (i == 1) {
	            column.setPreferredWidth(90);
	        } else if (i == 2 || i == 4) {
	            column.setPreferredWidth(120);
	        } else if (i == 3 || i == 5) {
	            column.setPreferredWidth(200);
	        } else if (i == 6 || i == 7 || i == 8 || i == 9) {
	            column.setPreferredWidth(80);
	        }
	        
	    }    		
	}

	private void setTableListener() {
//		table.addMouseListener(new java.awt.event.MouseAdapter() {
//		    @Override
//		    public void mouseClicked(java.awt.event.MouseEvent evt) {
//		    		System.out.println("mclk" + evt.getClickCount());
//		    	
//			        int row = table.rowAtPoint(evt.getPoint());
//			        int col = table.columnAtPoint(evt.getPoint());
//			    	if (evt.getClickCount() == 2) {
//				        if (row >= 0 && col == 1) {
//				            table.setValueAt("LIBERO", row, col);
//				           // table.getSelectionModel().setSelectionInterval(row, row);
//				        }
//		    	}
//		    }
//		});

		table.getModel().addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
		        int row = e.getFirstRow();
		        int col = e.getColumn();
		        //TableModelListaTagli model = (TableModelListaTagli)e.getSource();
		        int type = e.getType();


		       switch (type) {
	            case TableModelEvent.UPDATE:
	                final int currentRow = table.convertRowIndexToView(row);
	                final int currentColumn = table.convertColumnIndexToView(col);
	                if (col==1 && !tmListaTagli.isBcValidate()) {
	                    // if the test fails, I want to move back to the edited cell
	                    SwingUtilities.invokeLater(new Runnable() {
	                        public void run() {
	                            table.changeSelection(currentRow, currentColumn, false, false);

	                        }
	                    });
	                }
	                
			        else{
	                	if(tfCodMat.getText().isEmpty()){
		                	//verifica matriale	
	                		int modelRow = table.convertRowIndexToModel(currentRow);
	                		String codMat = (String) tmListaTagli.getValueAt(modelRow, 4);
	                		if(codMat !=null && !codMat.isEmpty()){
	                			tfCodMat.setText(codMat);
	                			checkInputMateriale();
	                		}
	                		
	                	}
	                	
	                }		                
	                
	                break;

	            // the rest of switch

		       	} // switch
			       
				



		        
			}
		});

	    CellEditorListener ChangeNotification = new CellEditorListener() {
	        public void editingCanceled(ChangeEvent e) {
	            System.out.println("The user canceled editing.");
	        }

	        public void editingStopped(ChangeEvent e) {
	            System.out.println("The user stopped editing successfully.");
		        final int row = table.getSelectedRow();

                if (!tmListaTagli.isBcValidate()) {
                    // if the test fails, I want to move back to the edited cell
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            table.changeSelection(row, 1, false, false);

                        }
                    });
                }
	        }
	    };
		
	    table.getDefaultEditor(String.class).addCellEditorListener(ChangeNotification);
	    
	    int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
	    InputMap inputMap = table.getInputMap(condition);
	    ActionMap actionMap = table.getActionMap();

	    // DELETE is a String constant that for me was defined as "Delete"
	    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),"DELETE");
	    actionMap.put("DELETE", new AbstractAction() {
	       public void actionPerformed(ActionEvent e) {
	    	   deleteRow();
	       }
	    });
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmNicim = new JFrame();
		frmNicim.setIconImage(Toolkit.getDefaultToolkit().getImage(NicimLisinger.class.getResource("/ico/monitor-go-icon.ico")));
		frmNicim.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				try {
					dbMng.disconnect();
				} catch (SQLException exc) {
					JOptionPane.showMessageDialog(null, "Errore durante la disconnessione al Database", "", JOptionPane.ERROR_MESSAGE);
					exc.printStackTrace();
				}
			}
		});
		frmNicim.setTitle("Nicim - - ->Lisinger");
		frmNicim.setBounds(100, 100, 795, 346);
		frmNicim.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		frmNicim.getContentPane().setLayout(gridBagLayout);
		
		JPanel pOperatore = new JPanel();
		GridBagConstraints gbc_pOperatore = new GridBagConstraints();
		gbc_pOperatore.insets = new Insets(0, 0, 5, 0);
		gbc_pOperatore.fill = GridBagConstraints.BOTH;
		gbc_pOperatore.gridx = 0;
		gbc_pOperatore.gridy = 0;
		frmNicim.getContentPane().add(pOperatore, gbc_pOperatore);
		
		JLabel lblOperatore = new JLabel("OPERATORE");
		pOperatore.add(lblOperatore);
		
		cbOperatore = new JComboBox();
		pOperatore.add(cbOperatore);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		frmNicim.getContentPane().add(scrollPane, gbc_scrollPane);
		
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		table.putClientProperty("terminateEditOnFocusLost", true);  
		
		scrollPane.setViewportView(table);
		
		JPanel pBarcode = new JPanel();
		GridBagConstraints gbc_pBarcode = new GridBagConstraints();
		gbc_pBarcode.insets = new Insets(0, 0, 5, 0);
		gbc_pBarcode.fill = GridBagConstraints.BOTH;
		gbc_pBarcode.gridx = 0;
		gbc_pBarcode.gridy = 2;
		frmNicim.getContentPane().add(pBarcode, gbc_pBarcode);
		
		JButton btnElimina = new JButton("Elimina");
		btnElimina.setIcon(new ImageIcon(NicimLisinger.class.getResource("/ico/draw-eraser-icon.ico")));
		btnElimina.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteRow();
			}

		});


		
		JButton btnInserisciLibero = new JButton("Inserisci Libero");
		btnInserisciLibero.setIcon(new ImageIcon(NicimLisinger.class.getResource("/ico/page-white-add-icon.ico")));
		btnInserisciLibero.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row =table.getSelectionModel().getMinSelectionIndex();
				if (row >= 0){
					table.setValueAt("LIBERO", row, 1);					
				}
			}
		});
		pBarcode.add(btnInserisciLibero);
		pBarcode.add(btnElimina);
		
		JButton btnGiu = new JButton("Sposta Gi\u00F9");
		btnGiu.setIcon(new ImageIcon(NicimLisinger.class.getResource("/ico/arrow-down-icon.ico")));
		btnGiu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				if(row != 7){
					tmListaTagli.spostaGiu(row);
					table.changeSelection(row+1, 1, false, false);
					//table.getSelectionModel().setSelectionInterval(row+1,row+1);
				}
			}
		});
		pBarcode.add(btnGiu);
		
		JButton btnSu = new JButton("Sposta Su");
		btnSu.setIcon(new ImageIcon(NicimLisinger.class.getResource("/ico/arrow-up-icon.ico")));
		btnSu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				if(row != 0){
					tmListaTagli.spostaSu(row);
					table.changeSelection(row-1, 1, false, false);
					//table.getSelectionModel().setSelectionInterval(row-1,row-1);
				}
			}
		});
		pBarcode.add(btnSu);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 3;
		frmNicim.getContentPane().add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{10, 56, 86, 86, 85, 100, 0};
		gbl_panel.rowHeights = new int[]{20, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblMateriale = new JLabel("MATERIALE");
		GridBagConstraints gbc_lblMateriale = new GridBagConstraints();
		gbc_lblMateriale.anchor = GridBagConstraints.WEST;
		gbc_lblMateriale.insets = new Insets(0, 0, 0, 5);
		gbc_lblMateriale.gridx = 1;
		gbc_lblMateriale.gridy = 0;
		panel.add(lblMateriale, gbc_lblMateriale);
		
		tfCodMat = new JTextField();
		tfCodMat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComponent component = (JComponent) e.getSource();
				component.transferFocus();
			}
		});
		tfCodMat.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				checkInputMateriale();
				
				
			}

		});
		GridBagConstraints gbc_tfCodMat = new GridBagConstraints();
		gbc_tfCodMat.anchor = GridBagConstraints.NORTHWEST;
		gbc_tfCodMat.insets = new Insets(0, 0, 0, 5);
		gbc_tfCodMat.gridx = 2;
		gbc_tfCodMat.gridy = 0;
		panel.add(tfCodMat, gbc_tfCodMat);
		tfCodMat.setColumns(10);
		
		tfDescMat = new JTextField();
		tfDescMat.setEditable(false);
		GridBagConstraints gbc_tfDescMat = new GridBagConstraints();
		gbc_tfDescMat.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfDescMat.anchor = GridBagConstraints.NORTH;
		gbc_tfDescMat.insets = new Insets(0, 0, 0, 5);
		gbc_tfDescMat.gridx = 3;
		gbc_tfDescMat.gridy = 0;
		panel.add(tfDescMat, gbc_tfDescMat);
		tfDescMat.setColumns(10);
		
		JLabel lblLunghezzaMaterialemm = new JLabel("LUNGHEZZA (mm)");
		GridBagConstraints gbc_lblLunghezzaMaterialemm = new GridBagConstraints();
		gbc_lblLunghezzaMaterialemm.anchor = GridBagConstraints.WEST;
		gbc_lblLunghezzaMaterialemm.insets = new Insets(0, 0, 0, 5);
		gbc_lblLunghezzaMaterialemm.gridx = 4;
		gbc_lblLunghezzaMaterialemm.gridy = 0;
		panel.add(lblLunghezzaMaterialemm, gbc_lblLunghezzaMaterialemm);

		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
		DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
		decimalFormat.setGroupingUsed(false);

		ftfLungMat = new JFormattedTextField(decimalFormat);
		ftfLungMat.setColumns(10); //whatever size you wish to set
		
		ftfLungMat.setEditable(false);
		GridBagConstraints gbc_ftfLunghezzaMateriale = new GridBagConstraints();
		gbc_ftfLunghezzaMateriale.anchor = GridBagConstraints.NORTHWEST;
		gbc_ftfLunghezzaMateriale.gridx = 5;
		gbc_ftfLunghezzaMateriale.gridy = 0;
		panel.add(ftfLungMat, gbc_ftfLunghezzaMateriale);
		
		
		JPanel pLista = new JPanel();
		GridBagConstraints gbc_pLista = new GridBagConstraints();
		gbc_pLista.fill = GridBagConstraints.BOTH;
		gbc_pLista.gridx = 0;
		gbc_pLista.gridy = 4;
		frmNicim.getContentPane().add(pLista, gbc_pLista);
		
		JButton btnInviaLista = new JButton("Invia Lista");
		btnInviaLista.setIcon(new ImageIcon(NicimLisinger.class.getResource("/ico/script-go-icon.ico")));
		btnInviaLista.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				generaLista();
			}

		});
		pLista.add(btnInviaLista);
		
		JButton btnEliminaLista = new JButton("Elimina Lista");
		btnEliminaLista.setIcon(new ImageIcon(NicimLisinger.class.getResource("/ico/bin-closed-icon.ico")));
		btnEliminaLista.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedOption = JOptionPane.showConfirmDialog(null, "Sei sicuro di eliminare tutto?","Attenzione!",JOptionPane.YES_NO_OPTION);
				if (selectedOption == JOptionPane.YES_OPTION) 
					pulisciTutto();
			}

		});
		
		pLista.add(btnEliminaLista);
		
	}

	private void deleteRow() {
		int row = table.getSelectedRow();
		tmListaTagli.remove(row);
		table.changeSelection(row, 1, false, false);
		//table.getSelectionModel().setSelectionInterval(row,row);
	}
	
	private void generaLista() {
		//se la lista è vuota non fare nulla
		if (tmListaTagli.isEmpty())
			return;

		//se manca il materiale avvisa
		if (tfCodMat.getText().isEmpty()){
			JOptionPane.showMessageDialog(null, "Specificare il materiale da utilizzare", "Attenzione", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			
			if (!dbMng.controllaLista(tmListaTagli.getDati())){
				int selectedOption = JOptionPane.showConfirmDialog(null, "Il materiale non è coerente tra tutte le righe! Continuare lo stesso?","Attenzione!",JOptionPane.YES_NO_OPTION);
				
				if (selectedOption == JOptionPane.NO_OPTION) {
					return;
				}
			}
			
			try {
				if(dbMng.fileExist()){
					int selectedOption = JOptionPane.showConfirmDialog(null, "Un file NicimLisinger esiste già! Vuoi Sovrascriverlo?","Attenzione!",JOptionPane.YES_NO_OPTION);
					if (selectedOption == JOptionPane.NO_OPTION) {
						return;
					}
					else{
						dbMng.deleteExistingFile();
					}
				}
				
				dbMng.generaLista(tmListaTagli.getDati(), (Operatore)cbOperatore.getSelectedItem(), tfCodMat.getText().trim(), ftfLungMat.getText().trim());
				JOptionPane.showMessageDialog(null,"Lista Inviata con successo.");
				pulisciTutto();
			
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,"Errore di scrittura sul file " + e.getMessage());
				e.printStackTrace();
				
			} 
		
		}catch (barcodeExc e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}
				
	}
	

	private void checkInputMateriale() {
		if(tfCodMat.getText().isEmpty()){
			pulisciMateriale();
			return;
		}
			
		
		try {
			String[] param = dbMng.getMatDesc(tfCodMat.getText());
			tfDescMat.setText(param[0]);
			ftfLungMat.setText(param[1]);
			
		} catch (SQLException | barcodeExc e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
			pulisciMateriale();
			tfCodMat.requestFocusInWindow();
		}
		
		
	}
	
	private void pulisciMateriale(){
		tfCodMat.setText("");
		tfDescMat.setText("");
		ftfLungMat.setText("");
		
	}
	
	private void pulisciTutto() {
		pulisciMateriale();
		tmListaTagli.clearLista();
		table.changeSelection(0, 1, false, false);
		table.requestFocus();
		
	}
	
	


}
