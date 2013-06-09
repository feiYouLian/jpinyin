package opensource.jpinyin;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Test
{
  public static void main(String[] args)
    throws IOException
  {
    compressFile(args[0], args[1]);
  }

  public static void compressFile(String fromFilename, String toFilename) throws IOException {
    BufferedInputStream in = new BufferedInputStream(new FileInputStream(fromFilename));
    ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(toFilename));
    ZipEntry entry = new ZipEntry("JPinyin");
    entry.setSize(in.available());
    zip.putNextEntry(entry);

    byte[] buffer = new byte[1024];
    int n;
    while ((n = in.read(buffer)) > -1)
    {
      zip.write(buffer, 0, n);
    }
    zip.close();
    in.close();
  }
}