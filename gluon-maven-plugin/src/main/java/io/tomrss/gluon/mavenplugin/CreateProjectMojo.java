package io.tomrss.gluon.mavenplugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "create", requiresProject = false)
public class CreateProjectMojo extends AbstractMojo {
    public void execute() throws MojoExecutionException {
        getLog().info("Hello world");
    }
}
