# OS Thread Management

This is a thread management module for the [OSP2](http://cdn.iiit.ac.in/cdn/enhanceedu.iiit.ac.in/wiki/images/OSP2_Manual.pdf) [Operating system](http://tolstenko.net/dados/Unicamp/2009.2/mc514/Pratica/01-MC514-2009s2-5001-01-OSP2.pdf).
## Thread Scheduling
Thread are scheduled according to their priority. Threads with the same priority are to
be scheduled according to the time they were inserted in the ready queue.
<br>
<br>
<ul>
<li>
The priority is calculated by the following expression:
<br>
<pre>
1.5 * total time the thread was waiting in the ready queue
minus
total CPU time the thread has used so far
minus
0.3 * total CPU time all the threads in the same task have used so far
</pre>
</li>
<li>When a thread is dispatched, it is given a CPU time slice of 100 time units. If the thread is
still running at the end of its time slice, it is preempted and put back into the ready queue.</li>
<li>If an interrupt occurs before the current thread finished its time slice, that thread has the
highest priority when the dispatcher is invoked next</li>
</ul>
