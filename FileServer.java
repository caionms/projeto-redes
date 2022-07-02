import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
                            fazDownload(sock, ois, nomeCliente);
                            break;
                        case 3:
                            removeArquivo(sock, ois, nomeCliente);
                            break;
                        case 4:
                            alteraNivel(sock, ois, nomeCliente);
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

    public static void listarArquivos(Socket sock, String nomeCliente) throws IOException {
        List<String> listaDeArquivos = new ArrayList<>();

        //Percorre todas as máquinas e salva os arquivos sem repetir
        File server = new File("./server");
        File[] serverFolders = server.listFiles();
        int qtdMaquinas = serverFolders.length; //Qtd de máquinas
        for(int i=1 ; i<=qtdMaquinas ; i++) {
            File maquina = new File("./server/" + i + "/" + nomeCliente);
            File[] maquinaFiles = maquina.listFiles();
            int qtdArquivos = maquinaFiles.length; //Qtd de máquinas
            for(int j=0 ; j<qtdArquivos ; j++) {
                if(!listaDeArquivos.contains(maquinaFiles[j].getName()))
                    listaDeArquivos.add(maquinaFiles[j].getName());
            }
        }

        //Percorre os arquivos encontrados do cliente
        String nomesArquivos = "";
        System.out.println("Arquivos encontrados: ");
        // Iterating array of files for printing name of all files present in the directory.
        for (int i = 0; i < listaDeArquivos.size(); i++) {
            System.out.println("\t" + listaDeArquivos.get(i));
            nomesArquivos += listaDeArquivos.get(i) + ",";
        }

        //Guarda no socket os nomes dos arquivos do cliente
        ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream()); //Camada de Enlace
        os.writeUTF(nomesArquivos);
        os.flush();
    }

    public static void fazDownload(Socket sock, ObjectInputStream ois, String nomeCliente) throws IOException {
        //Faz leitura dos dados para encontrar o arquivo
        String nomeArquivo = ois.readUTF();
        //Pega todas as máquinas
        File server = new File("./server");
        File[] serverFolders = server.listFiles();
        int qtdMaquinas = serverFolders.length; //Qtd de máquinas
        for(int i=1 ; i<=qtdMaquinas ; i++) {
            try {
                String path = "./server/" + i + "/" + nomeCliente + "/" + nomeArquivo;
                File fileEncontrado = new File(path);
                byte [] conteudoArquivoByteArray = ByteUtils.fileToByteArray(fileEncontrado);

                //Guarda o conteúdo no sock para o cliente
                ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream());
                os.writeObject(conteudoArquivoByteArray);

                System.out.println("Baixando " + path + "(" + fileEncontrado.length() + " bytes) da máquina " + i);

                os.flush();

                return;
            } catch (FileNotFoundException ignore) {
                System.out.println("Arquivo não encontrado na máquina " + i);
            }
        }
        System.out.println("Sentimos muito... O arquivo foi perdido no servidor...");
    }

    public static void removeArquivo(Socket sock, ObjectInputStream ois, String nomeCliente) throws IOException {
        //Faz leitura dos dados para encontrar o arquivo
        String nomeArquivo = ois.readUTF();

        //Pega todas as máquinas
        File server = new File("./server");
        File[] serverFolders = server.listFiles();
        int qtdMaquinas = serverFolders.length; //Qtd de máquinas
        for(int i=1 ; i<=qtdMaquinas ; i++) {
            String path = "./server/" + i + "/" + nomeCliente + "/" + nomeArquivo;
            File fileEncontrado = new File(path);
            if (fileEncontrado.delete()) {
                System.out.println("Arquivo removido da máquina " + i);
            } else {
                System.out.println("Arquivo não existe na máquina " + i);
            }
        }
    }

    public static void alteraNivel(Socket sock, ObjectInputStream ois, String nomeCliente) throws IOException {
        //Faz leitura dos dados para encontrar o arquivo
        String nomeArquivo = ois.readUTF();
        int nivel = ois.readInt();
        int qtdCopiasExistentes = 0;

        File fileEncontrado = null;
        File fileAtual = null;
        //Pega todas as máquinas
        File server = new File("./server");
        File[] serverFolders = server.listFiles();
        int qtdMaquinas = serverFolders.length; //Qtd de máquinas
        for(int i=1 ; i<=qtdMaquinas ; i++) {
            String path = "./server/" + i + "/" + nomeCliente + "/" + nomeArquivo;
            fileAtual = new File(path);
            if(fileAtual.exists()){
                qtdCopiasExistentes++;
                fileAtual = fileEncontrado;
            }

        }

        if(qtdCopiasExistentes == 0){
            System.out.println("O arquivo " + nomeArquivo + " não existe no servidor");
            return;
        }

        byte [] conteudoArquivoByteArray = ByteUtils.fileToByteArray(fileAtual);
        if(qtdCopiasExistentes == nivel)
            return;
        else if(nivel > qtdCopiasExistentes){
            //Percorre as máquinas criando as pastas e fazendo upload dos arquivos
            for(int i = 1 ; nivel > qtdCopiasExistentes ; i++ ) {
                String path = SERVER_DIRECTORY + i + "\\" + nomeCliente + "\\" + nomeArquivo;
                if(!new File(path).exists()) {
                    new File("./server/" + i).mkdirs();
                    new File("./server/" + i + "/" + nomeCliente).mkdirs();
                    FileOutputStream fos = new FileOutputStream(path);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    if(conteudoArquivoByteArray != null) {
                        bos.write(conteudoArquivoByteArray, 0 , conteudoArquivoByteArray.length);
                    }
                    bos.flush();
                    qtdCopiasExistentes++;
                }
            }
        }
        else {
            int countRemocao = 0;
            //Pega todas as máquinas
            for(int i=1 ; i<=qtdMaquinas ; i++) {

                if((qtdCopiasExistentes - countRemocao) == nivel) //chegou no nivel esperado
                    return;

                String path = "./server/" + i + "/" + nomeCliente + "/" + nomeArquivo;
                fileEncontrado = new File(path);
                if (fileEncontrado.delete()) {
                    countRemocao++;
                    System.out.println("Arquivo removido da máquina " + i);
                } else {
                    System.out.println("Arquivo não existe na máquina " + i);
                }
            }
        }
    }
}
