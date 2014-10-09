/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ------------------------
 * content taken from http://svn.apache.org/viewvc/hadoop/common/trunk/hadoop-common-project/hadoop-minikdc/src/test/java/org/apache/hadoop/minikdc/TestMiniKdc.java?view=markup&pathrev=1518847
 */
package fr.infologic.vei.sso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

class KerberosConfiguration extends Configuration
{
    private final String principal;
    private final String keytab;
    private final boolean isInitiator;

    private KerberosConfiguration(String principal, File keytab, boolean client)
    {
        this.principal = principal;
        this.keytab = keytab.getAbsolutePath();
        this.isInitiator = client;
    }

    static Configuration createClientConfig(String principal, File keytab)
    {
        return new KerberosConfiguration(principal, keytab, true);
    }

    static Configuration createServerConfig(String principal, File keytab)
    {
        return new KerberosConfiguration(principal, keytab, false);
    }

    static String getKrb5LoginModuleName()
    {
        return "com.sun.security.auth.module.Krb5LoginModule";
    }

    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(String name)
    {
        Map<String, String> options = new HashMap<>();
        options.put("keyTab", keytab);
        options.put("principal", principal);
        options.put("useKeyTab", "true");
        options.put("storeKey", "true");
        options.put("doNotPrompt", "true");
        options.put("useTicketCache", "true");
        options.put("renewTGT", "true");
        options.put("refreshKrb5Config", "true");
        options.put("isInitiator", Boolean.toString(isInitiator));
        options.put("debug", "true");

        return new AppConfigurationEntry[] { new AppConfigurationEntry(
                                                                       getKrb5LoginModuleName(),
                                                                       AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                                                                       options) };
    }
}