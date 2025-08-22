package ArrayList;

import ArrayList.internal.ArrayListEx;

/**
 * Factory class to hide implementation details and keep a stable API.
 */
public final class Lists {
    private Lists() {}

    public static <T> MyList<T> arrayList() {
        return new ArrayListEx<>();
    }

    public static <T> MyList<T> arrayList(int capacity) {
        return new ArrayListEx<>(capacity);
    }
}
