package proxy_server;
import java.io.*;
import java.net.*;
import java.util.*;

public class ClientThread extends Thread 
{
    private Socket socket;
    private boolean isStop;
    private BufferedReader in;
    private PrintWriter out;
    
    final static String CRLF = "\r\n";
    BufferedWriter proxyToClientBw;
	
    public ClientThread(Socket clientSocket)
    {
        this.socket = clientSocket;
        this.isStop = false;
    }
    
    public void run()
    {
        int beginIndex=-1;
        try
        {
            while(!isStop)
            {
                //create a buffer reader
                in = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
                //create a PrintWriter
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true); 
                String line;  
                String httpHeader = ""; //stores the html header
                String htmlFile =""; //stores the required html file
                while (true) {
                    line = in.readLine(); //read each line
                    if (line.equals(CRLF) || line.equals("")) // end of header is reached?
                    {
                        break; // if yes, break
                    }
                    httpHeader = httpHeader + line + "\n"; //add a new line to the header
                    if(line.contains("GET")) // if line contains get
                    {
                        //extract the url
                         beginIndex = line.indexOf("http");
                        if(beginIndex==-1)
                            beginIndex=line.indexOf("https");
                        int endIndex = line.indexOf("HTTP");
                        if(beginIndex!=-1)
                        htmlFile = line.substring(beginIndex, endIndex-1);
                    }
                    else
                    {
                         out.print("HTTP/1.0 500 Internal Error" + CRLF);
           
          //  send file not found message
            out.println("<html><head>");
            out.println("<title>404 Not Found</title>");
            out.println("</head><body>");
            out.println("<h1>Not Found</h1>");
            out.println("<p>The requested URL /" + htmlFile + " was not found on this server.</p>");
            out.println("</body></html>");
            out.println(CRLF);
            break;
                    }
                }
                System.out.println(httpHeader); // print httpHeader
                
              
                
                processRequest(htmlFile,beginIndex); // process the request
                closeConnection(); // close the connection
                
            }
        }
        catch(Exception e) //print error stack trace
        {
            //System.out.println(e.printStackTrace());
            //e.printStackTrace();
        }
    }
    
    public void processRequest(String htmlFile,int beginIndex) throws Exception
    {
       
        if(htmlFile!="" && beginIndex!=-1)
        {
            //Create URL class to access the requested url
            URL url = new URL(htmlFile);
       
            
           
          
				// Create a connection to remote server
				HttpURLConnection proxy_Server_Connection = (HttpURLConnection)url.openConnection();
				proxy_Server_Connection.setRequestProperty("Content-Type", 
						"application/x-www-form-urlencoded");
				proxy_Server_Connection.setRequestProperty("Content-Language", "en-US");  
				proxy_Server_Connection.setUseCaches(false);
				proxy_Server_Connection.setDoOutput(true);
			
				// Create Buffered Reader from remote Server
				BufferedReader proxy_Server = new BufferedReader(new InputStreamReader(proxy_Server_Connection.getInputStream()));
				

				// Send success code to client
				String line = "HTTP/1.0 200 OK\n" +
						"Proxy-agent: ProxyServer/1.0\n" +
						"\r\n";
				
				
				// Read from input stream between proxy and remote server
				while((line = proxy_Server.readLine()) != null){
					// Send on data to clien
                                        out.println(line);
				}
				
				

				// Close Down Resources
				if(proxy_Server != null){
					proxy_Server.close();
				}
			
           
         
            String inputLine;
            while((inputLine=in.readLine())!=null)
               // System.out.println(inputLine);
            in.close();
        }

        else 
        {
            //sent the HTTP head (404 Not Found)
            out.print("HTTP/1.0 500 Internal Error" + CRLF);
           
              
          //  send file not found message
            out.println("<html><head>");
            out.println("<title>404 Not Found</title>");
            out.println("</head><body>");
            out.println("<h1>Not Found</h1>");
            out.println("<p>The requested URL /" + htmlFile + " was not found on this server.</p>");
            out.println("</body></html>");
            out.println(CRLF);
        
    }
    }
    
    private void closeConnection()
    {
        try
        {
            out.close(); // close output stream
            in.close(); // close input stream
            socket.close(); //close socket
            isStop = true; //set isStop to true in order to exist the while loop
        }
        catch(Exception e)
        {
           // System.out.println(e.toString());
        }
    }
}