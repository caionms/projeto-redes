import java.io.File;
import java.io.Serializable;

public class Arquivo implements Serializable {
    private static String nomeArquivo;
    private static File arquivo;

    public Arquivo(String nome, String path) {
        this.nomeArquivo = nome;
        this.arquivo = new File(path);
    }

    public static String getNomeArquivo() {
        return nomeArquivo;
    }

    public static void setNomeArquivo(String nomeArquivo) {
        Arquivo.nomeArquivo = nomeArquivo;
    }

    public static File getArquivo() {
        return arquivo;
    }

    public static void setArquivo(File arquivo) {
        Arquivo.arquivo = arquivo;
    }
}
