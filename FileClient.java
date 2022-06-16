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

    public static void main (String [] args ) throws IOException {
        Scanner scanner = new Scanner(System.in);

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
            sock = new Socket(SERVER, SOCKET_PORT);
            System.out.println("Connecting...");

            int controle = 0;
            System.out.println("1-Download de um arquivo. \n 2-Upload de um arquivo. \n 3- Sair.");
            controle = Integer.parseInt(scanner.next());

            switch(controle) {
                case 1:
                    fazDownload();
                    break;
                case 2:
                    fazUpload(sock, controle);
                    break;
            }

        }
        finally {
            if (sock != null) sock.close();
        }


    }


    // server -> maquinas (n copias) -> cliente -> arquivos
    private static void fazDownload() throws IOException {
        //Passar o tamanho do nome do arquivo, o nome do arquivo, o tamanho do nome do cliente e o nome do cliente
        //Recebe o tamanho do arquivo e o arquivo (o nome ja tem)
        /* Execucao */
        int bytesRead;
        int current = 0;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        Socket sock = null;
        try {
            sock = new Socket(SERVER, SOCKET_PORT);
            System.out.println("Conectando...");

            System.out.println("Ações\n1-Download de arquivo.\n2-Upload de arquivo.\nDigite a acao desejada:");
            Scanner scanner = new Scanner(System.in);
            int controle = Integer.parseInt(scanner.next());

            switch (controle) {
                case 1:
                    fazDownload();
                    break;
                case 2:
                    fazUpload(sock, controle);
                    break;
            }

            /*
            fos = new FileOutputStream(FILE_TO_RECEIVED);
            bos = new BufferedOutputStream(fos);

            do {
                bytesRead =
                        is.read(mybytearray, current, (mybytearray.length-current));
                if(bytesRead >= 0) current += bytesRead;
            } while(bytesRead > -1);

            bos.write(mybytearray, 0 , current);
            bos.flush();
            System.out.println("File " + FILE_TO_RECEIVED
                    + " downloaded (" + current + " bytes read)");*/
        }
        finally {
            if (fos != null) fos.close();
            if (bos != null) bos.close();
            if (sock != null) sock.close();
        }
    }
    // int (acao) / int (numero de copias) / int (qtd de caracteres do cliente)
    // / string (nome do cliente) / int (qtd de caracteres do arquivo) / string(nome) / tamanho / bytearray
    private static void fazUpload(Socket sock, int acao) throws IOException {
        Scanner scanner = new Scanner(System.in);

        //Pega o nome do arquivo e o tamanho, transformando em bytearray
        System.out.println("Digite o nome do arquivo:");
        String nomeArquivo = scanner.next();


        //Pega o numero de copias, transformando em bytearray
        System.out.println("Digite a quantidade de copias:");
        int nCopias = Integer.parseInt(scanner.next());

        //Pega o path
        System.out.println("Digite o path:");
        String pathArquivo = scanner.next();
        //scanner.close();

        //Pega o file e o seu tamanho, transformando em bytearray
        File myFile = new File (pathArquivo);
        byte [] conteudoArquivoByteArray  = new byte [(int)myFile.length()];
        FileInputStream fis = new FileInputStream(myFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(conteudoArquivoByteArray,0,conteudoArquivoByteArray.length);

        ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream());
        os.writeInt(acao);
        os.writeInt(nCopias);
        os.writeUTF(CLIENT_NAME);
        os.writeUTF(nomeArquivo);
        os.writeObject(conteudoArquivoByteArray);

        System.out.println("Sending " + pathArquivo + "(" + myFile.length() + " bytes)");

        os.flush();

        System.out.println("Done.");
    }
}
