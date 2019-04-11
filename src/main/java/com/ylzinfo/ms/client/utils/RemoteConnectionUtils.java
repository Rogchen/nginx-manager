package com.ylzinfo.ms.client.utils;


import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.*;

/**
 * @Description:
 * @Product: IntelliJ IDEA
 * @Author Rogchen rogchen128@gmail.com
 * @Created Date: 2019/4/11 08:56
 **/
@Slf4j
public class RemoteConnectionUtils {

    private static String DEFAULTCHART = "UTF-8";

    /**
     * @Description 连接远程服务器
     * @Author Rogchen
     * @Date 9:08 2019/4/11
     * @Param [ip, port, username, password]
     * @Return ch.ethz.ssh2.Connection
     **/
    public static Connection login(String ip, int port, String username, String password) {
        boolean flag = false;
        try {
            Connection con = new Connection(ip, port);
            con.connect();
            flag = con.authenticateWithPassword(username, password);
            if (flag) {
                log.info("connection success...........");
                return con;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    public static String excute(Connection conn, String cmd, String chartset) {
        String result = "";
        try {
            if (conn != null) {
                Session session = conn.openSession();//打开一个会话
                session.execCommand(cmd);//执行命令
                result = processStdout(session.getStdout(), StringUtils.isBlank(chartset) ? DEFAULTCHART : chartset);
                //如果为得到标准输出为空，说明脚本执行出错了
                if (StringUtils.isBlank(result)) {
                    log.info("得到标准输出为空,链接conn:" + conn + ",执行的命令：" + cmd);
                    result = processStdout(session.getStderr(), DEFAULTCHART);
                } else {
                    log.info("执行命令成功,链接conn:" + conn + ",执行的命令：" + cmd);
                }
                conn.close();
                session.close();
            }
        } catch (IOException e) {
            log.info("执行命令失败,链接conn:" + conn + ",执行的命令：" + cmd + "  " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    private static String processStdout(InputStream in, String charset) {
        InputStream stdout = new StreamGobbler(in);
        StringBuffer buffer = new StringBuffer();
        ;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout, charset));
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line + "\n");
            }
        } catch (UnsupportedEncodingException e) {
            log.error("解析脚本出错：" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            log.error("解析脚本出错：" + e.getMessage());
            e.printStackTrace();
        }
        return buffer.toString();
    }

    private static SCPClient instance;

    public static synchronized SCPClient getInstance(String host, int port,
                                                     String username, String passward) {
        if (instance == null) {
            instance = new SCPClient(login(host, port, username, passward));
        }
        return instance;
    }

    public static void putFile(String localFile, String remoteTargetDirectory, Connection conn) {
        SCPClient client = new SCPClient(conn);
        try {
            client.put(localFile, remoteTargetDirectory);
            conn.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
