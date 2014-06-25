package com.starbugs.salut.util;

import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskListener;

/**
 * Created by starsasumi on 05/05/2014.
 */
public class GeneralTaskListener implements TaskListener {
    @Override
    public void taskExecuted(Task task) {
        System.out.println("GeneralTaskListener executed: " + task.getResult());
    }

    @Override
    public void executeFailed(Task task) {
        System.err.println("GeneralTaskListener failed to execute: " + task.getFault());
    }
}
