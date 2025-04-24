public class Tuple3 <T1, T2, T3> extends Tuple2<T1, T2> {
    protected final T3 item3;

    public Tuple3(T1 i1, T2 i2, T3 i3) {
        super(i1, i2);
        item3 = i3;
    }

    public T3 GetItem3() {
        return item3;
    }
}
