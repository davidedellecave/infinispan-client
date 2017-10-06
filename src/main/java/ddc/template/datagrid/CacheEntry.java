package ddc.template.datagrid;

import java.io.Serializable;
import java.util.UUID;

public class CacheEntry implements Serializable {
	private static final long serialVersionUID = -6081345163449421816L;

	private UUID id = UUID.randomUUID();
	private Object data;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return "(" + id.toString() + ", " + String.valueOf(data) + ")" ;
	}
}
