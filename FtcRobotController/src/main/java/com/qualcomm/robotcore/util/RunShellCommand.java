package com.qualcomm.robotcore.util;

import com.qualcomm.robotcore.exception.RobotCoreException;

public class RunShellCommand {
    private final static int BUFFER_SIZE = 524288;
    boolean logging = false;

    public RunShellCommand() {
    }

    public void enableLogging(boolean enable) {
        this.logging = enable;
    }

    public String run(String cmd) {
        if (this.logging) {
            RobotLog.v("running command: " + cmd);
        }
        String output = runCommand(cmd, false);
        if (this.logging) {
            RobotLog.v("         output: " + output);
        }
        return output;
    }

    public String runAsRoot(String cmd) {
        if (this.logging) {
            RobotLog.v("running command: " + cmd);
        }
        String a = runCommand(cmd, true);
        if (this.logging) {
            RobotLog.v("         output: " + a);
        }
        return a;
    }

    private String runCommand(String command, boolean isRoot) {
        String output = "";
        ProcessBuilder processBuilder = new ProcessBuilder();
        Process process = null;

        // send command
        if (isRoot) {
            processBuilder.command("su", "-c", command).redirectErrorStream(true);
        } else {
            processBuilder.command("sh", "-c", command).redirectErrorStream(true);
        }

        // read output
        try {
            process = processBuilder.start();
            process.waitFor();
            byte[] buffer = new byte[BUFFER_SIZE];

            int read = process.getInputStream().read(buffer);
            if (read > 0) {
                output = new String(buffer, 0, read);
            }
        } catch (Exception exception) {
            // do nothing
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return output;
    }

    public static void killSpawnedProcess(String processName, String packageName, RunShellCommand shell) throws RobotCoreException {
        try {
            int spawnedProcessPid = getSpawnedProcessPid(processName, packageName, shell);
            while (spawnedProcessPid != -1) {
                RobotLog.v("Killing PID " + spawnedProcessPid);
                shell.run(String.format("kill %d", spawnedProcessPid));
                spawnedProcessPid = getSpawnedProcessPid(processName, packageName, shell);
            }
        } catch (Exception e) {
            throw new RobotCoreException(String.format("Failed to kill %s instances started by this app", processName));
        }
    }

    public static int getSpawnedProcessPid(String processName, String packageName, RunShellCommand shell) {
        String run = shell.run("ps");
        CharSequence username = "invalid";
        for (String str : run.split("\n")) {
            if (str.contains(packageName)) {
                username = str.split("\\s+")[0];
                break;
            }
        }

        for(String line : run.split("\n")) {
            if (line.contains(processName) && line.contains(username)) {
                return Integer.parseInt(line.split("\\s+")[1]);
            }
        }
        return -1;
    }
}
