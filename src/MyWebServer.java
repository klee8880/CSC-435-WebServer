import java.io.*;
import java.net.*;

import javax.xml.soap.Text;

//http://localhost:2540/dog.txt, and http://localhost:2540/cat.html

class HTMLResponse {
	
	public enum contentType{
		txt,
		html
	}	
	public static String EOL = "\r\n";
	public String header = "HTTP/1.1 200 OK";
	private long length = 0;
	public contentType type = contentType.txt;
	private StringBuilder message;
	
	//Constructors
	public HTMLResponse () {
		super();
		message = new StringBuilder();
	}
	public HTMLResponse(String header, contentType type) {
		super();
		this.header = header;
		this.type = type;
		message = new StringBuilder();
	}
	public HTMLResponse(contentType type) {
		super();
		this.type = type;
		message = new StringBuilder();
	}
	
	//Methods
	/*
	 * Add to the current message
	 * Input string is appended to the end of the message and the message length is updated
	 */
	public HTMLResponse append(String message) {
		
		if (message == null) return this;
		
		length += message.length();
		this.message.append(message);
		
		return this;
	}
	/*
	 * Add to the current message
	 * Input string is appended to the end of the message and the message length is updated
	 */
	public HTMLResponse append(StringBuilder message) {
		if (message == null) return this;
		
		length += message.length();
		this.message.append(message);
		return this;
	}
	/*
	 * Generate a proper HTML response based on the inputs.
	 */
	public String generate() {
		
		//HTML Header
		StringBuilder response = new StringBuilder(header); 
		response.append(EOL);
		//Length
		response.append("Content-Length: "); 
		response.append(length); 
		response.append(EOL);
		//ContentType
		switch(type) {
		case txt:
			response.append("Content-Type: text/plain");
			break;
		case html:
			response.append("Content-Type: text/html");
			break;
		}
		//Line break
		response.append(EOL);
		response.append(EOL);
		
		//Read File
		response.append(message);
		
		
		return response.toString();
	}
}

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
		      
			//Save request string
			String sockdata;
			sockdata = in.readLine();
			String request[] = sockdata.split(" ");
			System.out.println(sockdata);
			
			
		    for (int i = 1; i < 8 || sockdata == null; i++) {
		    	sockdata = in.readLine();
		    	System.out.println(sockdata);
		    	System.out.flush ();
		    }
			
		    //Handle responses
		    HTMLResponse message = new HTMLResponse();
		    
		    //Sanitize for proper requests - Breaks code if improper request detected
		    if (!request[0].equals("GET")) {
			    message = new HTMLResponse(HTMLResponse.contentType.txt);
			    message.append("<UNHANDEDED REQEUST>");
			    out.print(message.generate());
			    out.flush();
			    return;
		    }
		    
		    File target = new File("." + request[1]);
		    
		    //Directory
		    if (target.isDirectory()) {
		    	message.type = HTMLResponse.contentType.html;
			    message.append("<pre>");
			    message.append("<h1>Index of ").append(request[1]).append("</h1>").append(HTMLResponse.EOL);
			    message.append(HTMLResponse.EOL);
			    
			    File[] dirFiles = target.listFiles();
			    
			    //Read content of directory
			    for(int i = 0; i < dirFiles.length; i++) {
			    	if (dirFiles[i].isDirectory()) {
			    		message.append("DIR:").append("<a href=\"").append(dirFiles[i].getName()).append("\">").append(dirFiles[i].getName()).append("</a> <br>").append(HTMLResponse.EOL);
			    	}
			    	else if (dirFiles[i].isFile()){
			    		message.append("FILE:").append("<a href=\"").append(dirFiles[i].getName()).append("\">").append(dirFiles[i].getName()).append("</a> <br>").append(HTMLResponse.EOL);
			    	}
			    }
		    }
		    //File
		    else if (target.isFile()) {
		    	//TODO: HTML type files
		    	if (request[1].endsWith(".html")) {
		    		
		    	}
		    	//TODO: TXT type files
		    	else if (request[1].endsWith(".txt")) {
		    		
		    	}
		    }
		    else {
		    	message.append("<UNHANDEDED REQEUST>");
		    }
		    
		    //Respond
		    System.out.print(message.generate());
		    out.print(message.generate());
		    out.flush();
		    return;
		    
		} catch (Exception x) {
		   	System.out.println("Connetion reset. Listening again...");
		}
	}
	
	private HTMLResponse printDirectory () {
		return null;
	}
	
	private HTMLResponse printFile() {
		return null;
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
