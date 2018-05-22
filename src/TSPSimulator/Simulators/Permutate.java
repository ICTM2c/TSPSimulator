package TSPSimulator.Simulators;//TSPSimulatorMain.Simulators.Permute.java

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import TSPSimulator.Util;

/**
 * Modified version of https://stackoverflow.com/questions/25820073/how-can-i-permute-a-generic-list-in-java.
 * Instead of returning a list containing all the possible combinations I provide a callback which is called for every permutation.
 * @param <E>
 */
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
            Util.swap(lst, start, i);
            List<E> newList = new ArrayList<>(lst);
            permute(newList, start + 1, callback);
            Util.swap(lst, start, i);
        }
    }
}