public class Tuple4<T1, T2, T3, T4> extends Tuple3<T1, T2, T3> {
    protected T4 item4;

    public Tuple4(T1 i1, T2 i2, T3 i3, T4 i4) {
        super(i1, i2, i3);
        item4 = i4;
    }

    public T4 GetItem4() {
        return item4;
    }
}
