import java.io.*;
import java.net.*;

class WebServerWorker extends Thread {
  Socket sock;
  WebServerWorker (Socket s) {sock = s;}
  public void run(){
    // Get I/O streams from the socket:
    PrintStream out = null;
    BufferedReader in = null;
    
    try {
    	
    
    	out = new PrintStream(sock.getOutputStream());
    	in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
      
    	//read request from remote
    	
    	
      
    	//Handle cases
      
      		//serve file
      
      		//change directory
      
    } catch (IOException x) {
    	System.out.println("Connetion reset. Listening again...");
    }
  }
}

public class MyWebServer{

  public static boolean controlSwitch = true;

  public static void main(String a[]) throws IOException {
	  int q_len = 6; /* Number of requests for OpSys to queue */
	  int port = 2540;
	  Socket sock;
	
	  ServerSocket servsock = new ServerSocket(port, q_len);
	
	  System.out.println("Clark Elliott's Port listener running at 2540.\n");
	  while (controlSwitch) {
	    	
		  sock = servsock.accept();
		  new WebServerWorker(sock).start();
      
    }
  }
}
