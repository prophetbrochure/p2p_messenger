package p2pmessenger.network;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

public class ZeroConfProtocol {
    public void zeroConfProtocol(String localIP, int port, String username, Server server) {
        try {
            InetAddress inetIP = InetAddress.getByName(localIP);
            JmDNS jmdns = JmDNS.create(inetIP);
            ServiceInfo serviceInfo = ServiceInfo.create(
                "_p2pchat._tcp.local.",
                username,
                port,
                "User=" + username
            );

            jmdns.registerService(serviceInfo);
            
            jmdns.addServiceListener("_p2pchat._tcp.local.", new ServiceListener() {
                @Override
                public void serviceAdded(ServiceEvent event) {
                    jmdns.requestServiceInfo(event.getType(), event.getName(), 1);
                }
        
                @Override
                public void serviceRemoved(ServiceEvent event) {
                }
                
                @Override
                public void serviceResolved(ServiceEvent event) {
                    ServiceInfo info = event.getInfo();
                    
                    String ip = info.getHostAddresses()[0];
                    int port = info.getPort();
                    String peerUsername = info.getName();
                    
                    if (peerUsername.equals(username)) {
                        return; // Это собственный сервис, пропускаем
                    }

                    server.connect(ip, port, username, false);
                }
            });
        } catch (IOException e) {}
    }
}
