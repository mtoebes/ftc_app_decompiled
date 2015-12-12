package com.qualcomm.robotcore.util;

import com.qualcomm.robotcore.exception.RobotCoreException;

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
        ProcessBuilder processBuilder = new ProcessBuilder(new String[0]);
        Process process = null;
        if (asRoot) {
            try {
                processBuilder.command(new String[]{"su", "-c", cmd}).redirectErrorStream(true);
            } catch (Exception e) {
                RobotLog.logStacktrace(e);
            }
        } else {
            processBuilder.command(new String[]{"sh", "-c", cmd}).redirectErrorStream(true);
        }

        try {
            process = processBuilder.start();
            process.waitFor();
            int read = process.getInputStream().read(bArr);
            if (read > 0) {
                output = new String(bArr, 0, read);
            }
        } catch (Exception e) {
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
                shell.run(String.format("kill %d", new Object[]{Integer.valueOf(spawnedProcessPid)}));
                spawnedProcessPid = getSpawnedProcessPid(processName, packageName, shell);
            }
        } catch (Exception e) {
            throw new RobotCoreException(String.format("Failed to kill %s instances started by this app", new Object[]{processName}));
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
