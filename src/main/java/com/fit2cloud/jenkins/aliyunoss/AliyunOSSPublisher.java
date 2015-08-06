package com.fit2cloud.jenkins.aliyunoss;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.io.PrintStream;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;

public class AliyunOSSPublisher extends Publisher {

    private PrintStream logger;
    String bucketName;
    String filesPath;
    String objectPrefix;

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getFilesPath() {
        return filesPath;
    }

    public void setFilesPath(String filesPath) {
        this.filesPath = filesPath;
    }

    public String getObjectPrefix() {
        return objectPrefix;
    }

    public void setObjectPrefix(String objectPrefix) {
        this.objectPrefix = objectPrefix;
    }

    @DataBoundConstructor
    public AliyunOSSPublisher(final String bucketName, final String filesPath,
                              final String objectPrefix) {
        this.bucketName = bucketName;
        this.filesPath = filesPath;
        this.objectPrefix = objectPrefix;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.STEP;
    }

    @Override
    public DescriptorImpl getDescriptor() {

        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public DescriptorImpl() {
            load();
        }

        @Override
        public String getDisplayName() {
            return "上传 Artifact 到阿里云 OSS";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }


        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindJSON(this, formData);
            save();
            return super.configure(req, formData);
        }

    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener)
        throws InterruptedException, IOException {
        AliyunOSSJobProperty.DescriptorImpl
            descriptor =
            AliyunOSSJobProperty.getAliyunOSSJobPropertyDescriptor();
        this.logger = listener.getLogger();
        final boolean buildFailed = build.getResult() == Result.FAILURE;
        if (buildFailed) {
            logger.println("Job构建失败,无需上传Aritfacts到阿里云OSS.");
            return true;
        }

        // Resolve file path
        String expFP = Utils.replaceTokens(build, listener, filesPath);

        if (expFP != null) {
            expFP = expFP.trim();
        }

        // Resolve virtual path
        String expVP = Utils.replaceTokens(build, listener, objectPrefix);
        if (Utils.isNullOrEmpty(expVP)) {
            expVP = null;
        }
        if (!Utils.isNullOrEmpty(expVP) && !expVP.endsWith(Utils.FWD_SLASH)) {
            expVP = expVP.trim() + Utils.FWD_SLASH;
        }

        boolean success = false;
        try {
            int
                filesUploaded =
                AliyunOSSClient.upload(build, listener, descriptor.getAliyunAccessKey(),
                                       descriptor.getAliyunSecretKey(),
                                       descriptor.getAliyunEndPointSuffix(), bucketName, expFP,
                                       expVP);
            if (filesUploaded > 0) {
                listener.getLogger().println("上传Artifacts到阿里云OSS成功，上传文件个数:" + filesUploaded);
                success = true;
            }

        } catch (Exception e) {
            this.logger.println("上传Artifact到阿里云OSS失败，错误消息如下:");
            this.logger.println(e.getMessage());
            e.printStackTrace(this.logger);
            success = false;
        }
        return success;
    }

}
