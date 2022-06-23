import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class FileServer {
    public static int SOCKET_PORT;  // Porta

    public static String SERVER_DIRECTORY = ".\\server\\";  // you may change this

    public final static int FILE_SIZE = 6022386; // file size temporary hard coded
    // should bigger than the file to be downloaded

    public static void main (String [] args ) throws IOException {
        System.out.println("Digite a porta desejada:");
        Scanner scanner = new Scanner(System.in);
        SOCKET_PORT = Integer.parseInt(scanner.next());

        ObjectInputStream ois = null;
        ServerSocket servsock = null;
        Socket sock = null;
        try {
            servsock = new ServerSocket(SOCKET_PORT); /* Camada de Rede */

            while (true) {
                System.out.println("Aguardando...");
                try {
                    sock = NetworkLayer.aceitaConexao(servsock);
                    System.out.println("Accepted connection : " + sock);

                    //Recebe do o nome do cliente para listagem dos arquivos
                    ois = new ObjectInputStream(sock.getInputStream());
                    String nomeCliente = ois.readUTF();

                    //Chama o método que envia a lista
                    listarArquivos(sock, nomeCliente);

                    ois = new ObjectInputStream(sock.getInputStream());

                    int acao = ois.readInt();

                    switch(acao) {
                        case 1:
                            fazUpload(ois, nomeCliente);
                            break;
                        case 2:
                            fazDownload(sock, ois);
                            break;
                    }

                } finally {
                    if (ois != null) ois.close();
                    if (sock != null) sock.close();
                }
            }
        }
        finally {
            if (servsock != null) servsock.close();
        }
    }

    public static void fazUpload(ObjectInputStream ois, String nomeCliente) throws IOException {
        //Pega os dados do sock
        int nCopias = ois.readInt();
        String nomeArquivo = ois.readUTF();
        Object bis = null;
        try {
            bis = ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if(bis != null) {
            System.out.println(bis);
        }

        System.out.println(nCopias);
        System.out.println(nomeCliente);
        System.out.println(nomeArquivo);

        //Faz uma cópia dos dados do bytearray
        byte [] file = (byte[]) bis;

        //Percorre as máquinas criando as pastas e fazendo upload dos arquivos
        for(int i = 1 ; i <= nCopias ; i++ ) {
            new File("./server/" + i).mkdirs();
            new File("./server/" + i + "/" + nomeCliente).mkdirs();
            String path = SERVER_DIRECTORY + i + "\\" + nomeCliente + "\\" + nomeArquivo + ".txt";
            FileOutputStream fos = new FileOutputStream(path);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            if(file != null) {
                bos.write(file, 0 , file.length);
            }
            bos.flush();
        }

        System.out.println("O upload foi feito com sucesso!");
    }

    //TODO: Alterar para percorrer todas as pastas.
    public static void listarArquivos(Socket sock, String nomeCliente) throws IOException {
        // Vai na pasta e cria uma lista de files
        File folder = new File("./server/1/" + nomeCliente);
        File[] listOfFiles = folder.listFiles();

        //Percorre os arquivos da pasta do cliente e guarda os nomes
        String nomesArquivos = "";
        System.out.println("Arquivos encontrados: ");
        // Iterating array of files for printing name of all files present in the directory.
        for (int i = 0; i < listOfFiles.length; i++) {
            System.out.println("\t" + listOfFiles[i].getName());
            nomesArquivos += listOfFiles[i].getName() + ",";
        }

        //Guarda no socket os nomes dos arquivos do cliente
        ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream()); //Camada de Enlace
        os.writeUTF(nomesArquivos);
        os.flush();
    }

    //TODO: Alterar para percorrer todas as pastas.
    public static void fazDownload(Socket sock, ObjectInputStream ois) throws IOException {
        //Faz leitura dos dados para encontrar o arquivo
        String nomeCliente = ois.readUTF();
        String nomeArquivo = ois.readUTF();
        String path = "./server/1/" + nomeCliente + "/" + nomeArquivo;
        File fileEncontrado = new File(path);
        byte [] conteudoArquivoByteArray = ByteUtils.fileToByteArray(fileEncontrado);

        //Guarda o conteúdo no sock para o cliente
        ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream());
        os.writeObject(conteudoArquivoByteArray);

        System.out.println("Downloading " + path + "(" + fileEncontrado.length() + " bytes)");

        os.flush();
    }
}
