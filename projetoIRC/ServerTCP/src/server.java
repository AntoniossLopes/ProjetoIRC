
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class server {
    public static void main(String argv[]) throws IOException {
        ServerSocket ss = new ServerSocket(5056);
        ServerSocket s1s = new ServerSocket(5057);
        System.out.println("Server up.");
        while(true) {
            Socket s = null;
            Socket s1 = null;
            try {
                //estabelece ligação com o cliente
                 s = ss.accept();
                 s1 = s1s.accept();
                 //conecta ao server e cria o JSONArray com a informação dos utilizadores
                System.out.println("Esta Conectado ao server :" + s);
                System.out.println("A ler a Isabela");
                String url = "http://socialiteorion2.dei.uc.pt:9014/v2/entities?options=keyValues&type=student&attrs=activity%2Ccalls_duration%2Ccalls_made%2Ccalls_missed%2Ccalls_received%2Cdepartment%2Clocation%2Csms_received%2Csms_sent&limit=100&fbclid=IwAR1pev6Oo_gX-LEM6gsbpUhNRg67ihAkARQbr85IUqbAc947yIfSM3CRj1Q";;
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("cache-control","no-cache");
                con.setRequestProperty("fiware-servicepath"," /");
                con.setRequestProperty("fiware-service","socialite");
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while((inputLine = in.readLine())!= null)
                {
                    response.append(inputLine);
                }
                in.close();
                inputLine = response.toString();
                System.out.println(inputLine);
                JSONArray array =new JSONArray(inputLine);
                System.out.println("Leitura Concluida.");

                //Recebe Dados e Envia Dados

                DataInputStream out = new DataInputStream(s.getInputStream());
                DataOutputStream manda = new DataOutputStream(s.getOutputStream());
                DataOutputStream mandasub = new DataOutputStream(s1.getOutputStream());

                Thread t = new TrataThead(s, s1, out, manda, mandasub, array, obj);


                t.start();
            }catch(Exception e){
                s.close();
                s1.close();
                e.printStackTrace();
            }
        }
    }
}


    // CRIA AS THREADS
    class TrataThead extends Thread {
        final DataInputStream recebe;
        final DataOutputStream manda;
        final DataOutputStream mandasub;
        final Socket s;
        final Socket s1;
        final URL obj;
        JSONArray array;
        private String id;
        int tempo=0;
        int sub[] = new int[5];
        int subaux[] = new int [5];
        int grupos[] = new int [5];
        int delay=1000;

        //CONSTRUTOR PARA AS THREADS
        public TrataThead(Socket s, Socket s1, DataInputStream out, DataOutputStream manda, DataOutputStream mandasub, JSONArray array, URL obj){
            this.manda = manda;
            this.recebe = out;
            this.s = s;
            this.s1 = s1;
            this.mandasub = mandasub;
            this.obj = obj;
            this.array=array;
        }
        @Override
        public void run(){
            //Cria thread com timertask para tratar do tempo entre cada notificação das subscrições;
            Timer timer = new Timer();
            TimerTask tarefa = new TimerTask() {
                public void run(){
                    tempo++;
                    grupos[1]++;
                    try {
                        //Vê quando é tempo de notificação
                        if(tempo%delay==0){
                            //Da update à informação do JSONArray e à informaçao dos grupos
                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                            con.setRequestMethod("GET");
                            con.setRequestProperty("cache-control","no-cache");
                            con.setRequestProperty("fiware-servicepath"," /");
                            con.setRequestProperty("fiware-service","socialite");
                            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            String inputLine;
                            StringBuffer response = new StringBuffer();
                            while((inputLine = in.readLine())!= null)
                            {
                                response.append(inputLine);
                            }
                            in.close();
                            inputLine = response.toString();
                            System.out.println(inputLine);
                            try {
                                array = new JSONArray(inputLine);
                                for(int i=0; i<array.length(); i++){
                                    JSONObject x = array.getJSONObject(i);
                                    if (!(x.getString("id").equalsIgnoreCase("studentroom1"))) {
                                        grupos[0]+=x.getInt("calls_duration");
                                        grupos[1]+=x.getInt("calls_made");
                                        grupos[2]+=x.getInt("calls_missed");
                                        grupos[3]+=x.getInt("sms_received");
                                        grupos[4]+=x.getInt("sms_sent");

                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //Verifica se há razão para notificar o cliente
                            for(int z=0; z<sub.length; z++){
                                if(sub[z]==1){
                                    switch(z){
                                        case 0:
                                            if(subaux[z]!=grupos[z]){
                                                mandasub.writeUTF("Notificação: Média da duração de chamadas alterou de " + subaux[z] + " para " + grupos[z]);
                                            }
                                            subaux[z]=grupos[z];
                                            break;
                                        case 1:
                                            if(subaux[z]!=grupos[z]){
                                                mandasub.writeUTF("Notificação: Média de chamadas feitas alterou de " + subaux[z] + " para " + grupos[z]);
                                            }
                                            subaux[z]=grupos[z];
                                            break;
                                        case 2:
                                            if(subaux[z]!=grupos[z]){
                                                mandasub.writeUTF("Notificação: Média de chemadas perdidas alterou de " + subaux[z] + " para " + grupos[z]);
                                            }
                                            subaux[z]=grupos[z];
                                            break;
                                        case 3:
                                            if(subaux[z]!=grupos[z]){
                                                mandasub.writeUTF("Notificação: Média de sms recebidos alterou de " + subaux[z] + " para " + grupos[z]);
                                            }
                                            subaux[z]=grupos[z];
                                            break;
                                        case 4:
                                            if(subaux[z]!=grupos[z]){
                                                mandasub.writeUTF("Notificação: Média de sms enviados alterou de " + subaux[z] + " para " + grupos[z]);
                                            }
                                            subaux[z]=grupos[z];
                                            break;
                                }
                            }
                        }
                    }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            //Trata do login
            String Lmessage;
            int flag=0;
            while(flag==0) {
                try {
                    manda.writeUTF("0");
                    id = recebe.readUTF();
                    for(int i = 0 ; i < array.length(); i++) {
                        JSONObject x = array.getJSONObject(i);
                        if (id.equalsIgnoreCase(x.getString("id"))) {
                            flag=1;
                            manda.writeUTF("1");
                            break;
                        }}
                } catch (IOException e) {
                    e.printStackTrace();
                }
                catch(JSONException x){
                    x.printStackTrace();
                }
            }
            //Começa o timer das subscrições
            timer.scheduleAtFixedRate(tarefa, 0, 1000);
            //Interação com o servidor no menu
            while (true) {
                try {
                    //Trata da mensagem de saída
                    Lmessage = recebe.readUTF();
                    if (Lmessage.equalsIgnoreCase("Exit")) {
                        this.s.close();
                        System.out.println("Ligacao Fechada.");
                        break;
                    }
                    //Recebe o input do menu do utilizador
                    if (Lmessage.equalsIgnoreCase("1")) {
                        while (true) {
                            for(int i = 0 ; i < array.length(); i++) {
                                JSONObject x = array.getJSONObject(i);
                                if (id.equalsIgnoreCase(x.getString("id"))) {
                                    manda.writeUTF(x.getString("type"));
                                    manda.writeUTF(Integer.toString(x.getInt("calls_duration")));
                                    manda.writeUTF(Integer.toString(x.getInt("calls_made")));
                                    manda.writeUTF(Integer.toString(x.getInt("calls_missed")));
                                    manda.writeUTF(x.getString("department"));
                                    manda.writeUTF(x.getString("location"));
                                    manda.writeUTF(Integer.toString(x.getInt("sms_received")));
                                    manda.writeUTF(Integer.toString(x.getInt("sms_sent")));
                                } // ESTA A DAR ATE AQUI
                            }
                        }
                    }

                    if (Lmessage.equalsIgnoreCase("2")) {
                        String Smessage;
                        int media = 0;
                        StringBuilder Umessage = new StringBuilder();
                        Umessage.append("Introduza quais Dados quer visualizar:");
                        Smessage = Umessage.toString();
                        manda.writeUTF(Smessage);
                        Lmessage = recebe.readUTF();
                        int numero = Integer.parseInt(Lmessage);
                        for(int i =0; i < array.length();i++) {
                            JSONObject x = array.getJSONObject(i);
                            if(numero == 1) {
                                media += x.getInt("calls_duration") ;
                            }
                            if(numero == 2) {
                                media += x.getInt("calls_made");
                            }
                            if(numero == 3) {
                                media += x.getInt("calls_missed");
                            }
                            if(numero == 4) {
                                media += x.getInt("sms_received");
                            }
                            if(numero == 5) {
                                media += x.getInt("sms_sent");
                            }
                        }
                        media = media/array.length();
                        manda.writeUTF(Integer.toString(media));
                    }

                    if (Lmessage.equalsIgnoreCase("3")) {
                        Lmessage = recebe.readUTF();
                        int numero = Integer.parseInt(Lmessage);
                        if(sub[numero-1]==1){
                            manda.writeUTF("0");
                        }else{
                            manda.writeUTF("1");
                            sub[numero-1]=1;
                        }
                        Lmessage=recebe.readUTF();
                        delay=Integer.parseInt(Lmessage);
                        if (delay<=0){
                                manda.writeUTF("0");
                                Lmessage = recebe.readUTF();
                                delay=Integer.parseInt(Lmessage);
                        } else {
                            manda.writeUTF("5");
                        }
                    }
                }catch(JSONException x){
                    x.printStackTrace();
                }

                catch (IOException e) {
                    e.printStackTrace();
                }

            }

            //FECHAR o INPUT E OUT DESTA THREAD DE CADA USER
            try {
                this.manda.close();
                this.recebe.close();
                this.mandasub.close();

            } catch (IOException e) {
                e.printStackTrace();

            }
        }
}