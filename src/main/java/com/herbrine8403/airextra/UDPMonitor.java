package com.herbrine8403.airextra;

import net.fabricmc.loader.api.FabricLoader;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.atomic.AtomicBoolean;

public class UDPMonitor implements Runnable {
    private static UDPMonitor instance;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean hasTouchController = new AtomicBoolean(false);
    private DatagramSocket socket;
    private Thread monitorThread;

    public static UDPMonitor getInstance() {
        if (instance == null) {
            instance = new UDPMonitor();
        }
        return instance;
    }

    public void start() {
        if (running.get()) return;
        
        AirExtraConfig config = AirExtraConfig.getInstance();
        running.set(true);
        hasTouchController.set(false);
        
        monitorThread = new Thread(this, "AirExtra-UDPMonitor");
        monitorThread.setDaemon(true);
        monitorThread.start();
        
        // 超时自动停止
        new Thread(() -> {
            try {
                Thread.sleep(config.udpTimeoutMinutes * 60L * 1000L);
                if (running.get() && !hasTouchController.get()) {
                    AirExtra.showWarning(config.touchControllerWarningText);
                }
                stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "AirExtra-UDPMonitor-Timeout").start();
    }

    public void stop() {
        if (!running.get()) return;
        running.set(false);
        
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        
        if (monitorThread != null) {
            monitorThread.interrupt();
        }
    }

    @Override
    public void run() {
        AirExtraConfig config = AirExtraConfig.getInstance();
        
        try {
            socket = new DatagramSocket(config.udpPort);
            socket.setSoTimeout(5000);
            
            byte[] buffer = new byte[1024];
            
            while (running.get()) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    
                    // 检查是否收到消息
                    if (packet.getLength() > 0) {
                        checkTouchController();
                        
                        if (hasTouchController.get()) {
                            stop();
                            break;
                        }
                    }
                } catch (Exception e) {
                    if (running.get()) {
                        // 超时是正常的，继续监听
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTouchController() {
        boolean touchControllerInstalled = FabricLoader.getInstance()
            .getModContainer("touchcontroller")
            .isPresent();
        
        if (touchControllerInstalled) {
            hasTouchController.set(true);
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    public boolean hasTouchController() {
        return hasTouchController.get();
    }
}