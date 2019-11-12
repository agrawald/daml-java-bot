# daml-java-bot

This application using DAML Java binding `Bot` API to stream active contract set from DAML backend, and saves them into a in-memory EhCache.

# how to run 

To set a project up:

    1. If you do not have it already, install the DAML SDK by running:
        `curl https://get.daml.com | sh -s 0.13.15`
    2. Build the Java code with Maven by running:
        `mvn compile`
    3. Start the sandbox by running:
         `daml start --sandbox-port 7600`
        > Note: this will take over your terminal, until you press CTRL-C to kill the sandbox. It will also open up your default web browser to show the Navigator, which will allow you to observe the contracts getting created by running one of the examples (see next step).