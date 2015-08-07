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
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Builder;

/**
 * Created by pfctgeorge on 15/8/6.
 */
public class AliyunOSSDownloader extends Builder {

    private PrintStream logger;
    String bucketName;
    String objectPath;

    @DataBoundConstructor
    public AliyunOSSDownloader(final String bucketName, final String objectPath) {
        this.bucketName = bucketName;
        this.objectPath = objectPath;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getObjectPath() {
        return objectPath;
    }

    public void setObjectPath(String objectPath) {
        this.objectPath = objectPath;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.STEP;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        @Override
        public String getDisplayName() {
            return "下载阿里云 OSS 上的文件";
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
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
        throws InterruptedException, IOException {
        AliyunOSSJobProperty.DescriptorImpl
            descriptor =
            AliyunOSSJobProperty.getAliyunOSSJobPropertyDescriptor();
        this.logger = listener.getLogger();
        try {
            AliyunOSSClient.download(build, listener,
                                     descriptor.getAliyunAccessKey(),
                                     descriptor.getAliyunSecretKey(),
                                     descriptor.getAliyunEndPointSuffix(),
                                     bucketName, objectPath);
            return true;

        } catch (Exception e) {
            this.logger.println("下载阿里云 OSS 上的文件失败，错误消息如下:");
            this.logger.println(e.getMessage());
            e.printStackTrace(this.logger);
            return false;
        }
    }
}
