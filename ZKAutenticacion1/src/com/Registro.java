package com;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;

public class Registro extends SelectorComposer<Component>{
	
	private static Logger log = Logger.getLogger(Registro.class.getName());
	
	@Wire
	private Textbox nombre,paterno,materno,correo,usuario,password,passwordconfirma;
	@Wire
	private Radiogroup sexo;
	@Wire
	private Button bregistrar;
	
	
	
	@Listen("onClick =#bregistrar")
	public void registrar(){
		
		PreparedStatement stmt;
	    Connection conn = null;    
	    
	try {
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(
                "jdbc:mysql://localhost/autenticacion?user=root&password=");
        
        
        
        stmt = conn.prepareStatement( "INSERT INTO usuario (nombre, paterno, materno, sexo, correo, usuario, password)VALUES (?,?,?,?,?,?,MD5(?))"); 
		        
        stmt.setString(1, nombre.getText());
        stmt.setString(2, paterno.getText());
        stmt.setString(3, materno.getText());
        stmt.setString(4, sexo.getSelectedItem().getValue());
        stmt.setString(5, correo.getText());
        stmt.setString(6, usuario.getText());
        stmt.setString(7, password.getText());
        
        stmt.executeUpdate();
        
		Messagebox.show("Usuario Registrado");
		Executions.sendRedirect("index.zul");
	}
	catch(Exception e){
        log.severe(e.toString());
    	}
	
	}
	
}
