package edu.yu.cs.com1320.project.impl;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

class TrieImplTest {
    @Test
    void TestTrie() {
        TrieImpl<Integer> x = new TrieImpl<>();
        x.put("Avi", 1);
        x.put("Avi", 9);
        x.put("da", 5);
    }
    @Test
    void TestTriedelete() {
        TrieImpl<Integer> x = new TrieImpl<>();
        x.put("A", 3);
        x.put("Avi", 1);
        x.put("Avi", 9);
        x.put("da", 5);
        Integer y = x.delete("Avi", 1);
        System.out.println(y);
        Integer z = x.delete("Avi", 1);
        System.out.println(z);
    }

    @Test
    void TestTriedeleteAll() {
        TrieImpl<Integer> x = new TrieImpl<>();
        x.put("Avi", 1);
        x.put("Avi", 9);
        x.put("Avit",9);
        x.put("da", 5);
        x.deleteAll("Avi");
        Set<Integer> y = x.deleteAll("Avit");
        System.out.println(y);
        Set<Integer> z = x.deleteAll("Avit");
        System.out.println(z);
    }
    @Test
    void TestTriedeleteAllpr() {
        TrieImpl<Integer> x = new TrieImpl<>();
        x.put("A", 3);
        x.put("Avi", 1);
        x.put("Avi", 9);
        x.put("da", 5);
        Set<Integer> y = x.deleteAllWithPrefix("A");
        System.out.println(y);
        Set<Integer> z = x.deleteAllWithPrefix("A");
        System.out.println(z);

    }

    @Test
    void TestTriedeleteAll2() {
        TrieImpl<Integer> x = new TrieImpl<>();
        x.put("Avi", 1);
        x.put("Avi", 9);
        x.put("da", 5);
        Set<Integer> y = x.deleteAll("Avi");
        System.out.println(y);
        Set<Integer> z = x.deleteAll("Avi");
        System.out.println(z);
    }
    @Test
    void TestTriegetallprefixsort() {
        class Strings{
            private String x;
            private int z;
            public Strings(String t, int y){
                this.x = t;
                this.z = y;
            }

            public String getstr() {
                return this.x;
            }

            public int getint(){
                return this.z;
            }
            @Override
            public String toString(){
                return this.x + " " + this.z;
            }
        }
        TrieImpl<Strings> x = new TrieImpl<>();
        Strings one = new Strings("one",1);
        Strings two = new Strings("two",2);
        Strings three = new Strings("three",3);
        x.put("avi",one);
        x.put("avit",two);
        x.put("avitz",three);
        Comparator<Strings> comparator = new Comparator<>() {
            @Override
            public int compare(Strings q, Strings r) {
                return r.getint() - q.getint();
            }
        };
        List<Strings> y = x.getAllWithPrefixSorted("",comparator);
        System.out.println(y);
    }
    @Test
    void TestTriegetallstringsort() {
        class Strings{
            private String x;
            private int z;
            public Strings(String t, int y){
                this.x = t;
                this.z = y;
            }

            public String getstr() {
                return this.x;
            }

            public int getint(){
                return this.z;
            }
            @Override
            public String toString(){
                return this.x + " " + this.z;
            }
        }
        TrieImpl<Strings> x = new TrieImpl<>();
        Strings one = new Strings("one",1);
        Strings two = new Strings("two",2);
        Strings three = new Strings("three",3);
        x.put("avi",one);
        x.put("avi",two);
        x.put("avitz",three);
        Comparator<Strings> comparator = new Comparator<>() {
            @Override
            public int compare(Strings q, Strings r) {
                return r.getint() - q.getint();
            }
        };
        List<Strings> y = x.getAllSorted("avi",comparator);
        System.out.println(y);
    }

}