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
import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.LoginContext;

import org.apache.directory.server.kerberos.shared.keytab.Keytab;
import org.apache.directory.server.kerberos.shared.keytab.KeytabEntry;
import org.apache.hadoop.minikdc.KerberosSecurityTestcase;
import org.apache.hadoop.minikdc.MiniKdc;
import org.junit.Assert;
import org.junit.Test;

public class TestMiniKdc extends KerberosSecurityTestcase {

  @Test
  public void testMiniKdcStart() {
    MiniKdc kdc = getKdc();
    Assert.assertNotSame(0, kdc.getPort());
  }

  @Test
  public void testKeytabGen() throws Exception {
    MiniKdc kdc = getKdc();
    File workDir = getWorkDir();

    kdc.createPrincipal(new File(workDir, "keytab"), "foo/bar", "bar/foo");
    Keytab kt = Keytab.read(new File(workDir, "keytab"));
    Set<String> principals = new HashSet<String>();
    for (KeytabEntry entry : kt.getEntries()) {
      principals.add(entry.getPrincipalName());
    }
    //here principals use \ instead of /
    //because org.apache.directory.server.kerberos.shared.keytab.KeytabDecoder
    // .getPrincipalName(IoBuffer buffer) use \\ when generates principal
    Assert.assertEquals(new HashSet<String>(Arrays.asList(
            "foo\\bar@" + kdc.getRealm(), "bar\\foo@" + kdc.getRealm())),
            principals);
  }

  @Test
  public void testKerberosLogin() throws Exception {
    MiniKdc kdc = getKdc();
    File workDir = getWorkDir();
    LoginContext loginContext = null;
    try {
      String principal = "foo";
      File keytab = new File(workDir, "foo.keytab");
      kdc.createPrincipal(keytab, principal);

      Set<Principal> principals = new HashSet<Principal>();
      principals.add(new KerberosPrincipal(principal));

      //client login
      Subject subject = new Subject(false, principals, new HashSet<Object>(),
              new HashSet<Object>());
      loginContext = new LoginContext("", subject, null,
              KerberosConfiguration.createClientConfig(principal, keytab));
      loginContext.login();
      subject = loginContext.getSubject();
      Assert.assertEquals(1, subject.getPrincipals().size());
      Assert.assertEquals(KerberosPrincipal.class,
              subject.getPrincipals().iterator().next().getClass());
      Assert.assertEquals(principal + "@" + kdc.getRealm(),
              subject.getPrincipals().iterator().next().getName());
      loginContext.logout();

      //server login
      subject = new Subject(false, principals, new HashSet<Object>(),
              new HashSet<Object>());
      loginContext = new LoginContext("", subject, null,
              KerberosConfiguration.createServerConfig(principal, keytab));
      loginContext.login();
      subject = loginContext.getSubject();
      Assert.assertEquals(1, subject.getPrincipals().size());
      Assert.assertEquals(KerberosPrincipal.class,
              subject.getPrincipals().iterator().next().getClass());
      Assert.assertEquals(principal + "@" + kdc.getRealm(),
              subject.getPrincipals().iterator().next().getName());
      loginContext.logout();

    } finally {
      if (loginContext != null) {
        loginContext.logout();
      }
    }
  }

}