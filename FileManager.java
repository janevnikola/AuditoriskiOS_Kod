import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.io.*;

class FileExistsException extends Exception {
}

interface FileManager {

    void createNewFile(String file) throws IOException, FileExistsException;

    File[] getFilesInFolder(File file) throws FileNotFoundException;

    void printFileNames(File file, PrintStream writer) throws FileNotFoundException;

    String getAbsolutePath(String relativePath) throws FileNotFoundException;

    long getFileSize(String file) throws FileNotFoundException;

    void printFilePermissions(File f, PrintStream writer) throws FileNotFoundException;

    void createFolder(String folder) throws FileExistsException;

    void renameFile(File src, File dest) throws FileExistsException, FileNotFoundException;

    Date getLastModified(String filePath) throws FileNotFoundException;

    boolean deleteFolder(File folder) throws FileNotFoundException, FileExistsException;

    File[] filterImagesFilesInDir(String dirPath) throws FileNotFoundException;

    void filterImagesFilesInDirRec(File file, PrintStream output);
}


public class FileManagerImplementations implements FileManager {


    @Override
    public void createNewFile(String file) throws IOException, FileExistsException {
        File f = new File(file);//deklarirame instanca
        if (f.exists()) {//dokolku postoi toj fajl frli exception
            throw new FileExistsException();
        } else {
            f.createNewFile();//kreirame empty fajl
        }

    }

    @Override
    public File[] getFilesInFolder(File file) throws FileNotFoundException {
        if (!file.exists()) {//ako ne postoi fajl frli exception
            throw new FileNotFoundException();
        }
        if (!file.isDirectory()) {//ako ne e direktorium frli exception
            throw new FileNotFoundException();
        }
        return file.listFiles();//izlistaj gi fajlovite vo toj direktorium
    }

    @Override
    public void printFileNames(File file, PrintStream writer) throws FileNotFoundException {

        File[] niza_fajlovi = getFilesInFolder(file);//vo niza od fajlovi gi stavame site fajlovi
        for (File i : niza_fajlovi) {//za sekoj fajl vo niza_fajlovi
            writer.print(i.getName());//pecati go negovoto ime
        }
    }

    @Override
    public String getAbsolutePath(String relativePath) throws FileNotFoundException {
        File file = new File(relativePath);//deklarmirame fajl so relativnata pateka
        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        return file.getAbsolutePath();
    }

    @Override
    public long getFileSize(String file) throws FileNotFoundException {
        File f = new File(file);//string file go wrappuvame vo File f objekt za da moze da manipulirame
        if (!f.exists()) {
            throw new FileNotFoundException();
        }
        return f.length();
    }

    @Override
    public void printFilePermissions(File f, PrintStream writer) throws FileNotFoundException {
        if (!f.exists()) {
            throw new FileNotFoundException();
        }

        writer.print(f.canRead());
        writer.print(f.canWrite());
        writer.print(f.canExecute());
    }

    @Override
    public void createFolder(String folder) throws FileExistsException {
        File fold = new File(folder);//wrapuvame vo File objekt fold
        if (fold.exists()) {
            throw new FileExistsException();
        }
        fold.mkdir();//kreira folder kaj so e dadena patekata
        //fold.mkdirs() ke go kreira folderot i site folderi nad nego vo patekata dokolku ne postojat
    }

    @Override
    public void renameFile(File src, File dest) throws FileExistsException, FileNotFoundException {
        if (src.exists()) {
            throw new FileExistsException();//ako izvorniot postoi frli exception
        }
        if (dest.exists()) {
            throw new FileExistsException();//ako destinaciskiot postoi frli exception
        }
        src.renameTo(dest);//rename go
    }

    @Override
    public Date getLastModified(String filePath) throws FileNotFoundException {
        File f = new File(filePath);//wrapuvame vo File objekt
        if (!f.exists()) {//dokolku ne postoi frli exception
            throw new FileNotFoundException();
        }
        Date date = new Date(f.lastModified());//kreirame date konstruktor so f.lastmodificiran fajl

        return date;//vrati go datumot
    }

    @Override
    public boolean deleteFolder(File folder) throws FileNotFoundException, FileExistsException {

        if (!folder.exists()) {
            throw new FileNotFoundException();
        }
        if (!folder.isDirectory()) {
            throw new FileNotFoundException();
        }

        File[] fajlovi = folder.listFiles();//gi listame i stavame site fajlovi i direktoriumi vo fajlovi nizata

        for (File f : fajlovi) {//za sekoj eden fajl
            if (f.isDirectory()) {//dokolku e direktorium
                deleteFolder(f);//povikaj ja povtorno ovaa funkcija. Ovaa rekurzija ke odi nadolu niz stebloto, primer: C\Users\ADmini
            } else {
                f.delete();//ako ne e direktorium tuku e folder, izbrisi go
            }
        }
        return folder.delete();//go brisime root folderot koga ke izbriseme se so ima vo nego
    }

    @Override
    public File[] filterImagesFilesInDir(String dirPath) throws FileNotFoundException {
        File folder = new File(dirPath);
        if (!folder.exists()) {
            throw new FileNotFoundException();
        }
        if (!folder.isDirectory()) {
            throw new FileNotFoundException();
        }
        return folder.listFiles(new FilenameFilter() {//ke gi vrati site koi zavrsuvaat na .jpg ili .png
            @Override
            public boolean accept(File dir, String name) {//se sto zavrsuva na .jpg ili .png od direktoriumot dir so ime name VRATI
                return name.endsWith(".jpg") || name.endsWith(".png");
            }
        });
    }

    @Override
    public void filterImagesFilesInDirRec(File file, PrintStream output) {

    }
}
