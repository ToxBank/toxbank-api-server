package org.toxbank.rest.protocol.db.test;

import java.sql.ResultSet;

import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.protocol.db.ReadProtocol;

public class ReadProtocolByTimeStampTest extends QueryTest<ReadProtocol> {

	@Override
	protected ReadProtocol createQuery() throws Exception {
		ReadProtocol q = new ReadProtocol();
		DBProtocol p = new DBProtocol();
		p.setTimeModified(1095368500000L);//in msec
		/**
		 * in DB: 1095368400000, 1326699051000, 915141600000 
		 */
		q.setValue(p);
		return q;
	}

	@Override
	protected void verify(ReadProtocol query, ResultSet rs) throws Exception {
		System.out.println(query.getValue().getTimeModified());
		int records = 0;
		while (rs.next()) {
			DBProtocol protocol = query.getObject(rs);
			//System.out.println(String.format("%d\t%s\t%s",protocol.getID(),protocol.getTimeModified(),new Date(protocol.getTimeModified())));
			//Assert.assertEquals(2,protocol.getID());
			//Assert.assertNotNull(protocol.getKeywords());
			//Assert.assertEquals(new Long(1326699051000L),protocol.getTimeModified());
			records++;
		}
	//	Assert.assertEquals(1,records);
		
	}
	
}	
