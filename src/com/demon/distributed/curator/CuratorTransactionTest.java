package com.demon.distributed.curator;

import java.util.Collection;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;

/**
 * Curator 事务
 */
public class CuratorTransactionTest {

	public static void main(String[] args) {
		
	}
	
	public static Collection<CuratorTransactionResult> transaction(CuratorFramework client) throws Exception {
		Collection<CuratorTransactionResult> results = client.inTransaction().create().forPath("/path/tx","transaction data".getBytes())
				.and().setData().forPath("/another/path","other data".getBytes())
				.and().delete().forPath("/yet/another/path")
				.and().commit();
		for (CuratorTransactionResult result : results) {
            System.out.println(result.getForPath() + " - " + result.getType());
        }
		return results;
	}
	
	public static CuratorTransaction startTransaction(CuratorFramework client){
		return client.inTransaction();
	}
	
	public static CuratorTransactionFinal addCreateToTransaction(CuratorTransaction transaction) throws Exception {
        return transaction.create().forPath("/a/path", "some data".getBytes()).and();
    }

    public static CuratorTransactionFinal addDeleteToTransaction(CuratorTransaction transaction) throws Exception {
        return transaction.delete().forPath("/another/path").and();
    }

    public static void commitTransaction(CuratorTransactionFinal transaction) throws Exception {
        transaction.commit();
    }

}
