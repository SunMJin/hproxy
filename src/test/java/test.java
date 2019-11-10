import org.apache.commons.compress.archivers.*;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;


import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class test {
    public static void main(String[] args) throws UnsupportedEncodingException {
        byte x[]=new byte[]{-97, -78, -2, -96, -25, -58, 85, 40, 124, -75, -73, -39, -120, 95, 120, -94};
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        byteArrayOutputStream.write(x,0,x.length);
        new ZipArchiveOutputStream(byteArrayOutputStream);

    }

    public static void zip(String srcDir, String targetFile) throws IOException {
        try (OutputStream fos = new FileOutputStream(targetFile);
             OutputStream bos = new BufferedOutputStream(fos);
             ArchiveOutputStream aos = new ZipArchiveOutputStream(bos)) {

            Path dirPath = Paths.get(srcDir);
            Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    ArchiveEntry entry = new ZipArchiveEntry(dir.toFile(), dirPath.relativize(dir).toString());
                    aos.putArchiveEntry(entry);
                    aos.closeArchiveEntry();
                    return super.preVisitDirectory(dir, attrs);
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    ArchiveEntry entry = new ZipArchiveEntry(file.toFile(), dirPath.relativize(file).toString());
                    aos.putArchiveEntry(entry);
                    IOUtils.copy(new FileInputStream(file.toFile()), aos);
                    aos.closeArchiveEntry();
                    return super.visitFile(file, attrs);
                }

            });
        }
    }

    }
