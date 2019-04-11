package com.ylzinfo.ms.client.controller;

import ch.ethz.ssh2.Connection;
import com.ylzinfo.ms.client.utils.RemoteConnectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;

/**
 * @Description: 连接linux服务并执行脚本
 * @Product: IntelliJ IDEA
 * @Author Rogchen rogchen128@gmail.com
 * @Created Date: 2019/4/11 09:28
 **/
@Controller
@RequestMapping("shell")
@Slf4j
public class ConShellController {


    @GetMapping(value = {"", "/","index", "editNginx"})
    public String edit() throws IOException {
        return "nginxShellEdit";
    }

    @PostMapping("loginTest")
    @ResponseBody
    public String loginTest(String host, int port, String username, String password){
        Connection con = RemoteConnectionUtils.login(host, port, username, password);
        if(con !=null){
            con.close();
            return "success";
        }
        return "error";
    }

    @PostMapping("getConf")
    @ResponseBody
    public String getConf(String host, int port, String username, String password, String defautPath, String charsetName) throws InterruptedException, IOException {
        charsetName = StringUtils.isEmpty(charsetName) ? "utf-8" : charsetName;
        String cmd = "/bin/bash -c \"cat " + defautPath + "\"";
        Connection con = RemoteConnectionUtils.login(host, port, username, password);
        String rt = RemoteConnectionUtils.excute(con, cmd, charsetName);
        return rt;
    }

    @PostMapping("saveConf")
    @ResponseBody
    public String saveConf(String host, int port, String username, String password, String defautPath, String charsetName, String conf) throws InterruptedException, IOException {
        charsetName = StringUtils.isEmpty(charsetName) ? "utf-8" : charsetName;
        //本地创建临时文件
        String tmp = this.getClass().getResource("/").getPath();
        log.info("临时文件目录："+tmp);
        File file = new File(tmp);
        if (!file.exists()) {
            file.mkdir();
        }
        //写入数据到本地
        String finename = defautPath.substring(defautPath.lastIndexOf("/"), defautPath.length());
        String filepath = defautPath.substring(0, defautPath.lastIndexOf("/"));
        File f1 = new File(tmp + finename);
        f1.createNewFile();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(f1), charsetName);
        BufferedWriter out = new BufferedWriter(outputStreamWriter);
        out.write(conf);
        out.flush();
        out.close();
        Connection con = RemoteConnectionUtils.login(host, port, username, password);
        RemoteConnectionUtils.putFile(tmp + finename, filepath, con);
        f1.delete();
        return "success";
    }

    @PostMapping("excute")
    public String runExcute(Model model,String host, int port, String username, String password, String charsetName, String path) {
        charsetName = StringUtils.isEmpty(charsetName) ? "utf-8" : charsetName;
        Connection con = RemoteConnectionUtils.login(host, port, username, password);
        String cmd = "/bin/bash -c \"cd " + path + " && nginx -s reload\"";
        String rt = RemoteConnectionUtils.excute(con, cmd, charsetName);
        log.info(rt);
        model.addAttribute("result",rt);
        return "success";
    }

}
