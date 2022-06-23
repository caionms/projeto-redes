import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileClient {

    public static int SOCKET_PORT;  // Porta
    public static String SERVER = "172.20.48.1";  // localhost
    public static String CLIENT_NAME;
    /*
     * file size temporary hard coded
     * should bigger than the file to be downloaded
     */
    public final static int FILE_SIZE = 6022386;

    private static Scanner scanner = new Scanner(System.in);

    public static void main (String [] args ) throws IOException {
        System.out.println("Digite o localhost:");
        SERVER = scanner.next();

        System.out.println("Digite a porta desejada:");
        SOCKET_PORT = Integer.parseInt(scanner.next());

        System.out.println("Digite o seu nome:");
        CLIENT_NAME = scanner.next();

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        OutputStream os = null;
        ServerSocket servsock = null;
        Socket sock = null;

        try {
            sock = new Socket(SERVER, SOCKET_PORT); //Transporte - Tentando fazer conexão com a rede

            System.out.println("Connecting...");

            //Envia o nome do cliente para listagem dos nomes dos arquivos
            ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream()); //Camada de Enlace
            oos.writeUTF(CLIENT_NAME);
            oos.flush();

            //Chama o método que faz a listagem
            listaArquivos(sock);

            int controle = 0;
            System.out.println("\n1-Upload de um arquivo.\n2-Download de um arquivo.\n3-Remoção de um arquivo.\n4-Alterar nível de tolerância a falhas.");
            controle = Integer.parseInt(scanner.next());

            switch(controle) {
                case 1:
                    fazUpload(sock, controle);
                    break;
                case 2:
                    fazDownload(sock, controle);
                    break;
            }
        }
        finally {
            if (sock != null) sock.close();
            if (scanner != null) scanner.close();
        }


    }

    // int (acao) / int (numero de copias) / int (qtd de caracteres do cliente)
    // / string (nome do cliente) / int (qtd de caracteres do arquivo) / string(nome) / tamanho / bytearray
    private static void fazUpload(Socket sock, int acao) throws IOException {
        System.out.println("Digite o nome do arquivo que você quer baixar:");
        String nomeArquivo = scanner.next();

        System.out.println("Digite a quantidade de copias:");
        int nCopias = Integer.parseInt(scanner.next());

        System.out.println("Digite o path:");
        String pathArquivo = scanner.next();
        //scanner.close();

        File myFile = new File (pathArquivo);
        byte [] conteudoArquivoByteArray = ByteUtils.fileToByteArray(myFile);

        //Guarda os valores no socket
        ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream());
        os.writeInt(acao);
        os.writeInt(nCopias);
        //os.writeUTF(CLIENT_NAME);
        os.writeUTF(nomeArquivo);
        os.writeObject(conteudoArquivoByteArray);

        System.out.println("Sending " + nomeArquivo + "(" + conteudoArquivoByteArray.length + " bytes)");
        os.flush();

        System.out.println("Done.");
    }

    private static void listaArquivos(Socket sock) throws IOException {
        //Recebe os dados do socket
        ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
        String nomesArquivos = ois.readUTF();
        String[] listaNomesArquivos = nomesArquivos.split(",");
        System.out.println("\nArquivos encontrados: ");
        for (String nome: listaNomesArquivos) {
            System.out.println("\t" + nome);
        }
    }



    // server -> maquinas (n copias) -> cliente -> arquivos
    private static void fazDownload(Socket sock, int acao) throws IOException {
        System.out.println("Digite o nome do arquivo:");
        String nomeArquivo = scanner.next();

        /* Camada de Enlace */
        ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream());
        os.writeInt(acao);
        os.writeUTF(CLIENT_NAME);
        os.writeUTF(nomeArquivo);
        os.flush();

        //listaArquivos(sock);

        System.out.println("Digite onde gostaria de salvar o arquivo:");
        String path = scanner.next();

        ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
        Object bis = null;
        try {
            bis = ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if(bis != null) {
            System.out.println(bis);
        }
        byte [] file = (byte[]) bis;

        FileOutputStream fos = new FileOutputStream(path);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        if(file != null) {
            bos.write(file, 0 , file.length);
        }
        bos.flush();
    }

}
