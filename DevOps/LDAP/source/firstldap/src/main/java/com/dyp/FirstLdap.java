package com.dyp;

import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

public class FirstLdap {
    private final String URL = "ldap://127.0.0.1:389/"; // ldap协议：//主机地址：端口
    private final String BASEDN = "dc=example,dc=org";  // 根据自己情况进行修改
    private final String FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    private LdapContext ctx = null;
    private final Control[] connCtls = null;
    private void LDAP_connect() throws Exception {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, FACTORY);
        env.put(Context.PROVIDER_URL, URL + BASEDN);
//        env.put(Context.SECURITY_AUTHENTICATION, "simple");

        String root = "cn=admin,dc=example,dc=org";  //根据自己情况修改
        env.put(Context.SECURITY_PRINCIPAL, root);   // 管理员
        env.put(Context.SECURITY_CREDENTIALS, "admin");  // 管理员密码

        try {
            ctx = new InitialLdapContext(env, connCtls);
            System.out.println( "连接成功" );

        } catch (javax.naming.AuthenticationException e) {
            System.out.println("连接失败：");
            throw e;
        } catch (Exception e) {
            System.out.println("连接出错：");
            throw e;
        }

    }
    private void closeContext(){
        if (ctx != null) {
            try {
                ctx.close();
                System.out.println( "连接关闭成功" );
            }
            catch (NamingException e) {
                e.printStackTrace();
                System.out.println( "连接关闭失败" );
            }

        }
    }
    private String getUserDN(String uid) throws Exception {
        String userDN = "";
        try {
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

            NamingEnumeration<SearchResult> en = ctx.search("", "uid=" + uid, constraints);

            if (en == null || !en.hasMoreElements()) {
                System.out.println("未找到该用户");
            }
            // maybe more than one element
            while (en != null && en.hasMoreElements()) {
                Object obj = en.nextElement();
                if (obj instanceof SearchResult) {
                    SearchResult si = (SearchResult) obj;
                    userDN += si.getName();
                    userDN += "," + BASEDN;
                } else {
                    System.out.println(obj);
                }
            }
        } catch (Exception e) {
            System.out.println("查找用户时产生异常。");
            throw e;
        }

        return userDN;
    }

    public boolean authenricate(String UID, String password) throws Exception {
        boolean valide = false;
        String userDN = "";
        try {
            userDN = getUserDN(UID);
            ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDN);
            ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            ctx.reconnect(connCtls);
            System.out.println(userDN + " 验证通过");
            valide = true;
        } catch (AuthenticationException e) {
            System.out.println(userDN + " 验证失败");
            System.out.println(e.toString());
            valide = false;
            throw e;
        } catch (NamingException e) {
            System.out.println(userDN + " 验证失败");
            valide = false;
            throw e;
        } catch (Exception e) {
            throw e;
        }
        return valide;
    }
    private  boolean addUser(String usrName, String pwd) {

        try {
            BasicAttributes attrsbu = new BasicAttributes();
            BasicAttribute objclassSet = new BasicAttribute("objectclass");
            objclassSet.add("inetOrgPerson");
            attrsbu.put(objclassSet);
            attrsbu.put("sn", usrName);
            attrsbu.put("cn", usrName);
            attrsbu.put("uid", usrName);
            attrsbu.put("userPassword", pwd);
            ctx.createSubcontext("uid=" + usrName, attrsbu);
            System.out.println("创建："+usrName+"成功");
            return true;
        } catch (NamingException ex) {
            ex.printStackTrace();
            System.out.println("创建："+usrName+"失败");
        }

        return false;
    }
    public static void main(String[] args) {
        FirstLdap ldap = new FirstLdap();
        try {
            ldap.LDAP_connect();
            ldap.addUser("firstUser","secret");
            if(ldap.authenricate("firstUser", "secret") == true){

                System.out.println( "该用户认证成功" );

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ldap.closeContext();
        }

    }
}
