import java.io.*;
import java.nio.charset.StandardCharsets;

public class Testes {

    public static void main (String [] args ) throws IOException, ClassNotFoundException {
        /*
        int bytesRead;
        Long nCopias = 0L;

        byte[] teste = ByteUtils.longToBytes(15L);

        InputStream is = new ByteArrayInputStream(teste);

        byte [] numeroDeCopiasByteArray  = new byte [Long.BYTES];

        bytesRead = is.read(numeroDeCopiasByteArray,0, Long.BYTES);

        nCopias = ByteUtils.bytesToLong(numeroDeCopiasByteArray); //Numero de copias

        System.out.println(nCopias);


        int bytesRead;
        int nCopias = 0;

        byte[] teste = ByteUtils.intToBytes(20);

        InputStream is = new ByteArrayInputStream(teste);

        byte [] numeroDeCopiasByteArray  = new byte [4];

        bytesRead = is.read(numeroDeCopiasByteArray,0, 4);

        nCopias = ByteUtils.bytesToInteger(numeroDeCopiasByteArray); //Numero de copias

        System.out.println(nCopias);

        String teste = "teste";
        //byte [] byteTeste = teste.getBytes(StandardCharsets.UTF_8);
        //System.out.println(new String(byteTeste));
        System.out.println(ByteUtils.bytesToString(ByteUtils.stringToBytes(teste)));
        */

        /*char teste = 'a';
        System.out.println(ByteUtils.bytesToChar(ByteUtils.charToBytes(teste)));*/

        byte [] byteTeste = "Joaozinho".getBytes(StandardCharsets.UTF_8);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeInt(1000);
        os.writeInt(2000);
        os.writeInt(3000);
        os.writeUTF("Testando");
        /*File myFile = new File ("C:\\Users\\flcai\\Desktop\\Redes\\teste\\teste.pdf");
        os.writeObject(myFile);*/
        byte [] testeba = ByteUtils.intToBytes(123);
        os.writeObject(testeba);
        os.close();

        // Client receive the bytes
        final byte[] bytes = bos.toByteArray();

        ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(bytes));
        System.out.println(is.readInt());
        System.out.println(is.readInt());
        System.out.println(is.readInt());
        System.out.println(is.readUTF());
        System.out.println(ByteUtils.bytesToInteger((byte[]) is.readObject()));

    }

}
