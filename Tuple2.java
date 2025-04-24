public class Tuple2 <T1, T2> {
    protected final T1 item1;
    protected final T2 item2;
    
    public Tuple2(T1 i1, T2 i2) {
        item1 = i1;
        item2 = i2;
    }
    
    public T1 GetItem1() {
        return item1;
    }
    
    public T2 GetItem2() {
        return item2;
    }
}
