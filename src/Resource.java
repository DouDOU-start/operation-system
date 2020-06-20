import java.util.ArrayList;

/**
 * @ClassName Resource
 * @Description TODO
 * @Author AICHI
 * @Date 2020/6/3 0:48
 * @Version 1.0
 */
public class Resource {

    private String RID;
    private int status;
    private ArrayList<WaitingList> waitingList = new ArrayList<>();

    public Resource(String rid) {
        RID = rid;
    }

    public String getRID() {
        return RID;
    }

    public void setRID(String RID) {
        this.RID = RID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setStatus(int operate, int num) {
        switch (operate) {
            case 1:
                this.status += num;
                break;
            case 0:
                this.status -= num;
                break;
            default:
                break;
        }
    }

    public ArrayList<WaitingList> getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(ArrayList<BlockList> waitingList) {
        waitingList = waitingList;
    }
}
