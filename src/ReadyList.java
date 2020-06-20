import java.util.ArrayList;

/**
 * @ClassName ReadyList
 * @Description 就绪队列
 * @Author AICHI
 * @Date 2020/6/2 17:26
 * @Version 1.0
 */
public class ReadyList {

    private ArrayList<Pcb> systemList = new ArrayList<>();      //系统优先级就绪队列
    private ArrayList<Pcb> userList = new ArrayList<>();        //用户优先级就绪队列
    private ArrayList<Pcb> initList = new ArrayList<>();        //初始优先级就绪队列

    /**
     * 加入就绪队列
     * @param pcb   进程
     */
    public void addToList(Pcb pcb, ReadyList readyList) {

        pcb.setStatus(Status.ready); //进程设置为就绪态

        switch (pcb.getPriority()) {       //根据进程优先级放入就绪队列
            case Priority.Init:
                readyList.getInitList().add(pcb);
                break;
            case Priority.User:
                readyList.getUserList().add(pcb);
                break;
            case Priority.System:
                readyList.getSystemList().add(pcb);
                break;
            default:
                break;
        }
    }

    /**
     *      从就绪队列中找到优先级最高的进程转换为运行态
     * @param pcb    进程
     * @param readyList     就绪队列
     */
    public Pcb readyToRun(Pcb pcb, ReadyList readyList,  Init init) {

        if (!readyList.getSystemList().isEmpty()) {
            pcb = readyList.getSystemList().get(0);
            readyList.getSystemList().remove(0);
        } else if (!readyList.getUserList().isEmpty()) {
            pcb = readyList.getUserList().get(0);
            readyList.getUserList().remove(0);
        } else if (!readyList.getInitList().isEmpty()) {
            pcb = readyList.getInitList().get(0);
            readyList.getInitList().remove(0);
        } else {
            pcb = null;
            System.out.println("No Process can run.");
        }

        if (pcb != null)
            pcb.setStatus(Status.running);   //获取到的进程设置为运行态

        return pcb;

    }

    /**
     *  从就绪队列删除该进程
     * @param readyList
     * @param pcb
     * @return
     */
    public Boolean remove(ReadyList readyList, Pcb pcb) {
        if (pcb == null)
            return false;
        switch (pcb.getPriority()) {
            case Priority.Init:
                for (Pcb p:readyList.getInitList()) {
                    if (p.getPID() == pcb.getPID()) {
                        readyList.getInitList().remove(p);
                        break;
                    }
                }
                break;
            case Priority.User:
                for (Pcb p:readyList.getUserList()) {
                    if (p.getPID() == pcb.getPID()) {
                        readyList.getUserList().remove(p);
                        break;
                    }
                }
                break;
            case Priority.System:
                for (Pcb p:readyList.getSystemList()) {
                    if (p.getPID() == pcb.getPID()) {
                        readyList.getSystemList().remove(p);
                        break;
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    public ArrayList<Pcb> getSystemList() {
        return systemList;
    }

    public void setSystemList(ArrayList<Pcb> systemList) {
        this.systemList = systemList;
    }

    public ArrayList<Pcb> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<Pcb> userList) {
        this.userList = userList;
    }

    public ArrayList<Pcb> getInitList() {
        return initList;
    }

    public void setInitList(ArrayList<Pcb> initList) {
        this.initList = initList;
    }
}
