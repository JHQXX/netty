package cn.itcast.netty.c1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestFilesCopy {
    public static void main(String[] args) throws IOException {
        String source="D:\\development\\phpstudy_pro\\COM";
        String target="D:\\development\\phpstudy_pro\\COMaaa";


        Files.walk(Paths.get(source)).forEach(path -> {
            try {
                String targetName=path.toString().replace(source,target);

                //表示为目录
                if (Files.isDirectory(path)) {
                    //D:\development\phpstudy_pro\COM\a   ==>   D:\development\phpstudy_pro\COMaaa\a
                    Files.createDirectory(Paths.get(targetName));
                }
                //普通文件
                else if (Files.isRegularFile(path)){
                    Files.copy(path,Paths.get(targetName));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


    }
}
