package com.example.embabelgamesagent;

import com.embabel.agent.config.annotation.EnableAgentShell;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("shell")
@EnableAgentShell
public class EmbabelShellConfig {}

