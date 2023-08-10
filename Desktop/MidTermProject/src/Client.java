import java.io.*;
import java.net.Socket;
import java.util.Scanner;
public class Client {
    Scanner scanner=new Scanner(System.in);
    public void start(){
        try(Socket socket = new Socket("localhost",4440);DataOutputStream output=new DataOutputStream(socket.getOutputStream())){
            ServHand servcon=new ServHand(socket);
            new Thread(servcon).start();
            while(true){
                String string=scanner.nextLine();
                if (string.equals("over")){
                    break;
                }
                output.writeUTF(string);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Client player=new Client();
        player.start();
    }
}

/**
 * class for handel server and help chat better
 */
class ServHand implements Runnable{
    private Socket client;
    private DataInputStream input;
    public ServHand(Socket client) throws IOException {
        this.client = client;
        input = new DataInputStream(client.getInputStream());
    }
    @Override
    public void run() {
        try {
            while (true){
                System.out.println(input.readUTF());

            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
