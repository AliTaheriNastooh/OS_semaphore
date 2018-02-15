import java.util.Scanner;
import java.util.concurrent.*;

public class main1 {
    static int[] arr;
    static int cnt=0;
    static class PRODUCER extends Thread {
        Semaphore full2;
        Semaphore empty2;
        Semaphore mutex2;
        String name_producer;
        public PRODUCER(Semaphore full1,Semaphore empty1,Semaphore mutex1, String threadName) {
            this.empty2=empty1;
            this.full2=full1;
            this.mutex2=mutex1;
            this.name_producer = threadName;
        }

        @Override
        public void run() {
            int counter=0;
            boolean flag1=true;
            while(flag1){
                int item=produce();
                System.out.println(this.name_producer+" is waiting for empty");
                try {
                    empty2.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(this.name_producer+" is waiting for mutex");
                try {
                    mutex2.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                arr[cnt]=item;
                cnt++;
                System.out.println(this.name_producer+" is release mutex");
                mutex2.release();
                System.out.println(this.name_producer+" is release full");
                full2.release();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(counter==100){
                    flag1=false;
                }
                counter++;
            }
        }
        int produce(){
            return 2;
        }
    }
    static class CUSTOMER extends Thread
    {
        Semaphore empty3;
        Semaphore full3;
        Semaphore mutex3;
        String customer_name;
        int item;
        public CUSTOMER(Semaphore mutex4,Semaphore full4,Semaphore empty4, String threadName)
        {
            this.mutex3 = mutex4;
            this.full3=full4;
            this.empty3=empty4;
            this.customer_name = threadName;
        }

        @Override
        public void run() {
            int counter=0;
            boolean flagme=true;
            while(flagme){
                System.out.println(this.customer_name+" is waiting for full");
                try {
                    full3.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(this.customer_name+" is waiting for mutex");
                try {
                    mutex3.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int result=arr[cnt-1];
                cnt--;
                System.out.println(this.customer_name+" is release mutex");
                mutex3.release();
                System.out.println(this.customer_name+" is release empty");
                empty3.release();
                try {
                    consume(result);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(counter==100){
                    flagme=false;
                }
                counter++;

            }

        }
        public void consume(int res) throws InterruptedException {
            item=res;
        }
    }

    public static void main(String arg[]) throws InterruptedException {
        Scanner scanner=new Scanner(System.in);
        System.out.print("please enter size of buffer:");
        int size=scanner.nextInt();
        System.out.print("please enter number of consumer N>4:");
        int nc=scanner.nextInt();
        System.out.print("please enter number of producer M>3:");
        int np=scanner.nextInt();
        arr=new int[size];
        Semaphore mymutex=new Semaphore(2,true);
        Semaphore myfull=new Semaphore(0,true);
        Semaphore myempty=new Semaphore(size,true);
//        for(int i=0;i<size;i++){
//            myempty.release();
//        }

        PRODUCER[] producer1=new PRODUCER[np];
        for(int i=0;i<np;i++) {
            producer1[i] = new PRODUCER(myfull, myempty, mymutex, "producer"+(i+1));
            producer1[i].start();
        }
        CUSTOMER[] customer1=new CUSTOMER[nc];
        for(int i=0;i<nc;i++){
            customer1[i]=new CUSTOMER(mymutex,myfull,myempty,"customer"+(i+1));
            customer1[i].start();
        }
    }
}
