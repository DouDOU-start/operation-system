/**
 * @ClassName WaitingList
 * @Description TODO
 * @Author AICHI
 * @Date 2020/6/18 12:33
 * @Version 1.0
 */
public class WaitingList {
    private Pcb pcb;
    private int num;

    public WaitingList(Pcb pcb, int num) {
        this.pcb = pcb;
        this.num = num;
    }

    public Pcb getPcb() {
        return pcb;
    }

    public void setPcb(Pcb pcb) {
        this.pcb = pcb;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
