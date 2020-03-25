/*
 * The MIT License
 *
 * Copyright 2020 wentwrong.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.jenkins.plugins;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 *
 * @author wentwrong
 */
public class TestcafeStep extends Step {
    private final List<String> browsers;
    private final String src;
    
    @DataBoundConstructor
    public TestcafeStep(List<String> browsers, String src) {
        this.browsers = browsers;
        this.src = src;
    }
    
    public List<String> getBrowsers() {
        return browsers;
    }
    
    public String getSrc() {
        return src;
    }

    @Override 
    public StepExecution start(StepContext context) throws Exception {
        return new Execution(src, browsers, context);
    }

    @Extension 
    public static final class DescriptorImpl extends StepDescriptor {

        @Override 
        public String getFunctionName() {
            return "testcafe";
        }

        @Override 
        public String getDisplayName() {
            return "Runs testcafe";
        }

        @Override 
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.singleton(Launcher.class);
        }
    }
    
    public static class Execution extends SynchronousStepExecution<Void> {
        private final List<String> browsers;
        private final String src;
        
        Execution(String src, List<String> browsers, StepContext context) {
            super(context);
            this.src = src;
            this.browsers = browsers;
        }

        @Override 
        protected Void run() throws Exception {
            EnvVars stepEnvs = getContext().get(EnvVars.class);
            TaskListener stepTaskListener = getContext().get(TaskListener.class);
            FilePath workDir = getContext().get(FilePath.class);
            Run run = getContext().get(Run.class);
            
            run.addAction(new TestcafeAction());
            
            boolean isUnix = getContext().get(Launcher.class).isUnix();
            
            getContext()
                    .get(Launcher.class)
                    .launch()
                    .envs(stepEnvs)
                    .pwd(workDir)
                    .stdout(stepTaskListener.getLogger())
                    .cmds("testcafe" + (isUnix ? "" : ".cmd"), String.join(",", browsers), src)
                    .join();
            
            return null;
        }

        private static final long serialVersionUID = 1L;

    }
}
