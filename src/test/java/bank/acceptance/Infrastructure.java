package bank.acceptance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static java.lang.Thread.sleep;

public class Infrastructure {

    static int getPid(List<String> lines) {
        String firstLine = lines.get(0);
        String[] words = firstLine.split("\\s+");
        return Integer.parseInt(words[5]);
    }

    static List<String> outputLines(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return reader.lines().toList();
    }

    static void startApp() throws IOException, InterruptedException {
        new Thread(Infrastructure::runApp).start();
        int pid = waitForPid();
        System.out.println("App has started. Pid is " + pid);
    }

    static void kill() {
        try {
            Process exec = Runtime.getRuntime().exec("cmd /c taskkill /F /PID " + waitForPid());
            outputLines(exec.getInputStream()).forEach(System.out::println);
            outputLines(exec.getErrorStream()).forEach(System.out::println);

            System.out.println("Application has been killed");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int waitForPid() throws IOException, InterruptedException {
        for (int i = 0; i <= 10; i++) {
            Process exec = Runtime.getRuntime().exec("cmd /c netstat -aon | find \"8080\" | find \"LISTEN\"");
            List<String> outputLines = outputLines(exec.getInputStream());
            if (!outputLines.isEmpty()) return getPid(outputLines);
            System.out.println("App has not started yet");
            sleep(1000);
        }
        throw new RuntimeException("App has not started");
    }

    private static void runApp() {
        try {
            Process process = Runtime.getRuntime().exec("cmd /c mvn exec:java");

            ProcessHandler inputStream = new ProcessHandler(process.getInputStream(), "INPUT");
            ProcessHandler errorStream = new ProcessHandler(process.getErrorStream(), "ERROR");
            /* start the stream threads */
            inputStream.start();
            errorStream.start();

            //outputLines(proc.getErrorStream()).forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ProcessHandler extends Thread {
        InputStream inputStream;
        String streamType;

        public ProcessHandler(InputStream inputStream, String streamType) {
            this.inputStream = inputStream;
            this.streamType = streamType;
        }

        public void run() {
            try {
                InputStreamReader inpStrd = new InputStreamReader(inputStream);
                BufferedReader buffRd = new BufferedReader(inpStrd);
                String line = null;
                while ((line = buffRd.readLine()) != null) {
                    System.out.println(streamType+ "::" + line);
                }
                buffRd.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
