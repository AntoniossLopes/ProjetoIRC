import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTCP {

    public static void main(String argv[]) throws Exception {
        String Lmessage;
        String Smessage;
        int array[] = new int[10];
        int soma = 0;
        float media = 0;

        ServerSocket ss = new ServerSocket(1234);
        Socket s = ss.accept();
        System.out.println("Esta Conectado ao server");

        //Recebe Dados
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream())); //in to server
        DataInputStream out = new DataInputStream(s.getInputStream()); //out to client
        //Envia Dados
        DataOutputStream manda = new DataOutputStream(s.getOutputStream());

        while(true) {
            Lmessage = out.readUTF();
            System.out.println("Recebeu Do Cliente: "+Lmessage);
            if(Lmessage.equalsIgnoreCase("DADOS")){
                Smessage = "Dados";
                manda.writeUTF(Smessage);
                for(int i = 0; i< 10; i++)
                {
                    Lmessage = out.readUTF();
                    System.out.println("Recebeu "+i+" : " + Lmessage);
                    int x = Integer.parseInt(Lmessage);
                    array[i]= x ;
                    StringBuilder Umessage = new StringBuilder();
                    Umessage.append("Numero Recebido Valor ");
                    Umessage.append(x);
                    String novo = Umessage.toString();
                    manda.writeUTF(novo);
                }
                System.out.println("Imprime valores no array");
                for(int i =  0; i< 10 ;i++)
                {
                    System.out.println("vetor["+i+"] = "+array[i]);
                }
            }

            if(Lmessage.equalsIgnoreCase("SOMA"))
            {
                if(array[0] == 0)
                {
                    System.out.println("ERRO");
                }
                else{
                    for(int i = 0 ; i < 10 ; i++)
                    {
                        soma += array[i];
                    }
                    StringBuilder Umessage = new StringBuilder();
                    Umessage.append("Valor da Soma:  ");
                    Umessage.append(soma);
                    String novo = Umessage.toString();
                    manda.writeUTF(novo);
                }
            }
            if(Lmessage.equalsIgnoreCase("MEDIA")) {
                if(array[0] == 0)
                {
                    System.out.println("ERRO");
                }
                media = (float)soma/10;
                StringBuilder Umessage = new StringBuilder();
                Umessage.append("Valor da Media:  ");
                Umessage.append(media);
                String novo = Umessage.toString();
                manda.writeUTF(novo);

            }
            if(Lmessage.equalsIgnoreCase("exit"))
                break;

        }
        ss.close();

    }
}

