/*************************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *************************GO-LICENSE-END***********************************/

package com.thoughtworks.go.server.service;

import com.thoughtworks.go.server.messaging.EmailMessageDrafter;
import com.thoughtworks.go.server.messaging.SendEmailMessage;
import com.thoughtworks.go.server.service.result.OperationResult;
import com.thoughtworks.go.serverhealth.HealthStateType;
import com.thoughtworks.go.util.SystemEnvironment;
import org.apache.log4j.Logger;

public class ArtifactsDiskSpaceFullChecker extends DiskSpaceChecker {
    private static final Logger LOGGER = Logger.getLogger(ArtifactsDiskSpaceFullChecker.class);
    public static final HealthStateType ARTIFACTS_DISK_FULL_ID = HealthStateType.artifactsDiskFull();

    public ArtifactsDiskSpaceFullChecker(SystemEnvironment systemEnvironment, EmailSender sender,
                                         GoConfigService goConfigService, final SystemDiskSpaceChecker diskSpaceChecker) {
        super(sender, systemEnvironment, goConfigService.artifactsDir(), goConfigService, ARTIFACTS_DISK_FULL_ID, diskSpaceChecker);
    }

    //for constructing SchedulingChecker
    public ArtifactsDiskSpaceFullChecker(SystemEnvironment systemEnvironment,
                                         GoConfigService goConfigService) {
        this(systemEnvironment, null, goConfigService, new SystemDiskSpaceChecker());
    }

    protected void createFailure(OperationResult result, long size, long availableSpace) {
        String msg = "Go has less than " + size + "Mb of disk space available. Scheduling has stopped, and will resume once more than " + size + "Mb is available.";
        LOGGER.error(msg);
        result.error("Go Server has run out of artifacts disk space. Scheduling has been stopped", msg, ARTIFACTS_DISK_FULL_ID);
    }

    protected SendEmailMessage createEmail() {
        return EmailMessageDrafter.noArtifactsDiskSpaceMessage(systemEnvironment, getAdminMail(), targetFolderCanonicalPath());
    }

    protected long limitInMb() {
        return systemEnvironment.getArtifactReposiotryFullLimit();
    }
}
