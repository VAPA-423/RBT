import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public class Ventana extends JFrame {
    private JPanel pArbol;
    private JTextField txtEntrada;
    private RBT<Integer> rbt = new RBT<>();
    private Font font = new Font("MONOSPACED", Font.BOLD, 25);
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
        });

        btnEliminar.addActionListener(e -> {
            // Acción para eliminar un nodo (no implementada)
            
        });

        p1.add(txtEntrada);
        p1.add(btnInsertar);
        p1.add(btnEliminar);
        add(p1, BorderLayout.NORTH);
        add(pArbol, BorderLayout.CENTER);

        setVisible(true);
    }

    public void calcularPosicion(Nodo<Integer> nodo, int nivel, Map<Nodo<Integer>, Point> posiciones, int ancho, int alto, int x, int aa){
        if(nodo == null) return;

        int distancia = ancho / (int) Math.pow(2, nivel);
        int y = nivel*120;
        posiciones.put(nodo, new Point(x, y));
        calcularPosicion(nodo.left, nivel+1, posiciones, ancho, alto, x-distancia, aa);
        calcularPosicion(nodo.right, nivel+1, posiciones, ancho, alto, x+distancia, aa);
    }

    public void dibujar() {
        int value = Integer.parseInt(txtEntrada.getText());
        rbt.insert(value);
        
        int aa = (int) Math.ceil(Math.log(rbt.size) / Math.log(2));
        aa = Math.max(aa, 1) + 2;

        int ancho = pArbol.getWidth();
        int alto = pArbol.getHeight();
        if(ancho <= 0 || alto <= 0){
            ancho = 1000;
            alto = 800;
        }

        posiciones.clear();
        int xCentro = ancho/2;
        calcularPosicion(rbt.root, 1, posiciones, ancho, alto, xCentro, aa);

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
        int diameter = 80;

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

            g2.setFont(font);
            String text = "" + nodo.elemento;
            FontMetrics fm = g2.getFontMetrics();
            int textX = x + (diameter - fm.stringWidth(text)) / 2;
            int textY = y + ((diameter - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(text, textX, textY);
        }
    }
}
