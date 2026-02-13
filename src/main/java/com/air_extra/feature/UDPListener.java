package com.air_extra.feature;

import com.air_extra.AirExtraClient;
import com.air_extra.config.AirExtraConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class UDPListener {
    
    private static final AtomicBoolean running = new AtomicBoolean(false);
    private static Thread listenerThread;
    private static DatagramSocket socket;
    private static volatile boolean hasReceivedMessage = false;
    
    public static void start(MinecraftClient client, AirExtraConfig config) {
        if (running.get() || !config.isEnableUDPListener()) {
            AirExtraClient.LOGGER.info("UDP Listener not started: running={}, enabled={}", 
                running.get(), config.isEnableUDPListener());
            return;
        }
        
        running.set(true);
        hasReceivedMessage = false;
        
        listenerThread = new Thread(() -> {
            listen(client, config);
        }, "AirExtra-UDP-Listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
        
        AirExtraClient.LOGGER.info("UDP Listener started on port {}", config.getUdpPort());
    }
    
    private static void listen(MinecraftClient client, AirExtraConfig config) {
        try {
            // 绑定到 localhost (IPv4)
            InetAddress localhost = InetAddress.getByName("127.0.0.1");
            socket = new DatagramSocket(null);
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(localhost, config.getUdpPort()));
            
            // 设置较短的读取超时，以便可以定期检查 running 状态
            socket.setSoTimeout(5000);
            
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            
            AirExtraClient.LOGGER.info("UDP Listener listening on 127.0.0.1:{}", config.getUdpPort());
            
            while (running.get()) {
                try {
                    socket.receive(packet);
                    
                    if (packet.getLength() > 0) {
                        String message = new String(packet.getData(), 0, packet.getLength()).trim();
                        AirExtraClient.LOGGER.info("Received UDP message from {}:{} - length: {}, content: {}", 
                            packet.getAddress(), packet.getPort(), packet.getLength(), message);
                        
                        // 检查 TouchController 模组
                        checkTouchController(client, config);
                        
                        hasReceivedMessage = true;
                    }
                } catch (java.net.SocketTimeoutException e) {
                    // 超时是正常的，继续循环检查 running 状态
                    continue;
                } catch (java.net.SocketException e) {
                    if (running.get()) {
                        AirExtraClient.LOGGER.warn("UDP socket exception: {}", e.getMessage());
                    }
                    break;
                }
            }
            
        } catch (Exception e) {
            if (running.get()) {
                AirExtraClient.LOGGER.error("UDP Listener error: {}", e.getMessage());
                e.printStackTrace();
            }
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            running.set(false);
            AirExtraClient.LOGGER.info("UDP Listener stopped (received message: {})", hasReceivedMessage);
        }
    }
    
    private static void checkTouchController(MinecraftClient client, AirExtraConfig config) {
        boolean hasTouchController = FabricLoader.getInstance()
            .getModContainer("touchcontroller")
            .isPresent();
        
        AirExtraClient.LOGGER.info("TouchController mod present: {}, player in world: {}", 
            hasTouchController, client.player != null);
        
        if (!hasTouchController && client.player != null) {
            AirExtraClient.LOGGER.info("Showing TouchController warning toast");
            ToastHelper.showWarningToast(client, config.touchControllerWarningText);
        }
    }
    
    public static void stop() {
        running.set(false);
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
        AirExtraClient.LOGGER.info("UDP Listener stop requested");
    }
    
    public static boolean isRunning() {
        return running.get();
    }
    
    public static boolean hasReceivedMessage() {
        return hasReceivedMessage;
    }
}
