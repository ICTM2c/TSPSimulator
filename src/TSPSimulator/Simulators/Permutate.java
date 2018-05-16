package TSPSimulator.Simulators;//TSPSimulatorMain.Simulators.Permute.java

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

class Permute<E> {

    public void listPermutations(List<E> lst, Consumer<List<E>> callback) {
        permute(lst, 0, callback);
    }

    private void permute(List<E> lst, int start, Consumer<List<E>> callback) {
        if (start >= lst.size()) {
            // nothing left to permute
            callback.accept(lst);
            return;
        }

        for (int i = start; i < lst.size(); i++) {
            // swap elements at locations start and i
            swap(lst, start, i);
            List<E> newList = new ArrayList<>(lst);
            permute(newList, start + 1, callback);
            swap(lst, start, i);
        }
    }

    private void swap(List<E> lst, int x, int y) {
        E temp = lst.get(x);
        lst.set(x, lst.get(y));
        lst.set(y, temp);
    }
}