package com;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;
import org.zkoss.zhtml.Label;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.websocket.SendResult;

import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.zhtml.Filedownload;
import org.zkoss.zhtml.S;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mysql.jdbc.ResultSetMetaData;
import com.mysql.jdbc.Statement;

public class Autenticacion extends SelectorComposer<Component>{	
	
	private static Logger log = Logger.getLogger(Autenticacion.class.getName());
	
	@Wire
	private Textbox id,nombre,paterno,materno,sexo,curp,correo,usuario,password;
	@Wire
	private Button bEntrar,bCancelar,bActualizar,bSalir,bLink;
	@Wire 
	private Window winlog,view;
	@Wire
	private Iframe reportframe;
	@Wire
	private Textbox nom,ap,am,sex,cu,cor,usu,fecha;
	
	AMedia fileContent;
	
	String Nombre = "gama";
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
    	if(comp.getId().equals("winlog")){
    		winlog.setTitle("Hello:" + Sessions.getCurrent().getAttribute("email").toString()+"!");
    		//Sessions.getCurrent().removeAttribute("user");
    		id.setText(Sessions.getCurrent().getAttribute("id").toString());
    		nombre.setText(Sessions.getCurrent().getAttribute("nombre").toString());
    		paterno.setText(Sessions.getCurrent().getAttribute("paterno").toString());
    		materno.setText(Sessions.getCurrent().getAttribute("materno").toString());
    		sexo.setText(Sessions.getCurrent().getAttribute("genero").toString());
    		correo.setText(Sessions.getCurrent().getAttribute("correo").toString());
    	}
    	
	}
	
	@Listen("onClick =#bEntrar")
	public void entrar() throws ClassNotFoundException{
		
		String databaseId = "";
		String databaseNombre = "";
	    String databasePaterno = "";
	    String databaseMaterno = "";
	    String databaseSexo = "";
	    String databaseCorreo = "";
	    String databaseUsuario = "";
	    String databasePassword = "";
	    
	    String email = correo.getText();
	    String pass = password.getText();
	    
		Connection conn = null;
        try
        {
            Class.forName ("com.mysql.jdbc.Driver").newInstance ();
            String userName = "root";
            String password = "";
            String url = "jdbc:mysql://localhost/autenticacion";
            
            conn = DriverManager.getConnection (url, userName, password);
            System.out.println ("Conexion de la Base de datos establecida");
            
            Statement s = (Statement) conn.createStatement ();
            s.executeQuery ("SELECT * FROM usuario WHERE correo='"+ email +"' && password= MD5('"+ pass +"') ");
            ResultSet rs = s.getResultSet();
            
            while (rs.next()) 
            {
            
            databaseId = rs.getString("id_usuario");
            databaseNombre = rs.getString("nombre");
            databasePaterno = rs.getString("paterno");
            databaseMaterno = rs.getString("materno");
            databaseSexo = rs.getString("sexo");
            databaseCorreo = rs.getString("correo");
            databaseUsuario = rs.getString("usuario");
            databasePassword = rs.getString("password");
            
            }
            if(email.equals(databaseCorreo) && databasePassword.equals(databasePassword)){
            	Messagebox.show("Bienvenid@ !!!   " + databaseUsuario);
            	
            	Sessions.getCurrent().setAttribute("email",email);
            	Sessions.getCurrent().setAttribute("id",databaseId);
            	Sessions.getCurrent().setAttribute("nombre",databaseNombre);
            	Sessions.getCurrent().setAttribute("paterno",databasePaterno);
            	Sessions.getCurrent().setAttribute("materno",databaseMaterno);
            	Sessions.getCurrent().setAttribute("genero",databaseSexo);
            	Sessions.getCurrent().setAttribute("correo",databaseCorreo);
            	Executions.getCurrent().sendRedirect("registro_sesion.zul");
              
            }else{
            	Messagebox.show("Usuario y Contraseña Incorrecto");
            }    
            
            rs.close ();
            s.close ();
             
        }
        catch (Exception e)
        {
            System.err.println ("ERROR: "+ e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                try
                {
                    conn.close ();
                    System.out.println ("Conexion de la base de datos terminada");
                }
                catch (Exception e) { /* ignore close errors */ }
            }
        }
	}
	
	@Listen("onClick=#bSalir")
	public void salir(){
		Executions.sendRedirect("C:\\Users\\Gama\\workspace\\ZKAutenticacion\\PDF\\"+"Prueba.pdf");
	}
	
	@Listen("onClick =#bCancelar")
	public void cancelar(){
		 EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
	            public void onEvent(ClickEvent event) throws Exception {
	                if(Messagebox.Button.YES.equals(event.getButton())) {
	                    // cancel order
	                    // ...
	                    // notify user
	                	Executions.sendRedirect("index.zul");
	                    Messagebox.show("The order has been cancelled.");
	                }
	            }
	        };
	        Messagebox.show("Are you sure you want to cancel?", "Cancel Order", new Messagebox.Button[]{
	                Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
	       // Executions.sendRedirect("index.zul");
	    
	}
	
	@Listen("onClick=#bActualizar")
	public void actualizar() {
		   	    
		EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
            public void onEvent(ClickEvent event) throws Exception {
            	
                if(Messagebox.Button.YES.equals(event.getButton())) {
                      	        		            	                		
            		PreparedStatement stmt;
            	    Connection conn = null; 
                	try {
                        Class.forName("com.mysql.jdbc.Driver");
                        conn = DriverManager.getConnection("jdbc:mysql://localhost/autenticacion?user=root&password=");
                        stmt = conn.prepareStatement("UPDATE usuario set curp = ?,usuario= ? WHERE id_usuario = ? "); 
                		        
                        stmt.setString(1, curp.getText());
                        stmt.setString(2, usuario.getText());
                        stmt.setString(3, id.getText());
                        stmt.executeUpdate();
                        
                		Messagebox.show("Usuario Actualizado");
                		//Executions.sendRedirect("index.zul");
                				
                		curp.setDisabled(true);
                		usuario.setDisabled(true);
                		bLink.setLabel("Ver");
                	}
                	catch(Exception e){
                        log.severe(e.toString());
                    	}
                }
                
        		        		
                try{
        			
        			Class.forName("com.mysql.jdbc.Driver");		
        			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/autenticacion?user=root&password=");
        			Statement st = (Statement) con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        			ResultSet  rs = st.executeQuery("select * from usuario where id_usuario='"+id.getText()+"'");
        			//ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();	
        			////////////////////////////////////////////////////////////////////////////
        			
        		Document document = new Document();
        		PdfWriter.getInstance(document, new FileOutputStream(Executions.getCurrent().getDesktop().getWebApp().getRealPath("\\PDF\\Prueba-"+id.getText()+".pdf")));
        		//PdfWriter.getInstance(document, new FileOutputStream("C:\\Users\\Gama\\workspace\\ZKAutenticacion\\pdf\\Prueba.pdf"));
        		document.open();
        		Image image = Image.getInstance("logo-seciti-alta.png");
        		image.scaleToFit(200,200);
        		image.setAlignment(Image.RIGHT);
        		document.add(image);
        		document.add(new Paragraph ("Secretaria de Ciencia, Tecnología e Innovación.",FontFactory.getFont("arial",22,Font.BOLD)));
        		document.add(new Paragraph ("___"));
        		//document.add(new Chunk("\n"));		
        		PdfPTable table = new PdfPTable(2);
        		float[] medidaCeldas = {0.55f, 1.25f};
        		PdfPCell cell = new PdfPCell(new Paragraph("Datos Personales",FontFactory.getFont("arial",15,Font.BOLD,BaseColor.WHITE)));
        		
        		cell.setColspan(2);
        		
        		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        		cell.setBackgroundColor(BaseColor.DARK_GRAY);
        		cell.setPadding(12);
        		table.addCell(cell);		
        		
                while(rs.next()){
        				
        		table.addCell(new Paragraph("Nombre",FontFactory.getFont("arial",15,Font.BOLD,BaseColor.BLACK)));
        		table.addCell(rs.getString(2));
        		table.addCell(new Paragraph("Apellido Paterno",FontFactory.getFont("arial",15,Font.BOLD,BaseColor.BLACK)));
        		table.addCell(rs.getString(3));
        		table.addCell(new Paragraph("Apellido Materno",FontFactory.getFont("arial",15,Font.BOLD,BaseColor.BLACK)));
        		table.addCell(rs.getString(4));
        		table.addCell(new Paragraph("Genero",FontFactory.getFont("arial",15,Font.BOLD,BaseColor.BLACK)));
        		table.addCell(rs.getString(5));
        		table.addCell(new Paragraph("Correo",FontFactory.getFont("arial",15,Font.BOLD,BaseColor.BLACK)));
        		table.addCell(rs.getString(6));
        		table.addCell(new Paragraph("Usuario",FontFactory.getFont("arial",15,Font.BOLD,BaseColor.BLACK)));
        		table.addCell(rs.getString(7));
        		table.addCell(new Paragraph("Fecha y Hora",FontFactory.getFont("arial",15,Font.BOLD,BaseColor.BLACK)));
        		table.addCell(new Date().toLocaleString());
        		table.setWidths(medidaCeldas);
        		table.setWidthPercentage(100);;
                }
        				
        		document.add(table);
        		
        		document.close();
        		}
        		catch(Exception e){
        			System.out.println(e);
        		}
        		
                
        		Properties props = new Properties();

        		props.setProperty("mail.smtp.host", "smtp.gmail.com");
        		props.setProperty("mail.smtp.starttls.enable", "true");
        		props.setProperty("mail.smtp.port","587");
        		props.setProperty("mail.smtp.user", "gamaliellobato@gmail.com");
        		props.setProperty("mail.smtp.auth", "true");

        		Session session = Session.getDefaultInstance(props);
        		session.setDebug(true);
        		
        		BodyPart texto = new MimeBodyPart();

        		texto.setText("Correo enviado desde Eclipse con archivo PDF");
        		BodyPart adjunto = new MimeBodyPart();
        		adjunto.setDataHandler(new DataHandler(new FileDataSource(Executions.getCurrent().getDesktop().getWebApp().getRealPath("\\PDF\\Prueba-"+id.getText()+".pdf"))));
        		adjunto.setFileName("Prueba-"+id.getText()+".pdf");
        		
        		MimeMultipart multiParte = new MimeMultipart();
        		multiParte.addBodyPart(texto);
        		multiParte.addBodyPart(adjunto);
        		
        		MimeMessage message = new MimeMessage(session);
        		message.setFrom(new InternetAddress("gamaliellobato@gmail.com"));
        		message.addRecipient(Message.RecipientType.TO, new InternetAddress(correo.getText()));
        		message.setSubject("Prueba JavaMail");
        		message.setContent(multiParte);
        		
        		Messagebox.show("Se envio notificacion de Actualizacion a: "+correo.getText());
        		
        		Transport t = session.getTransport("smtp");
        		t.connect("gamaliellobato@gmail.com","rwprmdftobxksxka");
        		t.sendMessage(message,message.getAllRecipients());
        		t.close();
               }
        };
        Messagebox.show("¿Estas seguro de Actualizar tus datos?", "Cancel Order", new Messagebox.Button[]{
                Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);	   
	};
	
	@Listen("onClick=#bLink")
	public void verdescargar() throws ClassNotFoundException, SQLException, DocumentException, IOException{
		
		//Messagebox.show("hola");
		String filePath = Executions.getCurrent().getDesktop().getWebApp().getRealPath("\\PDF\\Prueba-"+id.getText()+".pdf");
		
		File f = new File(filePath);
	      
	      byte[] buffer = new byte[(int) f.length()];
	      FileInputStream fs = new FileInputStream(f);
	      fs.read(buffer);
	      fs.close();
	      ByteArrayInputStream is = new ByteArrayInputStream(buffer);
	      fileContent = new AMedia("report", "pdf", "application/pdf", is); 
	      reportframe.setContent(fileContent);
	      System.out.println(fileContent);
	}
}


