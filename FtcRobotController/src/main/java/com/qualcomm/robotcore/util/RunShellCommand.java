/*
 * Copyright (c) 2014, 2015 Qualcomm Technologies Inc
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * (subject to the limitations in the disclaimer below) provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Qualcomm Technologies Inc nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE. THIS
 * SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.qualcomm.robotcore.util;

import com.qualcomm.robotcore.exception.RobotCoreException;

public class RunShellCommand {
    private final static int BUFFER_SIZE = 0x80000;
    boolean logging;

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

        for (String line : split) {
            if (line.contains(processName) && line.contains(charSequence)) {
                return Integer.parseInt(line.split("\\s+")[1]);
            }
        }
        return -1;
    }
}
