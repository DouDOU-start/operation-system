import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * @ClassName Init
 * @Description 初始化
 * @Author AICHI
 * @Date 2020/6/16 11:23
 * @Version 1.0
 */
public class Init {

    private static Pcb currentPcb;      //当前进程
    private static ArrayList<Pcb> existingPcb;   //存在的进程

    private static ReadyList readyList;      //就绪队列
    private static BlockList blockList;      //阻塞队列

    private static Resources currentResources;     //当前资源

    /**
     * 实例化直接初始化
     */
    public Init() {

        existingPcb = new ArrayList<>();
        readyList = new ReadyList();
        blockList = new BlockList();

        //现有资源初始化
        currentResources = new Resources();
        currentResources.getR1().setStatus(1);
        currentResources.getR2().setStatus(2);
        currentResources.getR3().setStatus(3);
        currentResources.getR4().setStatus(4);

        init();     //创建初始进程

    }

    /**
     * 下一个流程调度
     */
    public void Scheduler() {

        Scanner sc = new Scanner(System.in);

        while (true) {
            String str = sc.nextLine();  //获取控制台输入
            String[] s = str.split(" ");  //获取控制台输入
            if (s.length < 1 || s.length > 3)
                System.out.println("输入有误！重来！");
            else {
                switch (s[0]) {
                    case "cr":          //创建进程
                        if(!isInteger(s[2])) {
                            System.out.println("输入有误！重来！");
                            Scheduler();
                        }
                        cr(s[1], Integer.parseInt(s[2]));
                        break;
                    case "to":          //超时
                        to();
                        break;
                    case "req":         //请求资源
                        if(!isInteger(s[2])) {
                            System.out.println("输入有误！重来！");
                            Scheduler();
                        }
                        req(s[1], Integer.parseInt(s[2]));
                        System.out.println("Process " +  currentPcb.getPID() + " is running");
                        break;
                    case "rel":         //释放资源
                        if(!isInteger(s[2])) {
                            System.out.println("输入有误！重来！");
                            Scheduler();
                        }
                        rel(s[1], Integer.parseInt(s[2]));
                        break;
                    case "de":      //撤销进程
                        de(s[1]);
                        blockList.wakeUpBlockedProcesses(blockList, readyList, currentResources);   //唤醒阻塞进程
                        System.out.println("Process " +  currentPcb.getPID() + " is running");
                        break;
                    case "lps":         //打印输出所有进程信息
                        listAllProcessesAndStatus();
                        break;
                    case "lrs":         //打印输出现在资源状态
                        listAllResourcesAndStatus();
                        break;
                    case "help":        //帮助
                        help();
                        break;
                    default:
                        System.out.println("输入有误！重来！");
                        break;
                }
            }
        }

    }

    /**
     * 帮助
     */
    public void help() {
        System.out.println("-----------------------------------------------------");
        System.out.println("-cr" + "     " + "create <pid> <priority>");
        System.out.println("-req" + "    " + "request <resource> <number>");
        System.out.println("-rel" + "    " + "release <resource> <number>");
        System.out.println("-de" + "     " + "delete <pid>");
        System.out.println("-to" + "     " + "timeout");
        System.out.println("-lps" + "    " + "list all processes and their status");
        System.out.println("-lrs" + "    " + "list all resources and their status");
        System.out.println("------------------------------------------------------");
        System.out.println("");
    }

    /**
     *      创建初始进程
     * @return  当前进程即初始进程
     */
    public void init() {
        Pcb pcb = new Pcb("init", Priority.Init);   //创建初始进程
        existingPcb.add(pcb);   //添加存在的进程
        pcb.setStatus(Status.running);      //转换为运行态
        System.out.println("Process init is running");
        currentPcb = pcb;   //设置当前进程

        Scheduler();

    }

