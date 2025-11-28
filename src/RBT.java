import java.awt.Color;

public class RBT<T extends Comparable<T>> {
    public Nodo<T> root;
    public int size;
    public RBT() {
        this.root = null;
        this.size = 0;
    }

    public void insert(T elemento) {
        Nodo<T> nodo = new Nodo<>(elemento);
        nodo.color = Color.RED;
        nodo.left = null;
        nodo.right = null;

        if (root == null) {
            root = nodo;
            size++;
            return;
        } 

        Nodo<T> actual = root;
        Nodo<T> padre = null;
        while (actual != null) {
            padre = actual;
            
            if (actual.elemento.compareTo(elemento) > 0) {
                if (actual.left == null) {
                    actual.left = nodo;
                    size++;
                    break;
                }
                actual = actual.left;
            } else {
                if (actual.right == null) {
                    actual.right = nodo;
                    nodo.parent = actual;
                    size++;
                    break;
                }
                actual = actual.right;
            }
        }        
        nodo.parent = padre;
        if (padre == null) {
            root = nodo;
        } else if(padre.elemento.compareTo(elemento) > 0) {
            padre.left = nodo;
        } else {
            padre.right = nodo;
        }

        insertFix(nodo);
        root.color = Color.BLACK;
    }

    public void rotateLeft(Nodo<T> nodo){
        if(nodo == null) return;
        Nodo<T> y = nodo.right;

        if(y == null) return;
        
        nodo.right = y.left;

        if(y.left != null){
            y.left.parent = nodo;
        }

        y.parent = nodo.parent;

        if(nodo.parent == null){
            root = y;
        } else if(nodo == nodo.parent.left){
            nodo.parent.left = y;
        } else {
            nodo.parent.right = y;
        }

        y.left = nodo;
        nodo.parent = y;
    }

    public void rotateRight(Nodo<T> nodo){
        if(nodo == null) return;
        Nodo<T> y = nodo.left;

        if(y == null) return;
        
        nodo.left = y.right;

        if(y.right != null){
            y.right.parent = nodo;
        }

        y.parent = nodo.parent;

        if(nodo.parent == null){
            root = y;
        } else if(nodo == nodo.parent.right){
            nodo.parent.right = y;
        } else {
            nodo.parent.left = y;
        }

        y.right = nodo;
        nodo.parent = y;
    }

    public void insertFix(Nodo<T> nodo){
        while (nodo.parent != null && nodo.parent.color == Color.RED) {
            Nodo<T> grandparent = nodo.parent.parent;

            if(grandparent == null) break;

            if(nodo.parent == grandparent.left){
                Nodo<T> uncle = grandparent.right;
                
                if(uncle != null && uncle.color == Color.RED){
                    nodo.parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    grandparent.color = Color.RED;
                    nodo = grandparent;
                } else {
                    if(nodo == nodo.parent.right){
                        nodo = nodo.parent;
                        rotateLeft(nodo);
                    }
                    nodo.parent.color = Color.BLACK;
                    nodo.parent.parent.color = Color.RED;
                    rotateRight(grandparent);
                }
            }

            else {
                Nodo<T> uncle = grandparent.left;
                
                if(uncle != null && uncle.color == Color.RED){
                    nodo.parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    grandparent.color = Color.RED;
                    nodo = grandparent;
                } else {
                    if(nodo == nodo.parent.left){
                        nodo = nodo.parent;
                        rotateRight(nodo);
                    }
                    nodo.parent.color = Color.BLACK;
                    nodo.parent.parent.color = Color.RED;
                    rotateLeft(nodo.parent.parent);
                }
            }
        }
        root.color = Color.BLACK;
    }

    public int blackHeight(Nodo<T> nodo){
        // 0 indica no valido, en otro caso devuelve la altura negra desde el nodo dado
        if(nodo == null){
            return 1;
        }

        int leftBlackHeight = 0;
        int rightBlackHeight = 0;

        if(nodo.left != null){
            leftBlackHeight = blackHeight(nodo.left);
        }
        if(nodo.right != null){
            rightBlackHeight = blackHeight(nodo.right);
        }

        if(leftBlackHeight != rightBlackHeight){
            System.out.println("El arbol no cumple la propiedad de altura negra");
            return 0;
        }

        return nodo.color == Color.BLACK ? leftBlackHeight + 1 : leftBlackHeight;
    }

    public boolean validate(){
        if(root == null){
            return true;
        }

        //Raiz debe ser negra
        if(!root.color.equals(Color.BLACK)){
            System.out.println("El arbol no cumple la propiedad de que la raiz debe ser negra");
            return false;
        }

        RBValidate result = validateNode(root, null, null);
        return result.ok;
    }

    public RBValidate validateNode(Nodo<T> nodo, T min, T max){
        if(nodo == null){
            return new RBValidate(true, 1);
        }

        if((min != null && nodo.elemento.compareTo(min) <= 0) || (max != null && nodo.elemento.compareTo(max) >= 0)){
            System.out.println("El arbol no cumple la propiedad de ordenamiento de BST");
            return new RBValidate(false, 0);
        }

        if(nodo.color == Color.RED && nodo.left.color == Color.RED || nodo.right.color == Color.RED){
            System.out.println("El arbol no cumple la propiedad de no tener nodos rojos consecutivos");
            return new RBValidate(false, 0);
        }

        //Subarbol izquierdo
        RBValidate leftResult = validateNode(nodo.left, min, nodo.elemento);
        if(!leftResult.ok){
            return new RBValidate(false, 0);//Devuelve falso
        }
        
        //Subarbol derecho
        RBValidate rightResult = validateNode(nodo.right, nodo.elemento, max);
        if(!rightResult.ok){
            return new RBValidate(false, 0);
        }

        //Ambas alturas negras deben ser iguales
        if(leftResult.blackHeight != rightResult.blackHeight){
            System.out.println("El arbol no cumple la propiedad de altura negra");
            return new RBValidate(false, 0);
        }

        int bh = leftResult.blackHeight + (nodo.color == Color.BLACK ? 1 : 0);
        return new RBValidate(true, bh);
    }

    public void inOrder(){
        if(root==null){
            System.out.println("El arbol esta vacio");
            return;
        }
        inOrderRecursivo(root);
    }

    public void inOrderRecursivo(Nodo<T> nodo){
        if(nodo.left != null){
            inOrderRecursivo(nodo.left);
        }
        System.out.println(nodo.elemento);
        if(nodo.right != null){
            inOrderRecursivo(nodo.right);
        }
    }
}