package com.teradata.test.examples;

import com.google.inject.Inject;
import com.teradata.test.process.CliProcess;
import com.teradata.test.ssh.SshClient;

import javax.inject.Named;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;

public class ExampleSshClientUsage
{
    private final SshClient sshClient;

    @Inject
    public ExampleSshClientUsage(@Named("td_express") SshClient sshClient) {
        this.sshClient = sshClient;
    }

    public void execute(String command) {
        try (CliProcess cliProcess = sshClient.execute(command)) {
            // Within this method std::out,std::err will both be printed to the log
            cliProcess.waitForWithTimeoutAndKill(Duration.ofMinutes(10));
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Execution was interrupted", e);
        }
    }

    public void upload(String what, String where) {
        sshClient.upload(Paths.get(what), where);
    }
}
