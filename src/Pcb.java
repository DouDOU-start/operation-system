/**
 * @ClassName Pcb
 * @Description 进程
 * @Author AICHI
 * @Date 2020/6/2 17:11
 * @Version 1.0
 */
public class Pcb {

    private String PID;     //进程ID
    private Resources other_resources = new Resources();      //占用的资源
    private int status = Status.None;       //进程状态
    private CreationTree creationTree = new CreationTree();     //进程创建树
    private int priority;   //进程优先级


    public Pcb(String PID, int priority) {
        this.PID = PID;
        this.priority = priority;
    }

    public void printPcb() {
        System.out.println("");
        System.out.println("---------------------");
        System.out.println("PID：" + PID);
        System.out.println("priority：" + priority);
        switch (status) {
            case 1:
                System.out.println("Status：ready");
                break;
            case -1:
                System.out.println("Status：block");
                break;
            case 2:
                System.out.println("Status：running");
                break;
        }
        System.out.println("other R1：*" + other_resources.getR1().getStatus());
        System.out.println("other R2：*" + other_resources.getR2().getStatus());
        System.out.println("other R3：*" + other_resources.getR3().getStatus());
        System.out.println("other R4：*" + other_resources.getR4().getStatus());
        if (creationTree.getParent() == null)
            System.out.println("parent PID：null");
        else
            System.out.println("parent PID：" + creationTree.getParent().getPID());
        System.out.print("children PID：");
        if (creationTree.getChildren().size() == 0)
            System.out.println("null");
        else {
            for (Pcb pcb : creationTree.getChildren()) {
                System.out.print(pcb.getPID() + "  ");
            }
        }
    }

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public Resources getOther_resources() {
        return other_resources;
    }

    public void setOther_resources(Resources other_resources) {
        this.other_resources = other_resources;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public CreationTree getCreationTree() {
        return creationTree;
    }

    public void setCreationTree(CreationTree creationTree) {
        this.creationTree = creationTree;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