    /**
     *      创建新进程函数
     * @param PID           创建新进程的ID
     * @param priority      创建新进程的优先级
     * @return              返回当前进程
     */
    public Boolean cr(String PID, int priority) {

        if (priority < 0 || priority > 2) {     //进程优先级限制
            System.out.println("Process creation failed!");
            System.out.println("Priority can only be 0, 1, 2");
            Scheduler();
        }

        for (Pcb p : existingPcb) {     //判断进程是否已存在
            if (p.getPID().equals(PID)) {
                System.out.println("Process is exist.");
                return false;
            }
        }

        Pcb pcb = new Pcb(PID, priority);   //创建新进程
        existingPcb.add(pcb);   //添加存在的进程

        if (currentPcb == null) {
            pcb.setStatus(Status.running);      //转换为运行态
            currentPcb = pcb;   //设置当前进程
            System.out.println("Process " +  currentPcb.getPID() + " is running");
        } else {
            currentPcb.getCreationTree().addChildren(pcb);  //当前进程创建树添加子进程
            pcb.getCreationTree().setParent(currentPcb);    //子进程创建树添加父进程

            if (pcb.getPriority() <= currentPcb.getPriority()) {     //比较新进程和当前进程优先级，如果新进程优先级低于当前进程优先级则新进程进入就绪队列
                pcb.setStatus(Status.ready);    //新进程设置为就绪态
                readyList.addToList(pcb, readyList);    //新进程放入就绪队列
            } else {    //否则新进程转换为运行态，当前进程转换为就绪态并进入就绪队列
                pcb.setStatus(Status.running);
                currentPcb.setStatus(Status.ready);
                readyList.addToList(currentPcb, readyList);     //当前进程放入就绪队列
            }

            currentPcb = pcb.getPriority() > currentPcb.getPriority() ? pcb : currentPcb;   //根据优先级判断当前进程
            System.out.println("Process " +  currentPcb.getPID() + " is running");
            Init.currentPcb = currentPcb;   //设置当前进程
        }

//        Scheduler();

        return true;

    }

    /**
     * 超时（时间片用完）
     */
    public void to() {
        
        Pcb getPcb = null;

        readyList.addToList(currentPcb, readyList);     //超时进程放入就绪队列

        getPcb = readyList.readyToRun(getPcb, readyList, this);   //获取下一个要运行的进程

        
        currentPcb = getPcb;    //下一个要运行的进程设置为当前进程

        System.out.println("Process " +  currentPcb.getPID() + " is running");

//        Scheduler();

    }

    /**
     *      请求资源
     * @param rid   请求资源id
     * @param num   请求资源数量
     * @return  请求资源是否成功
     */
    public Boolean req(String rid, int num) {

        int status;

        switch (rid) {
            case "R1":
                if(currentResources.getR1().getStatus() < num) {  //判断现有资源数量是否大于请求资源数量，否则当前进程阻塞
                    currentResources.getR1().getWaitingList().add(new WaitingList(currentPcb, num));    //当前进程加入资源等待队列
                    currentPcb.setStatus(Status.block);     //当前进程设置为阻塞状态
                    blockList.getList().add(currentPcb);    //当前进程进入阻塞队列
                    currentPcb = readyList.readyToRun(currentPcb, readyList, this);    //下一个要运行的进程设置为当前进程
                    if (currentPcb == null) {
                        this.Scheduler();
                    }
                    return false;
                }
                status = currentResources.getR1().getStatus();  //现在资源数量
                currentResources.getR1().setStatus(status - num);   //设置资源被请求后 资源的数量
                currentPcb.getOther_resources().getR1().setStatus(num);     //设置进程占用资源数量
                return true;
            case "R2":
                if(currentResources.getR2().getStatus() < num) {
                    currentResources.getR2().getWaitingList().add(new WaitingList(currentPcb, num));
                    currentPcb.setStatus(Status.block);
                    blockList.getList().add(currentPcb);
                    currentPcb = readyList.readyToRun(currentPcb, readyList, this);
                    if (currentPcb == null) {
                        this.Scheduler();
                    }
                    return false;
                }
                status = currentResources.getR2().getStatus();
                currentResources.getR2().setStatus(status - num);
                currentPcb.getOther_resources().getR2().setStatus(num);
                return true;
            case "R3":
                if(currentResources.getR3().getStatus() < num) {
                    currentResources.getR3().getWaitingList().add(new WaitingList(currentPcb, num));
                    currentPcb.setStatus(Status.block);
                    blockList.getList().add(currentPcb);
                    currentPcb = readyList.readyToRun(currentPcb, readyList, this);
                    if (currentPcb == null) {
                        this.Scheduler();
                    }
                    return false;
                }
                status = currentResources.getR3().getStatus();
                currentResources.getR3().setStatus(status - num);
                currentPcb.getOther_resources().getR3().setStatus(num);
                return true;
            case "R4":
                if(currentResources.getR4().getStatus() < num) {
                    currentResources.getR4().getWaitingList().add(new WaitingList(currentPcb, num));
                    currentPcb.setStatus(Status.block);
                    blockList.getList().add(currentPcb);
                    currentPcb = readyList.readyToRun(currentPcb, readyList, this);
                    if (currentPcb == null) {
                        this.Scheduler();
                    }
                    return false;
                }
                status = currentResources.getR4().getStatus();
                currentResources.getR4().setStatus(status - num);
                currentPcb.getOther_resources().getR4().setStatus(num);
                return true;
            default:
                return false;
        }
    }

