package minesweeper.server;

import java.net.*;
import java.io.*;

import minesweeper.Board;

public class MinesweeperServer {
//we're asked to include the explanation of thread safety in the board.java file
	private final static int PORT = 4444;
	private ServerSocket serverSocket;
	private Board board;
    private boolean debug;
    private int numberPlayers;
	
    /**
     * Make a MinesweeperServer that listens for connections on port.
     * @param port port number, requires 0 <= port <= 65535.
     */
    public MinesweeperServer(int port, String[] args) throws IOException {
        numberPlayers = 0;
        if (args[0].equals("true"))
            this.debug = true;
        else
            this.debug = false;
        System.out.println(args.length);
        if (args.length == 1) {
            board = new Board();
        } else {
            String flag = args[1];
            if (flag.equals("-s"))
                board = new Board(Integer.parseInt(args[2]));
            else if (flag.equals("-f"))
                board = new Board(args[2]);
        }
        serverSocket = new ServerSocket(port);
    }
    
    /**
     * Run the server, listening for client connections and handling them.  
     * Never returns unless an exception is thrown.
     * @throws IOException if the main server socket is broken
     * (IOExceptions from individual clients do *not* terminate serve()).
     */
    public void serve() throws IOException {
        while (true) {
            // block until there is a client connects
            final Socket socket = serverSocket.accept();
            numberPlayers++;
            new Thread(new Runnable() {
                Socket mySocket = socket;

                public void run() {
                    try {
                        if (!mySocket.isClosed())
                            handleConnection(mySocket);
                    } catch (IOException e) {
                        System.out
                                .println("There were errors making client thread");
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    /**
     * Handle a single client connection. Returns when client disconnects.
     * 
     * @param socket
     *            socket where client is connected
     * @throws IOException
     *             if connection has an error or terminates unexpectedly
     */
    private void handleConnection(Socket socket) throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println("Welcome to Minesweeper. " + numberPlayers + " people are playing the game including you."+
                "Type 'help' for help.");

        try {
            for (String line = in.readLine(); line != null; line = in
                    .readLine()) {
                System.out.println(line);
                String output = handleRequest(line);
                if (output != null) {
                    if (output.equals("BOOM!")) {
                        if (!debug) {
                    // the statement specified the condition of the bomb
                            out.println(output);
                            socket.close();
                            numberPlayers--;
                            break;
                        }
                    }
                    if (output.equals("Connection stopped")) {
                        out.println(output);
                        socket.close();
                        numberPlayers--;
                        break;
                    }
                    System.out.println(output);
                    out.println(output);
                    out.flush();
                }
            }
        } finally {
            out.close();
            in.close();
        }
    }

    /**
     * handler for client input
     * 
     * make requested mutations on game state if applicable, then return
     * appropriate message to the user
     * 
     * @param input
     * @return
     */
    private String handleRequest(String input) {

        String regex = "(look)|(dig \\d+ \\d+)|(flag \\d+ \\d+)|(deflag \\d+ \\d+)|(help)|(bye)";
        if (!input.matches(regex)) {
            // invalid input
            return null;
        }
        String[] tokens = input.split(" ");
        if (tokens[0].equals("look")) {
            // 'look' request
            return board.toString();
        } else if (tokens[0].equals("help")) {
            // 'help' request
            return ("Type 'look' to see the board\r\nType 'dig x y' to dig the square where the indices is x,y\r\n" +
            		"Type 'flag x y' to mark the square square where the indices is x,y\r\nType 'deflag x y' to deflag the square square where the indices is x,y");
        } else if (tokens[0].equals("bye")) {
            // ask for 'bye'
            return ("Connection stopped");
        } else {
            int x = Integer.parseInt(tokens[1]);
            int y = Integer.parseInt(tokens[2]);
            if (tokens[0].equals("dig")) {
                // ask to 'dig x y'
                if (board.hasBomb(x, y) && !(board.getStr(x,y).equals("F"))) {
                    board.dig(x, y);
                    return "BOOM";
                }
                else {
                    board.dig(x, y);
                    return board.toString();
                }
            } else if (tokens[0].equals("flag")) {
                // ask to 'flag x y'
                board.flag(x, y);
                return board.toString();
            } else if (tokens[0].equals("deflag")) {
                // ask to 'deflag x y'
                board.deflag(x, y);
                return board.toString();
            }
        }
        // should not get here
        return "";
    }

    /**
     * Start a MinesweeperServer running on the default port.
     */
    public static void main(String[] args) {
        try {
            MinesweeperServer server = new MinesweeperServer(PORT, args);
            server.serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}