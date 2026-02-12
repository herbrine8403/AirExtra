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
    
    private final AirExtraConfig config;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread listenerThread;
    private DatagramSocket socket;
    
    public UDPListener(AirExtraConfig config) {
        this.config = config;
    }
    
    public void start() {
        if (running.get()) return;
        
        running.set(true);
        listenerThread = new Thread(this::listen, "AirExtra-UDP-Listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
        
        AirExtraClient.LOGGER.info("UDP Listener started on port {}", config.getUdpPort());
        
        Thread timeoutThread = new Thread(() -> {
            try {
                Thread.sleep(config.getUdpTimeoutMinutes() * 60 * 1000L);
                if (running.get()) {
                    stop();
                    AirExtraClient.LOGGER.info("UDP Listener timed out after {} minutes", config.getUdpTimeoutMinutes());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "AirExtra-UDP-Timeout");
        timeoutThread.setDaemon(true);
        timeoutThread.start();
    }
    
    private void listen() {
        try {
            socket = new DatagramSocket(config.getUdpPort(), InetAddress.getByName("127.0.0.1"));
            socket.setSoTimeout(1000);
            
            byte[] buffer = new byte[1024];
            
            while (running.get()) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    
                    String message = new String(packet.getData(), 0, packet.getLength()).trim();
                    AirExtraClient.LOGGER.info("Received UDP message: {}", message);
                    
                    handleReceivedMessage(message);
                    stop();
                    break;
                    
                } catch (java.net.SocketTimeoutException e) {
                    // Continue loop
                }
            }
            
        } catch (Exception e) {
            if (running.get()) {
                AirExtraClient.LOGGER.warn("UDP Listener error: {}", e.getMessage());
            }
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
    
    private void handleReceivedMessage(String message) {
        boolean hasTouchController = FabricLoader.getInstance()
            .getModContainer("touchcontroller")
            .isPresent();
        
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (!hasTouchController) {
            client.execute(() -> {
                ToastHelper.showWarningToast(client, config.touchControllerWarningText);
            });
        } else {
            AirExtraClient.LOGGER.info("TouchController mod detected");
        }
    }
    
    public void stop() {
        running.set(false);
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
        AirExtraClient.LOGGER.info("UDP Listener stopped");
    }
    
    public boolean isRunning() {
        return running.get();
    }
}