    /**
     * 打印输出所以进程的信息
     */
    public void listAllProcessesAndStatus() {

        for (Pcb pcb : existingPcb) {
            pcb.printPcb();
        }

        System.out.println("---------------------");
        System.out.println("");

        Scheduler();
    }

    /**
     * 打印输出所有资源状态
     */
    public void listAllResourcesAndStatus() {
        System.out.println("-----------------------------");
        System.out.println("resources" + "\t" + "status" + "\t" + "waitingList");

        System.out.print("R1" + "\t\t\t" + "*" + currentResources.getR1().getStatus() + "\t\t");
        for (WaitingList waitingList : currentResources.getR1().getWaitingList()) {
            System.out.print("-" + waitingList.getPcb().getPID() + "/*" + waitingList.getNum() + "  ");
        }
        System.out.println("");

        System.out.print("R2" + "\t\t\t" + "*" + currentResources.getR2().getStatus() + "\t\t");
        for (WaitingList waitingList : currentResources.getR2().getWaitingList()) {
            System.out.print("-" + waitingList.getPcb().getPID() + "/*" + waitingList.getNum() + "  ");
        }
        System.out.println("");

        System.out.print("R3" + "\t\t\t" + "*" + currentResources.getR3().getStatus() + "\t\t");
        for (WaitingList waitingList : currentResources.getR3().getWaitingList()) {
            System.out.print("-" + waitingList.getPcb().getPID() + "/*" + waitingList.getNum() + "  ");
        }
        System.out.println("");

        System.out.print("R4" + "\t\t\t" + "*" + currentResources.getR4().getStatus() + "\t\t");
        for (WaitingList waitingList : currentResources.getR4().getWaitingList()) {
            System.out.print("-" + waitingList.getPcb().getPID() + "/*" + waitingList.getNum() + "  ");
        }
        System.out.println("");

        System.out.println("-----------------------------");
    }

