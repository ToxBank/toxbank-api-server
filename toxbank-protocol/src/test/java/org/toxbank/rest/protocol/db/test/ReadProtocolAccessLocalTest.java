package org.toxbank.rest.protocol.db.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import net.toxbank.client.policy.AccessRights;
import net.toxbank.client.policy.PolicyRule;

import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.db.ReadProtocolAccessLocal;

public class ReadProtocolAccessLocalTest extends QueryTest<ReadProtocolAccessLocal> {

	@Override
	protected ReadProtocolAccessLocal createQuery() throws Exception {
		ReadProtocolAccessLocal q = new ReadProtocolAccessLocal();
		q.setFieldname(new DBProtocol(1,1));
		q.setValue("guest");
		return q;
	}

	@Override
	protected void verify(ReadProtocolAccessLocal query, ResultSet rs) throws Exception {
		int records = 0;
		while (rs.next()) {
			AccessRights policy = query.getObject(rs);
			//Assert.assertEquals(1,user.getID());
			//Assert.assertEquals("guest",user.getUserName());
			System.out.println(policy);
			for (PolicyRule rule : policy.getRules()) {
				Assert.assertTrue(rule.allowsGET());
				Assert.assertFalse(rule.allowsPOST());
				Assert.assertFalse(rule.allowsPUT());
				Assert.assertFalse(rule.allowsDELETE());
			}
			
			records++;
		}
		Assert.assertEquals(1,records);
		
	}

}