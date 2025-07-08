package com.github.GanenkovNA;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.InputStream;

public class Main {
    public static void main(String[] args) {
        String host = "192.168.0.10"; // IP или хост устройства
        String username = "kit";    // SSH-логин
        String password = "aaa"; // SSH-пароль
        int port = 22;              // Порт SSH (обычно 22)
        String command = "ip a";    // Команда для выполнения

        try{
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, port);
            session.setPassword(password);

            // Отключаем проверку ключа хоста (небезопасно, но для теста подойдет)
            session.setConfig("StrictHostKeyChecking", "no");

            System.out.println("Подключение...");
            session.connect();

            // Открываем канал для выполнения команд
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);

            // Получаем вывод команды
            channel.setInputStream(null);
            InputStream in = channel.getInputStream();
            InputStream err = channel.getErrStream();

            channel.connect();

            // Читаем стандартный вывод
            byte[] buffer = new byte[1024];
            StringBuilder output = new StringBuilder();
            while (true) {
                while (in.available() > 0) {
                    int len = in.read(buffer, 0, 1024);
                    if (len < 0) break;
                    output.append(new String(buffer, 0, len));
                }
                if (channel.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("Код выхода: " + channel.getExitStatus());
                    break;
                }
                Thread.sleep(1000);
            }

            // Читаем ошибки (если есть)
            StringBuilder errorOutput = new StringBuilder();
            while (err.available() > 0) {
                int len = err.read(buffer, 0, 1024);
                if (len < 0) break;
                errorOutput.append(new String(buffer, 0, len));
            }

            if (!errorOutput.isEmpty()) {
                System.err.println("Ошибка: " + errorOutput);
            } else {
                System.out.println("Результат: \n" + output);
            }

            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}