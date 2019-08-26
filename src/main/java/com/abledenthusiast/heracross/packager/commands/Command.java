package com.abledenthusiast.heracross.packager.commands;



public abstract class Command {


    public abstract String toExec();

    public Process exec() {
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(this.toExec());
        } catch(Exception e) {

        }
        return proc;
    }
}