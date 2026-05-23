package com.rve.systemmonitor.shizuku;

interface ICommandRunner {
    String executeCommand(String command);
    void destroy();
}
