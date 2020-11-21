package core;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

/** 
 * Data Source creation
 */

final class DataSource {
	
	static boolean flagFirstOverriding = true;
	
	/**
	 * 
	 * Create Data Source that was initialized with properties from
	 * WETA-INF/context.xml that is overriding by properties from request with
	 * decrypting password
	 * 
	 * @param HashMap property - Data Source property for overriding
	 * @return Data Source with given properties
	 */

	static BasicDataSource dataSourceFactory() {
		/*
		 * Data Source initialization Container is specified in WEB-INF/web.xml Data
		 * Source is specified in WETA-INF/context.xml
		 */
		BasicDataSource dataSource = null;
		try {
			InitialContext initContext = new InitialContext();
			dataSource = (BasicDataSource) initContext.lookup("java:comp/env/jdbc/TestDB");
			// Override Data Source Parameters
			// Decrypting and overriding Data Source password
			if (flagFirstOverriding) {
				dataSource.setPassword(Crypto.passwordDecryptor(dataSource.getPassword()));
				flagFirstOverriding = false;
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return dataSource;
	}

}
