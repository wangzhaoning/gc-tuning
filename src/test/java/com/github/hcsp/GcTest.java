package com.github.hcsp;

import com.github.kevinsawicki.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GcTest {
    // 在这个测试中，测试JVM会出现频繁的老年代的GC
    // 请思考一下为什么，并调整JVM的启动参数，使得老年代GC出现（Full GC/CMS GC）的次数小于3次。
    // 请不要调整-Xms和-Xmx
    private static final String JVM_ARGS = "-Dfile.encoding=UTF-8 -Xms256m -Xmx256m -Xmn130m -XX:SurvivorRatio=100 -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps";

    @Test
    public void isJava8() {
        Assertions.assertTrue(System.getProperty("java.version").startsWith("1.8.0"), "This test must be run on Java 8, now you're using " + System.getProperty("java.version"));
    }

    @Test
    public void noFullGc() throws InterruptedException {
        ApplicationThread application = new ApplicationThread();
        try {
            application.start();

            // Wait for startup
            Thread.sleep(10 * 1000);

            for (int i = 0; i < 100; ++i) {
                int statusCode = HttpRequest.get("http://localhost:8080/pdf")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .code();
                Assertions.assertEquals(200, statusCode);
            }
            String gcOutput = application.output.toString();
            Assertions.assertTrue(StringUtils.countMatches(gcOutput, "Full GC") < 3);
            Assertions.assertTrue(StringUtils.countMatches(gcOutput, "CMS-concurrent-sweep-start") < 3);
        } finally {
            application.kill();
        }
    }

    private static class ApplicationThread extends Thread {
        private Process applicationProcess;
        private StringBuffer output = new StringBuffer();

        @Override
        public synchronized void start() {
            List<String> args = new ArrayList<>(Arrays.asList(System.getProperty("java.home") + "/bin/java"));
            args.addAll(Arrays.asList(JVM_ARGS.split("\\s")));
            args.addAll(Arrays.asList("-jar", "target/gc-tuning-0.0.1.jar"));
            try {
                applicationProcess = new ProcessBuilder().command(args).start();
                connectStream(output, applicationProcess.getInputStream());
                connectStream(output, applicationProcess.getErrorStream());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }

            super.start();
        }

        public void kill() {
            if (applicationProcess != null) {
                applicationProcess.destroy();
            }
        }
    }

    static void connectStream(StringBuffer buffer, InputStream forkedProcessOutput) {
        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(forkedProcessOutput));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    buffer.append(line).append("\n");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