    /**
     *     请求释放资源
     * @param rid   释放资源id
     * @param num   释放资源数量
     */
    public void rel(String rid, int num) {

        int status;

        switch (rid) {
            case "R1":
                if(currentPcb.getOther_resources().getR1().getStatus() == 0) {  //判断当前进程是否占用该资源
                    System.out.println("The current process is not occupying resource R1");
                } else {
                    if (currentPcb.getOther_resources().getR1().getStatus() >= num) {
                        currentPcb.getOther_resources().getR1().setStatus(0, num);    //释放占用资源
                        currentResources.getR1().setStatus(1, num);     //现存资源数量增加
                    } else {
                        status = currentPcb.getOther_resources().getR1().getStatus();
                        currentPcb.getOther_resources().getR1().setStatus(0);    //释放占用资源
                        currentResources.getR1().setStatus(1, status);     //现存资源数量增加
                    }
                    System.out.println("Successfully released resources");
                }
                break;
            case "R2":
                if(currentPcb.getOther_resources().getR2().getStatus() == 0) {
                    System.out.println("The current process is not occupying resource R2");
                } else {
                    if (currentPcb.getOther_resources().getR2().getStatus() >= num) {
                        currentPcb.getOther_resources().getR2().setStatus(0, num);
                        currentResources.getR2().setStatus(1, num);
                    } else {
                        status = currentPcb.getOther_resources().getR2().getStatus();
                        currentPcb.getOther_resources().getR2().setStatus(0);
                        currentResources.getR2().setStatus(1, status);
                    }
                    System.out.println("Successfully released resources");
                }
                break;
            case "R3":
                if(currentPcb.getOther_resources().getR3().getStatus() == 0) {
                    System.out.println("The current process is not occupying resource R3");
                } else {
                    if (currentPcb.getOther_resources().getR3().getStatus() >= num) {
                        currentPcb.getOther_resources().getR3().setStatus(0, num);
                        currentResources.getR3().setStatus(1, num);
                    } else {
                        status = currentPcb.getOther_resources().getR3().getStatus();
                        currentPcb.getOther_resources().getR3().setStatus(0);
                        currentResources.getR3().setStatus(1, status);
                    }
                    System.out.println("Successfully released resources");
                }
                break;
            case "R4":
                if(currentPcb.getOther_resources().getR4().getStatus() == 0) {
                    System.out.println("The current process is not occupying resource R4");
                } else {
                    if (currentPcb.getOther_resources().getR4().getStatus() >= num) {
                        currentPcb.getOther_resources().getR4().setStatus(0, num);
                        currentResources.getR4().setStatus(1, num);
                    } else {
                        status = currentPcb.getOther_resources().getR4().getStatus();
                        currentPcb.getOther_resources().getR4().setStatus(0);
                        currentResources.getR4().setStatus(1, status);
                    }
                    System.out.println("Successfully released resources");
                }
                break;
            default:
        }

        blockList.wakeUpBlockedProcesses(blockList, readyList, currentResources);  //唤醒阻塞队列进程

    }

    /**
     *      释放该进程所有占用资源
     * @param pcb   进程
     */
    public void relAll(Pcb pcb) {

        int status;

        if (pcb.getOther_resources().getR1().getStatus() != 0) {    // 释放所有R1
            status = pcb.getOther_resources().getR1().getStatus();
            pcb.getOther_resources().getR1().setStatus(0);
            currentResources.getR1().setStatus(1, status);
        }

        if (pcb.getOther_resources().getR2().getStatus() != 0) {    // 释放所有R2
            status = pcb.getOther_resources().getR2().getStatus();
            pcb.getOther_resources().getR2().setStatus(0);
            currentResources.getR2().setStatus(1, status);
        }

        if (pcb.getOther_resources().getR3().getStatus() != 0) {    // 释放所有R3
            status = pcb.getOther_resources().getR3().getStatus();
            pcb.getOther_resources().getR3().setStatus(0);
            currentResources.getR3().setStatus(1, status);
        }

        if (pcb.getOther_resources().getR4().getStatus() != 0) {    // 释放所有R4
            status = pcb.getOther_resources().getR4().getStatus();
            pcb.getOther_resources().getR4().setStatus(0);
            currentResources.getR4().setStatus(1, status);
        }

        blockList.wakeUpBlockedProcesses(blockList, readyList, currentResources);   //唤醒阻塞队列进程

    }

    /**
     *  撤销进程
     * @param pid   撤销进程 id
     */
    public Boolean de(String pid) {

        Pcb pcb = null;

        for (Pcb p:existingPcb) {       //从存在进程队列中删除
            if (p.getPID().equals(pid)) {
                pcb = p;
                existingPcb.remove(p);
                break;
            }
        }

        if (pcb == null) {
            System.out.println("The process to be deleted does not exist.");
            return false;
        }

        if (currentPcb.getPID().equals(pcb.getPID())) {  //判断要删除的进程是否是当前运行进程
            currentPcb = readyList.readyToRun(currentPcb, readyList, this);   //获取下一个要运行的进程并设置为当前进程
        } else {
            blockList.remove(blockList, pcb);   //从阻塞队列中删除
            readyList.remove(readyList, pcb);   //从就绪队列中删除
        }

        relAll(pcb);    //释放所有占用的资源

//        if (pcb.getCreationTree().getChildren() == null)    //判断是否有子进程
//            return true;

        for (Pcb p:pcb.getCreationTree().getChildren()) {   //遍历撤销子进程，如果子进程为空直接返回true
            de(p.getPID());     //撤销子进程
        }

        return true;

    }

    /**
     *  判断字符串是否为整数
     * @param str
     * @return  返回布偶值
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
}
