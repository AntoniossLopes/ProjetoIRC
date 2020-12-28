import java.io.*;
import java.net.Socket;

public class client{

    public static void main(String arv[]) throws Exception {
        String Lmessage;
        String Umessage;
        int sub=0;

        Socket s = new Socket("localhost", 5056);
        Socket s1 = new Socket("localhost", 5057);
        //Enviar inputs do utilizador
        DataOutputStream out = new DataOutputStream(s.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        //Receber mensagens do server;
        DataInputStream in = new DataInputStream(s.getInputStream());
        DataInputStream insub = new DataInputStream(s1.getInputStream());
        Thread t = new TrataThread(s1, insub);
        t.start();
        //Login do utilizador
        while (in.readUTF().equals("0")) {
            System.out.print("Insire o seu id: ");
            Lmessage = br.readLine();
            out.writeUTF(Lmessage);
        }
        //Inicio do menu
        while (true) {
            System.out.flush();
            System.out.println("1.Dados Cliente.");
            System.out.println("2.Dados Server.");
            System.out.println("3.Subscrever a Dados.");
            System.out.println("Selecione a sua opcao:");
            Lmessage = br.readLine();
            //Mensagem de saída;
            if (Lmessage.equalsIgnoreCase("Exit")) {
                out.writeUTF(Lmessage);
                System.out.println("Ligacao Fechada.");
                break;
            }
            //Opções do menu
            if (Lmessage.equalsIgnoreCase("1")) {
                out.writeUTF(Lmessage);
                System.out.println("ID: " + in.readUTF());
                System.out.println("CALLS DURATION: " + in.readUTF());
                System.out.println("CALLS MADE: " + in.readUTF());
                System.out.println("CALLS MISSED: " + in.readUTF());
                System.out.println("DEPARTMENT: " + in.readUTF());
                System.out.println("LOCATION: " + in.readUTF());
                System.out.println("SMS RECEIVED: " + in.readUTF());
                System.out.println("SMS SENT: " + in.readUTF());
            }
            if (Lmessage.equalsIgnoreCase("2")) {
                out.writeUTF(Lmessage);
                Umessage = in.readUTF();
                System.out.println(Umessage);
                System.out.println("1.CALLS DURATION");
                System.out.println("2.CALLS MADE");
                System.out.println("3.CALLS MISSED");
                System.out.println("4.SMS RECEIVED");
                System.out.println("5.SMS SENT");
                Lmessage = br.readLine();
                out.writeUTF(Lmessage);
                System.out.println("Media da Opcao Selecionada: " + in.readUTF());

            }
            if (Lmessage.equalsIgnoreCase("3")) {
                out.writeUTF(Lmessage);
                System.out.println("Insira o que quer subscrever:");
                System.out.println("1.CALLS DURATION");
                System.out.println("2.CALLS MADE");
                System.out.println("3.CALLS MISSED");
                System.out.println("4.SMS RECEIVED");
                System.out.println("5.SMS SENT");
                Lmessage = br.readLine();
                out.writeUTF(Lmessage);
                Lmessage=in.readUTF();
                //Verifica estado da subscrição
                if (Lmessage.equals("0")) {
                    System.out.println("Já estava subscrito!");
                } else{
                    System.out.println("Subscrição feita!");
                }  //Envia tempo entre cada notificação
                    System.out.print("Insira o tempo, em segundos, entre cada notificação: ");
                    Lmessage = br.readLine();
                    out.writeUTF(Lmessage);
                    System.out.println(Lmessage);
                    Lmessage=in.readUTF();
                    while (Lmessage.equals("0")) {
                        System.out.println("Tempo tem que ser maior que 0");
                        Lmessage = br.readLine();
                    }
                    out.writeUTF(Lmessage);
                System.out.println("Tempo de notificação definido!");
            }
        }
        s.close();
    }
}
//Thread que fica a espera das notificações do server
class TrataThread extends Thread{
    final Socket s1;
    final DataInputStream insub;

    public TrataThread(Socket s1, DataInputStream insub) {
        this.s1 = s1;
        this.insub = insub;
    }
    @Override
    public void run(){
        try{
            while(true){
                System.out.println(insub.readUTF());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}