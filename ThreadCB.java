package osp.Threads;
import java.util.Vector;
import java.util.Enumeration;
import osp.Utilities.*;
import osp.IFLModules.*;
import osp.Tasks.*;
import osp.EventEngine.*;
import osp.Hardware.*;
import osp.Devices.*;
import osp.Memory.*;
import osp.Resources.*;
import java.util.*;


/*
   This class is responsible for actions related to threads, including
   creating, killing, dispatching, resuming, and suspending threads.

   @OSPProject Threads
*/
public class ThreadCB extends IflThreadCB implements Comparable<ThreadCB>
{   

    private static HashMap<Integer, List<ThreadCB>> taskTable; //TaskTable has linked list which acts as the ready queue
    private long entryTimeInQueue;


    /*
       The thread constructor. Must call 

       	   super();

       as its first statement.

       @OSPProject Threads
    */

    

    public ThreadCB()
    {
        // your code goes here

        super();

    }

    /*
       This method will be called once at the beginning of the
       simulation. The student can set up static variables here.
       
       @OSPProject Threads
    */
    public static void init()
    {
        // your code goes here
        taskTable = new HashMap<Integer, List<ThreadCB>>();

    }

    /* 
        Sets up a new thread and adds it to the given task. 
        The method must set the ready status 
        and attempt to add thread to task. If the latter fails 
        because there are already too many threads in this task, 
        so does this method, otherwise, the thread is appended 
        to the ready queue and dispatch() is called.

	The priority of the thread can be set using the getPriority/setPriority
	methods. However, OSP itself doesn't care what the actual value of
	the priority is. These methods are just provided in case priority
	scheduling is required.

	@return thread or null

        @OSPProject Threads
    */
    static public ThreadCB do_create(TaskCB task)
    {

        ThreadCB newThread = null;

        if(task == null || task.getThreadCount() > IflThreadCB.MaxThreadsPerTask){
            ThreadCB.dispatch();
            return null;
        }

        newThread = new ThreadCB();

        if(task.addThread(newThread) == FAILURE){
            ThreadCB.dispatch();
            return null;
        }
        newThread.setTask(task);

        
        newThread.setStatus(ThreadReady); //Set status to thread ready

        /*Put the thread in the TaskTable
        TaskTable has linked list which acts as the ready queue; the head of the queue has the highest priority thread in the group 
        i.e. in that task*/
        List<ThreadCB> threadList = taskTable.get(task.getID());
        if(threadList == null)
            taskTable.put(task.getID(), threadList = new ArrayList<ThreadCB>());
        threadList.add(newThread);

        //Store the time when a thread was inserted in the ready queue
        newThread.entryTimeInQueue = HClock.get();

        //Set priority of the newly created thread
        double priority = (1.5*(HClock.get()-newThread.getEntryTimeInQueue()))-(newThread.getTimeOnCPU())-(0.3*getAllCPUTime(task));
        newThread.setPriority((int)priority);

        //After assigning priority put the thread in the apporiate place in the queue
        Collections.sort(threadList, Collections.reverseOrder());

        ThreadCB.dispatch();
        return newThread;


    }

    /* 
	Kills the specified thread. 

	The status must be set to ThreadKill, the thread must be
	removed from the task's list of threads and its pending IORBs
	must be purged from all device queues.
        
	If some thread was on the ready queue, it must removed, if the 
	thread was running, the processor becomes idle, and dispatch() 
	must be called to resume a waiting thread.
	
	@OSPProject Threads
    */
    public void do_kill()
    {   
        int status = this.getStatus();

        //Thread is in the ready queue
        if(status == ThreadReady){
            TaskCB task = this.getTask();
            List<ThreadCB> threadList = taskTable.get(task.getID());
            threadList.remove(this); //Remove the thread from ready queue
            task.removeThread(this);
        }

        if(status == ThreadRunning){
            PageTable pageTable= MMU.getPTBR();
            TaskCB currentTask = pageTable.getTask();
            currentTask.setCurrentThread(null);
            MMU.setPTBR(null);
        }
        //Thread is suspended or waiting for I/O
        if(status == ThreadWaiting){
            
        }

        //Set status to thread kill/destroyed
        this.setStatus(ThreadKill); 

        ThreadCB.dispatch();

    }

    /** Suspends the thread that is currenly on the processor on the 
        specified event. 

        Note that the thread being suspended doesn't need to be
        running. It can also be waiting for completion of a pagefault
        and be suspended on the IORB that is bringing the page in.
	
	Thread's status must be changed to ThreadWaiting or higher,
        the processor set to idle, the thread must be in the right
        waiting queue, and dispatch() must be called to give CPU
        control to some other thread.

	@param event - event on which to suspend this thread.

        @OSPProject Threads
    */
    public void do_suspend(Event event)
    {
        // your code goes here

    }

    /** Resumes the thread.
        
	Only a thread with the status ThreadWaiting or higher
	can be resumed.  The status must be set to ThreadReady or
	decremented, respectively.
	A ready thread should be placed on the ready queue.
	
	@OSPProject Threads
    */
    public void do_resume()
    {
        // your code goes here

    }

    /* 
        Selects a thread from the run queue and dispatches it. 

        If there is just one theread ready to run, reschedule the thread 
        currently on the processor.

        In addition to setting the correct thread status it must
        update the PTBR.
	
	@return SUCCESS or FAILURE

        @OSPProject Threads
    */
    public static int do_dispatch()
    {
        // your code goes here
        return 0;

    }

    /*
        Called whenever we require to get the total CPU time all the threads in the same task.
    */
    private static double getAllCPUTime(TaskCB task){

        double totalTime = 0;

        if(task == null)
            return -1;

        List<ThreadCB> threadList = taskTable.get(task.getID());
        for(ThreadCB thread: threadList)
            totalTime = totalTime + thread.getTimeOnCPU();
        
        return totalTime;
    }

    public long getEntryTimeInQueue(){

        return this.entryTimeInQueue;
    }

    public void setEntryTimeInQueue(long time){

        this.entryTimeInQueue = time;
    }

    public int compareTo(ThreadCB thread){

        if(this.getPriority()>thread.getPriority())
            return 1;
        if(this.getPriority()<thread.getPriority())
            return -1;
        return 0;
    }


    /*
       Called by OSP after printing an error message. The student can
       insert code here to print various tables and data structures in
       their state just after the error happened.  The body can be
       left empty, if this feature is not used.

       @OSPProject Threads
    */
    public static void atError()
    {
        // your code goes here

    }

    /** Called by OSP after printing a warning message. The student
        can insert code here to print various tables and data
        structures in their state just after the warning happened.
        The body can be left empty, if this feature is not used.
       
        @OSPProject Threads
     */
    public static void atWarning()
    {
        // your code goes here

    }


    /*
       Feel free to add methods/fields to improve the readability of your code
    */

}

/*
      Feel free to add local classes to improve the readability of your code
*/
