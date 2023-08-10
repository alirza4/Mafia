import javafx.beans.property.Property;

import java.net.Socket;

public interface Rol {
     public void Property();

}
class Mafia implements Rol{
     private Socket client;
     public Mafia(Socket client) {
          this.client=client;
     }


     @Override
     public void Property() {
     }

}
class Citizen implements Rol{
     @Override
     public void Property() {
     }
}




class DoctorLecter extends Mafia{
     public DoctorLecter(Socket client) {
          super(client);
     }
}
class GodFather extends Mafia{
     public GodFather(Socket client) {
          super(client);
     }
}
class SimpleMafia extends Mafia{
     public SimpleMafia(Socket client) {
          super(client);
     }
}
class Doctor extends Citizen{

}
class Detective extends Citizen{

}
class professional extends Citizen{

}
class HardDie extends Citizen{

}
class mayor extends Citizen{

}
class Psychologist extends Citizen{

}
class SimpleCitizen extends Citizen{

}