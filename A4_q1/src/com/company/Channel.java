package com.company;
import java.io.*;
import java.net.*;

/*****************************************************************
 * Memorial University of Newfoundland<br>
 * 7894 Concurrent Programming<br>
 * Bi-directional Channel class.
 *
 * <p>Hides the implementation of bi-directional channels using sockets.
 *
 * @author Dennis Peters $Author: dpeters $
 * @version $Revision: 853 $ $Date: 2009-07-31 21:36:09 -0230 (Fri, 31 Jul 2009) $
 ****************************************************************/
public class Channel 
{
  /** Server for sockets. 
   * null in the case where this channel is created by opening socket to
   * another host.
   */
  private ServerSocket serv;
  /** The socket. */
  private Socket sock;
  /** Reader for socket. */
  private BufferedReader recv;
  /** Writer for socket. */
  private PrintWriter snd;
  
  /**
   * Open a channel to the local host on given port.
   * @param port Port on host to connect to.
   */
  Channel(int port)
    throws UnknownHostException, IOException
  {
    serv = null;
    sock = new Socket("localhost", port);
    snd = new PrintWriter(sock.getOutputStream(), true);
    recv = new BufferedReader(new InputStreamReader(sock.getInputStream()));
  }

  /**
   * Open a channel to the given host and port.
   * @param host Host name to connect to
   * @param port Port on host to connect to.
   */
  Channel(String host, int port)
    throws UnknownHostException, IOException
  {
    serv = null;
    sock = new Socket(host, port);
    snd = new PrintWriter(sock.getOutputStream(), true);
    recv = new BufferedReader(new InputStreamReader(sock.getInputStream()));
  }

  /**
   * Accept a channel on the given server
   * @param s Channel is accepted on this server
   */
  Channel(ServerSocket s)
    throws IOException
  {
    serv = s;
    sock = s.accept();
    snd = new PrintWriter(sock.getOutputStream(), true);
    recv = new BufferedReader(new InputStreamReader(sock.getInputStream()));
  }

  /**
   * Receive a message from the channel
   */
  public String receive()
    throws IOException
  {
    return recv.readLine();
  }

  /**
   * Send b on the channel
   */
  public void send(String b)
  {
    snd.println(b);
  }

  /**
   * Test for available data.
   * @return True if there is something to receive on this channel, false
   * otherwise.
   */
  public boolean ready()
    throws IOException
  {
    return recv.ready();
  }

  /**
   * Close all the channels
   */
  public void close()
    throws IOException
  {
    snd.close();
    recv.close();
    sock.close();
    if (serv != null) {
      serv.close();
    }
  }

}
