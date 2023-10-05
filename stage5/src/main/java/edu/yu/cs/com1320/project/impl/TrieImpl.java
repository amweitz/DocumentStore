package edu.yu.cs.com1320.project.impl;
import edu.yu.cs.com1320.project.Trie;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TrieImpl < Value > implements Trie < Value > {
    private static final int alphabetSize = 256; // extended ASCII
    private Node root; // root of trie

    public static class Node < Value > {
        private HashSet < Value > val = new HashSet < > ();
        private Node[] links = new Node[alphabetSize];
    }
    public TrieImpl() {}
    private HashSet < Value > get(String key) {
        Node x = get(this.root, key, 0);
        if (x == null) {
            return null;
        }
        if (x.val == null) {
            HashSet < Value > temp = new HashSet < > ();
            return temp;
        }
        return x.val;
    }

    private Node get(Node x, String key, int d) {
        if (x == null) {
            return null;
        }
        if (d == key.length()) {
            return x;
        }
        char sub = key.charAt(d);
        return get(x.links[sub], key, d + 1);
    }
    /**
     * add the given value at the given key
     * @param key
     * @param val
     */
    public void put(String key, Value val) {
        if (val == null) {
            this.deleteAll(key);
        }
        this.root = put(root, key, val, 0);
    }

    private Node put(Node x, String key, Value val, int d) {
        if (x == null) {
            x = new Node();
        }
        if (d == key.length()) {
            x.val.add(val);
            return x;
        }
        char sub = key.charAt(d);
        x.links[sub] = put(x.links[sub], key, val, d + 1);
        return x;
    }

    /**
     * get all exact matches for the given key, sorted in descending order.
     * Search is CASE SENSITIVE.
     * @param key
     * @param comparator used to sort  values
     * @return a List of matching Values, in descending order
     */
    public List < Value > getAllSorted(String key, Comparator < Value > comparator) {
        Set < Value > q = get(key);
        if (q == null) {
            ArrayList < Value > t = new ArrayList < > ();
            return t;
        }
        ArrayList < Value > temp = new ArrayList < > (q);
        Collections.sort(temp, comparator);
        return temp;
    }

    /**
     * get all matches which contain a String with the given prefix, sorted in descending order.
     * For example, if the key is "Too", you would return any value that contains "Tool", "Too", "Tooth", "Toodle", etc.
     * Search is CASE SENSITIVE.
     * @param prefix
     * @param comparator used to sort values
     * @return a List of all matching Values containing the given prefix, in descending order
     */
    public List < Value > getAllWithPrefixSorted(String prefix, Comparator < Value > comparator) {
        Set < Value > q = new HashSet < > ();
        collect(get(this.root, prefix, 0), prefix, q);
        ArrayList < Value > temp = new ArrayList < > (q);
        Collections.sort(temp, comparator);
        return temp;
    }

    /**
     * Delete the subtree rooted at the last character of the prefix.
     * Search is CASE SENSITIVE.
     * @param prefix
     * @return a Set of all Values that were deleted.
     */
    public Set < Value > deleteAllWithPrefix(String prefix) {
        Set < Value > q = new HashSet < > ();
        collect(get(this.root, prefix, 0), prefix, q); //collecting all the values we will delete
        this.root = deleteAllWithPrefix(this.root, prefix, 0); //Delete the subtree rooted at the last character of the prefix.
        return q;
    }
    //Delete the subtree rooted at the last character of the prefix.
    private Node deleteAllWithPrefix(Node x, String pre, int d) {
        if (x == null || d == pre.length()) {
            return null;
        } else {
            char c = pre.charAt(d);
            x.links[c] = deleteAllWithPrefix(x.links[c], pre, d + 1);
        }
        return x;
    }
    //collecting all the values we will delete
    private void collect(Node x, String pre, Set < Value > q) {
        if (x == null) {
            return;
        }
        if (x.val != null) {
            q.addAll(x.val);
        }
        for (char c = 0; c < alphabetSize; c++) {
            if (x.links[c] != null) {
                collect(x.links[c], pre + c, q);
            }
        }
    }

    /**
     * Delete all values from the node of the given key (do not remove the values from other nodes in the Trie)
     * @param key
     * @return a Set of all Values that were deleted.
     */
    public Set < Value > deleteAll(String key) {
        if (get(key) == null) {
            Set < Value > blank = new HashSet < > ();
            return blank;
        }
        Set < Value > temp = get(key);
        this.root = deleteAll(this.root, key, 0);
        return temp;
    }

    private Node deleteAll(Node x, String key, int d) {
        if (x == null) {
            return null;
        }
        if (d == key.length()) {
            Set < Value > blank = new HashSet < > ();
            x.val = (HashSet) blank;
        } else {
            char c = key.charAt(d);
            x.links[c] = deleteAll(x.links[c], key, d + 1);
        }
        if (x.val != null) {
            return x;
        }
        for (char sub = 0; sub < alphabetSize; sub++) {
            if (x.links[sub] != null) {
                return x;
            }
        }
        return null;
    }

    /**
     * Remove the given value from the node of the given key (do not remove the value from other nodes in the Trie)
     * @param key
     * @param val
     * @return the value which was deleted. If the key did not contain the given value, return null.
     */
    public Value delete(String key, Value val) {
        Set < Value > temp = get(key);
        if (temp == null) {
            return null;
        }
        if (!temp.contains(val)) {
            return null;
        }
        if (temp.size() == 1) {
            deleteAll(key);
            return val;
        }
        Node del = get(this.root, key, 0);
        del.val.remove(val);
        return val;
    }
}