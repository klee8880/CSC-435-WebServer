import java.io.*;
import java.net.*;

//http://localhost:2540

class HTMLResponse {
	
	public enum contentType{
		txt,
		html
	}	
	public static String EOL = "\r\n";
	public String header = "HTTP/1.1 200 OK";
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
		this.message.append(message);
		
		return this;
	}
	/*
	 * Add to the current message
	 * Input string is appended to the end of the message and the message length is updated
	 */
	public HTMLResponse append(StringBuilder message) {
		if (message == null) return this;
		this.message.append(message);
		return this;
	}
	//Generate a proper HTML response based on the inputs.
	public String generate() {
		
		//HTML Header
		StringBuilder response = new StringBuilder(header).append(EOL);
		//Length
		response.append("Content-Length: ").append(message.length()).append(EOL);
		//ContentType
		switch(type) {
		case txt:
			response.append("Content-Type: text/plain").append(EOL);;
			break;
		case html:
			response.append("Content-Type: text/html").append(EOL);;
			break;
		}
		//Line break
		response.append(EOL);
		
		//Read File
		response.append(message);
		
		
		return response.toString();
	}
}

class WebServerWorker extends Thread {
	Socket sock;
	WebServerWorker (Socket s) {sock = s;}
	
	@Override
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
		    }
			
		    //Handle responses
		    HTMLResponse message;
		    
		    //Sanitize for proper requests - Breaks code if improper request detected
		    //Detect if not GET request
		    if (!request[0].equals("GET")) {
			    message = errorResponse();
		    }
		    //Detect Favicon request
		    else if (request[1].endsWith(".ico")){
		    	System.out.println("Favicon reqeust ignored...");
		    	return;
		    }
		    //Detect root access request
		    else if (!request[1].startsWith("/")) {
		    	System.out.println("Request for root detected. Redirecting...");
		    	request[1] = "/";
		    }
		    
		    File target = new File("." + request[1]);
		    
		    //Directory
		    if (target.isDirectory()) {
		    	message = printDirectory(request[1], target);
		    }
		    //File
		    else if (target.isFile()) {
		    	message = printFile(request[1], target);
		    }
		    //fake-cgi
		    else if (request[1].startsWith("/cgi/")) {
		    	System.out.println("cgi reqeust detected.");
		    	message = CGIResponse(request[1]);
		    }
		    else {
		    	message = errorResponse();
		    }
		    
		    //Respond
		    System.out.println(message.generate());
		    out.print(message.generate());
		    out.flush();
		    return;
		    
		} catch (Exception x) {
			x.printStackTrace();
		   	System.out.println("Connetion reset. Listening again...");
		}
	}
	//Generate a HTML file for HTTP representing the file directory
	private HTMLResponse printDirectory (String path, File file) {
		HTMLResponse message = new HTMLResponse();
		
		message.type = HTMLResponse.contentType.html;
	    message.append("<pre>");
	    message.append("<h1>Index of ").append(path).append("</h1>").append(HTMLResponse.EOL);
	    message.append(HTMLResponse.EOL);
	    
	    //TODO: add a link to the parent directory
	    
	    //Read content of directory
	    File[] dirFiles = file.listFiles();
	    for(int i = 0; i < dirFiles.length; i++) {
	    	if (dirFiles[i].isDirectory()) {
	    		message.append("DIR:").append("<a href=\"").append(dirFiles[i].getName()).append("/\">").append(dirFiles[i].getName()).append("</a> <br>").append(HTMLResponse.EOL);
	    	}
	    	else if (dirFiles[i].isFile()){
	    		message.append("FILE:").append("<a href=\"").append(dirFiles[i].getName()).append("\">").append(dirFiles[i].getName()).append("</a> <br>").append(HTMLResponse.EOL);
	    	}
	    }
		
		return message;
	}
	//Generate a txt or html file for HTTP
	private HTMLResponse printFile(String path, File file) throws IOException {
		HTMLResponse message = new HTMLResponse();
    	//HTML type files
    	if (path.endsWith(".html")) {
    		message.type = HTMLResponse.contentType.html;
    	}
    	//TXT type files
    	else if (path.endsWith(".txt")) {
    		message.type = HTMLResponse.contentType.txt;
    	}
    	//ERROR handler
    	else {
    		return errorResponse();
    	}
    	
    	//read the file into 
    	BufferedReader in = new BufferedReader(new FileReader(file));
    	
    	String nextLine;
    	while((nextLine = in.readLine()) != null) {
    		message.append(nextLine);
    	}
    	//close the file
    	in.close();
    	
    	return message;
	}
	//Return computed value based on the requested CGI
	private HTMLResponse CGIResponse(String request) {
		
		//Split the string into components parts
		String[] components = request.split("/|\\?|&");
		for (int i = 0; i < components.length; i++) {
			System.out.println(components[i]);
		}
		
		//Detect request
		if (components[2].equals("addnums.fake-cgi")) {
			//Format a HTML response
			HTMLResponse response = new HTMLResponse(HTMLResponse.contentType.html);
			//Call addnums for string
			response.append(addnums(components[3], components[4], components[5]));
			return response;
		}
		//Unhandled cases.
		else {
			return errorResponse();
		}
	}
	//Respond with an error to the client
	private HTMLResponse errorResponse() {
		HTMLResponse message = new HTMLResponse();
		message = new HTMLResponse(HTMLResponse.contentType.txt);
	    message.append("<UNHANDEDED REQEUST>");
	    return message;
	}
	
	//Add Number simulated script
	private String addnums(String name, String firstNum, String secondNum) {
		
		int first = Integer.parseInt(firstNum.substring(5));
		int second = Integer.parseInt(secondNum.substring(5));
		
		//Build a response
		StringBuilder response = new StringBuilder("Hello ").append(name.substring(7));
		response.append(", the sum of ").append(first).append(" & ").append(second).append(" is ").append(first + second).append('.');
		
		//return a new string of responses.
		return response.toString();
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
