/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nuevo.envio.de.archivos.en.java;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Servidor extends JFrame implements ActionListener {
    private static final int PUERTO = 12345; // Puerto del servidor
    private ServerSocket servidor;
    private Socket cliente;
    private BufferedReader entrada;
    private BufferedWriter salida;
    private JTextArea areaTexto;
    private JButton btnEnviar;
    private JButton btnArchivo;
    private JFileChooser fileChooser;
    private File carpetaDescarga; // Carpeta para descargar archivos

    public Servidor() {
        super("Servidor de Chat");

        // Crear la interfaz gráfica de usuario
        areaTexto = new JTextArea(15, 30);
        JScrollPane scrollPane = new JScrollPane(areaTexto);
        btnEnviar = new JButton("Enviar");
        btnEnviar.addActionListener(this);
        btnArchivo = new JButton("Enviar Archivo");
        btnArchivo.addActionListener(this);
        fileChooser = new JFileChooser();
        carpetaDescarga = new File("carpeta_descarga"); // Carpeta específica para descargar archivos

        JPanel panel = new JPanel();
        panel.add(scrollPane);
        panel.add(btnEnviar);
        panel.add(btnArchivo);

        add(panel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Iniciar el servidor
        try {
            servidor = new ServerSocket(PUERTO);
            areaTexto.append("Servidor iniciado en el puerto " + PUERTO + "\n");
            cliente = servidor.accept();
            entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            salida = new BufferedWriter(new OutputStreamWriter(cliente.getOutputStream()));
            areaTexto.append("Cliente conectado: " + cliente.getInetAddress().getHostAddress() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnEnviar) {
            String mensaje = areaTexto.getText();
            try {
                salida.write(mensaje + "\n");
                salida.flush();
                areaTexto.append("Servidor: " + mensaje + "\n");
                areaTexto.setCaretPosition(areaTexto.getDocument().getLength());
                areaTexto.requestFocus();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == btnArchivo) {
            int resultado = fileChooser.showOpenDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                File archivo = fileChooser.getSelectedFile();
                try {
                    // Leer el archivo en un array de bytes
                    FileInputStream fis = new FileInputStream(archivo);
                    byte[] buffer = new byte[(int) archivo.length()];
                    fis.read(buffer);
                    fis.close();

                    // Enviar el nombre del archivo y su contenido al cliente
                    salida.write("Archivo:" + archivo.getName() + "\n");
                    salida.flush();
                    salida.write(new String(buffer) + "\n");
                    salida.flush();
                    areaTexto.append("Archivo enviado: " + archivo.getName() + "\n");

                    // Guardar el archivo en la carpeta de descarga en el servidor
                    File archivoDescarga = new File(carpetaDescarga, archivo.getName());
                    FileOutputStream fos = new FileOutputStream(archivoDescarga);
                    fos.write(buffer);
                    fos.close();
                    areaTexto.append("Archivo guardado en: " + archivoDescarga.getAbsolutePath() + "\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
         Servidor servidor = new Servidor();
}
    }
