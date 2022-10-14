package io.tomrss.gluon.mavenplugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "create", requiresProject = false)
public class CreateProjectMojo extends AbstractMojo {

    @Parameter(property = "projectGroupId")
    private String projectGroupId;

    @Parameter(property = "projectArtifactId")
    private String projectArtifactId;

    @Parameter(property = "projectVersion")
    private String projectVersion;

    @Parameter(property = "basePackage")
    private String basePackage;

    @Parameter(property = "generationDirectory")
    private String generationDirectory;

    @Parameter(property = "rawFilesDirectory")
    private String rawFilesDirectory;


    @Parameter(property = "rawFilesDirectory")
    private String rawFilesDirectory;


    public void execute() throws MojoExecutionException {
        getLog().info("Hello world");
    }
}
