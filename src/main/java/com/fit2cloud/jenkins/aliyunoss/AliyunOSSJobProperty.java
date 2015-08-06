package com.fit2cloud.jenkins.aliyunoss;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import jenkins.model.Jenkins;

public class AliyunOSSJobProperty extends JobProperty<AbstractProject<?, ?>> {

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) Jenkins.getInstance().getDescriptor(getClass());
    }

    public static DescriptorImpl getAliyunOSSJobPropertyDescriptor() {
        return (DescriptorImpl) Jenkins.getInstance().getDescriptor(AliyunOSSJobProperty.class);
    }

    @Extension
    public static final class DescriptorImpl extends JobPropertyDescriptor {

        private String aliyunAccessKey;
        private String aliyunSecretKey;
        private String aliyunEndPointSuffix;

        public DescriptorImpl() {
            super(AliyunOSSJobProperty.class);
            load();
        }

        @DataBoundConstructor
        public DescriptorImpl(String aliyunAccessKey,
                              String aliyunSecretKey,
                              String aliyunEndPointSuffix) {
            this.aliyunAccessKey = aliyunAccessKey;
            this.aliyunSecretKey = aliyunSecretKey;
            this.aliyunEndPointSuffix = aliyunEndPointSuffix;
        }

        public String getAliyunAccessKey() {
            return aliyunAccessKey;
        }

        public void setAliyunAccessKey(String aliyunAccessKey) {
            this.aliyunAccessKey = aliyunAccessKey;
        }

        public String getAliyunSecretKey() {
            return aliyunSecretKey;
        }

        public void setAliyunSecretKey(String aliyunSecretKey) {
            this.aliyunSecretKey = aliyunSecretKey;
        }

        public String getAliyunEndPointSuffix() {
            return aliyunEndPointSuffix;
        }

        public void setAliyunEndPointSuffix(String aliyunEndPointSuffix) {
            this.aliyunEndPointSuffix = aliyunEndPointSuffix;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindJSON(this, formData);
            save();
            return super.configure(req, formData);
        }

        @Override
        public boolean isApplicable(java.lang.Class<? extends Job> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "阿里云 OSS 账号设置";
        }

    }
}