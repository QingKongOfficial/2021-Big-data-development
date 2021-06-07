package FileSynchronizer;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class FileSynchronizer {

    public static void main(String[] args) {
        ThreadPoolExecutor threadPool=
                new ThreadPoolExecutor(3, 10, 30, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        final String path="F:\\\\WORKS\\Eclipse\\2021-Big-data-development-main\\大数据怎么存\\";
        S3Util.DownLoadFile(path);
        WatchService watchService= null;
        final CopyOnWriteArraySet<String> Monitor=new CopyOnWriteArraySet<>();
        new Timer().schedule(new TimerTask() {
    
            public void run() {
                Monitor.clear();
            }
        }, 0,1000);
        try {
            watchService = FileSystems.getDefault().newWatchService();
            Path dir = Paths.get(path);
            dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);
            while (true){
                WatchKey key=watchService.take();
                List<WatchEvent<?>> eventList=key.pollEvents();
                for(final WatchEvent<?> event:eventList){
                    if(event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)){
                            Monitor.add(path+event.context());
                            System.out.println("create the file "+event.context()+" ,now begin to synchronize");
                            FutureTask<Void> task = new FutureTask<>(new Callable<Void>() {
                                @Override
                                public Void call() {
                                    if (S3Util.UpLoadFile(path + event.context())) {
                                        System.out.println("file " + path + event.context() + " update successfully");
                                    }
                                    else
                                        System.out.println("file " + path + event.context() + " failed to update");
                                    return null;
                                }
                            });
                            threadPool.execute(task);
                    }else if(event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)){
                        System.out.println("delete the file "+event.context()+" ,now begin to synchronize");
                        FutureTask<Void> task=new FutureTask<>(new Callable<Void>() {
                    
                            public Void call() {
                                if(S3Util.DeleteFile(path+event.context()))
                                    System.out.println("file " + path + event.context() + "successfully delete");
                                else
                                    System.out.println("file " + path + event.context() + "failed to delete");
                                return null;
                            }
                        });
                        threadPool.execute(task);
                    }else if(event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY)){
                        if(!Monitor.contains(path+event.context())) {
                            System.out.println("change the file " + event.context() + " ,now begin to synchronize");
                            FutureTask<Void> task = new FutureTask<>(new Callable<Void>() {
                                @Override
                                public Void call() {
                                    if(S3Util.UpLoadFile(path + event.context()))
                                        System.out.println("file " + path + event.context() + " update successfully");
                                    else
                                        System.out.println("file " + path + event.context() + " failed to update");
                                    return null;
                                }
                            });
                            threadPool.execute(task);
                        }
                    }
                }
                key.reset();
            }
        }catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally{
            threadPool.shutdown();
            threadPool=null;
            watchService=null;
            System.gc();
        }

    }

}
