import java.sql.SQLOutput;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;

public class Main {
    public static void main(String[] args) throws InterruptedException {//main funkcijata (metodot) e master niskata t.e tatko niska
        System.out.println("Izvrsuvanje vo main");
        Incrementor incrementor = new Incrementor();
      //  Incrementor incrementor2 = new Incrementor();
        ThreadClass threadEden = new ThreadClass("Niska 1", incrementor);//niskata se ragja t.e e vo sostojba born
        ThreadClass threadDva = new ThreadClass("Niska 2", incrementor);//niskata se ragja t.e e vo sostojba born
        //threadEden.run(); //namesto da se kreira nova niska, run metodoto se izvrsuva vo main niskata
        //za da se kreira nova niska namesto thread.run treba da napiseme thread.start

        threadEden.start();//sega sostojbata na thread niskata e vo sostojba ready t.e spremna za izvrsuvanje
        //i za da premine vo sostojba running t.e da se izvrsuva, odlucuva schedulerot (dokolku ima dovolno memorija vo RAM,prioritet itn)
        threadDva.start();
        threadEden.join();
        // threadEden.join();//se dodeka ne se izvrsi thread eden cekaj
        //threadEden.join(5) so PARAMETAR MILLISECONDS VO JOIN METODOT KAZUVAME KOLKU EDEN MILISEKUNDI TREBA DA CEKA PROGRAMATA DA ZAVRSI DADENIOT THREAD
        //DOKOLKU NE ZAVRSI ZA 5 MILISEKUNDI, PRODOLZI PONATAMU SO PROGRAMATA
        threadDva.join();//se dodeka ne se izvrsi thread dva cekaj Main metod i ne izvrsuvaj nisto so e posle join metodot
        //System.out.println("Kraj na programata");//dokolku nemame join metod, togas ke zavrsi programata pa ke se kreiraat niskite
        //poradi toa sto dodeka se dade pravo na izvrsuvanje, da se kreiraat niskite.

        if (threadEden.isAlive() && threadDva.isAlive()) {//dokolku threadovite se zivi
            System.out.println("Thread 1 i thread 2 seuste se alive");
            threadEden.interrupt();//odi vo sostojba dead
            threadDva.interrupt();//odi vo sostojba dead
        }

        System.out.println("End incrementor "  + incrementor.getCounter());// ke ispecati End: 0 bidejki poprvo ke zavrsi ovoj println otkolku niskite
        // zatoa ke pisime join za da programata poceka da se izvrsat threadovite pa posle da se ispecati incrementor.getCOunter
    //    System.out.println("End incrementor 2: " + incrementor.getCounter());
    }


}

class ThreadClass extends Thread {
    private String name;
    Incrementor incrementor;

    public ThreadClass(String name, Incrementor incrementor) {
        this.name = name;
        this.incrementor = incrementor;
    }

    @Override
    public void run() {
        System.out.println("Kreiranje nova niska, vo run metodot\n");//run metodata se kreira koga master threadot ke kreira nov thread (niska)
        for (int i = 0; i < 20; i++) {
            // System.out.println("Niskata: "+name+" "+"pat: "+i);

              //  incrementor.NebezbedenIncrement();//zgolemuvanje na counter shared memorijata so pomos na threadovi
            try {
                incrementor.BezbedenSemaforIncrement();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Incrementor {
    private static Semaphore semafor=new Semaphore(2);//dozvoli onolku kolku sto mislis deka e potrebno threadovi da operiraat
    //so kriticnata sekcija, a drugite da cekaat
    //1 permit (dozvola) dozvoluva samo eden thread da pristapi do kriticnata sekcija, itn
    private static Lock brava = new ReentrantLock();//reenterLock ima implementirano mutex brava
    //mutex (Lock) dozvoluva samo eden thread da pristapi do spodelenata memorija t.e kriticnata sekcija i site drugi threadovi
    //da cekaat
    private static int counter = 0;//dokolku sakame counter da ni e spodelen pomegju procesi pisuvame static


    public static void BezbedenSemaforIncrement() throws InterruptedException {
        semafor.acquire();//nasiot semafor vo daden moment pravi acquire t.e mu dozvoluva da pristapi do kriticnata sekcija
        // na nekoj od threadovite i acquire povici moze da imame kolku permits t.e dozvoli
        counter++;
        semafor.release();//go predava klucot na sledniot thread koj sto ceka

    }



    public static void NebezbedenIncrement() throws InterruptedException {
        counter++;//ne e atomicna operacija
        //se cita prvo counter
        //se inkrementira counter
        //pa se zapisuva vo memorija
        Thread.sleep(5);//kazuvame threadot koj ja izvrsuva increment funkcijata da spie 5 millisekuni
    }

    //sinhronizacija od monitor na nivo na proces
    public synchronized void BezbedenIncrement() { //za dadeniot monitor ovaa funkcija se sinhronizira
        //monitor pretstavuva samiot proces t.e objekt koj se naogjame i monitorot i monitorot e tokmu toj proces
        //mutex implementacija kade sto mozeme da izvrsuvame samo ako se naogjame vo monitorot na dadeniot proces.
        //Vo drugite slucaevi site drugi threadovi cekaat se dodeka ne zavrsi monitorot za dadeniot thread ne moze nitu eden drug
        //thread da pristapi do synchronized funkcija
        counter++;

        //dokolku ne sakame synchronized da piseme vo deklariranjeto na funkcijata
        //pisuvame ISTOTO GO PRAVI
        //synchronized (this){
        //   counter++;
        //}
    }

    //sinhronizacija od monitorot na nivo na klasa a ne na nivo na proces
//ako imame povekje instanci od klasata tie na nivo na klasa ke pristapuvaat do counter vo ovoj slucaj
    public void BezbedenClassIncrement() {
        synchronized (Incrementor.class) {//pristapuvame do klasata t.e ja sinhronizirame
            counter++;
        }
    }

    //MUTEX-prestatvuva nekoj kluc koj sto ima lock i unlock i dokolku go imame klucot pristapuvame do kelija od memorija
    //dokolku napravime .lock mozeme da ja menuvame vrednosta, ja zaklucuvame kelijata samo za nas
    //a .unlock go davame klucot na sledniot thread koj sto ceka


    public static void BezbedenMutexIncrement(){
        brava.lock();//zakluci ja kelijata kade sto e promenlivata counter
        counter++;//zgolemi
        brava.unlock();//otkluci ja kelijata kade sto e promenlivata counter

        //dokolku nemame unlock togas site threadovi cekaat toj sto go ima klucot da ja otkluci kelijata od memorijata
        //no toj thread go izgubil klucot i doagja do deadlock koga site procesi cekaat
    }

    public static int getCounter() {
        return counter;
    }
}



