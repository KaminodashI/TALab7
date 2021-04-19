public class BTree {
    private int m;
    private Node root;

    public class Node{
        int n;
        int key[] = new int[2 * m - 1];
        Node child[] = new Node[2 * m];
        boolean leaf = true;

        public int find(int k){
            for (int i = 0; i < this.n; i++) {
                if(this.key[i]==k){
                    return i;
                }
            }
            return -1;
        };
    }

    public BTree(int m){
        this.m = m;
        root = new Node();
        root.n = 0;
        root.leaf = true;
    }

    private void split(Node a, int pos, Node b){
        Node c = new Node();
        c.leaf = b.leaf;
        c.n = m-1;
        for (int i = 0; i < m-1; i++) {
            c.key[i] = b.key[i+m];
        }
        if(!b.leaf){
            for (int i = 0; i < m; i++) {
                c.child[i] = b.child[i+m];
            }
        }
        b.n = m-1;
        for (int i = a.n; i >= pos+1 ; i--) {
            a.child[i+1] = a.child[i];
        }
        a.child[pos+1]=c;

        for (int i = a.n-1; i >= pos ; i--) {
            a.key[i+1] = a.key[i];
        }
        a.key[pos]=b.key[m-1];
        a.n = a.n+1;
    }

    public void insert(int key){
        Node r = root;
        if(r.n == 2*m-1){
            Node s = new Node();
            root = s;
            s.leaf = false;
            s.n = 0;
            s.child[0] = r;
            split(s, 0, r);
            insertValue(s, key);
        }
        else {
            insertValue(r, key);
        }
    }

    private void insertValue(Node a, int k){
        if(a.leaf){
            int i = 0;
            for (i = a.n-1; i >= 0 && k<a.key[i]; i--) {
                a.key[i+1] = a.key[i];
            }
            a.key[i+1] = k;
            a.n = a.n+1;
        }
        else {
            int i = 0;
            for(i = a.n-1; i >=0 && k < a.key[i]; i--){
            };
            Node tmp = a.child[++i];
            if(tmp.n == 2*m-1){
                split(a, i, tmp);
                if(k > a.key[i]){
                    i++;
                }
            }
            insertValue(a.child[i], k);
        }
    }

    public void display(){
        display(root);
    }

    private void display(Node a){
        assert (a == null);
        for (int i = 0; i < a.n; i++) {
            System.out.println(a.key[i] + " ");
        }
        if(!a.leaf){
            for (int i = 0; i < a.n+1; i++) {
                display(a.child[i]);
            }
        }
    }

    private Node findNode(Node a, int key){
        int i = 0;
        if(a == null)
            return a;
        for (i = 0; i < a.n; i++){
            if(key < a.key[i]){
                break;
            }
            if(key == a.key[i]){
                return a;
            }
        }
        if(a.leaf){
            return null;
        }
        else {
            return findNode(a.child[i], key);
        }
    }

    public Node search(int key){ // Return NODE, but not value(object) which could be saved here
        Node node = this.findNode(root, key);
        if(node != null){
            return node;
        }
        else {
            return null; // Wrong, need to fix or write that this function can return "null"
        }
    }

    private int findValue(Node a, int key){
        int i = 0;
        if(a == null)
            return -1;
        for (i = 0; i < a.n; i++){
            if(key < a.key[i]){
                break;
            }
            if(key == a.key[i]){
                return key;
            }
        }
        if(a.leaf){
            return -1;
        }
        else {
            return findValue(a.child[i], key);
        }
    }

    public int searchValue(int key){
        int value = findValue(root, key);
        if(value != -1){
            return value;
        }
        else {
            return -1; // Wrong, need to fix or write that this function can return "null" (other undefined behavior)
        }
    }

    private void remove(Node a, int key){
        int pos = a.find(key);
        if(pos != -1){
            if (a.leaf) {
                int i = 0;
                for(; i < a.n && a.key[i] != key; i++){
                };
                for(; i< a.n; i++){
                    if(i != 2*m-2){
                        a.key[i] = a.key[i+1];
                    }
                }
                a.n--;
                return;
            }
            if(!a.leaf){
                Node pred = a.child[pos];
                int predKey = 0;
                if(pred.n >= m){
                    while(true){
                        if (pred.leaf) {
                            predKey = pred.key[pred.n-1];
                            break;
                        }
                        else {
                            pred = pred.child[pred.n];
                        }
                    }
                    remove(pred, predKey);
                    a.key[pos] = predKey;
                    return;
                }
                Node nextNode = a.child[pos+1];
                if(nextNode.n >= m){
                    int nextKey = nextNode.key[0];
                    if(!nextNode.leaf){
                        nextNode = nextNode.child[0];
                        while (true){
                            if(nextNode.leaf){
                                nextKey = nextNode.key[nextNode.n-1];
                                break;
                            }
                            else {
                                nextNode = nextNode.child[nextNode.n];
                            }
                        }
                    }
                    remove(nextNode, nextKey);
                    a.key[pos] = nextKey;
                    return;
                }

                int temp = pred.n + 1;
                pred.key[pred.n++] = a.key[pos];
                for (int i = 0, j = pred.n; i < nextNode.n; i++) {
                    pred.key[j++] = nextNode.key[i];
                    pred.n++;
                }
                for (int i = 0; i < nextNode.n + 1; i++) {
                    pred.child[temp++] = nextNode.child[i];
                }

                a.child[pos] = pred;
                for (int i = pos; i < a.n; i++) {
                    if (i != 2 * m - 2) {
                        a.key[i] = a.key[i + 1];
                    }
                }
                for (int i = pos + 1; i < a.n + 1; i++) {
                    if (i != 2 * m - 1) {
                        a.child[i] = a.child[i + 1];
                    }
                }
                a.n--;
                if (a.n == 0) {
                    if (a == root) {
                        root = a.child[0];
                    }
                    a = a.child[0];
                }
                remove(pred, key);
                return;
            }
        } else {
            for (pos = 0; pos < a.n; pos++) {
                if (a.key[pos] > key) {
                    break;
                }
            }
            Node tmp = a.child[pos];
            if (tmp.n >= m) {
                remove(tmp, key);
                return;
            }
            if (true) {
                Node nb = null;
                int devider = -1;

                if (pos != a.n && a.child[pos + 1].n >= m) {
                    devider = a.key[pos];
                    nb = a.child[pos + 1];
                    a.key[pos] = nb.key[0];
                    tmp.key[tmp.n++] = devider;
                    tmp.child[tmp.n] = nb.child[0];
                    for (int i = 1; i < nb.n; i++) {
                        nb.key[i - 1] = nb.key[i];
                    }
                    for (int i = 1; i <= nb.n; i++) {
                        nb.child[i - 1] = nb.child[i];
                    }
                    nb.n--;
                    remove(tmp, key);
                    return;
                } else if (pos != 0 && a.child[pos - 1].n >= m) {

                    devider = a.key[pos - 1];
                    nb = a.child[pos - 1];
                    a.key[pos - 1] = nb.key[nb.n - 1];
                    Node child = nb.child[nb.n];
                    nb.n--;

                    for (int i = tmp.n; i > 0; i--) {
                        tmp.key[i] = tmp.key[i - 1];
                    }
                    tmp.key[0] = devider;
                    for (int i = tmp.n + 1; i > 0; i--) {
                        tmp.child[i] = tmp.child[i - 1];
                    }
                    tmp.child[0] = child;
                    tmp.n++;
                    remove(tmp, key);
                    return;
                } else {
                    Node lt = null;
                    Node rt = null;
                    boolean last = false;
                    if (pos != a.n) {
                        devider = a.key[pos];
                        lt = a.child[pos];
                        rt = a.child[pos + 1];
                    } else {
                        devider = a.key[pos - 1];
                        rt = a.child[pos];
                        lt = a.child[pos - 1];
                        last = true;
                        pos--;
                    }
                    for (int i = pos; i < a.n - 1; i++) {
                        a.key[i] = a.key[i + 1];
                    }
                    for (int i = pos + 1; i < a.n; i++) {
                        a.child[i] = a.child[i + 1];
                    }
                    a.n--;
                    lt.key[lt.n++] = devider;

                    for (int i = 0, j = lt.n; i < rt.n + 1; i++, j++) {
                        if (i < rt.n) {
                            lt.key[j] = rt.key[i];
                        }
                        lt.child[j] = rt.child[i];
                    }
                    lt.n += rt.n;
                    if (a.n == 0) {
                        if (a == root) {
                            root = a.child[0];
                        }
                        a = a.child[0];
                    }
                    remove(lt, key);
                    return;
                }
            }
        }
    }

    public void remove(int key){
        Node a = findNode(root, key);
        if(a == null){
            System.out.println("> Value with that key doesn`t exist");
            return;
        }
        remove(root, key);
    }

}
