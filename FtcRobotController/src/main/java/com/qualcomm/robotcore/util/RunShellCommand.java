package com.qualcomm.robotcore.util;

import com.qualcomm.robotcore.exception.RobotCoreException;

import java.io.IOException;

public class RunShellCommand {
    private final static int BUFFER_SIZE = 0x80000;
    boolean logging;

    public RunShellCommand() {
        this.logging = false;
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
        String output = runCommand(cmd, true);
        if (this.logging) {
            RobotLog.v("         output: " + output);
        }
        return output;
    }

    private String runCommand(String cmd, boolean asRoot) {
        byte[] bArr = new byte[BUFFER_SIZE];
        String output = "";
        ProcessBuilder processBuilder = new ProcessBuilder();
        Process process = null;
        if (asRoot) {
            try {
                processBuilder.command("su", "-c", cmd).redirectErrorStream(true);
            } catch (RuntimeException e) {
                RobotLog.logStacktrace(e);
            }
        } else {
            processBuilder.command("sh", "-c", cmd).redirectErrorStream(true);
        }

        try {
            process = processBuilder.start();
            process.waitFor();
            int read = process.getInputStream().read(bArr);
            if (read > 0) {
                output = new String(bArr, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (RuntimeException ignored) {
            throw new RobotCoreException(String.format("Failed to kill %s instances started by this app", processName));
        }
    }

    public static int getSpawnedProcessPid(String processName, String packageName, RunShellCommand shell) {
        String run = shell.run("ps");
        CharSequence charSequence = "invalid";
        for (String str : run.split("\n")) {
            if (str.contains(packageName)) {
                charSequence = str.split("\\s+")[0];
                break;
            }
        }
        String[] split = run.split("\n");

        for(String line : split) {
            if (line.contains(processName) && line.contains(charSequence)) {
                return Integer.parseInt(line.split("\\s+")[1]);
            }
        }
        return -1;
    }
}
