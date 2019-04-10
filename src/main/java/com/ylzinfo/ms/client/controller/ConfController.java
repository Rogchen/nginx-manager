package com.ylzinfo.ms.client.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;

/**
 * @Description:
 * @Product: IntelliJ IDEA
 * @Author Rogchen rogchen128@gmail.com
 * @Created Date: 2019/4/10 10:07
 **/
@Controller
@Slf4j
public class ConfController {

    @GetMapping(value = {"/", "editNginx"})
    public String edit() throws IOException {
        return "nginxEdit";
    }

    @PostMapping("getConf")
    @ResponseBody
    public String getConf(String defautPath, String charsetName, Model model) throws InterruptedException, IOException {
        charsetName = StringUtils.isEmpty(charsetName) ? "utf-8" : charsetName;
        InputStreamReader reader = new InputStreamReader(new FileInputStream(defautPath), charsetName);
        BufferedReader br = new BufferedReader(reader);
        String line;
        StringBuffer sb = new StringBuffer(1024);
        //网友推荐更加简洁的写法
        while ((line = br.readLine()) != null) {
            // 一次读入一行数据
            System.out.println(line);
            sb.append(line).append("\r\n");
        }
        model.addAttribute("nginxconf", sb.toString());
        return sb.toString();
    }

    @PostMapping("updateConf")
    @ResponseBody
    public String updateConf(String path, String defautPath, String charsetName, String conf) throws InterruptedException, IOException {
        System.out.println(conf);
        charsetName = StringUtils.isEmpty(charsetName) ? "utf-8" : charsetName;
        File file = new File(defautPath);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), charsetName);
        BufferedWriter out = new BufferedWriter(outputStreamWriter);
        out.write(conf);
        out.flush();
        out.close();
        String os = System.getProperty("os.name");
        String cmd = "";
        if (os.toLowerCase().startsWith("win")) {
            cmd = "cmd /c \"d: && cd " + path + " && nginx -s reload\"";
        } else {    //linux
            cmd = "/bin/bash -c cd "+path;
        }
        String rt = exec(cmd);
        log.info("输出控制台返回：" + rt);
        return "updateConf";
    }


    public String exec(String command) throws InterruptedException {
        String returnString = "";
        Process pro = null;
        Runtime runTime = Runtime.getRuntime();
        if (runTime == null) {
            System.err.println("Create runtime false!");
        }
        try {
            log.info("begin shell....");
            pro = runTime.exec(command);
//            //输出执行结果
            BufferedReader input = new BufferedReader(new InputStreamReader(pro.getInputStream(), "gbk"));
            PrintWriter output = new PrintWriter(new OutputStreamWriter(pro.getOutputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                returnString = returnString + line + "\n";
            }
            input.close();
            output.close();
//            pro.destroy();
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
        return returnString;
    }
}
