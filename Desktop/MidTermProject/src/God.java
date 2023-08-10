import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * a class for server
 */
public class God {
    private static ArrayList<ClientHandler> Clients=new ArrayList<>();
    private static ArrayList<String> usernames=new ArrayList<>();
    private static ArrayList<String> CitizenTeam=new ArrayList<>();
    private static ArrayList<String> MafiaTeam=new ArrayList<>();
    private static ArrayList<Player> players=new ArrayList<>();
    private static ArrayList<Boolean>startgame=new ArrayList<>();
    private static ArrayList<Player> deadplayer=new ArrayList<>();
    private static boolean wantknow=false;
    private static int night=1;
    private static ExecutorService numP= Executors.newFixedThreadPool(10);
    private static ArrayList<String> rols= new ArrayList<>();
    public static void main(String[] args) throws IOException {
        ServerSocket server=new ServerSocket(4440);
        int num=1;
        rols.add("DoctorLecter");
        rols.add("GodFather");
       rols.add("SimpleMafia");
        rols.add("Doctor");
        rols.add("Detective");
        rols.add("HardDie");
        rols.add("mayor");
        rols.add("Psychologist");
        rols.add("Citizen");
        rols.add("professional");
        try{
            while (true){
            Socket socket=server.accept();
            System.out.println("Client number "+num+" Connected");
            num++;
            ClientHandler Clienthandler=new ClientHandler(players,socket,Clients,usernames,rols,CitizenTeam,MafiaTeam,night,startgame,deadplayer,wantknow);
            Clients.add(Clienthandler);
            numP.execute(Clienthandler);
        }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}

/**
 * a class for handle clients
 */
class ClientHandler implements Runnable{
    private Socket client;
    private Player player;
    private  ArrayList<Boolean>startgame;
    private ArrayList<Player>players;
    private ArrayList<ClientHandler> clients;
    private ArrayList<String> usernames;
    private ArrayList<String> Mteam;
    private ArrayList<String> Cteam;
    private ArrayList<String> rols;
    private ArrayList<Player> deads;
    private DataInputStream input;
    private DataOutputStream output;
    private int night;
    private boolean wantknow;
    private Random random=new Random();

    /**
     *
     * @param players list of player
     * @param client
     * @param clients list of clients
     * @param usernames list of username
     * @param rols list of player
     * @param Cteam list of citizen team
     * @param Mteam list of mafia team
     * @param night for take long to start game
     * @param startgame state of start game
     * @param deads list of dead player
     * @param wantknow state doing work
     * @throws IOException
     */
    public ClientHandler(ArrayList<Player>players,Socket client,ArrayList<ClientHandler>clients, ArrayList<String> usernames,ArrayList<String> rols,ArrayList<String> Cteam,ArrayList<String> Mteam,int night,ArrayList<Boolean> startgame,ArrayList<Player>deads,boolean wantknow) throws IOException {
        this.players=players;
        this.client = client;
        this.clients=clients;
        this.usernames=usernames;
        this.rols=rols;
        this.Cteam=Cteam;
        this.Mteam=Mteam;
        this.night=night;;
        this.wantknow=wantknow;
        this.startgame=startgame;
        this.deads=deads;
        input = new DataInputStream(client.getInputStream());
        output = new DataOutputStream(client.getOutputStream());
    }
    public ClientHandler(){

    }
    @Override
    /**
     * method for manage game
     * and every think happened in this method
     */
    public void run() {
        try {
            String Str="";
            String rol="";
            String name="";
            String Rol="";
            boolean state=false;
            boolean NorD=false;
            boolean know=true;
            boolean start=false;
            boolean gamestate=true;
            boolean voting=true;

            while (true){
                if (!state){
                    output.writeUTF("Plz enter ur name");
                    name=input.readUTF();
                    state=true;
                    for (String sl:usernames){
                        if (name.equals(sl)){
                            state=false;
                            break;
                        }
                    }
                    if (state){
                        usernames.add(name);
                        int rand=random.nextInt(rols.size());
                        rol=rols.get(rand);
                        rols.remove(rand);
                        Rol=rol;
                        System.out.println(rol);
                        if (rol.equals("GodFather") || rol.equals("DoctorLecter") || rol.equals("SimpleMafia") ){
                            Mteam.add(name);
                        }
                        else {
                            Cteam.add(name);
                        }
                        player=new Player(name,rol);
                        players.add(player);
                        night++;
                        while (true){
                            output.writeUTF("do u want to start the game?!\n1)yes\n2)no");
                            String choosen1=input.readUTF();
                            int choosen2=Integer.parseInt(choosen1);
                            if (choosen2==1){
                                boolean s=true;
                                startgame.add(s);
                                break;
                            }
                        }
                        long time1=System.currentTimeMillis();
                        long time2=System.currentTimeMillis();
                        while (time2-time1<10000/night) {
                            time2=System.currentTimeMillis();
                        }
                        output.writeUTF(" ur name is ok and ur rol is "+rol);
                    }
                }
                if (know && state){
                        if (player.getRol().equals("GodFather") || player.getRol().equals("DoctorLecter")|| player.getRol().equals("SimpleMafia")){
                            for (Player P:players){
                                if (P.getRol().equals("GodFather") || P.getRol().equals("DoctorLecter") || P.getRol().equals("SimpleMafia")){
                                    if (!P.getRol().equals(rol)){
                                        output.writeUTF("ur teamMate "+P.getRol()+" is "+P.getName());
                                    }

                                }
                            }
                        }
                        if (player.getRol().equals("mayor")){
                            for (Player P:players){
                                if (P.getRol().equals("Doctor")){
                                    output.writeUTF("doctor: "+P.getName());
                                }
                            }
                        }
                    if (player.getRol().equals("Doctor")){
                        for (Player P:players){
                            if (P.getRol().equals("mayor")){
                                output.writeUTF("mayor: "+P.getName());
                            }
                        }
                    }
                    know=false;
                }
                if (Mteam.size()>Cteam.size()){
                    output.writeUTF("Mafia win the game");
                    gamestate=false;
                }
                else if (Mteam.size()<1){
                    output.writeUTF("Citizen win the game");
                    gamestate=false;
                }
                if (!gamestate){
                    break;
                }
                    if (state && !NorD && !know){
                        output.writeUTF("its Day: ");
                            for (Player deadplayer:deads){
                                output.writeUTF(deadplayer.getName()+" gone out");
                            }
                            if (wantknow){
                                for (Player deadplayer:deads){
                                    output.writeUTF(deadplayer.getName()+"  "+deadplayer.getRol()+" gone out");
                                }
                                wantknow=false;
                            }

                        output.writeUTF("start chat");
                        long time1=System.currentTimeMillis();
                        long time2=System.currentTimeMillis();
                        while (time2-time1<10000) {
                            if (player.isCantalk()){
                                Str=input.readUTF();
                                for (ClientHandler Cl:clients){
                                    Cl.output.writeUTF(player.getName()+" : "+Str);
                                }
                            }

                            time2=System.currentTimeMillis();
                            if (time2-time1<10000 && time2-time1>9999){
                                output.writeUTF("plz say ur last word");
                            }
                        }
                            output.writeUTF("chat finish");
                        if (voting){
                            output.writeUTF("voting start");
                            output.writeUTF("plz choose one player: ");
                            int num=1;
                            for (String player:usernames){
                                output.writeUTF(num+") " +player);
                                num++;
                            }
                            String choosen=input.readUTF();
                            int choosen1=Integer.parseInt(choosen);
                            output.writeUTF("ur choosen player is: "+usernames.get(choosen1-1));
                            for (Player p:players){
                                if (p.getName().equals(usernames.get(choosen1-1))){
                                    for (ClientHandler c:clients){
                                        c.output.writeUTF(p.getName()+" vote to"+usernames.get(choosen1-1));
                                    }
                                   p.vote+=1;
                                }
                            }
                        }
                        long time3=System.currentTimeMillis();
                        long time4=System.currentTimeMillis();
                        while (time3-time4<10000) {
                            time3=System.currentTimeMillis();
                            if (time3-time4<10000 && 9999<time3-time4){
                                output.writeUTF("result");
                            }
                        }
                        output.writeUTF("voting finish");
                        boolean mayorstate=false;
                        for (ClientHandler C: clients){
                            if (C.player.getRol().equals("mayor")){
                                mayorstate=true;
                                break;
                            }

                        }
                        if (mayorstate){
                            if (player.getRol().equals("mayor")){
                                output.writeUTF("do u want to remove this voting?\n1)Yes\n2)No");
                                String choosen6=input.readUTF();
                                int choosen7=Integer.parseInt(choosen6);
                                if (choosen7==2){
                                    while (true){
                                        ClientHandler out=new ClientHandler();
                                        int votnum=0;
                                        for (ClientHandler Cl:clients){
                                            if (votnum<Cl.player.vote){
                                                output.writeUTF("2");
                                                votnum=Cl.player.vote;
                                                out=Cl;
                                            }
                                        }
                                        output.writeUTF(out.player.getName()+" is going out");
                                        clients.remove(out);
                                        deads.add(out.player);
                                        usernames.remove(out.player.getName());
                                        out.client.close();
                                        break;
                                    }
                                }
                            }
                            else {
                                long time8=System.currentTimeMillis();
                                long time9=System.currentTimeMillis();
                                while (time8-time9<6000) {
                                    time8=System.currentTimeMillis();
                                }
                            }
                        }
                        else {
                            while (true){
                                ClientHandler out=new ClientHandler();
                                int votnum=0;
                                for (ClientHandler Cl:clients){
                                    if (votnum<Cl.player.vote){
                                        votnum=Cl.player.vote;
                                        out=Cl;
                                    }
                                }
                                output.writeUTF(out.player.getName()+" is going out");
                                usernames.remove(out.player.getName());
                                clients.remove(out);
                                deads.add(out.player);
                                out.client.close();
                                break;
                            }
                        }
                        voting=true;
                        NorD=true;
                    }
                if (Mteam.size()>Cteam.size()){
                    output.writeUTF("Mafia win the game");
                    gamestate=false;
                }
                else if (Mteam.size()<1){
                    output.writeUTF("Citizen win the game");
                    gamestate=false;
                }
                if (!gamestate){
                    break;
                }
                    if (state && NorD && !know){
                        output.writeUTF("its night");
                        if (rol.equals("GodFather")){
                            output.writeUTF("u want to shot who?!");
                            int num=1;
                            for (String player:Cteam){
                                output.writeUTF(num+") " +player);
                                num++;
                            }
                            String choosen=input.readUTF();
                            int choosen1=Integer.parseInt(choosen);
                            output.writeUTF("ur choosen player is: "+Cteam.get(choosen1-1));
                            for (Player p:players){
                                if (p.getName().equals(Cteam.get(choosen1-1))){
                                    p.setAlive(false);
                                }
                            }
                            NorD=false;
                        }
                        else{
                            output.writeUTF("Godfather turn");
                            synchronized (client){
                                client.wait(5000);
                            }
                        }
                       if (rol.equals("DoctorLecter")){
                            int num=1;
                            for (String player:Cteam){
                                output.writeUTF(num+") " +player);
                                num++;
                            }
                            String choosen1=input.readUTF();
                            int choosen2=Integer.parseInt(choosen1);
                            output.writeUTF("ur choosen player is: "+Cteam.get(choosen2-1));
                            for (ClientHandler p:clients){
                                if (p.player.getRol().equals("GodFather")){
                                    p.output.writeUTF(p.player.getName()+"("+rol+")"+" choose "+Cteam.get(choosen2-1));
                                }
                            }
                            for (String player:Mteam){
                                output.writeUTF(num+") " +player);
                                num++;
                            }
                            output.writeUTF("plz choose :");
                            String choosen3=input.readUTF();
                            int choosen4=Integer.parseInt(choosen3);
                            output.writeUTF("ur choosen player is: "+Mteam.get(choosen4-1));
                           for (Player p:players){
                               if (p.getName().equals(Mteam.get(choosen4-1))){
                                   p.setAlive(true);
                               }
                           }
                        }

                       else{
                           output.writeUTF("DoctorLecter turn");
                           synchronized (client){
                               client.wait(5000);
                           }
                       }
                         if (rol.equals("SimpleMafia")){
                            int num=1;
                            for (String player:Cteam){
                                output.writeUTF(num+") " +player);
                                num++;
                            }
                            String choosen=input.readUTF();
                            int choosen3=Integer.parseInt(choosen);
                            output.writeUTF("ur choosen player is: "+Cteam.get(choosen3-1));
                            for (ClientHandler p:clients){
                                if (p.player.getRol().equals("GodFather")){
                                    p.output.writeUTF(p.player.getName()+"("+rol+")"+" choose "+Cteam.get(choosen3-1));
                                }
                            }
                        }
                         else{
                             synchronized (client){
                                 output.writeUTF("SimpleMafia turn");
                                 client.wait(5000);
                             }
                         } if (rol.equals("Doctor")){
                            output.writeUTF("u want to cure who?!");
                            int num=1;
                            for (String player:usernames){
                                output.writeUTF(num+") " +player);
                                num++;
                            }
                            String choosen=input.readUTF();
                            int choosen4=Integer.parseInt(choosen);
                            System.out.println(usernames.get(choosen4-1));
                            output.writeUTF("ur choosen player is: "+usernames.get(choosen4-1));
                            for (Player p:players){
                                if (p.getName().equals(usernames.get(choosen4-1))){
                                    p.setAlive(true);
                                }
                            }
                        }
                        else{
                            synchronized (client){
                                output.writeUTF("Doctor turn");
                                client.wait(5000);
                            }
                        } if (rol.equals("Detective")){
                            int num=1;
                            for (String player:usernames){
                                output.writeUTF(num+") " +player);
                                num++;
                            }
                            String choosen=input.readUTF();
                            int choosen5=Integer.parseInt(choosen);
                            output.writeUTF("ur choosen player is: "+usernames.get(choosen5-1));
                            for (Player p:players){
                                if (p.getName().equals(usernames.get(choosen5-1))){
                                    System.out.println("111");
                                    if (p.getRol().equals("SimpleMafia") || p.getRol().equals("DoctorLecter")){

                                        output.writeUTF(p.getName()+" Mafia");
                                    }
                                }
                            }
                        }
                        else{
                            synchronized (client){
                                output.writeUTF("Detective turn");
                                client.wait(5000);
                            }
                        } if (rol.equals("professional")){
                            output.writeUTF("do u want to shot someone?!\n1)yes\n2)no");
                            String choosen1=input.readUTF();
                            int choosen2=Integer.parseInt(choosen1);
                            if (choosen2==1){
                                int num=1;
                                for (String player:usernames){
                                    output.writeUTF(num+") " +player);
                                    num++;
                                }
                                String choosen3=input.readUTF();
                                int choosen4=Integer.parseInt(choosen3);
                                boolean shot=false;
                                for (Player p:players){
                                    if (p.getName().equals(usernames.get(choosen4-1)) && (p.getRol().equals("SimpleMafia") || p.getRol().equals("DoctorLecter")) ){
                                      output.writeUTF("good choice");
                                        p.setAlive(false);
                                        shot=true;
                                        break;
                                    }
                                }
                                if (!shot){
                                    player.setAlive(false);
                                    output.writeUTF("u die");
                                }
                            }
                        }
                        else{
                            synchronized (client){
                                output.writeUTF("professional turn");
                                client.wait(5000);
                            }
                        } if (rol.equals("HardDie")){
                            output.writeUTF("do u want know who went out?!");
                            String choosen1=input.readUTF();
                            int choosen2=Integer.parseInt(choosen1);
                            if (choosen2==1){
                                wantknow=true;

                            }

                        }
                        else{
                            synchronized (client){
                                output.writeUTF("HardDie turn");
                                client.wait(5000);
                            }
                        } if (rol.equals("Psychologist")){
                            output.writeUTF("do u want to talk down someone ?!");
                            output.writeUTF("do u want to shot someone?!\n1)yes\n2)no");
                            String choosen1=input.readUTF();
                            int choosen2=Integer.parseInt(choosen1);
                            if (choosen2==1){
                                int num=1;
                                for (String player:usernames){
                                    output.writeUTF(num+") " +player);
                                    num++;
                                }
                                String choosen3=input.readUTF();
                                int choosen4=Integer.parseInt(choosen3);
                                for (Player p:players){
                                    if (p.getName().equals(usernames.get(choosen4-1))){
                                        output.writeUTF("u talk down "+usernames.get(choosen4-1));
                                        p.setCantalk(false);
                                    }
                                }

                            }
                            notifyAll();
                        }
                       else{
                           synchronized (client){
                               output.writeUTF("Psychologist turn");
                               client.wait(5000);
                           }
                        }
                        if (!player.isAlive()){
                            deads.add(player);
                            usernames.remove(player.getName());
                            if (Mteam.contains(player.getName())){
                                output.writeUTF("removed M");
                                Mteam.remove(player.getName());
                            }
                            else if (Cteam.contains(player.getName())){
                                output.writeUTF("removed C");
                                Cteam.remove(player.getName());
                            }
                            clients.remove(client);
                            client.close();
                        }
                        NorD=false;
                    }
                if (Mteam.size()>Cteam.size()){
                    output.writeUTF("Mafia win the game");
                    gamestate=false;
                }
                else if (Mteam.size()<1){
                    output.writeUTF("Citizen win the game");
                    gamestate=false;
                }
                if (!gamestate){
                    break;
                }
                }
            }
        catch (Exception ex){
            ex.printStackTrace();
        }
        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/**
 * a class for property of player
 */
class Player {
    private String name;
    private String rol;
    private boolean alive;
    private boolean cantalk;
     int vote=0;

    /**
     *
     * @param name name of player
     * @param rol rol of player
     */
    public Player( String name, String rol) {
        this.name = name;
        this.rol = rol;
        alive = true;
        cantalk=true;
    }
    public String getName() {
        return name;
    }
    public String getRol() {
        return rol;
    }
    public boolean isAlive() {
        return alive;
    }
    public boolean isCantalk() {
        return cantalk;
    }
    public void setAlive(boolean alive) {
        this.alive = alive;}
    public void setCantalk(boolean cantalk) {
        this.cantalk = cantalk;
    }
}