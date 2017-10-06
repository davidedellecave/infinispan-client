package ddc.template.datagrid;

import java.io.IOException;
import java.util.UUID;

public class Main {
	private static final String DATAGRID_HOST="localhost";
	private static final String CACHE_NAME="default";
	private static final String USERNAME="app";
	private static final String PASSWORD="app";
	
	public static void main(String[] args) throws IOException {
		Main m = new Main();
		System.out.printf("----------- test1\n");
		m.test1();
		System.out.printf("----------- test2\n");
		m.test2();
	}
	
	
	private void test1() throws IOException {
		try (CacheManagerConnection manager = new CacheManagerConnection(DATAGRID_HOST, USERNAME, PASSWORD)) {		
			manager.put(CACHE_NAME, "1", "pippo");
			Object value = manager.get(CACHE_NAME, "1");
			System.out.printf("value = %s\n", value);
		} 		
	}
	
	private void test2() throws IOException {
		try (CacheManagerConnection manager = new CacheManagerConnection(DATAGRID_HOST, USERNAME, PASSWORD)) {
			
			CacheEntry e = new CacheEntry();
			e.setData(new String("pippo"));
			
			put(manager, e);
			CacheEntry retrived = get(manager, e.getId());			
			System.out.printf("item %s", retrived);
		} 		
	}

	private void put(CacheManagerConnection manager, CacheEntry e ) {
		manager.put(CACHE_NAME, e.getId().toString(), e);
	}
	
	private CacheEntry get(CacheManagerConnection manager, UUID key) {
		return (CacheEntry) manager.get(CACHE_NAME, key.toString());
	}
	
}
