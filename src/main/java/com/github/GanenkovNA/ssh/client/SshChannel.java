package com.github.GanenkovNA.ssh.client;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;

public class SshChannel {
    private final Session session;
    private Integer exitStatus;

    public SshChannel(Session session) {
        this.session = session;
    }

    public String[] execChannel(String command){
        try{
            // Открываем канал для выполнения команд
            ChannelExec channel = (ChannelExec) session.openChannel("exec");

            // Указываем комманду
            channel.setCommand(command);
            // Указываем, что не будем передавать данные на сервер
            channel.setInputStream(null);

            // Получаем потоки  вывода и ошибок
            InputStream in = channel.getInputStream();
            InputStream err = channel.getErrStream();
            // Создаём буферы для хранения вывода
            StringBuilder stdoutBuffer = new StringBuilder();
            StringBuilder stderrBuffer = new StringBuilder();
            // Поток для чтения stdout
            Thread stdoutThread = new Thread(() -> printStream(in, stdoutBuffer));
            // Поток для чтения stderr
            Thread stderrThread = new Thread(() -> printStream(err, stderrBuffer));

            // Запускаем потоки и подключаем канал
            stdoutThread.start();
            stderrThread.start();
            channel.connect();

            // Ждём завершения потоков
            stdoutThread.join();
            stderrThread.join();

            // Завершаем все процессы
            in.close();
            err.close();
            channel.disconnect();

            // Проверяем код завершения
            exitStatus = channel.getExitStatus();

            return new String[] {
                    exitStatus.toString(),
                    stdoutBuffer.toString(),
                    stderrBuffer.toString()
            };
        }catch (JSchException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // Метод для печати потока
    private static void printStream(InputStream stream, StringBuilder stdBuffer) {
        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = stream.read(buffer)) > 0) {
                stdBuffer.append(new String(buffer, 0, len));
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка печати потока", e);
        }
    }
}
