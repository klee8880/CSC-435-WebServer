import java.io.*;
import java.net.*;

class ListenWorker extends Thread {    // Class definition
  Socket sock;                   // Class member, socket, local to ListnWorker.
  ListenWorker (Socket s) {sock = s;} // Constructor, assign arg s
                                      //to local sock
  public void run(){
    // Get I/O streams from the socket:
    PrintStream out = null;
    BufferedReader in = null;
    try {
      out = new PrintStream(sock.getOutputStream());
      in = new BufferedReader
        (new InputStreamReader(sock.getInputStream()));
      String sockdata;
      while (true) {
        sockdata = in.readLine ();
        if (sockdata != null) System.out.println(sockdata);
        System.out.flush ();
      }
      //sock.close(); // close this connection, but not the server;
    } catch (IOException x) {
      System.out.println("Connetion reset. Listening again...");
    }
  }
}

public class MyListener {

  public static boolean controlSwitch = true;

  public static void main(String a[]) throws IOException {
    int q_len = 6; /* Number of requests for OpSys to queue */
    int port = 2540;
    Socket sock;

    ServerSocket servsock = new ServerSocket(port, q_len);

    System.out.println("Clark Elliott's Port listener running at 2540.\n");
    while (controlSwitch) {
      // wait for the next client connection:
      sock = servsock.accept();
      new ListenWorker (sock).start(); // Uncomment to see shutdown bug:
      // try{Thread.sleep(10000);} catch(InterruptedException ex) {}
    }
  }
}