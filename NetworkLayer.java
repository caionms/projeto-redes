import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkLayer {
    public static Socket aceitaConexao (ServerSocket servsock) throws IOException {
        return servsock.accept();
    }

    public static void uploadClientToServer(Socket sock, int acao, int nCopias, String nomeCliente, String nomeArquivo, byte[] conteudoArquivoByteArray) throws IOException {
        ObjectOutputStream os = new ObjectOutputStream(sock.getOutputStream());
        os.writeInt(acao);
        os.writeInt(nCopias);
        os.writeUTF(nomeCliente);
        os.writeUTF(nomeArquivo);
        os.writeObject(conteudoArquivoByteArray);

        System.out.println("Sending " + nomeArquivo + "(" + conteudoArquivoByteArray.length + " bytes)");
        os.flush();
    }
}
