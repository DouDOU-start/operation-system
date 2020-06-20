import java.util.ArrayList;

/**
 * @ClassName CreationTree
 * @Description 创建树
 * @Author AICHI
 * @Date 2020/6/2 17:23
 * @Version 1.0
 */
public class CreationTree {

    private Pcb Parent;     //父进程
    private ArrayList<Pcb> Children = new ArrayList<>();    //子进程

    public Pcb getParent() {
        return Parent;
    }

    public void setParent(Pcb parent) {
        Parent = parent;
    }

    public ArrayList<Pcb> getChildren() {
        return Children;
    }

    public void setChildren(ArrayList<Pcb> children) {
        Children = children;
    }

    public void addChildren(Pcb child) {
        Children.add(child);
    }
}
