import java.awt.*;
import java.util.*;
import javax.swing.*;

public class Ventana extends JFrame {
    private JPanel pArbol;
    private JTextField txtEntrada;
    private RBT<Integer> rbt = new RBT<>();
    private Font font = new Font("MONOSPACED", Font.BOLD, 30);
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private Map<Nodo<Integer>, Point> posiciones = new HashMap<>();

    public void ventana(){
        setTitle("Gráfica RBT");
        setSize(screenSize);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p1;
        p1 = new JPanel();
        p1.setLayout(new GridLayout(1,3,10,10));

        //Donde se grafica el árbol
        pArbol = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarArbol((Graphics2D) g);
            }
        };
        
        txtEntrada = new JTextField(20);
        txtEntrada.setFont(font);
        txtEntrada.setHorizontalAlignment(JTextField.CENTER);

        JButton btnInsertar = new JButton("Insertar");
        JButton btnEliminar = new JButton("Eliminar");
        btnInsertar.setFont(font);
        btnEliminar.setFont(font);

        btnInsertar.addActionListener(e -> {
            dibujar();
            txtEntrada.setText("");
        });

        btnEliminar.addActionListener(e -> {
            try{
                int value = Integer.parseInt(txtEntrada.getText());
                rbt.delete(value);
                actualizarPosiciones();
                pArbol.repaint();

                txtEntrada.setText("");

            } catch (Exception ex){
                System.out.println("Valor inválido.");
            }
        });

        p1.add(txtEntrada);
        p1.add(btnInsertar);
        p1.add(btnEliminar);
        add(p1, BorderLayout.NORTH);
        add(pArbol, BorderLayout.CENTER);

        setVisible(true);
    }

    public void calcularPosicion(Nodo<Integer> nodo, int x, int y, int dx){
        if(nodo == null) return;

        posiciones.put(nodo, new Point(x, y));

        int nextDx = dx/2;
        if(nodo.left != null){
            calcularPosicion(nodo.left, x - dx, y + 120, nextDx);
        }

        if(nodo.right != null){
            calcularPosicion(nodo.right, x + dx, y + 120, nextDx);
        }
    }

    public void actualizarPosiciones(){
        posiciones.clear();

        int ancho = pArbol.getWidth();
        if (ancho <= 0) ancho = 1000; 
            
        int xInicial = pArbol.getWidth()/2;
        int dx = pArbol.getWidth()/6;

        calcularPosicion(rbt.root, xInicial, 80, dx);
    }

    public void dibujar() {
        int value = Integer.parseInt(txtEntrada.getText());
        rbt.insert(value);

        actualizarPosiciones();
        pArbol.repaint();
    }

    private void dibujarArbol(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibujar las conexiones entre nodos
        g2.setStroke(new BasicStroke(3));
        g2.setColor(Color.BLACK);

        for(Map.Entry<Nodo<Integer>, Point> entry : posiciones.entrySet()) {
            Nodo<Integer> nodo = entry.getKey();
            Point point = entry.getValue();

            if (nodo.left != null){
                Point leftPoint = posiciones.get(nodo.left);
                g2.drawLine(point.x, point.y, leftPoint.x, leftPoint.y);
            }

            if (nodo.right != null){
                Point rightPoint = posiciones.get(nodo.right);
                g2.drawLine(point.x, point.y, rightPoint.x, rightPoint.y);
            }
        }   

        //Dibujar nodos
        int diameter = 100;

        for (Map.Entry<Nodo<Integer>, Point> entry : posiciones.entrySet()) {
            Nodo<Integer> nodo = entry.getKey();
            Point point = entry.getValue();

            int x = point.x -diameter / 2;
            int y = point.y -diameter / 2;

            g2.setColor(nodo.color);
            g2.fillOval(x, y, diameter, diameter);

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(x, y, diameter, diameter);

            //Color del texto 
            if(nodo.color == Color.BLACK){
                g2.setColor(Color.WHITE);
            } else {
                g2.setColor(Color.BLACK);
            }

            g2.setFont(font);
            String text = "" + nodo.elemento;
            FontMetrics fm = g2.getFontMetrics();
            int textX = x + (diameter - fm.stringWidth(text)) / 2;
            int textY = y + ((diameter - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(text, textX, textY);
        }
    }
}
