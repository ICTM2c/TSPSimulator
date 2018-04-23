package Simulators;//Simulators.Permute.java
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

class Permute<E> {

    public Set<List<E>> listPermutations(List<E> lst) {
        Set<List<E>> perms = new HashSet<List<E>>();
        permute(lst, 0, perms);
        return perms;
    }

    private void permute(List<E> lst, int start, Set<List<E>> perms) {
        if (start >= lst.size()) {
            // nothing left to permute 
            perms.add(lst);
        }

        for (int i = start; i < lst.size(); i++) {
            // swap elements at locations start and i
            swap(lst, start, i);
            List<E> newList = new ArrayList<>(lst);
            permute(newList, start + 1, perms);
            swap(lst, start, i);
        }
    }

    private void swap(List<E> lst, int x, int y) {
        E temp = lst.get(x);
        lst.set(x, lst.get(y));
        lst.set(y, temp);
    }
}