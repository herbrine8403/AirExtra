package com.air_extra.feature;

import com.air_extra.AirExtraClient;
import com.air_extra.config.AirExtraConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class UDPListener {
    
    private static final AtomicBoolean running = new AtomicBoolean(false);
    private static Thread listenerThread;
    private static DatagramSocket socket;
    
    public static void start(MinecraftClient client, AirExtraConfig config) {
        if (running.get() || !config.isEnableUDPListener()) {
            return;
        }
        
        running.set(true);
        listenerThread = new Thread(() -> {
            listen(client, config);
        }, "AirExtra-UDP-Listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
        
        AirExtraClient.LOGGER.info("UDP Listener started on port {}", config.getUdpPort());
    }
    
    private static void listen(MinecraftClient client, AirExtraConfig config) {
        try {
            socket = new DatagramSocket(config.getUdpPort(), InetAddress.getByName("127.0.0.1"));
            socket.setSoTimeout(config.getUdpTimeoutMinutes() * 60 * 1000);
            
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            
            socket.receive(packet);
            String message = new String(packet.getData(), 0, packet.getLength()).trim();
            
            AirExtraClient.LOGGER.info("Received UDP message: {}", message);
            checkTouchController(client, config);
            
        } catch (Exception e) {
            if (running.get()) {
                AirExtraClient.LOGGER.warn("UDP Listener error: {}", e.getMessage());
            }
        } finally {
            stop();
        }
    }
    
    private static void checkTouchController(MinecraftClient client, AirExtraConfig config) {
        boolean hasTouchController = FabricLoader.getInstance()
            .getModContainer("touchcontroller")
            .isPresent();
        
        if (!hasTouchController && client.player != null) {
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
        AirExtraClient.LOGGER.info("UDP Listener stopped");
    }
    
    public static boolean isRunning() {
        return running.get();
    }
}
