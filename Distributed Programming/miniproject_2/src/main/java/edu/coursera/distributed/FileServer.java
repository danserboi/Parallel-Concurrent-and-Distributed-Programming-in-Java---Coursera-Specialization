package edu.coursera.distributed;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A basic and very limited implementation of a file server that responds to GET
 * requests from HTTP clients.
 */
public final class FileServer {
    /**
     * Main entrypoint for the basic file server.
     *
     * @param socket Provided socket to accept connections on.
     * @param fs A proxy filesystem to serve files from. See the PCDPFilesystem
     *           class for more detailed documentation of its usage.
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    public void run(final ServerSocket socket, final PCDPFilesystem fs)
            throws IOException {
        /*
         * Enter a spin loop for handling client requests to the provided
         * ServerSocket object.
         */
        while (true) {
            // We use socket.accept to get a Socket object
            Socket s = socket.accept();
            /*
             * Using Socket.getInputStream(), we parse the received HTTP
             * packet. In particular, we are interested in confirming this
             * message is a GET and parsing out the path to the file we are
             * GETing.
             */
            InputStream stream = s.getInputStream();
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader buffered = new BufferedReader(reader);

            String line = buffered.readLine();
            assert line != null;
            assert line.startsWith("GET");
            PCDPPath path = new PCDPPath(line.split(" ")[1]);
            /*
             * Using the parsed path to the target file, we construct an
             * HTTP reply and write it to Socket.getOutputStream(). If the file
             * exists, the HTTP reply should be formatted as follows:
             *
             *   HTTP/1.0 200 OK\r\n
             *   Server: FileServer\r\n
             *   \r\n
             *   FILE CONTENTS HERE\r\n
             *
             * If the specified file does not exist, we return a reply
             * with an error code 404 Not Found.
             */
            PrintWriter printer = new PrintWriter(s.getOutputStream());
            String content = fs.readFile(path);
            if(content != null){
                printer.write("HTTP/1.0 200 OK\r\n\r\n\r\n");
                printer.write(content + "\r\n");
            } else {
                printer.write("HTTP/1.0 404 Not Found\r\n\r\n\r\n");
            }
            printer.close();
            buffered.close();
        }
    }
}
