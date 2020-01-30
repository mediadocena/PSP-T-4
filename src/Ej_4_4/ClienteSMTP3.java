package Ej_4_4;

import java.io.IOException;
import java.io.Writer;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.spec.InvalidKeySpecException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import org.apache.commons.net.smtp.*;

public class ClienteSMTP3 {
	public static void main(String[] args) throws NoSuchAlgorithmException, UnrecoverableKeyException,
			KeyStoreException, InvalidKeyException, InvalidKeySpecException {

		// TODO Auto-generated method stub

				AuthenticatingSMTPClient client=new AuthenticatingSMTPClient();
				Salieri salieri=new Salieri();
				String server=salieri.controlaStringSt("Introduzca servidor SMTP");
				
				System.out.println("Necesita negociacion TLS (S/N) Default N");
				boolean negociacion=salieri.Confirmar(salieri.controlaString());
				
				String username=salieri.controlaStringSt("Introduzca usuario");
				
				String password=salieri.controlaStringSt("Introduzca password");
				
				int puerto=salieri.controlaenteroSt("Introduzca puerto");

				String remitente=salieri.controlaStringSt("Introduzca remitente");
				
				String destino1=salieri.controlaStringSt("Introduzca destinatario");

				String asunto=salieri.controlaStringSt("Introduzca asunto");
				
				String mensaje=salieri.controlaStringSt("Introduzca mensaje");
				
				try {
					int respuesta;
					KeyManagerFactory kmf=KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
					kmf.init(null,null);
					KeyManager km=kmf.getKeyManagers()[0];
					
					client.connect(server,puerto);
					System.out.println("1 - "+client.getReplyString());
					client.setKeyManager(km);
					
					respuesta=client.getReplyCode();
					if(!SMTPReply.isPositiveCompletion(respuesta)) {
						client.disconnect();
						System.err.println("Conexion rechazada");
						System.exit(1);
					}
						
						client.ehlo(server);
						System.out.println("2 - "+client.getReplyString());
						
						if(negociacion) {
							if(client.execTLS()) {
								System.out.println("3 - "+client.getReplyString());
								
								if(client.auth(AuthenticatingSMTPClient.AUTH_METHOD.PLAIN, username, password)) {
									System.out.println("4 - "+client.getReplyString());
									
									SimpleSMTPHeader cabecera=new SimpleSMTPHeader(remitente,destino1,asunto);
									
									client.setSender(remitente);
									client.addRecipient(destino1);
									System.out.println("5 - "+client.getReplyString());
									
									Writer writer=client.sendMessageData();
									
									if(writer==null) {
										System.out.println("FALLO AL ENVIAR DATA");
										System.exit(1);
									}
									
									writer.write(cabecera.toString());
									writer.write(mensaje);
									writer.close();
									System.out.println("6 - "+client.getReplyString());
									
									boolean exito=client.completePendingCommand();
									System.out.println("7 - "+client.getReplyString());
									
									if(!exito) {
										System.out.println("FALLO AL FINALIZAR TRANSACCION");
										System.exit(1);
									}else
										System.out.println("Mensaje enviado con EXITO");
								}else
										System.out.println("USUARIO NO AUTENTICADO");
						}else
							System.out.println("FALLO AL EJECUTAR STARTLLS");
					}else {
						if(client.auth(AuthenticatingSMTPClient.AUTH_METHOD.PLAIN, username, password)) {
							System.out.println("4 - "+client.getReplyString());
							
							SimpleSMTPHeader cabecera=new SimpleSMTPHeader(remitente,destino1,asunto);
							
							client.setSender(remitente);
							client.addRecipient(destino1);
							System.out.println("5 - "+client.getReplyString());
							
							Writer writer=client.sendMessageData();
							
							if(writer==null) {
								System.out.println("FALLO AL ENVIAR DATA");
								System.exit(1);
							}
							
							writer.write(cabecera.toString());
							writer.write(mensaje);
							writer.close();
							System.out.println("6 - "+client.getReplyString());
							
							boolean exito=client.completePendingCommand();
							System.out.println("7 - "+client.getReplyString());
							
							if(!exito) {
								System.out.println("FALLO AL FINALIZAR TRANSACCION");
								System.exit(1);
							}else
								System.out.println("Mensaje enviado con EXITO");
						}else
								System.out.println("USUARIO NO AUTENTICADO");
					}
				} catch (Exception e) {
					// TODO: handle exception
					System.err.println("Could not connect to server");
					e.printStackTrace();
					System.exit(1);
				}
				try {
					client.disconnect();
				} catch (IOException f) {
					// TODO: handle exception
					f.printStackTrace();
				}
				
				System.out.println("Fin de envio");
				System.exit(0);
	}// main
}// ..ClienteSMTP3
