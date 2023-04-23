/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nuevo.envio.de.archivos.en.java;

/**
 *
 * @author andro
 */

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Cliente extends JFrame implements ActionListener {
    private static final String IP_SERVIDOR = "127.0.0.1"; // Dirección IP del servidor
    private static final int PUERTO = 12345; // Puerto del servidor
    private Socket cliente;
    private BufferedReader entrada;
    private BufferedWriter salida;
    private JTextArea areaTexto;
    private JButton btnEnviar;
    private JFileChooser fileChooser;

    public Cliente() {
        super("Cliente de Chat");

        // Crear la interfaz gráfica de usuario
        areaTexto = new JTextArea(15, 30);
        JScrollPane scrollPane = new JScrollPane(areaTexto);
        btnEnviar = new JButton("Enviar");
        btnEnviar.addActionListener(this);
        fileChooser = new JFileChooser();

        JPanel panel = new JPanel();
        panel.add(scrollPane);
        panel.add(btnEnviar);

        add(panel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Conectar con el servidor
        try {
            cliente = new Socket(IP_SERVIDOR, PUERTO);
            entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            salida = new BufferedWriter(new OutputStreamWriter(cliente.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Iniciar el hilo para recibir mensajes del servidor
        Thread hiloReceptor = new Thread(new Runnable() {
            public void run() {
                try {
                    while (true) {
                        String mensaje = entrada.readLine();
                        if (mensaje != null) {
                            if (mensaje.startsWith("Archivo:")) {
                                // Recibir archivo del servidor
                                String nombreArchivo = mensaje.substring(9);
                                String contenidoArchivo = entrada.readLine();
                                areaTexto.append("Archivo recibido: " + nombreArchivo + "\n");

                                // Guardar archivo en el cliente
                                int resultado = fileChooser.showSaveDialog(Cliente.this);
                                if (resultado == JFileChooser.APPROVE_OPTION) {
                                    File archivoGuardado = fileChooser.getSelectedFile();
                                    FileOutputStream fos = new FileOutputStream(archivoGuardado);
                                    fos.write(contenidoArchivo.getBytes());
                                    fos.close();
                                    areaTexto.append("Archivo guardado en: " + archivoGuardado.getAbsolutePath() + "\n");
                                }
                            } else {
                                // Mostrar mensaje del servidor en el chat
                                areaTexto.append("Servidor: " + mensaje + "\n");
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        hiloReceptor.start();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnEnviar) {
            String mensaje = areaTexto.getText();
            try {
                salida.write(mensaje + "\n");
                salida.flush();
                areaTexto.append("Cliente: " + mensaje + "\n");
                areaTexto.setCaretPosition(areaTexto.getDocument().getLength());
                areaTexto.requestFocus();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Cliente cliente = new Cliente();
    }
}

