import java.io.*;
import java.net.*;

//http://localhost:2540/dog.txt, and http://localhost:2540/cat.html

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
        String sockdata = in.readLine();
        
        //TODO: Collection data on the requirements
        while (sockdata != null) {
          System.out.println(sockdata);
          System.out.flush ();
          sockdata = in.readLine ();
        }
        
        //TODO: Remove temporary response code
        
    	
    	//Handle cases
      
      		//serve file
      
      		//change directory
        System.out.println("Closing thread");
        sock.close();
        
    } catch (IOException x) {
    	System.out.println("Connetion reset. Listening again...");
    }
  }
}

public class MyWebServer{

  public static boolean controlSwitch = true;
  private static int port = 2540;

  public static void main(String a[]) throws IOException {
	  int q_len = 6;
	  Socket sock;
	
	  ServerSocket servsock = new ServerSocket(port, q_len);
	
	  System.out.println("Clark Elliott's Port listener running at 2540.\n");
	  while (controlSwitch) {
		  sock = servsock.accept();
		  System.out.println("New Connection Aquired...");
		  new WebServerWorker(sock).start();
    }
  }
}
