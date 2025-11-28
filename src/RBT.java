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

    public Nodo<T> buscar(T e) {
        Nodo<T> actual = root;
        
        while(actual != null) {
            if (e.compareTo(actual.elemento) == 0) {
                return actual;
            }
            if (actual.elemento.compareTo(e) > 0) {
                actual = actual.left;
            } else {
                actual = actual.right;
            }
        }
        return null;
    }

    public Nodo<T> minimum(Nodo<T> nodo){
        Nodo<T> actual = nodo;

        while(actual.left != null){
            actual = actual.left;
        }

        return actual;
    }

    public void transplant(Nodo<T> u, Nodo<T> v){
        if(u.parent == null){
            root = v;
        } else if(u == u.parent.left){
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }

        if(v != null){
            v.parent = u.parent;
        }
    }


    public void delete(T elemento){
        Nodo<T> actual = buscar(elemento);
        if(actual == null) return;

        Nodo<T> nodo = actual;
        Color colorActual = actual.color;
        Nodo<T> x = null;

        if(actual.left == null){
            x = actual.right;
            transplant(nodo, nodo.right);

        } else if(nodo.right == null){
            x = nodo.left;
            transplant(nodo, nodo.left);
        } else {
            actual = minimum(nodo.right);
            colorActual = actual.color;
            x = actual.right;
            
            if(actual.parent == nodo){
                if(x != null) x.parent = actual;
            } else {
                transplant(actual, actual.right);
                actual.right = nodo.right;
                actual.right.parent = actual;
            }

            transplant(nodo, actual);
            actual.left = nodo.left;
            actual.left.parent = actual;
            actual.color = nodo.color;
        }

        if(colorActual == Color.BLACK) deleteFix(x);
    }

    public void deleteFix(Nodo<T> x){
        while (x != root && x.color == Color.BLACK) {
            if(x == x.parent.left){
                Nodo<T> w = x.parent.right;

                if(w != null && w.color == Color.RED){
                    w.color = Color.BLACK;
                    x.parent.color = Color.RED;
                    rotateLeft(x.parent);
                    w = x.parent.right;
                }

                if((w.left == null || w.left.color == Color.BLACK) && (w.right == null || w.right.color == Color.BLACK)){
                    w.color = Color.RED;
                    x = x.parent;
                } else {
                    if(w.right == null || w.right.color == Color.BLACK){
                        if(w.left != null) w.left.color = Color.BLACK;
                        w.color = Color.RED;
                        rotateRight(w);
                        w = x.parent.right;
                    }

                    w.color = x.parent.color;
                    x.parent.color = Color.BLACK;

                    if(w.right != null) w.right.color = Color.BLACK;
                    rotateLeft(x.parent);
                    x = root;
                }
            } else {
                Nodo<T> w = x.parent.left;

                if(w != null && w.color == Color.RED){
                    w.color = Color.BLACK;
                    x.parent.color = Color.RED;
                    rotateRight(x.parent);
                    w = x.parent.left;
                }

                if((w.right == null || w.right.color == Color.BLACK) && (w.left == null || w.left.color == Color.BLACK)){
                    w.color = Color.RED;
                    x = x.parent;
                } else {
                    if(w.left == null || w.left.color == Color.BLACK){
                        if(w.right != null) w.right.color = Color.BLACK;
                        w.color = Color.RED;
                        rotateLeft(w);
                        w = x.parent.left;
                    }

                    w.color = x.parent.color;
                    x.parent.color = Color.BLACK;
                    if(w.left != null)w.left.color = Color.BLACK;
                    rotateRight(x.parent);
                    x = root;
                }
            }
        }

        if(x != null) x.color = Color.BLACK;
    }
}