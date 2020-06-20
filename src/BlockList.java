import java.util.ArrayList;

/**
 * @ClassName BlockList
 * @Description 阻塞队列
 * @Author AICHI
 * @Date 2020/6/2 23:40
 * @Version 1.0
 */
public class BlockList {

    private ArrayList<Pcb> list = new ArrayList<>();    //阻塞队列

    /**
     *      唤醒阻塞队列进程
     * @param currentResources
     */
    public void wakeUpBlockedProcesses(BlockList blockList, ReadyList readyList,  Resources currentResources) {

        Resource resource;

        resource = currentResources.getR1();
        wakeUp(blockList, readyList, resource);

        resource = currentResources.getR2();
        wakeUp(blockList, readyList, resource);

        resource = currentResources.getR3();
        wakeUp(blockList, readyList, resource);

        resource = currentResources.getR4();
        wakeUp(blockList, readyList, resource);

    }

    private void wakeUp(BlockList blockList, ReadyList readyList, Resource resource) {
        //判断阻塞队列是否为空 且 阻塞队列首部进程需求的资源数小于等于可用资源数量
        if (!resource.getWaitingList().isEmpty() && resource.getStatus() >= resource.getWaitingList().get(0).getNum()) {
            int num = resource.getWaitingList().get(0).getNum();    //需要资源数
            resource.getWaitingList().get(0).getPcb().setStatus(Status.ready);  //设置为就绪态
            resource.getWaitingList().get(0).getPcb().getOther_resources().getR1().setStatus(1, num);   //设置占用资源
            resource.setStatus(0, num);     //现存资源数 减
            blockList.getList().remove(resource.getWaitingList().get(0).getPcb());      //移出阻塞队列
            readyList.addToList(resource.getWaitingList().get(0).getPcb(), readyList);
            resource.getWaitingList().remove(0);    //移出阻塞等待队列
        }
    }

    /**
     *   从阻塞队列删除该进程
     * @param blockList
     * @param pcb
     * @return
     */
    public Boolean remove(BlockList blockList, Pcb pcb) {
        if (pcb == null)
            return false;
        for (Pcb p:blockList.getList()) {   //从阻塞队列中删除
            if(p.getPID() == pcb.getPID()) {
                blockList.getList().remove(p);
                break;
            }
        }
        return true;
    }

    public ArrayList<Pcb> getList() {
        return list;
    }

    public void setList(ArrayList<Pcb> list) {
        this.list = list;
    }
}
