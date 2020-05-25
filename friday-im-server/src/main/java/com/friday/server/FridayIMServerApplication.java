package com.friday.server;

import com.friday.server.server.FridayIMIntelServer;
import com.friday.server.server.FridayIMServer;
import com.friday.server.zk.RegistryZK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@SpringBootApplication
public class FridayIMServerApplication implements CommandLineRunner {

    @Autowired
    private FridayIMServer fridayIMServer;
    @Autowired
    private FridayIMIntelServer fridayIMIntelServer;

    public static void main(String[] args) {
        SpringApplication.run(FridayIMServerApplication.class, args);
        log.info("Friday IM Server Application is run ^_^");
    }

    @Value("${server.port}")
    private int httpPort;

    @Value("${netty.server.port}")
    private int imServerPort;

    @Override
    public void run(String... args) throws UnknownHostException {
        String addr = InetAddress.getLocalHost().getHostAddress();
        Thread thread = new Thread(new RegistryZK(addr, imServerPort, httpPort));
        thread.setName("REGISTRY-ZK");
        thread.start();
        fridayIMServer.start();
        fridayIMIntelServer.start();
    }
}
