

public interface Moveable {
    /**
     * default를 사용하면 인터페이스도 몸체가 있는 메서드를 만들 수 있다.
     * 다중 상속이 안되는 것이 많기 때문에...
     * 그래서 adapter패턴 보다는 default를 사용하는 것이 좋다.
     */
    public abstract void left();
    public abstract void right();
    public abstract void up();
    default public void down() {}; //여기서 직접 구현해서 다른 곳에서 굳이 구현X
    default public void attack() {}; //player가 어택하면 버블은 움직이기만 함

    default public void attack(Enemy enemy) {}; //적군리스트 가두기
}

