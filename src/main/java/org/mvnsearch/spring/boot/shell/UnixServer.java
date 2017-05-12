package org.mvnsearch.spring.boot.shell;

import org.newsclub.net.unix.AFUNIXServerSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Vitasek L.
 */
@SuppressWarnings("Duplicates")
public class UnixServer {

    public BootShim bootShim;
    private final String path;

    public UnixServer(BootShim bootShim, String path) {
        this.bootShim = bootShim;
        this.path = path;
    }

    public BootShim getBootShim() {
        return bootShim;
    }

    public void setBootShim(BootShim bootShim) {
        this.bootShim = bootShim;
    }

    public void run() throws IOException {
        final File socketFile = new File(path);
        socketFile.deleteOnExit();

        //using PosixFilePermission to set file permissions 660 instead of 666
        Set<PosixFilePermission> perms = new HashSet<>();
        //add owners permission
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.remove(PosixFilePermission.OWNER_EXECUTE);
        //add group permissions
        perms.add(PosixFilePermission.GROUP_READ);
        perms.add(PosixFilePermission.GROUP_WRITE);
        perms.remove(PosixFilePermission.GROUP_EXECUTE);
        //add others permissions
        perms.remove(PosixFilePermission.OTHERS_READ);
        perms.remove(PosixFilePermission.OTHERS_WRITE);
        perms.remove(PosixFilePermission.OTHERS_EXECUTE);

        Files.setPosixFilePermissions(Paths.get(path), perms);

        final ExecutorService executorService = Executors.newCachedThreadPool();

        try (AFUNIXServerSocket server = AFUNIXServerSocket.newInstance()) {
            server.bind(new AFUNIXSocketAddress(socketFile));
            System.out.println("server: " + server);

            while (!Thread.interrupted()) {
                System.out.println("Waiting for connection...");
                executorService.execute(new ClientConnection(this, server.accept()));
            }
        } finally {
            executorService.shutdown();
        }
    }


}
