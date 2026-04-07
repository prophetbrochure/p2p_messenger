package app;

import java.io.IOException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import Network.Server;

public class ZeroConfProtocol {
    public void zeroConfProtocol(String localIP, int port, String Username, Server server) {
        try {
            JmDNS jmdns = JmDNS.create(localIP);
            ServiceInfo serviceInfo = ServiceInfo.create(
                "_p2pchat._tcp.local.",
                Username,
                port,
                "User=" + Username
            );

            jmdns.registerService(serviceInfo);
            
            jmdns.addServiceListener("_p2pchat._tcp.local.", new ServiceListener() {
                @Override
                public void serviceAdded(ServiceEvent event) {}
        
                @Override
                public void serviceRemoved(ServiceEvent event) {}
                
                @Override
                public void serviceResolved(ServiceEvent event) {
                    ServiceInfo info = event.getInfo();
                    
                    String ip = info.getHostAddresses()[0];
                    int port = info.getPort();
                    String peerUsername = info.getName();
                    
                    if (peerUsername.equals(Username)) {
                        return; // Это собственный сервис, пропускаем
                    }

                    server.connect(ip, port, Username, false);
                }
            });
        } catch (IOException e) {}
    }
}
