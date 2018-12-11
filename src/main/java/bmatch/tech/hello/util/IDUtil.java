package bmatch.tech.hello.util;

import java.util.UUID;

public class IDUtil {

    /**
     * 获取主键
     * @return
     */
    public static String getId(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
