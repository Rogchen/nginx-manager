package com.ylzinfo.ms;

import org.junit.Test;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class NginxManagerApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void getpath(){
        String defautPath = "/home/ylz/nginx.conf";
        String finename = defautPath.substring(defautPath.lastIndexOf("/"),defautPath.length());
        System.out.println(finename);
    }
}
