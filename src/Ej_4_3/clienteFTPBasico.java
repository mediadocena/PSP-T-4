package Ej_4_3;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.SocketException;
import java.util.jar.JarInputStream;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class clienteFTPBasico extends JFrame{

	static JTextField cab= new JTextField();
	static JTextField cab2= new JTextField();
	static JTextField cab3= new JTextField();
	
	private static JTextField campo=new JTextField();
	private static JTextField campo2=new JTextField();
	
	JButton botonCargar=new JButton("Subir fichero");
	JButton botonDescargar=new JButton("Descargar fichero");
	JButton botonBorrar=new JButton("Eliminar fichero");
	JButton botonCreaDir=new JButton("Crear carpeta");
	JButton botonDelDir=new JButton("Eliminar carpeta");
	JButton botonSalir=new JButton("Salir");
	
	JButton btnLogin=new JButton("Login");
	
	static JList listaDirec = new JList();
	
	private final Container c=getContentPane();
	
	static FTPClient cliente=new FTPClient();
	String servidor="localhost";
	String user="usuario";
	String pass="usuario";
	boolean login;
	
	static String direcInicial="/";
	static String direcSelec=direcInicial;
	static String ficheroSelec="";
	
	
	public clienteFTPBasico() throws SocketException, IOException {
		super("CLIENTE BASICO FTP");
		cliente.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		cliente.connect(servidor);
		cliente.enterLocalPassiveMode();
		
		JFrame panelLogin=new JFrame();
		JLabel permiso=new JLabel("Introduce usuario y pass");
		JTextField campoUser=new JTextField();
		JPasswordField campoPass=new JPasswordField();
		
		panelLogin.setSize(400, 400);
		panelLogin.setVisible(true);
		
		
		panelLogin.add(permiso);
		panelLogin.add(campoUser);
		panelLogin.add(campoPass);
		panelLogin.add(btnLogin);
		
		Dimension size=permiso.getPreferredSize();
		permiso.setBounds(120, 50,size.width, size.height);
		permiso.setVisible(true);
		
		campoUser.setBounds(100,90,180,20);
		campoUser.setVisible(true);
		
		campoPass.setBounds(100,130,180,20);
		campoPass.setVisible(true);
		
		btnLogin.setBounds(120, 300, 130, 30);
		

			listaDirec.addListSelectionListener(new ListSelectionListener() {
				
				@Override
				public void valueChanged(ListSelectionEvent lse) {
					// TODO Auto-generated method stub
					if(lse.getValueIsAdjusting()) {
						ficheroSelec="";
						
						String fic=listaDirec.getSelectedValue().toString();
						
						if(listaDirec.getSelectedIndex()==0) {
							if(!fic.equals(direcInicial)) {
								try {
									cliente.changeToParentDirectory();
									direcSelec=cliente.printWorkingDirectory();
									FTPFile[] ff2=null;
									
									cliente.changeWorkingDirectory(direcSelec);
									ff2=cliente.listFiles();
									campo.setText("");
									
									llenarLista(ff2,direcSelec);
								} catch (IOException e) {
									// TODO: handle exception
									e.printStackTrace();
								}
							}
							}else {
								if(fic.substring(0,6).equals("(DIR) ")) {
									try {
										fic=fic.substring(6);
										String direcSelec2="";
										if(direcSelec.equals("/"))
											direcSelec2=direcSelec+fic;
										else
											direcSelec2=direcSelec+"/"+fic;
										
										
											cliente.changeWorkingDirectory(direcSelec2);
											FTPFile[] ff2=cliente.listFiles();
											campo.setText("DIRECTORIO: "+fic+", "+ff2.length+" elementos");
											direcSelec=direcSelec2;
											llenarLista(ff2,direcSelec);
									} catch (IOException e2) {
										// TODO Auto-generated catch block
											e2.printStackTrace();
									}
								}else {
									ficheroSelec=direcSelec;
									if(direcSelec.equals("/"))
										ficheroSelec+=fic;
									else
										ficheroSelec+="/"+fic;
								}
							}
							campo2.setText("DIRECTORIO ACTUAL: "+direcSelec);
						}
					}
			});
			
			botonSalir.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					try {
						cliente.disconnect();
					} catch (IOException e2) {
						// TODO: handle exception
						e2.printStackTrace();
					}
					System.exit(0);
					
				}
			});
			
			botonCreaDir.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String nombreCarpeta=JOptionPane.showInputDialog(null,"Introduce nombre de directorio","carpeta");
					if(!(nombreCarpeta==null)) {
						String directorio=direcSelec;
						if(!direcSelec.contentEquals("/"))
							directorio=directorio+"/";
						
						directorio+=nombreCarpeta.trim();
						
						try {
							if(cliente.makeDirectory(directorio)) {
								String m=nombreCarpeta.trim()+" => Se ha creado correctamtente";
								JOptionPane.showMessageDialog(null, m);
								campo.setText(m);
								cliente.changeWorkingDirectory(direcSelec);
								
								FTPFile[] ff2= cliente.listFiles();
								llenarLista(ff2,direcSelec);
							}else {
								JOptionPane.showMessageDialog(null, "No ha podido crearse el directorio");
							}
						} catch (IOException e2) {
							// TODO: handle exception
							e2.printStackTrace();
						}
					}
					
				}
			});
			
			btnLogin.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					user=campoUser.getText();
					pass=campoPass.getText();
					System.out.println("PASS==>"+pass);
					try {
						Login(user,pass, panelLogin);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			
			botonDelDir.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String nombreCarpeta=JOptionPane.showInputDialog(null,"Introduce nombre de directorio a eliminar","carpeta");
					if(!(nombreCarpeta==null)) {
						String directorio=direcSelec;
						if(!direcSelec.contentEquals("/"))
							directorio=directorio+"/";
						
						directorio+=nombreCarpeta.trim();
						
						try {
							if(cliente.removeDirectory(directorio)) {
								String m=nombreCarpeta.trim()+" => Se ha eliminado correctamtente";
								JOptionPane.showMessageDialog(null, m);
								campo.setText(m);
								cliente.changeWorkingDirectory(direcSelec);
								
								FTPFile[] ff2= cliente.listFiles();
								llenarLista(ff2,direcSelec);
							}else {
								JOptionPane.showMessageDialog(null, "No ha podido eliminarse el directorio");
							}
						} catch (IOException e2) {
							// TODO: handle exception
							e2.printStackTrace();
						}
					}
				}
			});
			
			botonCargar.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JFileChooser f=new JFileChooser();
					
					f.setFileSelectionMode(JFileChooser.FILES_ONLY);
					f.setDialogTitle("Selecciona el Fichero a SUBIR AL SERVIDOR FTP");
					
					int returnVal=f.showDialog(f, "Cargar");
					if(returnVal==JFileChooser.APPROVE_OPTION) {
						File file=f.getSelectedFile();
						String archivo=file.getAbsolutePath();
						String nombreArchivo=file.getName();
						try {
							SubirFichero(archivo,nombreArchivo);
						} catch (IOException e2) {
							// TODO: handle exception
							e2.printStackTrace();
						}
					}
				}
			});
			
			botonDescargar.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					String directorio=direcSelec;
					if(!direcSelec.equals("/"))
						directorio=directorio+"/";
					if(!ficheroSelec.contentEquals("")) {
						DescargarFichero(directorio+ficheroSelec,ficheroSelec);
					}
					
				}
			});
			
			botonBorrar.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					String directorio=direcSelec;
					if(!direcSelec.equals("/"))
						directorio=directorio+"/";
					if(!ficheroSelec.equals(""))
						BorrarFichero(directorio + ficheroSelec,ficheroSelec);
				}
			});
		
	}
	
	private void Login(String user, String pass, JFrame panelLogin) throws IOException {
		login=cliente.login(user, pass);
		if(login) {
			panelLogin.setVisible(false);
			cliente.changeWorkingDirectory(direcInicial);
			
			FTPFile[] files=cliente.listFiles();
			
			llenarLista(files,direcInicial);
			
			campo.setText("<<ARBOL DE DIRECTORIOS CONSTRUIDO>>");
			cab.setText("Servidor FTP: "+servidor);
			cab2.setText("Usuario: "+user);
			cab3.setText("DIRECTORIO RAIZ: "+direcInicial);
			
			listaDirec.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			JScrollPane barraDesplazamiento=new JScrollPane(listaDirec);
			barraDesplazamiento.setPreferredSize(new Dimension(335,420));
			barraDesplazamiento.setBounds(new Rectangle(5,65,335,420));
			c.add(barraDesplazamiento);
			c.setLayout(null);
			
			
			botonCargar.setBounds(370, 65, 150, 30);
			add(botonCargar);
			
			botonDescargar.setBounds(370, 115, 150, 30);
			add(botonDescargar);
			
			botonBorrar.setBounds(370, 165, 150, 30);
			add(botonBorrar);
			
			botonCreaDir.setBounds(370, 215, 150, 30);
			add(botonCreaDir);
			
			botonDelDir.setBounds(370, 265, 150, 30);
			add(botonDelDir);
			
			botonSalir.setBounds(370, 315, 150, 30);
			add(botonSalir);
			
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setSize(580,600);
			setVisible(true);
		}
	}
	
	private static void llenarLista(FTPFile[] files,String direc2) {
		if(files==null) return;
		
		DefaultListModel modeloLista=new DefaultListModel();
		
		listaDirec.setForeground(Color.blue);
		Font fuente=new Font("Courier", Font.PLAIN, 12);
		listaDirec.setFont(fuente);
		
		listaDirec.removeAll();
		
		try {
			cliente.changeWorkingDirectory(direc2);
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		direcSelec=direc2;
		
		modeloLista.addElement(direc2);
		
		for (int i = 0; i < files.length; i++) {
			if(!(files[i].getName()).equals(".") && !(files[i].getName()).equals("..")) {
				String f=files[i].getName();
				
				if(files[i].isDirectory()) f="(DIR) "+f;
				
				modeloLista.addElement(f);
			} 
		}
		
		try {
			listaDirec.setModel(modeloLista);
		} catch (NullPointerException n) {
			;
			// TODO: handle exception
		}
	}
	
	private void BorrarFichero(String nombreCompleto, String nombreFichero) {
		int seleccion=JOptionPane.showConfirmDialog(null, "Desea eliminar el fichero seleccionado?");
		if(seleccion==JOptionPane.OK_OPTION) {
			try {
				if(cliente.deleteFile(nombreCompleto)) {
					String m=nombreFichero+" => Eliminado correctamente";
					JOptionPane.showMessageDialog(null, m);
					campo.setText(m);
					cliente.changeWorkingDirectory(direcSelec);
					FTPFile[] ff2=cliente.listFiles();
					llenarLista(ff2, direcSelec);
				}else
					JOptionPane.showMessageDialog(null, nombreFichero+" => No se ha podido eliminar");
			} catch (IOException e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		}
	}
	
	private boolean SubirFichero(String archivo, String soloNombre) throws IOException {
		cliente.setFileType(FTP.BINARY_FILE_TYPE);
		BufferedInputStream in=new BufferedInputStream(new FileInputStream(archivo));
		boolean ok=false;
		
		cliente.changeWorkingDirectory(direcSelec);
		
		if(cliente.storeFile(soloNombre, in)) {
			String s=" "+soloNombre+" => Subido correctamente";
			campo.setText(s);
			campo2.setText("Se va a actualizar el arbol de directorios");
			JOptionPane.showMessageDialog(null, s);
			
			FTPFile[] ff2=cliente.listFiles();
			llenarLista(ff2,direcSelec);
			ok=true;
		}else
			campo.setText("No se ha podido subir el archivo: "+soloNombre);
		return ok;
	}
	
	private void DescargarFichero(String nombreCompleto, String nombreFichero) {
		String archivoyCarpetaDestino="";
		String carpetaDestino="";
		JFileChooser f=new JFileChooser();
		
		f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		f.setDialogTitle("Selecciona el Directorio donde DESCARGAR el fichero");
		
		int returnVal=f.showDialog(null, "Descargar");
		if(returnVal==JFileChooser.APPROVE_OPTION) {
			File file=f.getSelectedFile();
			carpetaDestino=(file.getAbsolutePath()).toString();
			archivoyCarpetaDestino=carpetaDestino+File.separator+nombreFichero;
			try {
				cliente.setFileType(FTP.BINARY_FILE_TYPE);
				BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream(archivoyCarpetaDestino));
				if(cliente.retrieveFile(nombreCompleto, out))
					JOptionPane.showMessageDialog(null, nombreFichero+" se ha descargado con exito");
				else
					JOptionPane.showMessageDialog(null, "No se ha podido descargar: "+nombreFichero);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				// TODO: handle exception
			}
		}
	}
	
	
	public static void main(String[] args) throws SocketException, IOException {
		// TODO Auto-generated method stub
		new clienteFTPBasico();
	}

}