import java.awt.Color;

public class Nodo <T extends Comparable <T>>{
    T elemento;
    Nodo<T> parent;
    Nodo<T> left;
    Nodo<T> right;
    Color color;

    public Nodo(T elemento) {
        this.elemento = elemento;
        this.parent = null;
        this.left = null;
        this.right = null;
        this.color = Color.RED;
    }
    
    @Override
    public String toString(){
        return 
            "Elemento: " 
            + elemento + 
            "\nP: " + (parent != null ? parent.elemento : "/") +
            "\nL: " + (left != null ? left.elemento : "/") +
            "\nR: " + (right != null ? right.elemento : "/") +
            "\nColor: " + (color == Color.RED ? "R" : "B");

    }
}