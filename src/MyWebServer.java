import java.io.*;
import java.net.*;

//http://localhost:2540/dog.txt, and http://localhost:2540/cat.html

class WebServerWorker extends Thread {
  Socket sock;
  WebServerWorker (Socket s) {sock = s;}
  
  private static String EOL = "\r\n";
  
  public void run(){
    // Get I/O streams from the socket:
    PrintStream out = null;
    BufferedReader in = null;
    
    try {
    	
    	out = new PrintStream(sock.getOutputStream());
    	in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
      
    	//read request from remote
        String sockdata;
        
        for (int i = 0; i < 8; i++) {
        	sockdata = in.readLine();
        	System.out.println(sockdata);
        	System.out.flush ();
        }
    	
        //TODO:TEMPORARY RESPONSE
        out.print("HTTP/1.1 200 OK\r\n");
        out.print("Content-Length: 100\r\n");
        out.print("Content-Type: text/plain\r\n\r\n");
        out.print("<PLACEHOLDERDATA>");
        
    	//Handle cases
      
      		//serve file
      
      		//change directory
        System.out.println("Terminated");
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
	
	  @SuppressWarnings("resource")
	  ServerSocket servsock = new ServerSocket(port, q_len);
	
	  System.out.println("Clark Elliott's Port listener running at 2540.\n");
	  while (controlSwitch) {
		  sock = servsock.accept();
		  System.out.println("New Connection Aquired...");
		  new WebServerWorker(sock).start();
    }
  }
}
